package VWAP.Tests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import VWAP.FxDataStreamObject;
import VWAP.FxDataProcessor;

public class FxDataProcessorTests {
    @Test
    void testRetrieveFxDataValidInputAM() {
        String input = "10:30 AM,USD/JPY,145.56,1000.5";
        FxDataStreamObject result = FxDataProcessor.retrieveFxData(input);

        assertEquals("USD/JPY", result.mCcyPair);
        assertEquals(10, result.mHour);
        assertEquals(30, result.mMinute);
        assertEquals(145.56, result.mPrice);
        assertEquals(1000.5, result.mVolume);
    }

    @Test
    void testRetrieveFxDataValidInputPM() {
        String input = "02:15 PM,EUR/USD,1.2345,500.75";
        FxDataStreamObject result = FxDataProcessor.retrieveFxData(input);

        assertEquals("EUR/USD", result.mCcyPair);
        assertEquals(14, result.mHour); // Converted to 24-hour format
        assertEquals(15, result.mMinute);
        assertEquals(1.2345, result.mPrice);
        assertEquals(500.75, result.mVolume);
    }

    @Test
    void testRetrieveFxDataInvalidInput() {
        String input = "Invalid data line";
        FxDataStreamObject result = FxDataProcessor.retrieveFxData(input);


        assertEquals("", result.mCcyPair);
        assertEquals(0, result.mHour);
        assertEquals(0, result.mMinute);
        assertEquals(0.0, result.mPrice);
        assertEquals(0.0, result.mVolume);
    }

    @Test
    void testRetrieveFxDataIncompleteData() {
        String input = "10:30 AM,USD/JPY,145.56";
        FxDataStreamObject result = FxDataProcessor.retrieveFxData(input);

        assertEquals("", result.mCcyPair);
        assertEquals(0, result.mHour);
        assertEquals(0, result.mMinute);
        assertEquals(0.0, result.mPrice);
        assertEquals(0.0, result.mVolume);
    }

    @Test
    void testRetrieveFxDataEdgeCaseMidnight() {
        String input = "12:00 AM,AUD/CAD,0.9876,200";
        FxDataStreamObject result = FxDataProcessor.retrieveFxData(input);

        assertEquals("AUD/CAD", result.mCcyPair);

        assertEquals(0, result.mHour); // Midnight in 24-hour format
        assertEquals(0, result.mMinute);
        assertEquals(0.9876, result.mPrice);
        assertEquals(200.0, result.mVolume);
    }

    @Test
    void testRetrieveFxDataEdgeCaseNoon() {
        String input = "12:00 PM,NZD/USD,0.6543,300";
        FxDataStreamObject result = FxDataProcessor.retrieveFxData(input);

        assertEquals("NZD/USD", result.mCcyPair);

        assertEquals(12, result.mHour); // Noon in 24-hour format
        assertEquals(0, result.mMinute);
        assertEquals(0.6543, result.mPrice);
        assertEquals(300.0, result.mVolume);
    }
    @Test
    void testRetrieveFxDataInvalidPriceVolume() {
        String input = "10:30 AM,USD/JPY,ABC,XYZ";
        FxDataStreamObject result = FxDataProcessor.retrieveFxData(input);

        assertEquals("", result.mCcyPair);
        assertEquals(0, result.mHour);
        assertEquals(0, result.mMinute);
        assertEquals(0.0, result.mPrice);
        assertEquals(0.0, result.mVolume);
    }
}

