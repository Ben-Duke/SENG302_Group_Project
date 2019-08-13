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
 *     email field is not empty<br>
 *     password field is notempty<br>
 *     there is a email && password match in the database for these inputs.<br>
 */
@Constraints.Validate
public class LoginFormData implements Constraints.Validatable<List<ValidationError>> {

    public String email;
    public String password;
    public Boolean user = false;

    private List<ValidationError> errors = new ArrayList<>();
    private final Logger logger = LoggerFactory.getLogger("application");

    /**
     * Method to get  the (String) email from the  form.
     *
     * @return A String representing the email in the form.
     */
    public String getEmail(){
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getUser() {
        return user;
    }

    public void setUser(Boolean user) {
        this.user = user;
    }

    /**
     * Method to validate the Form data.
     *
     * Validation requirements:<br>
     *  *     email field is not empty<br>
     *  *     password field is notempty<br>
     *  *     there is a email && password match in the database for these
     *        inputs.<br>
     *
     * @return A List<E> containing all the validation errors. Or null if no
     *         errors exist.
     */
    @Override
    public List<ValidationError> validate() {

        boolean hasEmailData = ! (email == null || email.length() == 0);
        boolean hasPasswordData = ! (password == null || password.length() == 0);

        if (! hasEmailData) {
            errors.add(new ValidationError("email", "No email was given"));
        }

        if (! hasPasswordData) {
            errors.add(new ValidationError("password", "No password was given"));
        }

        // checking for correct password
        if (hasEmailData && hasPasswordData) {
            LoginFactory loginFactory = new LoginFactory();
            if (! loginFactory.isPasswordMatch(email, password)) {
                String errorStr = "Incorrect login information";
                errors.add(new ValidationError("email", errorStr));
                errors.add(new ValidationError("password", errorStr));
            }
        }

        if (! errors.isEmpty()) {
            return errors;
        } else {
            return null; //requirement of the inherited validate method (I think)
        }
    }
}