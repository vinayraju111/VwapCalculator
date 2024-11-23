package VWAP;

public class CcyFxData {
    private final Object mCcyFxDataLock = new Object();
    public CcyFxData(int minute){
        mHour = 0;
        mMinute = minute;
        mAggregatedPriceVolume = 0;
        mAggregatedVolume = 0;
    }
     private double mAggregatedPriceVolume;
     private double mAggregatedVolume;
     private int mHour;
     private int mMinute;
     public synchronized void ProcessFxDataUpdate(double price,double volume,int hour){
         synchronized (mCcyFxDataLock) {
             //if the existing data corresponds to same hour & min then append the values.
             if (hour == mHour) {
                 mAggregatedPriceVolume += price * volume;
                 mAggregatedVolume += volume;
             } else {
                 //if this update is the first in that hour & min then reset all the members.
                 mHour = hour;
                 mAggregatedPriceVolume = price * volume;
                 mAggregatedVolume = volume;
             }
         }
     }
     public double AggregatedPriceVolume(int hour, int minute) {
         synchronized (mCcyFxDataLock) {
             // past one hour data need to be evaluated any other data should be ignored
             // if current time is 4:30, we will check the data for 4:00 to 4:30
             // and 3:31 to 3:59
             if ((mHour == hour && mMinute <= minute) || (mHour == hour - 1 && mMinute > minute)){
                 return mAggregatedPriceVolume;
             }
             return 0;
         }
     }
     public double AggregatedVolume(int hour, int minute) {
         synchronized (mCcyFxDataLock) {
             if ((mHour == hour && mMinute <= minute) || (mHour == hour - 1 && mMinute > minute)){
                 return mAggregatedVolume;
             }
             return 0;
         }
     }
}
