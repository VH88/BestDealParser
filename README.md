# BestDealParser
Search the web for the best price you can get for the item you want.  
Export search results as csv or html file.  
Configure Selenium WebDriver using /config/settings.json  
Run App.java and look for your report in the /reports directory.

- Search Keyword - keyword that will be typed in the search bar
- Filter Keyword - keywords to test the match for items titles. Supports multiple keyword.
  - Type OR, AND or NOT for boolean operations. White space is treated as OR.
  - Escape white space in the keyword with '\s'.
  - Use curly brackets { } to group keywords. Nested groups are supported.  
    filter keyword example: "Motherboard AND NOT {MSI Intel DDR2}".  
- Sort Type - sort search results by price high to low, low to high, or leave as default.
- Price Type - list only used, only new, or all items.
- Minimum Price - the minimum price for a desired item.
- Maximum price - the maximum price for a desired item.

Currently only Amazon is available.  
WebDriver searches for an item using provided keywords.  
Asserts each item title with provided filter keyword and each price with specified price range.  
Makes a list of items and outputs it to csv or html file.  

CSV file has 4 columns for item name, url, price for new and price for used.  

HTML file has 3 columns - link to the item with title as text, price for new and price for used.  
By clicking on column headers table can be sorted in ascending or descending order for prices as well as titles.   
HTML report template is in the /src/main/resources directory.
To make your own template look for template specific keywords inside curly brackets.  
{TABLE_RECORDS} will be replaced be a sequence of ```<tr><td><a href="">title</a></td><td>price new</td><td>price used</td></tr>```  

# Prerequisites
- Java SDK 1.8.
- Chrome or Firefox browser.
- Connection to the internet.

# Installing
1. Clone or download repository.
2. Open in your favorite Java IDE.

- If you will be using chrome browser with the version different from 74.0 [download chromedriver for your version](http://chromedriver.chromium.org/downloads) and put it into /drivers directory of this project. 
- If you will be using firefox browser with the version different from 66.0 [download geckodriver for your version](https://github.com/mozilla/geckodriver/releases) and put it into /drivers directory of this project.


# Author
- Vadim Harenco

# License
Do-whatever-you-want-with-it license.
