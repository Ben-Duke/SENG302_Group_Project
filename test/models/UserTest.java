package models;

import accessors.UserAccessor;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;
import testhelpers.BaseTestWithApplicationAndDatabase;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Class to JUnit test the User class.
 */
public class UserTest extends BaseTestWithApplicationAndDatabase {

    private String plaintextPassword = "testPw123";
    private String alternativePlainTextPassword = "hellloWorld4556!";

    /**
     * Checks the
     *
     * public User(String email, String plaintextPassword)
     *
     * constructor does not store the password as plaintext.
     */
    @Test
    public void user_checkPasswordInDbIsNotPlaintext_constructorEmailPass() {
        User user = new User("test@test.com", this.plaintextPassword);
        UserAccessor.insert(user);

        User userFromDb = UserAccessor.getUsersFromEmail("test@test.com").get(0);
        assertNotEquals(userFromDb.getPasswordHash(), this.plaintextPassword);
    }

    /**
     * Checks the
     *
     * public User(String email,
     *                 String plaintextPassword,
     *                 String fName,
     *                 String lName,
     *                 LocalDate dateOfBirth,
     *                 String gender)
     *
     * constructor does not store the password as plaintext.
     */
    @Test
    public void user_checkPasswordInDbIsNotPlaintext_fullConstructor() {
        User user = new User(
                "test@test.com",
                this.plaintextPassword,
                "aaaaa",
                "bbbbbb",
                LocalDate.now(),
                "Male");
        UserAccessor.insert(user);

        User userFromDb = UserAccessor.getUsersFromEmail("test@test.com").get(0);
        assertNotEquals(userFromDb.getPasswordHash(), this.plaintextPassword);
    }

    /**
     * Checks the hashAndSetPassword method does not store the password as
     * plaintext.
     */
    @Test
    public void hashAndSetPassword_checkPasswordInDbIsNotPlaintext() {
        User user = new User(
                "test@test.com",
                this.plaintextPassword,
                "aaaaa",
                "bbbbbb",
                LocalDate.now(),
                "Male");
        UserAccessor.insert(user);

        user.hashAndSetPassword(this.alternativePlainTextPassword);
        UserAccessor.update(user);

        User userFromDb = UserAccessor.getUsersFromEmail("test@test.com").get(0);
        assertNotEquals(userFromDb.getPasswordHash(), this.alternativePlainTextPassword);
    }

    /**
     * Checks the passwordHash set by the hashAndSetPassword method is a valid
     * match to the plaintext password it was generated from, according to bcrypt.
     */
    @Test
    public void hashAndSetPassword_checkPasswordHashesMatch() {
        User user = new User(
                "test@test.com",
                this.plaintextPassword,
                "aaaaa",
                "bbbbbb",
                LocalDate.now(),
                "Male");
        UserAccessor.insert(user);

        user.hashAndSetPassword(this.alternativePlainTextPassword);
        UserAccessor.update(user);

        User userFromDb = UserAccessor.getUsersFromEmail("test@test.com").get(0);
        assertTrue(BCrypt.checkpw(this.alternativePlainTextPassword,
                                  userFromDb.getPasswordHash()));
    }
}