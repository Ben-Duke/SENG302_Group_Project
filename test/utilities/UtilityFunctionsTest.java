package utilities;

import models.TravellerType;
import org.junit.Test;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;
import play.test.WithApplication;
import testhelpers.BaseTestWithApplicationAndDatabase;

import java.util.List;

import static org.junit.Assert.*;


/**
 * Class to test the UtilityFunction class.
 */
public class UtilityFunctionsTest extends BaseTestWithApplicationAndDatabase {

    @Override
    public void populateDatabase() {
        // Do not populate the database with any data
        // addTravelTypes test needs empty Traveller Types table

        TestDatabaseManager.clearAllData();
    }

    /**
     * Unit test for adding traveller types function
     */
    @Test
    public void addTravelTypes() {
        assertEquals(0, TravellerType.find.all().size());

        //Add travel types
        UtilityFunctions.addTravellerTypes();
        assertEquals(7, TravellerType.find.all().size());
    }

    //
    // ----------------- Non-database tests -----------------------------
    //

    @Test
    public void validateMaxCharLimitEmptyStringZeroMax() {
        assertTrue(UtilityFunctions.validateMaxCharLimit("", 0));
    }

    @Test
    public void validateMaxCharLimitEmptyStringNonZeroMax() {
        assertTrue(UtilityFunctions.validateMaxCharLimit("", 1));
    }

    @Test
    public void validateMaxCharLimitBelowMax() {
        assertTrue(UtilityFunctions.validateMaxCharLimit("a", 2));
    }

    @Test
    public void validateMaxCharLimitAtMax() {
        assertTrue(UtilityFunctions.validateMaxCharLimit("bb", 2));
    }

    @Test
    public void validateMaxCharLimitAboveMax() {
        assertFalse(UtilityFunctions.validateMaxCharLimit("bbb", 2));
    }

    @Test
    public void validateMinCharLimitEmptyStringandNegativeLimit() {
        assertTrue(UtilityFunctions.validateMinCharLimit("", -1));
    }

    @Test
    public void validateMinCharLimitNonEmptyStringNegativeLimit() {
        assertTrue(UtilityFunctions.validateMinCharLimit("a", -1));
    }

    @Test
    public void validateMinCharLimitEmptyStringZeroLimit() {
        assertTrue(UtilityFunctions.validateMinCharLimit("", 0));
    }

    @Test
    public void validateMinCharLimitNonEmptyStringZeroLimit() {
        assertTrue(UtilityFunctions.validateMinCharLimit("a", 0));
    }

    @Test
    public void validateMinCharLimitBelowLimit() {
        assertFalse(UtilityFunctions.validateMinCharLimit("a", 2));
    }

    @Test
    public void validateMinCharLimitAtLimit() {
        assertTrue(UtilityFunctions.validateMinCharLimit("abcd", 4));
    }

    @Test
    public void validateMinCharLimitAboveLimit() {
        assertTrue(UtilityFunctions.validateMinCharLimit("abcdefg", 3));
    }

    @Test
    public void isStringAllAlphabeticEmptyString() {
        assertTrue(UtilityFunctions.isStringAllAlphabetic(""));
    }

    @Test
    public void isStringAllAlphabeticLowercase() {
        assertTrue(UtilityFunctions.isStringAllAlphabetic("abcdefg"));
    }

    @Test
    public void isStringAllAlphabeticUppercase() {
        assertTrue(UtilityFunctions.isStringAllAlphabetic("ACBD"));
    }

    @Test
    public void isStringAllAlphabeticMixedCase() {
        assertTrue(UtilityFunctions.isStringAllAlphabetic("ldGoijasdOAISJDpoaijd"));
    }

    @Test
    public void isStringAllAlphabeticSingleNumber() {
        assertFalse(UtilityFunctions.isStringAllAlphabetic("1"));
    }

    @Test
    public void isStringAllAlphabeticSingleSymbol() {
        assertFalse(UtilityFunctions.isStringAllAlphabetic("!"));
    }

    @Test
    public void isStringAllAlphabeticMixedLettersAndNonLetters() {
        assertFalse(UtilityFunctions.isStringAllAlphabetic("hello1"));
    }

