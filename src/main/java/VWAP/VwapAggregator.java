package VWAP;

import java.util.Map;
import java.util.HashMap;
import java.util.Vector;

public class VwapAggregator {
    private final Object mVwapAggregatorLock = new Object();

    private final Map<String,CcyFxData[]> mFxData;

    public VwapAggregator(){
        mFxData = new HashMap<>();
    }

    private CcyFxData[] GetCcyFxDataList(String ccyPair) {
         if (ccyPair.isEmpty()) {
             return new CcyFxData[0];
         }
         synchronized (mVwapAggregatorLock) {
             //Initialize the dictionary if the ccyPair Don't exist already.
             if (!mFxData.containsKey(ccyPair)) {
                 mFxData.put(ccyPair, new CcyFxData[60]);
                 CcyFxData[] ccyFxData = mFxData.get(ccyPair);
                 for (int i = 0; i < 60; i++)
                     ccyFxData[i] = new CcyFxData();
             }
             return mFxData.get(ccyPair);
         }
    }

    private Vector<String> GetCcyPairs()
    {
        synchronized (mVwapAggregatorLock){
            return new Vector<>(mFxData.keySet());
        }
    }
    public void  CalculateVwap(int hh, int mm){
        Vector<String> ccyPairs = GetCcyPairs();
        for (String ccyPair: ccyPairs) {
            CcyFxData[] ccyFxData = GetCcyFxDataList(ccyPair);

            double totalPriceVolume = 0;
            double totalVolume = 0;
            for (CcyFxData ccyFxDatum : ccyFxData) {
                totalPriceVolume += ccyFxDatum.AggregatedPriceVolume(hh, mm);
                totalVolume += ccyFxDatum.AggregatedVolume(hh, mm);
            }
            //calculate the vwap only if totalVolume is not zero.
            if (totalVolume != 0) {
                System.out.println(ccyPair + ":" + (totalPriceVolume / totalVolume));
            }
        }
    }

    public void processFxDataUpdate(String ccyPair, double price, double volume, int hh, int mm)
    {
        CcyFxData[] ccyFxDataList = GetCcyFxDataList(ccyPair);
        // Ignore all records which have invalid minute time stamp.
        if (mm<60 && mm>0){
            ccyFxDataList[mm].ProcessFxDataUpdate(price, volume,hh, mm);
        }
    }
}
