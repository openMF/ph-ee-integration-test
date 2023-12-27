package org.mifos.integrationtest.common;

import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;import org.springframework.stereotype.Component;
import java.io.FileWriter;
import java.io.IOException;

@Component
public class CsvHelper {

    public void createCsvFileWithHeaders(String filePath, String[] header)throws IOException {
        CSVWriter writer = (CSVWriter) new CSVWriterBuilder(new FileWriter(filePath)).withQuoteChar('\0').build();
        writer.writeNext(header);
        writer.close();
    }

    public void addRow(String filePath, String[] row)throws IOException {
        ICSVWriter writer = new CSVWriterBuilder(new FileWriter(filePath, true)).withQuoteChar('\0').build();
        writer.writeNext(row);
        writer.close();
    }
}
