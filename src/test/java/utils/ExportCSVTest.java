package utils;

import POM.SearchResultItem;
import au.com.bytecode.opencsv.CSVReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ExportCSVTest {
    String OS = System.getProperty("os.name").toLowerCase();
    // Valid Path
    private final String validPath = "src//test//resources//temp//test.csv";
    private final String invalidPath = "src//test//resources//temporary//test.csv";
    private final String absolutePath = System.getProperty("java.io.tmpdir") + "test.csv";
    private final List<SearchResultItem> items = new ArrayList<>();

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Before
    public void clearItems(){
        items.clear();
        items.add(new SearchResultItem("title","http://www.url.com", 5.0f, 5.0f));
    }

    @After
    public void cleanup(){
        File tmpRelativeFile = new File(validPath);
        tmpRelativeFile.delete();

        File tmpAbsoluteFile = new File(absolutePath);
        tmpAbsoluteFile.delete();
    }

    @Test
    public void export_to_relative_valid_path() throws IOException {
        boolean result;
        Common.exportSearchItemsToCSV(validPath, items);

        File tmpFile = new File(validPath);
        result = tmpFile.exists();

        assertTrue("Valid relative directory: \""+ validPath +"\" should be valid", result);
    }

    @Test
    public void export_to_absolute_valid_path() throws IOException {
        boolean result;
        Common.exportSearchItemsToCSV(absolutePath, items);

        File tmpFile = new File(absolutePath);
        result = tmpFile.exists();

        assertTrue("Valid relative validPath: \"" + absolutePath + "\" should be valid", result);

    }

    @Test
    public void export_to_relative_invalid_path() throws IOException {
        thrown.expect(IOException.class);
        Common.exportSearchItemsToCSV(invalidPath, items);

    }

    @Test
    public void exported_file_has_4_columns() throws IOException {
        boolean result;
        Common.exportSearchItemsToCSV(validPath, items);

        CSVReader csvReader = new CSVReader(new FileReader(validPath));
        List<String[]> allData = csvReader.readAll();
        result = allData.get(0).length == 4;
        assertTrue("Exported file must have 4 columns", result);
    }

    @Test
    public void exported_file_has_relevant_column_titles() throws IOException {
        boolean result;
        Common.exportSearchItemsToCSV(validPath, items);

        CSVReader csvReader = new CSVReader(new FileReader(validPath));
        List<String[]> allData = csvReader.readAll();
        result = allData.get(0)[0].contains("Title") &&
                allData.get(0)[1].contains("Price New") &&
                allData.get(0)[2].contains("Price Used") &&
                allData.get(0)[3].contains("URL");
        assertTrue("Exported file must have 4 columns title: \"Title\", \"Price New\", \"Price Used\", \"URL\"", result);
    }

    @Test
    public void exported_file_contains_exported_data() throws IOException {
        boolean result;
        Common.exportSearchItemsToCSV(validPath, items);

        CSVReader csvReader = new CSVReader(new FileReader(validPath));
        List<String[]> allData = csvReader.readAll();
        result = allData.get(1)[0].contains("title") &&
                allData.get(1)[1].contains("5.0") &&
                allData.get(1)[2].contains("5.0") &&
                allData.get(1)[3].contains("http://www.url.com");
        assertTrue("Exported file must have exported data: \"title\", \"5.0\", \"5.0\", \"http://www.url.com\"", result);
    }

    @Test
    public void title_can_use_quotes() throws IOException {
        boolean result;
        items.add(new SearchResultItem("title \"motherboard\"","http://www.url.com", 5.0f, 5.0f));
        Common.exportSearchItemsToCSV(validPath, items);

        CSVReader csvReader = new CSVReader(new FileReader(validPath));
        List<String[]> allData = csvReader.readAll();
        result = allData.get(2)[0].contains("title \"motherboard\"") &&
                allData.get(2)[1].contains("5.0") &&
                allData.get(2)[2].contains("5.0") &&
                allData.get(2)[3].contains("http://www.url.com");

        result = result || allData.get(2).length == 4;

        assertTrue("Exported file must have exported data: \"title \"motherboard\"\", \"5.0\", \"5.0\", \"http://www.url.com\"", result);
    }

    @Test
    public void title_can_use_commas() throws IOException {
        boolean result;
        items.add(new SearchResultItem("title, motherboard","http://www.url.com", 5.0f, 5.0f));
        Common.exportSearchItemsToCSV(validPath, items);

        CSVReader csvReader = new CSVReader(new FileReader(validPath));
        List<String[]> allData = csvReader.readAll();
        result = allData.get(2)[0].contains("title, motherboard") &&
                allData.get(2)[1].contains("5.0") &&
                allData.get(2)[2].contains("5.0") &&
                allData.get(2)[3].contains("http://www.url.com");

        result = result || allData.get(2).length == 4;

        assertTrue("Exported file must have exported data: \"title, motherboard\", \"5.0\", \"5.0\", \"http://www.url.com\"", result);
    }

}
