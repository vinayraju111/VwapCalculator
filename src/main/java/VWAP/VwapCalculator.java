package VWAP;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class VwapCalculator implements Runnable{
    public VwapCalculator(VwapAggregator vwapAggregator){
        mVwapAggregator = vwapAggregator;
        mScanner  = new Scanner(System.in);
    }

        private final VwapAggregator mVwapAggregator;
        Scanner mScanner;
    @Override
    public void run() {
        System.out.println("Starting VWAP Calculator...");
        while (true) {
                String response = PromptForVWAP();
                if (!response.equals("Y")){
                    System.out.println("Terminating the Calculator");
                    return ;
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm");
                String curentTime = LocalTime.now().toString();

                System.out.println("Calculating VWAP for past hour. Current time: " + curentTime);
                String[] timetokens = curentTime.split(":");
                mVwapAggregator.CalculateVwap(Integer.parseInt(timetokens[0]), Integer.parseInt(timetokens[1]));
        }
    }

    private String PromptForVWAP (){
        System.out.println("Calculate VWAP (Y), Exit(any key) :-" );
        return mScanner.nextLine().toUpperCase();
    }
}
