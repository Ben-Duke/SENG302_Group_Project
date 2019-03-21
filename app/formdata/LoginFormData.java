package formdata;

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

//        if (password == null || password.length() == 0) {
//            errors.add(new ValidationError("password", "No password was given"));
//        } else if (!(username == null || username.length() == 0)) {
//            //todo make request to factories to see if user is there
//            for (Traveller traveller : HomeController.getTravellers()) {
//                if (traveller.getUsername().equals(username)) {
//                    user = true;
//                    //todo make request to factories to see if user and password match
//                    if (traveller.getPassword().equals(password)) {
//                        break;
//                    } else {
//                        errors.add(new ValidationError("password", "Wrong password for this username"));
//                    }
//                }
//            }
//            if (user == false) {
//                errors.add(new ValidationError("username", "There is no profile associated to this username"));
//            }
//        }

        if (errors.size() > 0) {
            return errors;
        }
        return null;
    }
}

