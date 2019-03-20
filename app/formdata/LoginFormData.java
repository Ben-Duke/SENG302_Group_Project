package formdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;
import factories.LoginFactory;

/**
 * A Form class to contain and validate user data entered on the login page.<br>
 *
 * Validation requirements:<br>
 *     username field is not empty<br>
 *     password field is notempty<br>
 *     there is a username && password match in the database for these inputs.<br>
 */
@Constraints.Validate
public class LoginFormData implements Constraints.Validatable<List<ValidationError>> {

    public String username;
    public String password;
    public Boolean user = false;
    private List<ValidationError> errors = new ArrayList<>();
    private final Logger logger = LoggerFactory.getLogger("application");
    public String getUsername(){
        return this.username;
    }

    /**
     * Method to validate the Form data.
     *
     * Validation requirements:<br>
     *  *     username field is not empty<br>
     *  *     password field is notempty<br>
     *  *     there is a username && password match in the database for these
     *        inputs.<br>
     *
     * @return A List<E> containing all the validation errors. Or null if no
     *         errors exist.
     */
    @Override
    public List<ValidationError> validate() {

        boolean hasUserNameData = ! (username == null || username.length() == 0);
        boolean hasPasswordData = ! (password == null || password.length() == 0);

        if (! hasUserNameData) {
            errors.add(new ValidationError("username", "No username was given"));
        }

        if (! hasPasswordData) {
            errors.add(new ValidationError("password", "No password was given"));
        }

        // checking for correct password
        if (hasUserNameData && hasPasswordData) {
            LoginFactory loginFactory = new LoginFactory();
            if (! loginFactory.isPasswordMatch(username, password)) {
                String errorStr = "Incorrect login information";
                errors.add(new ValidationError("username", errorStr));
                errors.add(new ValidationError("password", errorStr));
            }
        }

        if (errors.size() > 0) {
            return errors;
        } else {
            return null; //requirement of the inherited validate method (I think)
        }
    }
}