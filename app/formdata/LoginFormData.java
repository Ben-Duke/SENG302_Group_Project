package formdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;
import factories.LoginFactory;

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
                errors.add(new ValidationError("username", "Incorrect login information"));
                errors.add(new ValidationError("password", "Incorrect login information"));
            }
        }







        if (errors.size() > 0) {
            return errors;
        }
        return null;
    }
}

