package POM;


public class SearchResultItem {
    private final String title;
    private final String url;
    private final float priceNew;
    private final float priceUsed;

    public SearchResultItem(String title, String url, float priceNew, float priceUsed) {
        this.title = title;
        this.url = url;
        this.priceNew = priceNew;
        this.priceUsed = priceUsed;
    }

    public SearchResultItem(SearchResultItem item){
        this.title = item.title;
        this.url = item.url;
        this.priceNew = item.priceNew;
        this.priceUsed = item.priceUsed;
    }

    public String getTitle(){    return title;   }
    public String getUrl(){      return url;     }
    public float getPriceNew(){ return priceNew; }
    public float getPriceUsed(){ return priceUsed; }
}
