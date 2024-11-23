package VWAP.Tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import VWAP.VwapAggregator;

import java.util.HashMap;

public class VwapAggregatorTests {
    private VwapAggregator vwapAggregator;

    @BeforeEach
    public void setUp() {
        vwapAggregator = new VwapAggregator();
    }

    @Test
    public void TestDictionaryCreation()
    {
        vwapAggregator.processFxDataUpdate("USD/AUD", 10, 100, 10, 5);
        vwapAggregator.processFxDataUpdate("USD/AUD", 10, 100, 10, 5);
        vwapAggregator.processFxDataUpdate("USD/AUD", 10, 100, 10, 6);
        vwapAggregator.processFxDataUpdate("USD/SGD", 11, 100, 10, 15);
        vwapAggregator.processFxDataUpdate("USD/SGD", 11, 100, 10, 15);
        vwapAggregator.processFxDataUpdate("USD/SGD", 11, 100, 10, 16);

        // the following requests should be ignored
        vwapAggregator.processFxDataUpdate("", 10, 100, 10, 5);
        vwapAggregator.processFxDataUpdate("", 10, 100, 10, 5);
        vwapAggregator.processFxDataUpdate("", 10, 100, 10, 6);

        HashMap<String, Double> vwapValues = vwapAggregator.CalculateVwap(10, 6);
        assertEquals(1, vwapValues.size());

        vwapValues = vwapAggregator.CalculateVwap(10, 16);
        assertEquals(2, vwapValues.size());

        vwapValues = vwapAggregator.CalculateVwap(10, 46);
        assertEquals(2, vwapValues.size());

        vwapValues = vwapAggregator.CalculateVwap(11, 6);
        assertEquals(1, vwapValues.size());

        vwapValues = vwapAggregator.CalculateVwap(10, 6);
        assertEquals(1, vwapValues.size());

        vwapValues = vwapAggregator.CalculateVwap(11, 16);
        assertEquals(0, vwapValues.size());
    }

