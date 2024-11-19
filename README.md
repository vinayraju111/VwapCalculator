# VwapCalculator

**Description**

We read the fx data from an input CSV file and watch for live updates on the file for new data throught the day. We prompt the user to confirm if we need to calculate the VWAP, on request we calculate the VWAP for past one hour data that we read through the above stream.

The program mainly runs on two types of threads
FxDataProcessor thread - which takes each FX Data update from stream and process it to store in a compressed format.
VWAP calculator thread - which on request iterates over the compressed data to calculate the VWAP for past one hour.

**Data Structure**

We store the data in a dictionary wherein the key is a currency pair and value is an array of 60 elements. each element represents data accumulated for the past one min in that hour. We mainly store two fields in this data, one is Aggregated volume and the other is aggregated price volume. Also we keep a note of the hour for which the data is aggregated. When we get data for a new hour and minute, we reset these values so that the aggregation starts from 0 again. Upon calculation as well we check whether the data here is aggregated for past one hour or not with the help of hour stored in that data.

**Assumptions**

We assume that the data is sent in that csv file in a ascending time order.


**How to Run the program**

We need to set the environment variable FXDATA_CSV to the path of the CSV file where we store the FX data to be processed.
Load the maven project in the IntelliJ and run Main.java

**Further improvements**

Currently we read the FX data from one stream (CSV file), if it is ok to feed multiple streams (may be separate stream for each currency pair) then we could simply change the program to read a list of file paths from the environment variable and in code we could creata a separate FxDataProcessor thread for each of them.

**Testing details**

Tested the program with a CSV file of 150 MB data.


![TestEvidence 150MB CSV file](https://github.com/user-attachments/assets/c454e7e9-879a-4182-880b-9a6b328e0852)
