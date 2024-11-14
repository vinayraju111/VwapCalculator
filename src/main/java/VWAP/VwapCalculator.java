package VWAP;

import java.io.IOException;
import java.util.Scanner;

public class VwapCalculator implements Runnable{
    public VwapCalculator(VwapAggregator vwapAggregator){
        mVwapAggregator = vwapAggregator;
        mScanner  = new Scanner(System.in);
    }

        private VwapAggregator mVwapAggregator;
        Scanner mScanner;
    @Override
    public void run() {
        System.out.println("Starting VWAP Calculator...");
        while (true) {
                String ccyPair = PromptForCcyPair();
                if (ccyPair.isEmpty()){
                    System.out.println("Terminating the Calculator");
                    return ;
                }
                System.out.println("Calculating VWAP for ccyPair " + ccyPair);
                double vwap = mVwapAggregator.CalculateVwap(ccyPair);
                System.out.println("VWAP : "+vwap);
        }
    }

    private String PromptForCcyPair (){
        System.out.println("Enter the CCY_PAIR (Enter Blank to Exit) :-" );
        String ccyPair = mScanner.nextLine().toUpperCase();
        return ccyPair;
    }
}
