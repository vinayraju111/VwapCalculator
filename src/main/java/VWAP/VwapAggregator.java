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
                     ccyFxData[i] = new CcyFxData(i);
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
    public HashMap<String, Double>  CalculateVwap(int hour, int minute){
        Vector<String> ccyPairs = GetCcyPairs();

        HashMap<String, Double> vwapValues = new HashMap<>();

        for (String ccyPair: ccyPairs) {
            CcyFxData[] ccyFxData = GetCcyFxDataList(ccyPair);

            double totalPriceVolume = 0;
            double totalVolume = 0;
            for (CcyFxData ccyFxDatum : ccyFxData) {
                totalPriceVolume += ccyFxDatum.AggregatedPriceVolume(hour, minute);
                totalVolume += ccyFxDatum.AggregatedVolume(hour, minute);
            }
            //calculate the vwap only if totalVolume is not zero.
            if (totalVolume != 0) {
                vwapValues.put(ccyPair, (totalPriceVolume / totalVolume));
            }
        }
        return vwapValues;
    }

    public void processFxDataUpdate(FxDataStreamObject obj)
    {
        if (obj.mCcyPair.isEmpty())
        {
            System.out.println("Empty currency pair received! Ignore the request.");
            return;
        }
        CcyFxData[] ccyFxDataList = GetCcyFxDataList(obj.mCcyPair);
        // Ignore all records which have invalid minute time stamp.
        if (obj.mMinute<60 && obj.mMinute>0){
            ccyFxDataList[obj.mMinute].ProcessFxDataUpdate(obj.mPrice, obj.mVolume, obj.mHour);
        }
    }
}
