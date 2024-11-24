package VWAP;

public class FxDataStreamObject {
    public String mCcyPair;
    public int mHour;
    public int mMinute;
    public double mPrice;
    public double mVolume;

    public FxDataStreamObject(String ccyPair, int hour, int min, double price, double volume)
    {
        mCcyPair = ccyPair;
        mHour = hour;
        mMinute = min;
        mPrice = price;
        mVolume = volume;
    }
    public FxDataStreamObject()
    {
        mCcyPair = ""; mHour = 0; mMinute = 0; mPrice = 0; mVolume = 0;
    }
}
