package VWAP;

public class CcyFxData {
    private final Object mCcyFxDataLock = new Object();
    public CcyFxData(){
        mHh = 0;
        mAggregatedPriceVolume = 0;
        mAggregatedVolume = 0;
    }
     private double mAggregatedPriceVolume;
     private double mAggregatedVolume;
     private int mHh;
     private int mMm;
     public synchronized void ProcessFxDataUpdate(double price,double volume,int hh, int mm){
         synchronized (mCcyFxDataLock) {
             //if the existing data corresponds to same hour & min then append the values.
             if (hh == mHh) {
                 mMm = mm;
                 mAggregatedPriceVolume += price * volume;
                 mAggregatedVolume += volume;
             } else {
                 //if this update is the first in that hour & min then reset all the members.
                 mHh = hh;
                 mMm = mm;
                 mAggregatedPriceVolume = price * volume;
                 mAggregatedVolume = volume;
             }
         }
     }
     public double AggregatedPriceVolume(int hh, int mm) {
         synchronized (mCcyFxDataLock) {
             // past one hour data need to be evaluated any other data should be ignored
             // if current time is 4:30, we will check the data for 4:00 to 4:30
             // and 3:31 to 3:59
             if ((mHh == hh && mMm <= mm) || (mHh == hh - 1 && mMm > mm)){
                 return mAggregatedPriceVolume;
             }
             return 0;
         }
     }
     public double AggregatedVolume(int hh, int mm) {
         synchronized (mCcyFxDataLock) {
             if ((mHh == hh && mMm <= mm) || (mHh == hh - 1 && mMm > mm)){
                 return mAggregatedVolume;
             }
             return 0;
         }
     }
}