    @Test
    public void isStringAllAlphabeticSpace() {
        assertFalse(UtilityFunctions.isStringAllAlphabetic(" "));
    }

    @Test
    public void isStringAllAlphabeticSpaceInMiddle() {
        assertFalse(UtilityFunctions.isStringAllAlphabetic("hello world"));
    }

    @Test
    public void isStringAllAlphabeticTeReoCharacter() {
        assertFalse(UtilityFunctions.isStringAllAlphabetic("ē"));
    }

    @Test
    public void isStringAllAlphabeticTeReoCharacterJapaneseCharacter() {
        assertFalse(UtilityFunctions.isStringAllAlphabetic("骨"));
    }

    @Test
    public void isStringADoubleEmptyString() {
        assertFalse(UtilityFunctions.isStringADouble(""));
    }

    @Test
    public void isStringADoubleSpace() {
        assertFalse(UtilityFunctions.isStringADouble(" "));
    }

    @Test
    public void isStringADoubleLetter() {
        assertFalse(UtilityFunctions.isStringADouble("a"));
    }

    @Test
    public void isStringADoubleLetters() {
        assertFalse(UtilityFunctions.isStringADouble("abc"));
    }

    @Test
    public void isStringADoubleSpacialCharacter() {
        assertFalse(UtilityFunctions.isStringADouble("!"));
    }

    @Test
    public void isStringADoubleWithInt() {
        assertTrue(UtilityFunctions.isStringADouble("1123"));
    }

    @Test
    public void isStringADoubleWithFloatingPoint() {
        assertTrue(UtilityFunctions.isStringADouble("1.1"));
    }

    @Test
    public void isStringADoubleAtMaxDouble() {
        assertTrue(UtilityFunctions.isStringADouble(Double.toString(Double.MAX_VALUE)));
    }

    @Test
    public void isStringADoubleAtMinDouble() {
        assertTrue(UtilityFunctions.isStringADouble(Double.toString(Double.MIN_VALUE)));
    }

    @Test
    public void isStringAnIntEmptyString() {
        assertFalse(UtilityFunctions.isStringAnInt(""));
    }

    @Test
    public void isStringAnIntSpecialSymbol() {
        assertFalse(UtilityFunctions.isStringAnInt("!"));
    }

    @Test
    public void isStringAnIntEscapeChar() {
        assertFalse(UtilityFunctions.isStringAnInt("\\"));
    }

    @Test
    public void isStringAnIntLetter() {
        assertFalse(UtilityFunctions.isStringAnInt("a"));
    }

    @Test
    public void isStringAnIntWithDouble() {
        assertFalse(UtilityFunctions.isStringAnInt("1.231"));
    }

    @Test
    public void isStringAnIntMixOfNumbersLettersSpecial() {
        assertFalse(UtilityFunctions.isStringAnInt("12al!laIkf2*(#Uip892"));
    }

    @Test
    public void isStringAnIntBelowMinimum() {
        assertFalse(UtilityFunctions.isStringAnInt("-2147483649"));
    }

    @Test
    public void isStringAnIntAboveMaximum() {
        assertFalse(UtilityFunctions.isStringAnInt("2147483648"));
    }

    @Test
    public void isStringAnIntAtMaximum() {
        assertTrue(UtilityFunctions.isStringAnInt("2147483647"));
    }

    @Test
    public void isStringAnIntAtMinimum() {
        assertTrue(UtilityFunctions.isStringAnInt("-2147483648"));
    }

    @Test
    public void isStringAnIntSmallInt() {
        assertTrue(UtilityFunctions.isStringAnInt("5"));
    }

    @Test
    public void isStringAnIntWithBigInt() {
        assertTrue(UtilityFunctions.isStringAnInt("21483648"));
    }

    @Test
    public void isStringAlphaNumericEmptyString() {
        assertTrue(UtilityFunctions.isStringAlphaNumeric(""));
    }

    @Test
    public void isStringAlphaNumericLetters() {
        assertTrue(UtilityFunctions.isStringAlphaNumeric("lkajLKSDjoaso"));
    }

    @Test
    public void isStringAlphaNumericNumbers() {
        assertTrue(UtilityFunctions.isStringAlphaNumeric("1238234"));
    }