    @Test
    public void TestVWAPCalculation()
    {
        vwapAggregator.processFxDataUpdate("USD/AUD", 10, 100, 10, 5);
        vwapAggregator.processFxDataUpdate("USD/AUD", 10.1, 110, 10, 5);
        vwapAggregator.processFxDataUpdate("USD/AUD", 10.2, 120, 10, 15);
        vwapAggregator.processFxDataUpdate("USD/AUD", 10.4, 140, 10, 15);
        vwapAggregator.processFxDataUpdate("USD/AUD", 10.1, 150, 10, 16);
        vwapAggregator.processFxDataUpdate("USD/AUD", 10.2, 194, 10, 17);
        vwapAggregator.processFxDataUpdate("USD/AUD", 9.9, 100, 10, 25);
        vwapAggregator.processFxDataUpdate("USD/AUD", 9.8, 110, 10, 25);
        vwapAggregator.processFxDataUpdate("USD/AUD", 10.5, 130, 10, 32);
        vwapAggregator.processFxDataUpdate("USD/AUD", 10.4, 110, 10, 33);
        vwapAggregator.processFxDataUpdate("USD/AUD", 10.2, 99, 10, 39);
        vwapAggregator.processFxDataUpdate("USD/AUD", 12, 22, 10, 45);
        vwapAggregator.processFxDataUpdate("USD/AUD", 11.1, 56, 10, 52);
        vwapAggregator.processFxDataUpdate("USD/AUD", 10.4, 66, 10, 54);
        vwapAggregator.processFxDataUpdate("USD/AUD", 12.9, 10, 10, 55);
        vwapAggregator.processFxDataUpdate("USD/AUD", 10.2, 45, 10, 55);
        vwapAggregator.processFxDataUpdate("USD/AUD", 11.2, 11, 11, 3);
        vwapAggregator.processFxDataUpdate("USD/AUD", 10, 140, 11, 4);

        /* For 11:04 we consider all the above fx data updates for USD/AUD
         * Aggregated volume = 1713
         * Aggregate price volume = 17554.8
         * VWap = AggPriceVolume / AggregatedVolume
         * */
        HashMap<String, Double> vwapValues = vwapAggregator.CalculateVwap(11, 4);
        assertEquals(1, vwapValues.size());
        assertEquals( (17554.8/1713), vwapValues.get("USD/AUD"), 0.000001);

        /* For 11:06 we ignore first two updated from the above fx data for USD/AUD
         * Aggregated volume = 1503
         * Aggregate price volume = 15443.8
         * VWap = AggPriceVolume / AggregatedVolume
         * */
        vwapValues = vwapAggregator.CalculateVwap(11, 6);
        assertEquals(1, vwapValues.size());
        assertEquals( (15443.8/1503), vwapValues.get("USD/AUD"), 0.000001);

        /* For 11:16 we ignore first five updated from the above fx data for USD/AUD
         * Aggregated volume = 1093
         * Aggregate price volume = 11248.8
         * VWap = AggPriceVolume / AggregatedVolume
         * */
        vwapValues = vwapAggregator.CalculateVwap(11, 16);
        assertEquals(1, vwapValues.size());
        assertEquals( (11248.8/1093), vwapValues.get("USD/AUD"), 0.000001);

        /* For 11:32 we ignore first nine updated from the above fx data for USD/AUD
         * Aggregated volume = 559
         * Aggregate price volume = 5837
         * VWap = AggPriceVolume / AggregatedVolume
         * */
        vwapValues = vwapAggregator.CalculateVwap(11, 32);
        assertEquals(1, vwapValues.size());
        assertEquals( (5837.0/559), vwapValues.get("USD/AUD"), 0.000001);

        /* For 11:39 we ignore first eleven updated from the above fx data for USD/AUD
         * Aggregated volume = 350
         * Aggregate price volume = 3683.2
         * VWap = AggPriceVolume / AggregatedVolume
         * */
        vwapValues = vwapAggregator.CalculateVwap(11, 39);
        assertEquals(1, vwapValues.size());
        assertEquals( (3683.2/350), vwapValues.get("USD/AUD"), 0.000001);

        /* For 11:55 we ignore first sixteen updated from the above fx data for USD/AUD
         * Aggregated volume = 151
         * Aggregate price volume = 1523.2
         * VWap = AggPriceVolume / AggregatedVolume
         * */
        vwapValues = vwapAggregator.CalculateVwap(11, 55);
        assertEquals(1, vwapValues.size());
        assertEquals( (1523.2 /151), vwapValues.get("USD/AUD"), 0.000001);

        /* For 12:06 we ignore first two updated from the above fx data for USD/AUD
         * Aggregated volume = 0
         * Aggregate price volume = 0.0
         * VWap = AggPriceVolume / AggregatedVolume
         * There won't be anyVwap for this CcyPair
         * */
        vwapValues = vwapAggregator.CalculateVwap(12, 4);
        assertEquals(0, vwapValues.size());
    }

    @Test
    public void testConcurrentUpdates() throws InterruptedException {
        int nThreads = 100;
        int price = 87;
        int volume = 10;
        String ccyPair1 = "USD/AUD";
        String ccyPair2 = "SGD/INR";
        Thread[] threads = new Thread[nThreads];
        for (int i =0; i < nThreads; i++)
        {
            if (i % 2 == 0) {
                threads[i] = new Thread(() -> vwapAggregator.processFxDataUpdate(ccyPair1, price, volume, 12, 30));
            }
            else {
                threads[i] = new Thread(() -> vwapAggregator.processFxDataUpdate(ccyPair2, price, volume, 12, 30));
            }
        }
        for (int i =0; i < nThreads; i++)
        {
            threads[i].start();
        }
        for (int i =0; i < nThreads; i++)
        {
            threads[i].join();
        }


        HashMap<String, Double>  vwapValues = vwapAggregator.CalculateVwap(12, 30);
        assertEquals(2, vwapValues.size());
    }




}
