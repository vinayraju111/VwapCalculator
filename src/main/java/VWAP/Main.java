package VWAP;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        String filePath = System.getenv("FXDATA_CSV");
        VwapAggregator vwapAggregator = new VwapAggregator();
        FxDataProcessor fxDataProcessor = new FxDataProcessor(filePath, vwapAggregator);
        VwapCalculator vwapCalculator= new VwapCalculator(vwapAggregator);
        Thread dataProcessorThread = new Thread(fxDataProcessor);
        Thread calculatorThread = new Thread(vwapCalculator);
        dataProcessorThread.start();
        calculatorThread.start();
        calculatorThread.join();
        dataProcessorThread.interrupt();
    }
}