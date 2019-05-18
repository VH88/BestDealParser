package utils;

import jdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class MatchKeywordToStringTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final String stringUnderTest = "   GIGABYTE       GA-A320M-S2H           (AMD Ryzen AM4 / MicroATX /     2xDDR4/ HDMI/ Realtek ALC887/ 3xPCIe/ USB3.1 Gen 1/ LAN/ Motherboard)    ";

    @Test
    public void found_keyword_return_true(){
        String keyword = "GIGABYTE";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("Keyword that exist in a string under test must return true\nText: "+stringUnderTest+"\nTest Data: "+keyword,result);
    }

    @Test
    public void keyword_being_trimmed_before_matching(){
        String keyword = "  GIGABYTE  ";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("keyword must be trimmed before matching\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void match_is_case_insensitive(){
        String keyword = "gigabyte";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("matching must be case insensitive\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void match_not_found_returns_false(){
        String keyword = "abcde";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertFalse("keyword that does not exist in the string must return false\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void empty_keyword_returns_true(){
        String keyword = "";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("empty keyword returns true\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void keyword_can_escape_space(){
        String keyword = "Ryzen\\sAM4";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("White space inside keyword can be escaped with '\\s'\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void keyword_separated_by_space_act_as_OR(){
        String keyword = "Motherboard abcde";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("keyword separated by space act as OR\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void multiple_spaces_replaced_by_single_space(){
        String keyword = "Motherboard   abcde";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("keywords separated by space act as OR\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void multiple_keywords_are_supported(){
        String keyword = "Motherboard GIGABYTE USB LAN DDR3";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("multiple keywords are supported\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void keywords_with_OR_positive(){
        String keyword = "Motherboard OR abcde";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("keywords can use OR operator\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void keywords_with_OR_negative(){
        String keyword = "abcdefg OR abcde";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertFalse("keywords can use OR operator\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void multiple_OR_are_supported(){
        String keyword = "Motherboard OR GIGABYTE OR USB OR LAN";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("keywords with multiple OR operators are supported\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void keywords_can_use_NOT(){
        String keyword = "NOT Motherboard";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertFalse("keywords can use NOT operator\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void multiple_keywords_with_NOT_are_supported(){
        String keyword = "NOT Motherboard NOT GIGABYTE NOT USB";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertFalse("multiple keywords can use NOT\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void keywords_can_use_AND(){
        String keyword = "Motherboard  AND GIGABYTE";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("keywords can use AND operator\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }


    @Test
    public void absent_keyword_with_AND(){
        String keyword = "Motherboard  AND abcde";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertFalse("absent keyword with AND operator return false\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void combine_AND_and_NOT(){
        String keyword = "Motherboard  AND NOT abcde";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("AND operator can be combined with NOT operator\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void multiple_AND_are_supported(){
        String keyword = "Motherboard AND GIGABYTE AND USB3 AND LAN";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("keywords can have multiple AND statements\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void multiple_AND_and_OR_can_be_combined(){
        String keyword = "Motherboard AND GIGABYTE OR abcde AND LAN";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("keywords can combine AND and OR in a statement\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void multiple_AND_and_space_can_be_combined(){
        String keyword = "Motherboard AND GIGABYTE abcde AND LAN";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("keywords can combine AND and space(as OR operator) in a statement\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void group_keywords(){
        String keyword = "{GIGABYTE abcde} AND Motherboard";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("keywords can be grouped using curly brackets {}\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void group_can_have_single_keyword(){
        String keyword = "{GIGABYTE}";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("groups can have one keywords\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void group_keywords_support_NOT_positive(){
        String keyword = "NOT {GIGABYTE AND Motherboard}";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertFalse("NOT operator can be used with a group\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void group_keywords_support_NOT_negative(){
        String keyword = "NOT {GIGABYTE AND abcde}";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("NOT operator can be used with a group\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void group_with_NOT_no_need_for_space_positive(){
        String keyword = "NOT{GIGABYTE AND Motherboard}";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertFalse("no need for space between NOT and a group\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void group_with_NOT_no_need_for_space_negative(){
        String keyword = "NOT{GIGABYTE AND abcde}";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("no need for space between NOT and a group\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }


    @Test
    public void multiple_spaces_inside_group_are_trimmed(){
        String keyword = "{      GIGABYTE      Motherboard}";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("spaces inside the group are trimmed\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void multiple_OR_groups_positive(){
        String keyword = "{abcde} OR {Motherboard}";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("multiple groups can be supported\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void multiple_OR_groups_negative(){
        String keyword = "{abcde} OR {abcde}";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertFalse("multiple groups can be supported\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void multiple_groups_are_supported_positive(){
        String keyword = "{GIGABYTE abcde} AND {Motherboard abcds}";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("multiple groups can be supported\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void multiple_groups_are_supported_negative(){
        String keyword = "{NOT GIGABYTE abcde} AND {abcds}";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertFalse("multiple groups can be supported\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void group_with_AND_no_need_for_space(){
        String keyword = "{GIGABYTE abcde}AND{Motherboard abcds}";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("no need for space between AND and a group\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }



    @Test
    public void group_with_OR_no_need_for_space(){
        String keyword = "{GIGABYTE abcde}OR{Motherboard abcds}";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("no need for space between OR and a group\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void nested_groups(){
        String keyword = "{{GIGABYTE AND Motherboard} AND NOT Intel} AND {USB2 OR USB3}";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("groups can have groups inside them\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void OR_on_its_own_is_not_allowed(){
        String keyword = "OR";
        thrown.expect(SyntaxException.class);
        thrown.expectMessage("Could not parse keywords");
        Common.matchKeywordsToString(stringUnderTest, keyword);
    }

    @Test
    public void NOT_on_its_own_is_not_allowed(){
        String keyword = "NOT";
        thrown.expect(SyntaxException.class);
        thrown.expectMessage("Could not parse keywords");
        Common.matchKeywordsToString(stringUnderTest, keyword);
    }

    @Test
    public void NOT_without_keyword_is_not_allowed(){
        String keyword = "Motherboard NOT";
        thrown.expect(SyntaxException.class);
        thrown.expectMessage("Could not parse keywords");
        Common.matchKeywordsToString(stringUnderTest, keyword);
    }

    @Test
    public void AND_on_its_own_is_not_allowed(){
        String keyword = "AND";
        thrown.expect(SyntaxException.class);
        thrown.expectMessage("Could not parse keywords");
        Common.matchKeywordsToString(stringUnderTest, keyword);
    }

    @Test
    public void sequential_not_is_not_allowed(){
        String keyword = "NOT NOT Motherboard";
        thrown.expect(SyntaxException.class);
        thrown.expectMessage("Could not parse keywords");
        Common.matchKeywordsToString(stringUnderTest, keyword);
    }

    @Test
    public void NOT_before_AND_is_not_allowed(){
        String keyword = "NOT AND Motherboard";
        thrown.expect(SyntaxException.class);
        thrown.expectMessage("Could not parse keywords");
        Common.matchKeywordsToString(stringUnderTest, keyword);
    }

    @Test
    public void AND_before_OR_is_not_allowed(){
        String keyword = "Motherboard AND OR abcde";
        thrown.expect(SyntaxException.class);
        thrown.expectMessage("Could not parse keywords");
        Common.matchKeywordsToString(stringUnderTest, keyword);
    }

    @Test
    public void NOT_before_OR_is_not_allowed(){
        String keyword = "NOT OR Motherboard";
        thrown.expect(SyntaxException.class);
        thrown.expectMessage("Could not parse keywords");
        Common.matchKeywordsToString(stringUnderTest, keyword);
    }

    @Test
    public void lowcase_bool_processed_as_keyword(){
        String keyword = "not Motherboard";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("only uppercase booleans are supported", result);
    }

    @Test
    public void keyword_with_OR_in_the_beginning_is_not_allowed(){
        String keyword = "OR Motherboard";
        thrown.expect(SyntaxException.class);
        thrown.expectMessage("Could not parse keywords");
        Common.matchKeywordsToString(stringUnderTest, keyword);
    }

    @Test
    public void keyword_with_AND_in_the_beginning_is_not_allowed(){
        String keyword = "AND Motherboard";
        thrown.expect(SyntaxException.class);
        thrown.expectMessage("Could not parse keywords");
        Common.matchKeywordsToString(stringUnderTest, keyword);
    }

    @Test
    public void keyword_with_OR_in_the_end_is_not_allowed(){
        String keyword = "Motherboard OR";
        thrown.expect(SyntaxException.class);
        thrown.expectMessage("Could not parse keywords");
        Common.matchKeywordsToString(stringUnderTest, keyword);
    }

    @Test
    public void keyword_with_AND_in_the_end_is_not_allowed(){
        String keyword = "Motherboard AND";
        thrown.expect(SyntaxException.class);
        thrown.expectMessage("Could not parse keywords");
        Common.matchKeywordsToString(stringUnderTest, keyword);
    }

    @Test
    public void empty_groups_are_allowed(){
        String keyword = "{}";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("empty groups are allowed\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void ignore_empty_curly_brackets(){
        String keyword = "Motherboard {}";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("ignore empty curly brackets {}\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);
    }

    @Test
    public void group_missing_closing_bracket(){
        String keyword = "{Motherboard";
        thrown.expect(SyntaxException.class);
        thrown.expectMessage("Group is missing one or more curly brackets");
        Common.matchKeywordsToString(stringUnderTest, keyword);

    }

    @Test
    public void group_missing_opening_bracket(){
        String keyword = "Motherboard}";
        thrown.expect(SyntaxException.class);
        thrown.expectMessage("Group is missing one or more curly brackets");
        Common.matchKeywordsToString(stringUnderTest, keyword);
    }

    @Test
    public void empty_group_with_AND_is_not_allowed(){
        String keyword = "{} AND Motherboard";
        thrown.expect(SyntaxException.class);
        thrown.expectMessage("Empty group before AND or after NOT");
        Common.matchKeywordsToString(stringUnderTest, keyword);
    }

    @Test
    public void empty_group_with_OR_is_allowed(){
        String keyword = "{} OR Motherboard";
        boolean result = Common.matchKeywordsToString(stringUnderTest, keyword);
        assertTrue("ignore empty curly brackets {}\nText: "+stringUnderTest+"\nTest Data: "+keyword, result);

    }

    @Test
    public void empty_group_with_NOT_is_not_allowed(){
        String keyword = "NOT {}";
        thrown.expect(SyntaxException.class);
        thrown.expectMessage("Empty group before AND or after NOT");
        Common.matchKeywordsToString(stringUnderTest, keyword);
    }

}
