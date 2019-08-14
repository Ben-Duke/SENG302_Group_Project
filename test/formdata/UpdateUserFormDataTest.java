package formdata;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import controllers.ApplicationManager;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.ApplicationLoader;
import play.Environment;
import play.data.validation.ValidationError;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceApplicationLoader;
import play.test.Helpers;
import play.test.WithApplication;
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.TestDatabaseManager;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * A class to to test the UpdateUserFormData class using JUnit4.
 */
public class UpdateUserFormDataTest extends BaseTestWithApplicationAndDatabase {

    @Test
    public void validateNoErrorsMale() {
        LocalDate localDateNow = LocalDate.now().minusYears(10);
        User user = new User("test",
                "testtesttest",
                "test",
                "test",
                localDateNow,
                "Male");
        user.save();

        UpdateUserFormData updateUserFormData = new UpdateUserFormData(user, false);
        List<ValidationError> errors = updateUserFormData.validate();
        assertEquals(1, errors.size());
    }

    @Test
    public void validateNoErrorsFemale() {
        LocalDate localDateNow = LocalDate.now().minusYears(10);
        User user = new User("test",
                "testtesttest",
                "test",
                "test",
                localDateNow,
                "Female");

        UpdateUserFormData updateUserFormData = new UpdateUserFormData(user, false);
        List<ValidationError> errors = updateUserFormData.validate();
        assertEquals(1, errors.size());
    }

    @Test
    public void validateEmptyFirstName() {
        LocalDate localDateNow = LocalDate.now().minusYears(10);
        User user = new User("test",
                "testtesttest",
                "",
                "test",
                localDateNow,
                "Male");

        UpdateUserFormData updateUserFormData = new UpdateUserFormData(user, false);
        List<ValidationError> errors = updateUserFormData.validate();
        assertEquals(2, errors.size());
    }

    @Test
    public void validateFirstNameNull() {
        LocalDate localDateNow = LocalDate.now().minusYears(10);
        User user = new User("test",
                "testtesttest",
                null,
                "test",
                localDateNow,
                "Male");

        UpdateUserFormData updateUserFormData = new UpdateUserFormData(user, false);
        List<ValidationError> errors = updateUserFormData.validate();
        assertEquals(2, errors.size());
    }

    @Test
    public void validateFirstNameNumber() {
        LocalDate localDateNow = LocalDate.now().minusYears(10);
        User user = new User("test",
                "testtesttest",
                "1",
                "test",
                localDateNow,
                "Male");

        UpdateUserFormData updateUserFormData = new UpdateUserFormData(user, false);
        List<ValidationError> errors = updateUserFormData.validate();
        assertEquals(2, errors.size());
    }

    @Test
    public void validateFirstNameSymbol() {
        LocalDate localDateNow = LocalDate.now().minusYears(10);
        User user = new User("test",
                "testtesttest",
                "%%@@#",
                "test",
                localDateNow,
                "Male");

        UpdateUserFormData updateUserFormData = new UpdateUserFormData(user, false);
        List<ValidationError> errors = updateUserFormData.validate();
        assertEquals(2, errors.size());
    }

    @Test
    public void validateEmptyLastName() {
        LocalDate localDateNow = LocalDate.now().minusYears(10);
        User user = new User("test",
                "testtesttest",
                "test",
                "",
                localDateNow,
                "Male");

        UpdateUserFormData updateUserFormData = new UpdateUserFormData(user, false);
        List<ValidationError> errors = updateUserFormData.validate();
        assertEquals(2, errors.size());
    }

    @Test
    public void validateLastNameNull() {
        LocalDate localDateNow = LocalDate.now().minusYears(10);
        User user = new User("test",
                "testtesttest",
                "test",
                null,
                localDateNow,
                "Male");

        UpdateUserFormData updateUserFormData = new UpdateUserFormData(user, false);
        List<ValidationError> errors = updateUserFormData.validate();
        assertEquals(2, errors.size());
    }

    @Test
    public void validateLastNameNumber() {
        LocalDate localDateNow = LocalDate.now().minusYears(10);
        User user = new User("test",
                "testtesttest",
                "test",
                "234",
                localDateNow,
                "Female");

        UpdateUserFormData updateUserFormData = new UpdateUserFormData(user, false);
        List<ValidationError> errors = updateUserFormData.validate();
        assertEquals(2, errors.size());
    }

    @Test
    public void validateLastNameSymbol() {
        LocalDate localDateNow = LocalDate.now().minusYears(10);
        User user = new User("test",
                "testtesttest",
                "test",
                "&*^%",
                localDateNow,
                "Female");

        UpdateUserFormData updateUserFormData = new UpdateUserFormData(user, false);
        List<ValidationError> errors = updateUserFormData.validate();
        assertEquals(2, errors.size());
    }

    @Test
    public void validateGenderNull() {
        LocalDate localDateNow = LocalDate.now().minusYears(10);
        User user = new User("test",
                "testtesttest",
                "test",
                "test",
                localDateNow,
                null);

        UpdateUserFormData updateUserFormData = new UpdateUserFormData(user, false);
        List<ValidationError> errors = updateUserFormData.validate();
        assertEquals(2, errors.size());
    }

    @Test
    public void validateGenderBadAlphabeticalLong() {
        LocalDate localDateNow = LocalDate.now().minusYears(10);
        User user = new User("test",
                "testtesttest",
                "test",
                "test",
                localDateNow,
                "qqqqqq");

        UpdateUserFormData updateUserFormData = new UpdateUserFormData(user, false);
        List<ValidationError> errors = updateUserFormData.validate();
        assertEquals(2, errors.size());
    }

    @Test
    public void validateGenderBadAlphabeticalShort() {
        LocalDate localDateNow = LocalDate.now().minusYears(10);
        User user = new User("test",
                "testtesttest",
                "test",
                "test",
                localDateNow,
                "p");

        UpdateUserFormData updateUserFormData = new UpdateUserFormData(user, false);
        List<ValidationError> errors = updateUserFormData.validate();
        assertEquals(2, errors.size());
    }

    @Test
    public void validateGenderBadAlphabeticalNumber() {
        LocalDate localDateNow = LocalDate.now().minusYears(10);
        User user = new User("test",
                "testtesttest",
                "test",
                "test",
                localDateNow,
                "5");

        UpdateUserFormData updateUserFormData = new UpdateUserFormData(user, false);
        List<ValidationError> errors = updateUserFormData.validate();
        assertEquals(2, errors.size());
    }

    @Test
    public void validateGenderBadAlphabeticalSymbol() {
        LocalDate localDateNow = LocalDate.now().minusYears(10);
        User user = new User("test",
                "testtesttest",
                "test",
                "test",
                localDateNow,
                "!");

        UpdateUserFormData updateUserFormData = new UpdateUserFormData(user, false);
        List<ValidationError> errors = updateUserFormData.validate();
        assertEquals(2, errors.size());
    }

    @Test
    public void validateBirthDateNull() {
        LocalDate localDateNow = LocalDate.now().minusYears(10);
        User user = new User("test",
                "testtesttest",
                "test",
                "test",
                null,
                "Male");

        UpdateUserFormData updateUserFormData = new UpdateUserFormData(user, false);
        List<ValidationError> errors = updateUserFormData.validate();
        assertEquals(2, errors.size());
    }

}