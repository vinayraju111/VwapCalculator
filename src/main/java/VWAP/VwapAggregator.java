package VWAP;

import java.util.Map;
import java.util.HashMap;

public class VwapAggregator {
    private final Map<String,CcyFxData[]> mFxData;

    public VwapAggregator(){
        mFxData = new HashMap<String, CcyFxData[]>();
    }

    public synchronized CcyFxData[] GetCcyFxDataList(String ccyPair, boolean createOnNull) {
        //Initialize the dictionary if the ccyPair Don't exist already.
        if (!mFxData.containsKey(ccyPair)) {
            if (createOnNull) {
                mFxData.put(ccyPair, new CcyFxData[60]);
                CcyFxData[] ccyFxData = mFxData.get(ccyPair);
                for (int i = 0; i < 60; i++)
                    ccyFxData[i] = new CcyFxData();
            }
            else {
                System.out.println("No data exists for " + ccyPair);
                return new CcyFxData[0];
            }
        }
        return mFxData.get(ccyPair);
    }
    public double CalculateVwap(String ccyPair){
        CcyFxData[] ccyFxData = GetCcyFxDataList(ccyPair, false);
        double totalPriceVolume = 0;
        double totalVolume = 0;
        for (CcyFxData ccyFxDatum : ccyFxData) {
            totalPriceVolume += ccyFxDatum.AggregatedPriceVolume();
            totalVolume += ccyFxDatum.AggregatedVolume();
        }
        //calculate the vwap only if totalVolume is not zero.
        if (totalVolume != 0 ){
            return totalPriceVolume/totalVolume;
        }
        return 0;
    }

    public void processFxDataUpdate(String ccyPair, double price, double volume, int hh, int mm)
    {
        CcyFxData[] ccyFxDataList = GetCcyFxDataList(ccyPair, true);
        // Ignore all records which have invalid minute time stamp.
        if (mm<60 && mm>0){
            ccyFxDataList[mm].ProcessFxDataUpdate(price, volume,hh);
        }
    }
}
