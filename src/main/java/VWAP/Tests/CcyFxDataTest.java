package VWAP.Tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import VWAP.CcyFxData;

public class CcyFxDataTest {
    private CcyFxData ccyFxData;

    @BeforeEach
    public void setUp() {
        ccyFxData = new CcyFxData();
    }

    @Test
    public void testProcessFxDataUpdate_SameHour() {
        ccyFxData.ProcessFxDataUpdate(100, 10, 12, 30);
        ccyFxData.ProcessFxDataUpdate(200, 5, 12, 30);

        // Query the same minute
        assertEquals(2000, ccyFxData.AggregatedPriceVolume(12, 30), 0);
        assertEquals(15, ccyFxData.AggregatedVolume(12, 30), 0);

        // Query after 30 mins
        assertEquals(2000, ccyFxData.AggregatedPriceVolume(13, 0), 0);
        assertEquals(15, ccyFxData.AggregatedVolume(13, 0), 0);

        // Query after 59 mins
        assertEquals(2000, ccyFxData.AggregatedPriceVolume(13, 29), 0);
        assertEquals(15, ccyFxData.AggregatedVolume(13, 29), 0);

        // Query after 1 hour
        assertEquals(0, ccyFxData.AggregatedPriceVolume(13, 30), 0);
        assertEquals(0, ccyFxData.AggregatedVolume(13, 30), 0);
    }

    @Test
    public void testProcessFxDataUpdate_NewHour() {
        ccyFxData.ProcessFxDataUpdate(100, 10, 12, 59);
        assertEquals(1000, ccyFxData.AggregatedPriceVolume(12, 59));
        assertEquals(10, ccyFxData.AggregatedVolume(12, 59));

        // Send update in next hour but same minute
        ccyFxData.ProcessFxDataUpdate(200, 11, 13, 59);

        // Ideally we will not have this scenario - as we already received update in the next hour
        // so we do not expect the query request in the past. But still we should return 0
        assertEquals(0, ccyFxData.AggregatedPriceVolume(12, 59));
        assertEquals(0, ccyFxData.AggregatedVolume(12, 59));

        // Query the same minute as latest process request
        assertEquals(2200, ccyFxData.AggregatedPriceVolume(13, 59));
        assertEquals(11, ccyFxData.AggregatedVolume(13, 59));

        // Query after 30 mins
        assertEquals(2200, ccyFxData.AggregatedPriceVolume(14, 20), 0);
        assertEquals(11, ccyFxData.AggregatedVolume(14, 20), 0);

        // Query after 59 mins
        assertEquals(2200, ccyFxData.AggregatedPriceVolume(14, 58), 0);
        assertEquals(11, ccyFxData.AggregatedVolume(14, 58), 0);

        // Query after 1 hour
        assertEquals(0, ccyFxData.AggregatedPriceVolume(14, 59), 0);
        assertEquals(0, ccyFxData.AggregatedVolume(14, 59), 0);
    }


    @Test
    public void testConcurrentUpdates() throws InterruptedException {
        int nThreads = 100;
        int price = 87;
        int volume = 10;
        Thread[] threads = new Thread[nThreads];
        for (int i =0; i < nThreads; i++)
        {
            threads[i] = new Thread(() -> ccyFxData.ProcessFxDataUpdate(price, volume, 12, 30));
        }
        for (int i =0; i < nThreads; i++)
        {
            threads[i].start();
        }
        for (int i =0; i < nThreads; i++)
        {
            threads[i].join();
        }

        double exAggregatedPriceVolume = price * volume * nThreads;
        double exAggregateVolume = volume * nThreads;

        assertEquals(exAggregatedPriceVolume, ccyFxData.AggregatedPriceVolume(12, 30), 0);
        assertEquals(exAggregateVolume, ccyFxData.AggregatedVolume(12, 30), 0);
    }
}