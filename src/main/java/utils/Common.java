package utils;

import POM.SearchResultItem;
import POM.enums.PriceType;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import jdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Common {
    //TODO: javadoc
    //TODO: test cases
    public static void filterByKeywords(List<SearchResultItem> items, String keywords){
        items.removeIf(_i -> !Common.matchKeywordsToString(_i.getTitle(), keywords));
    }

    public static void filterByPrice(List<SearchResultItem> items, float priceMin, float priceMax){
        items.removeIf(i -> filterByPrice(i, priceMin, priceMax));
    }

    public static boolean filterByPrice(SearchResultItem item, float priceMin, float priceMax){
        return ((item.getPriceNew() <= priceMax && item.getPriceNew() >= priceMin) ||
                (item.getPriceUsed() <= priceMax && item.getPriceUsed() >= priceMin));
    }

    public static boolean filterByPrice(float itemPriceNew, float itemPriceUsed, float priceMin, float priceMax){
        return ((itemPriceNew <= priceMax && itemPriceNew >= priceMin) ||
                (itemPriceUsed <= priceMax && itemPriceUsed >= priceMin));
    }

    public static void filterByPriceType(List<SearchResultItem> items, PriceType type){
        switch (type){
            case ALL:
                items.removeIf(i -> filterByPriceType(i) != PriceType.ALL);
                break;
            case USED:
                items.removeIf(i -> filterByPriceType(i) != PriceType.USED);
                break;
            case NEW:
                items.removeIf(i -> filterByPriceType(i) != PriceType.NEW);
                break;
        }
    }

    public static PriceType filterByPriceType(SearchResultItem item){
        if(item.getPriceNew() >= 0.0f && item.getPriceUsed() < 0.0f){
            return PriceType.NEW;
        } else if(item.getPriceNew() < 0.0f && item.getPriceUsed() >= 0.0f){
            return  PriceType.USED;
        } else if(item.getPriceNew() >= 0.0f || item.getPriceUsed() >= 0.0f){
            return PriceType.ALL;
        }else {
            return null;
        }
    }

    public static PriceType filterByPriceType(float itemPriceNew, float itemPriceUsed){


        return PriceType.ALL;
    }

    /**
     *
     * @param path
     * @param items
     * @param searchKeywords
     * @param filterKeywords
     * @param priceType
     * @param priceMin
     * @param priceMax
     * @throws IOException
     */
    public static void exportSearchItemsToHTML(String path, List<SearchResultItem> items,
                                               String searchKeywords, String filterKeywords, PriceType priceType, float priceMin, float priceMax) throws IOException {
        /*
            import template
            replace all keywords with data
            export html
         */
        String st;
        String finalSt = "";
        String record = "";
        String priceNew, priceUsed;
        File file = new File("src//main//resources//report_template.html");
        BufferedReader br = new BufferedReader(new FileReader(file));

        while ((st = br.readLine()) != null) {
            finalSt += st + "\n";
        }

        finalSt = finalSt
            .replace("{SEARCH_KEYWORDS}", "\""+searchKeywords+"\"")
            .replace("{FILTER_KEYWORDS}", "\""+filterKeywords+"\"")
            .replace("{PRICE_TYPE}", "\""+priceType.toString()+"\"")
            .replace("{PRICE_MINIMUM}", "$"+priceMin)
            .replace("{PRICE_MAXIMUM}", "$"+priceMax)
            .replace("{ITEM_COUNT}", String.valueOf(items.size()));

        for( SearchResultItem i : items){
            priceNew = (i.getPriceNew() > 0.0f) ? "$" + i.getPriceNew() : "n/a";
            priceUsed = (i.getPriceUsed() > 0.0f) ? "$" + i.getPriceUsed() : "n/a";

            record += "<tr>\n<td><a href=\""+i.getUrl()+"\">"+i.getTitle().replace("&", "&#38;")+"</a></td>\n<td>"+priceNew+"</td>\n<td>"+priceUsed+"</td>\n</tr>\n";
        }


        finalSt = finalSt.replace("{TABLE_RECORDS}", record);

        FileWriter writer = new FileWriter(path);
        writer.append(finalSt);
        writer.flush();
        writer.close();

}

    /**
     * <p>Exports a list of {@link SearchResultItem} to a CSV file</p>
     * <p>Supports comma and quotes in the {@link SearchResultItem} title</p>
     *
     * @param path path to valid directory with valid filename
     * @param items List of {@link SearchResultItem}
     * @throws IOException by {@link FileWriter} when path is not valid
     */
    public static void exportSearchItemsToCSV(String path, List<SearchResultItem> items) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(path));

        String[] header = new String[]{"Title", "Price New", "Price Used", "URL"};
        writer.writeNext(header);

        List<String[]> allData = new ArrayList<>();
        for (SearchResultItem i : items){
            String[] data = new String[]{i.getTitle(), String.valueOf(i.getPriceNew()), String.valueOf(i.getPriceUsed()), i.getUrl()};
            allData.add(data);
        }
        writer.writeAll(allData);
        writer.close();
    }

    /**
     * Imports a list of {@link SearchResultItem} from a CSV file
     * <p>File must have 4 columns and headers "Title", "Price New", "Price Old", "URL" </p>
     *
     * @param path path to valid directory with existing csv file
     * @param items List of {@link SearchResultItem} to be populated with data
     * @throws IOException when could not find file or file is not a list of {@link SearchResultItem}
     */
    public static void importSearchItemsFromCSV(String path, List<SearchResultItem> items) throws IOException {
        String title;
        String url;
        float priceNew;
        float priceOld;

        CSVReader csvReader = new CSVReader(new FileReader(path));
        List<String[]> allData = csvReader.readAll();

        // Validate Header and number of columns
        if((!allData.get(0)[0].contains("Title") && !allData.get(0)[1].contains("Price New") &&
                !allData.get(0)[2].contains("Price Old") && !allData.get(0)[3].contains("URL")) || allData.get(0).length != 4){
            throw new IOException("ERROR! Wrong file.\nFile must contain 4 columns with headers \"Title\", \"Price New\", \"Price Old\", \"URL\"");
        }

        // Remove header
        allData.remove(0);

        for(String[] data : allData){
            title = data[0];
            priceNew = Float.parseFloat(data[1]);
            priceOld = Float.parseFloat(data[2]);
            url = data[3];
            items.add(new SearchResultItem(title, url, priceNew, priceOld));
        }

        csvReader.close();
    }

    /**
     * Helps {@link #processRecursiveAllKeywords(String, KeywordCollection)} and its private methods
     * to temporary store keywords and the end result as the method is looking for a match.
     */
    private static class KeywordCollection {
        private String keywords;
        private boolean result;

        KeywordCollection(String keywords) {
            this.keywords = keywords;
            this.result = false;
        }

        KeywordCollection(KeywordCollection keywordCollection) {
            this.keywords = keywordCollection.getKeywords();
            this.result = keywordCollection.getResult();
        }

        String getKeywords() {
            return keywords;
        }

        void setKeywords(String keywords) {
            this.keywords = keywords;
        }

        boolean getResult() {
            return result;
        }

        void setResult(boolean result) {
            this.result = result;
        }
    }

    /**
     * <p>Match keywords with a given string. Case insensitive.</p>
     * <p>Type OR, AND or NOT for boolean operations. White space is treated as OR.</p>
     * <p>Escape white space in the keyword with '\s'</p>
     * <p>Use curly brackets {} to group keywords. Nested groups are supported.</p>
     * <br>Keyword example:<br>
     *     Motherboard AND NOT {MSI Intel DDR2}
     *
     * @param text String to be searched for keywords
     * @param keywords a string of keywords
     * @return : true if keyword pattern match
     */
    public static boolean matchKeywordsToString(String text, String keywords) {
        text = text.toLowerCase();
        String rUnacceptable = "((^|\\s)(OR|AND|NOT)$|(^|\\s)(AND\\sOR|NOT\\sNOT|NOT\\sAND|NOT\\sOR|AND\\sAND|OR\\sOR)(\\s|$)|^(OR|AND)\\s)";
        String rEmptyGroup = "(\\{\\s}\\sAND\\s|\\sAND\\s\\{\\s}|(\\s|^)NOT\\s\\{\\s})";
        Pattern pUnacceptable = Pattern.compile(rUnacceptable);
        Pattern pEmptyGroup = Pattern.compile(rEmptyGroup);
        Pattern pCurlyBracketOpened = Pattern.compile("\\{");
        Pattern pCurlyBracketClosed = Pattern.compile("}");
        Matcher m;
        int countCurlyBracketOpened = 0;
        int countCurlyBracketClosed = 0;
        boolean result;

        // Replace all multiple spaces with one space and trim spaces on both ends and replace all OR with space
        keywords = keywords.trim().replaceAll("\\s+", " ");

        // If string is empty return true
        if (keywords.equals("")) return true;

        // Check for error in the syntax of keywords
        m = pUnacceptable.matcher(keywords);
        if (m.find()) throw new SyntaxException("ERROR! Could not parse keywords. Syntax error inside keywords string");
        // Test that all groups are enclosed
        m = pCurlyBracketOpened.matcher(keywords);
        while (m.find()) countCurlyBracketOpened++;

        m = pCurlyBracketClosed.matcher(keywords);
        while (m.find()) countCurlyBracketClosed++;
        if (countCurlyBracketOpened != countCurlyBracketClosed)
            throw new SyntaxException("ERROR! Group is missing one or more curly brackets");


        // Replace all spaces between words with OR
        keywords = keywords.replaceAll("\\sOR\\s", " ");

        // Put a space around bracket to identify the group. Trim and replace double spaces.
        keywords = keywords.replaceAll("\\{", " { ").replaceAll("}", " } ").trim().replaceAll("\\s+", " ");

        m = pEmptyGroup.matcher(keywords);
        while (m.find()) throw new SyntaxException("Empty group before AND or after NOT is not allowed");

        keywords = keywords.replaceAll("\\{\\s}", "").trim().replaceAll("\\s+", " ");

        KeywordCollection k = new KeywordCollection(keywords);
        while (processRecursiveAllKeywords(text, k)) ;

        result = k.getResult();
        return result;
    }

    /**
     * Process all keywords and give the final result if text matches keywords or not.<br>
     * Keywords need to be asserted for the right syntax.
     * <ul>
     *     <li>Replace all OR with a white space</li>
     *     <li>Add spaces before and after braces</li>
     *     <li>Trim the string</li>
     *     <li>Remove duplicate spaces</li>
     *     <li>Remove empty groups if there are any</li>
     *     <li>Assert for the right syntax</li>
     * </ul>
     * List of unacceptable combinations:
     * <ul>
     *     <li>AND, OR, NOT on it's own</li>
     *     <li>AND OR, NOT NOT, NOT AND, NOT OR, AND AND, OR OR, OR AND</li>
     *     <li>Can't start with OR or AND</li>
     *     <li>Can't end with OR or AND</li>
     *     <li>NOT { }, { } AND, AND { }</li>
     * </ul>
     * @param text search this text for matching keywords
     * @param keywords reference to {@link KeywordCollection} that has a string of keywords that need to be processed
     *                 and bool to store final result.
     * @return false when processing is done and no more keywords left to process.
     */
    private static boolean processRecursiveAllKeywords(String text, KeywordCollection keywords) {
        String tempKeyword;
        KeywordCollection tempKeywordCollection;
        int firstSpace;
        boolean isMoreToProcess;

        // Find next space. If there's none then  no more keywords left to process
        firstSpace = keywords.getKeywords().indexOf(" ");
        if (firstSpace == -1) {
            // This is the last keyword. Process and terminate recursion
            keywords.setResult(keywords.getResult() || processSingleKeyword(text, keywords.getKeywords()));
            return false;
        } else {
            // If there's a space at index '3' then it's may be a NOT, or an AND
            if (firstSpace == 3) {
                // check if it's AND -------------------------------------
                if (keywords.getKeywords().charAt(0) == 'A' && keywords.getKeywords().charAt(1) == 'N' && keywords.getKeywords().charAt(2) == 'D') {
                    // Check if there's NOT after AND
                    if (keywords.getKeywords().length() > 6) {
                        if (keywords.getKeywords().charAt(4) == 'N' && keywords.getKeywords().charAt(5) == 'O' && keywords.getKeywords().charAt(6) == 'T') {
                            tempKeyword = keywords.getKeywords().substring(4);
                            tempKeywordCollection = new KeywordCollection(tempKeyword);
                            isMoreToProcess = processNOT(text, tempKeywordCollection);
                            keywords.setKeywords(tempKeywordCollection.getKeywords());
                            keywords.setResult(keywords.getResult() && !tempKeywordCollection.getResult());
                            return isMoreToProcess;
                        }
                    }

                    tempKeyword = keywords.getKeywords().substring(4);

                    if (tempKeyword.charAt(0) == '{') {
                        tempKeywordCollection = new KeywordCollection(tempKeyword);
                        isMoreToProcess = processGroup(text, tempKeywordCollection);
                        keywords.setResult(keywords.getResult() && tempKeywordCollection.getResult());
                        keywords.setKeywords(tempKeywordCollection.getKeywords());
                        return isMoreToProcess;
                    }

                    // There are no operators before the keywords. Process as AND keyword
                    firstSpace = tempKeyword.indexOf(" ");
                    if (firstSpace == -1) {
                        keywords.setResult(keywords.getResult() && processSingleKeyword(text, tempKeyword));
                        return false;
                    } else {
                        tempKeyword = tempKeyword.substring(0, firstSpace);
                        keywords.setResult(keywords.getResult() && processSingleKeyword(text, tempKeyword));
                        keywords.setKeywords(keywords.getKeywords().substring(firstSpace + 5));//'NOT keyword(firstSpace)'
                        return true;
                    }


                    // Check if it's NOT -------------------------------------
                } else if (keywords.getKeywords().charAt(0) == 'N' && keywords.getKeywords().charAt(1) == 'O' && keywords.getKeywords().charAt(2) == 'T') {
                    // Process as OR NOT
                    tempKeywordCollection = keywords;
                    isMoreToProcess = processNOT(text, tempKeywordCollection);
                    keywords.setKeywords(tempKeywordCollection.getKeywords());
                    keywords.setResult(keywords.getResult() || tempKeywordCollection.getResult());
                    return isMoreToProcess;
                }
            }

            // There's no AND or NOT ----------------------------------------------------------------------
            if (keywords.getKeywords().charAt(0) == '{') {
                tempKeywordCollection = keywords;
                isMoreToProcess = processGroup(text, tempKeywordCollection);
                keywords.setResult(keywords.getResult() || tempKeywordCollection.getResult());
                keywords.setKeywords(tempKeywordCollection.getKeywords());
                return isMoreToProcess;
            }

            // There are no operators before the keywords. Process as OR keyword
            tempKeyword = keywords.getKeywords().substring(0, firstSpace);
            keywords.setResult(keywords.getResult() || processSingleKeyword(text, tempKeyword));
            keywords.setKeywords(keywords.getKeywords().substring(firstSpace + 1));
            return true;
        }

    }

    /**
     * {@code return text.contains(keyword.replaceAll("\\\\s"," ").toLowerCase());
     * @param text search this text for matching keywords
     * @param keyword single keyword without spaces
     * @return true if matches
     */
    private static boolean processSingleKeyword(String text, String keyword) {
        return text.contains(keyword.replaceAll("\\\\s"," ").toLowerCase());
    }

    /**
     * When {@link #processRecursiveAllKeywords(String, KeywordCollection)} finds a NOT operator send the keywords here
     * <p>Check if there's a group after operator NOT.</p>
     * Process keyword or group as NOT
     *
     * @param text search this text for matching keywords
     * @param keywords reference to {@link KeywordCollection} container that has a string of keywords.
     *                The final result of the match is written to {@link KeywordCollection#result}.
     *                If there are keywords left to process they will be put in {@link KeywordCollection#keywords}
     * @return true if there are keywords left to process or false if no more left.
     */
    private static boolean processNOT(String text, KeywordCollection keywords) {
        int firstSpace;
        String tempKeyword;
        KeywordCollection tempKeywordCollection;
        boolean isMoreToProcess;
        // check if there's a group after NOT
        tempKeyword = keywords.getKeywords().substring(4);
        if (tempKeyword.charAt(0) == '{') {
            tempKeywordCollection = new KeywordCollection(tempKeyword);
            isMoreToProcess = processGroup(text, tempKeywordCollection);
            keywords.setResult(tempKeywordCollection.getResult());
            keywords.setKeywords(tempKeywordCollection.getKeywords());
            return isMoreToProcess;
        }

        // There are no operators before the keywords.
        firstSpace = tempKeyword.indexOf(" ");
        if (firstSpace == -1) {
            keywords.setResult(processSingleKeyword(text, tempKeyword));
            return false;
        } else {
            tempKeyword = tempKeyword.substring(0, firstSpace);
            keywords.setResult(processSingleKeyword(text, tempKeyword));
            keywords.setKeywords(keywords.getKeywords().substring(firstSpace + 5));//'NOT keyword(firstSpace)'
            return true;
        }
    }

    /**
     * Opens the group by removing curly brackets and process it with
     * {@link #processRecursiveAllKeywords(String, KeywordCollection)}
     * <p>Used by {@link #processRecursiveAllKeywords(String, KeywordCollection)}</p>
     *
     * @param text search this text for matching keywords
     * @param keywords reference to {@link KeywordCollection} container that has a string of keywords.
     *                The final result of the match is written to {@link KeywordCollection#result}.
     *                If there are keywords left to process they will be put in {@link KeywordCollection#keywords}
     * @return true if there are keywords left to process or false if no more left.
     */
    private static boolean processGroup(String text, KeywordCollection keywords) {
        int bracketEnd;
        String tempKeyword;
        // Cut out the group keyword and submit for processing
        bracketEnd = getEndIndexOfCurlyBracket(keywords.getKeywords());
        tempKeyword = keywords.getKeywords().substring(2, bracketEnd - 1); // bracket - one space ' }'
        KeywordCollection k = new KeywordCollection(tempKeyword);
        while (processRecursiveAllKeywords(text, k)) ;
        // Combine results as OR
        keywords.setResult(k.getResult());
        if (bracketEnd + 1 == keywords.keywords.length()) return false;
        else {
            keywords.setKeywords(keywords.getKeywords().substring(bracketEnd + 2));// one for bracket + one for space '} '
            return true;
        }
    }

    /**
     * <p>Finds the index of the closing brace for grouped keywords.<br> Supports nested groups.</p><br>
     * <p>Used by {@link #processGroup(String, KeywordCollection)} to cut out keywords from the group</p>
     *
     * @param keywords group of keywords including opening brace '{' and closing brace '}'
     * @return index of the char that is a closing brace for a given group of keywords.
     */
    private static int getEndIndexOfCurlyBracket(String keywords) {
        int bracketCount = 0;
        int bracketEnd = 0;
        for (int i = 1; i < keywords.length(); i++) {
            if (keywords.charAt(i) == '{') bracketCount++;
            else if (keywords.charAt(i) == '}') {
                if (bracketCount == 0) {
                    bracketEnd = i;
                    break;
                } else bracketCount--;
            }
        }
        return bracketEnd;
        }



}

