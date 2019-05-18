import POM.Amazon.AmazonSearchResultPage;
import POM.SearchResultItem;
import POM.enums.PriceType;
import POM.enums.SortByType;

import java.util.ArrayList;
import java.util.List;

//TODO:write javadoc
class AmazonSearchForProduct extends BaseActions {
    private static AmazonSearchResultPage p;
    private final boolean isFirefox;
    private final String searchKeyword;
    private final String filterKeyword;
    private final SortByType sortByType;
    private final PriceType filterPriceType;
    private final float filterPriceMax;
    private final float filterPriceMin;

    public AmazonSearchForProduct(boolean isFirefox, String searchKeyword) {
        this.isFirefox = isFirefox;
        this.searchKeyword = searchKeyword;
        this.filterKeyword = "";
        this.sortByType = SortByType.DEFAULT;
        this.filterPriceType = PriceType.ALL;
        this.filterPriceMax = Float.MAX_VALUE;
        this.filterPriceMin = 0.0f;
    }
    public AmazonSearchForProduct(boolean isFirefox, String searchKeyword, String filterKeyword) {
        this.isFirefox = isFirefox;
        this.searchKeyword = searchKeyword;
        this.filterKeyword = filterKeyword;
        this.sortByType = SortByType.DEFAULT;
        this.filterPriceType = PriceType.ALL;
        this.filterPriceMax = Float.MAX_VALUE;
        this.filterPriceMin = 0.0f;
    }

    public AmazonSearchForProduct(boolean isFirefox, String searchKeyword, String filterKeyword, SortByType sortByType) {
        this.isFirefox = isFirefox;
        this.searchKeyword = searchKeyword;
        this.filterKeyword = filterKeyword;
        this.sortByType = sortByType;
        this.filterPriceType = PriceType.ALL;
        this.filterPriceMax = Float.MAX_VALUE;
        this.filterPriceMin = 0.0f;
    }

    public AmazonSearchForProduct(boolean isFirefox, String searchKeyword, String filterKeyword, SortByType sortByType, PriceType filterPriceType) {
        this.isFirefox = isFirefox;
        this.searchKeyword = searchKeyword;
        this.filterKeyword = filterKeyword;
        this.sortByType = sortByType;
        this.filterPriceType = filterPriceType;
        this.filterPriceMax = Float.MAX_VALUE;
        this.filterPriceMin = 0.0f;
    }

    /**
     *
     * @param isFirefox
     * @param searchKeyword
     * @param filterKeyword
     * @param sortByType
     * @param filterPriceType
     * @param filterPriceMax
     */
    public AmazonSearchForProduct(boolean isFirefox, String searchKeyword, String filterKeyword, SortByType sortByType, PriceType filterPriceType, float filterPriceMax) {
        this.isFirefox = isFirefox;
        this.searchKeyword = searchKeyword;
        this.filterKeyword = filterKeyword;
        this.sortByType = sortByType;
        this.filterPriceType = filterPriceType;
        this.filterPriceMax = filterPriceMax;
        this.filterPriceMin = 0.0f;
    }

    public AmazonSearchForProduct(boolean isFirefox, String searchKeyword, String filterKeyword, SortByType sortByType, PriceType filterPriceType, float filterPriceMax, float filterPriceMin) {
        this.isFirefox = isFirefox;
        this.searchKeyword = searchKeyword;
        this.filterKeyword = filterKeyword;
        this.sortByType = sortByType;
        this.filterPriceType = filterPriceType;
        this.filterPriceMax = filterPriceMax;
        this.filterPriceMin = filterPriceMin;
    }


    public void getResult(List<SearchResultItem> items){

        initializeFireFox(true);

        p = new AmazonSearchResultPage(driver, wait);
        p.load();
        p.search(searchKeyword);
        p.sortBy(sortByType);

        p.presetPriceType(filterPriceType);
        p.presetKeywords(filterKeyword);
        p.presetFilterPriceMin(filterPriceMin);
        p.presetFilterPriceMax(filterPriceMax);
        do{
            p.addItemsFromPage(items);
        }while (p.nextPage());


        p.quit();
        return;
    }

}
