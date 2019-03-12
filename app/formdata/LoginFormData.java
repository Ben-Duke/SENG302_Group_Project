package formdata;

import controllers.HomeController;
import models.Traveller;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;

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
        } else if (!(username == null || username.length() == 0)) {
            for (Traveller traveller : HomeController.getTravellers()) {
                if (traveller.getUsername().equals(username)) {
                    user = true;
                    if (traveller.getPassword().equals(password)) {
                        break;
                    } else {
                        errors.add(new ValidationError("password", "Wrong password for this username"));
                    }
                }
            }
            if (user == false) {
                errors.add(new ValidationError("username", "There is no profile associated to this username"));
            }
        }

        if (errors.size() > 0) {
            return errors;
        }
        return null;
    }
}

