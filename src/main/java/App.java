import POM.Amazon.AmazonSearchResultPage;
import POM.SearchResultItem;
import POM.enums.PriceType;
import POM.enums.SortByType;
import utils.Common;

import java.util.ArrayList;
import java.util.List;

public class App extends BaseActions{
    private static AmazonSearchForProduct p;

    public static void main(String[] args){

        String searchKeyword = "motherboard";
        String filterKeyword = "Motherboard AND NOT{AMD DDR1 DDR2 DDR3}";
        SortByType sortType = SortByType.DEFAULT;
        PriceType priceType = PriceType.ALL;
        float priceMin = 40.0f;
        float priceMax = 200.0f;

        p = new AmazonSearchForProduct(
                true,
                searchKeyword,
                filterKeyword,
                sortType,
                priceType,
                priceMax,
                priceMin);


        List<SearchResultItem> items = new ArrayList<>();

        p.getResult(items);

//        try {
//            exportCSVReport(items,"motherboard");
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        try {
            exportHTMLReport(items, searchKeyword, filterKeyword, priceType, priceMin, priceMax);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
