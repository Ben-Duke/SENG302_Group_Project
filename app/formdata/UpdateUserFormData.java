package formdata;

import models.User;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;

import static play.mvc.Results.unauthorized;

@Constraints.Validate
public class UpdateUserFormData implements Constraints.Validatable<List<ValidationError>> {

    private List<ValidationError> errors = new ArrayList<>();

    public String firstName;
    public String lastName;
    public String gender;
    public String dob;


    public UpdateUserFormData() {

    }

    public UpdateUserFormData(User user) {
        this.firstName = user.getfName();
        this.lastName = user.getlName();
        this.gender = user.getGender();
        this.dob = user.getDateOfBirth().toString();
    }

    @Override
    public List<ValidationError> validate() {

        if (firstName.matches(".*\\d+.*") || firstName.length() < 1) {
            errors.add(new ValidationError("fName",
                                "First name should only contain alphabets."));
        }

        if (lastName.matches(".*\\d+.*") || lastName.length() < 1) {
            errors.add(new ValidationError("lName",
                    "Last name should only contain alphabets."));
        }

        if (gender == null) {
            errors.add(new ValidationError("gender",
                    "Please select a gender."));
        }

        if (dob.length() < 8) {
            errors.add(new ValidationError("dateOfBirth",
                    "Please enter the date correctly."));
        }


        if (errors.isEmpty()) {
            return errors;
        } else {
            return null; //requirement of the inherited validate method (I think)
        }
    }

}
