package VWAP;

public class CcyFxData {
    private final Object lock = new Object();
    public CcyFxData(){
        mHh = 0;
        mAggregatedPriceVolume = 0;
        mAggregatedVolume = 0;
    }
     private double mAggregatedPriceVolume;
     private double mAggregatedVolume;
     private int mHh;
     public synchronized void ProcessFxDataUpdate(double price,double volume,int hh){
         synchronized (lock) {
             //if the existing data corresponds to same hour & min then append the values.
             if (hh == mHh) {
                 mAggregatedPriceVolume += price * volume;
                 mAggregatedVolume += volume;
             } else {
                 //if this update is the first in that hour & min then reset all the members.
                 mHh = hh;
                 mAggregatedPriceVolume = price * volume;
                 mAggregatedVolume = volume;
             }
         }
     }
     public double AggregatedPriceVolume() {
         synchronized (lock) {
             return mAggregatedPriceVolume;
         }
     }
     public double AggregatedVolume() {
         synchronized (lock) {
             return mAggregatedVolume;
         }
     }
}