    @Test
    public void isStringAlphaNumericMixedLettersAndNumbers() {
        assertTrue(UtilityFunctions.isStringAlphaNumeric("A7yj87AY876y76AO"));
    }

    @Test
    public void isStringAlphaNumericWithSingleSpace() {
        assertFalse(UtilityFunctions.isStringAlphaNumeric(" "));
    }

    @Test
    public void isStringAlphaNumericWithSpaceInMiddle() {
        assertFalse(UtilityFunctions.isStringAlphaNumeric("asd 123"));
    }

    @Test
    public void isStringAlphaNumericSpaceAtFront() {
        assertFalse(UtilityFunctions.isStringAlphaNumeric(" 123"));
    }

    @Test
    public void isStringAlphaNumericSpaceAtBack() {
        assertFalse(UtilityFunctions.isStringAlphaNumeric("123 "));
    }

    @Test
    public void isStringAlphaNumericUnderScore() {
        assertFalse(UtilityFunctions.isStringAlphaNumeric("hello_world"));
    }

    @Test
    public void isStringAlphaNumericTeReoMacron() {
        assertFalse(UtilityFunctions.isStringAlphaNumeric("hēllo"));
    }

    @Test
    public void isStringAlphaNumericJapaneseChar() {
        assertFalse(UtilityFunctions.isStringAlphaNumeric("h骨llo"));
    }

    @Test
    public void isStringAlphaNumericVeryLongString() {
        assertTrue(UtilityFunctions.isStringAlphaNumeric("jhbgfdsexd" +
                "crfvtgbyhnujimkolkmjnhbgvfcdxsdcfvgtyhnjmkmjnihuybgtvfcdsxecrfv" +
                "tgbyhnjmjhbgfdsexdcrfvtgbyhnujimkolkmjnhbgvfcdxsdcfvgtyhnjmkmjni" +
                "huybgtvfcdsxecrfvtgbyhnjmjhbgfdsexdcrfvtgbyhnujimkolkmjnhbgvfcdxs" +
                "dcfvgtyhnjmkmjnihuybgtvfcdsxecrfvtgbyhnjmjhbgfdsexdcrfvtgbyhnujim" +
                "kolkmjnhbgvfcdxsdcfvgtyhnjmkmjnihuybgtvfcdsxecrfvtgbyhnjmjhbgfd" +
                "sexdcrfvtgbyhnujimkolkmjnhbgvfcdxsdcfvgtyhnjmkmjnihuybgtvfcdsx" +
                "ecrfvtgbyhnjmjhbgfdsexdcrfvtgbyhnujimkolkmjnhbgvfcdxsdcfvgtyhnjmkm" +
                "jnihuybgtvfcdsxecrfvtgbyhnjmjhbgfdsexdcrfvtgbyhnujimkolkmjnhbgv" +
                "fcdxsdcfvgtyhnjmkmjnihuybgtvfcdsxecrfvtgbyhnjmjhbgfdsexdcrfvtgbyh" +
                "nujimkolkmjnhbgvfcdxsdcfvgtyhnjmkmjnihuybgtvfcdsxecrfvtgbyhnjmjh" +
                "bgfdsexdcrfvtgbyhnujimkolkmjnhbgvfcdxsdcfvgtyhnjmkmjnihuybgtvfcd" +
                "sxecrfvtgbyhnjmjhbgfdsexdcrfvtgbyhnujimkolkmjnhbgvfcdxsdcfvgtyhnjm" +
                "kmjnihuybgtvfcdsxecrfvtgbyhnjmjhbgfdsexdcrfvtgbyhnujimkolkmjnhbgvfc" +
                "dxsdcfvgtyhnjmkmjnihuybgtvfcdsxecrfvtgbyhnjmjhbgfdsexdcrfvtgbyhnuji" +
                "mkolkmjnhbgvfcdxsdcfvgtyhnjmkmjnihuybgtvfcdsxecrfvtgbyhnjmjhbgfd" +
                "sexdcrfvtgbyhnujimkolkmjnhbgvfcdxsdcfvgtyhnjmkmjnihuybgtvfcdsxecr" +
                "fvtgbyhnjmjhbgfdsexdcrfvtgbyhnujimkolkmjnhbgvfcdxsdcfvgtyhnjmkmjnih" +
                "uybgtvfcdsxecrfvtgbyhnjmjhbgfdsexdcrfvtgbyhnujimkolkmjnhbgvfcdxsdc" +
                "fvgtyhnjmkmjnihuybgtvfcdsxecrfvtgbyhnjmjhbgfdsexdcrfvtgbyhnujimkolkm" +
                "jnhbgvfcdxsdcfvgtyhnjmkmjnihuybgtvfcdsxecrfvtgbyhnjmjhbgfdsexdcrfvtg" +
                "byhnujimkolkmjnhbgvfcdxsdcfvgtyhnjmkmjnihuybgtvfcdsxecrfvtgbyhnj" +
                "mjhbgfdsexdcrfvtgbyhnujimkolkmjnhbgvfcdxsdcfvgtyhnjmkmjnihuybgt" +
                "vfcdsxecrfvtgbyhnjmjhbgfdsexdcrfvtgbyhnujimkolkmjnhbgvfcdxsdcfvg" +
                "tyhnjmkmjnihuybgtvfcdsxecrfvtgbyhnjmjhbgfdsexdcrfvtgbyhnujimkolkm" +
                "jnhbgvfcdxsdcfvgtyhnjmkmjnihuybgtvfcdsxecrfvtgbyhnjmjhbgfdsexdcrf" +
                "vtgbyhnujimkolkmjnhbgvfcdxsdcfvgtyhnjmkmjnihuybgtvfcdsxecrfvtgbyhnjm"));
    }

