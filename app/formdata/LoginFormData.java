package formdata;

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


    @Override
    public List<ValidationError> validate() {


        if (username == null || username.length() == 0) {
            errors.add(new ValidationError("username", "No username was given"));
        }

        if (password == null || password.length() == 0) {
            errors.add(new ValidationError("password", "No password was given"));
        }

        // checking for correct password
        if ((username == null || username.length() == 0) &&
                (password == null || password.length() == 0)) {
            LoginFactory loginFactory = new LoginFactory();
            if (! loginFactory.isPasswordMatch(username, password)) {
                errors.add(new ValidationError("password", "Incorrect login information"));
            }
        }







        if (errors.size() > 0) {
            return errors;
        }
        return null;
    }
}

