

import POM.SearchResultItem;
import POM.enums.PriceType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utils.Common;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TestingNonWebdrive {
    public static void main(String[] args) throws IOException, ParseException {
        String exportPath = "src//test//resources//temp//test.html";
        List<SearchResultItem> items = new ArrayList<>();
        Common.importSearchItemsFromCSV("src//test//resources//TestData_SearchResultItem.csv", items);


        Common.exportSearchItemsToHTML(exportPath, items, "searchKeyword", "filterKeyword", PriceType.ALL, -1.0f, 200.0f);
    }
}