    @Test
    public void validateInvalidNationality() {
    }

    @Test
    public void validateInvalidPassport() {
    }

    @Test
    public void validateType() {
    }

    @Test
    public void isEmailValidEmptyString() {
        assertFalse(UtilityFunctions.isEmailValid(""));
    }

    @Test
    public void isEmailValidNoAtSymbol() {
        assertFalse(UtilityFunctions.isEmailValid("test"));
    }

    @Test
    public void isEmailValidNoDomain() {
        assertFalse(UtilityFunctions.isEmailValid("test@"));
    }

    @Test
    public void isEmailValidWithInvalidDomainExtension() {
        assertFalse(UtilityFunctions.isEmailValid("test@."));
    }

    @Test
    public void isEmailValidWithTooManyDomainPeriodsInARowAfter() {
        assertFalse(UtilityFunctions.isEmailValid("test@test."));
    }

    @Test
    public void isEmailValidWithTooManyDomainPeriodsInARowBefore() {
        assertFalse(UtilityFunctions.isEmailValid("test@.test"));
    }

    @Test
    public void isEmailValidNoUsername() {
        assertFalse(UtilityFunctions.isEmailValid("@test.com"));
    }

    @Test
    public void isEmailValidWithInvalidUsername() {
        assertFalse(UtilityFunctions.isEmailValid(".@test.com"));
    }

    @Test
    public void isEmailValidWithMultiplePeriodsInARowInUsername() {
        assertFalse(UtilityFunctions.isEmailValid("test.@test.com"));
    }

    @Test
    public void isEmailValidTopLevelDomain() {
        assertFalse(UtilityFunctions.isEmailValid("test@test"));
    }

    @Test
    public void isEmailValidWithUniEmail() {
        assertTrue(UtilityFunctions.isEmailValid("lsh73@uclive.ac.nz"));
    }

    @Test
    public void isEmailValidSecondLevelDomain() {
        assertTrue(UtilityFunctions.isEmailValid("test@test.test"));
    }

    @Test
    public void isEmailValidUsernameShort() {
        assertTrue(UtilityFunctions.isEmailValid("t@test.test"));
    }

    @Test
    public void isEmailValidLong() {
        assertTrue(UtilityFunctions.isEmailValid("ilushmleviruasjkdoaasdasdasdasdasdasdasdasdasdasdasdasd" +
                "asddasdadkjahmikueijawasdasdasdjadiaksjdiajsduahduadjyagsduyah@test.test"));
    }

    @Test
    public void isEmailValidPeriodInUsername() {
        assertTrue(UtilityFunctions.isEmailValid("te.st@test.test"));
    }
}