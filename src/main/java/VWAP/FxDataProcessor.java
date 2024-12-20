package VWAP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//FxDataProcessor will open the Csv file and read line by line in a thread
// once it finishes the entire file it keep on looking for new lines.
//[Timestamp, Currency-pair, Price, Volume] the CSV need to be in this format.
public class FxDataProcessor implements Runnable {
    private final String filePath;
    private long lastKnownPosition = 0;
    private final VwapAggregator mVwapAggregator;

    public FxDataProcessor(String filePath, VwapAggregator vwapAggregator) {
        this.filePath = filePath;
        this.mVwapAggregator = vwapAggregator;
    }

    @Override
    public void run() {
        System.out.println("Starting Fx Monitor...");
        while (true) {
            try {
                readMarketDataFromCsv();
                //System.out.println("Sleeping before we check updated FxData stream");
                Thread.sleep(10);

            } catch (InterruptedException e) {
                System.out.println("Thread Interrupted . Stopping FxMonitor");
                break;
            } catch (Exception e) {
                System.out.println("An error occurred while reading the file : " + e.getMessage());
                break;
            }
        }
    }
    public static FxDataStreamObject retrieveFxData(String line){
        try {
            // stream should be in the below format
            // 10:30 AM, USD/AUD, 10, 1000
            String[] tokens = line.split(",");
            if (tokens.length == 4) {

                String time = tokens[0];
                String ccyPair = tokens[1].toUpperCase();
                double price = Double.parseDouble(tokens[2]);
                double volume = Double.parseDouble(tokens[3]);

                String[] timeTokens = time.split(":");
                int hour = Integer.parseInt(timeTokens[0]);
                String[] timeSubToken = timeTokens[1].split(" ");
                int minute = Integer.parseInt(timeSubToken[0]);
                // we will convert the hour into 24 hour format so that it would be
                // easy to save the compressed format for the whole day
                if (timeSubToken[1].equalsIgnoreCase("PM") && hour != 12)
                    hour += 12;
                if (timeSubToken[1].equalsIgnoreCase("AM") && hour == 12)
                    hour = 0;

                return new FxDataStreamObject(ccyPair, hour, minute, price, volume);
            }
        }catch (Exception e){
            System.out.println("Error occurred while parsing the line " + line);
        }
        return new FxDataStreamObject();
    }
    private void readMarketDataFromCsv() throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            //Skip the Bytes that are already Processed.
            br.skip(lastKnownPosition);
            String line;
            while ((line = br.readLine()) != null) {
                lastKnownPosition += line.length() + 2;
                FxDataStreamObject obj = retrieveFxData(line);
                mVwapAggregator.processFxDataUpdate(obj);

            }
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}