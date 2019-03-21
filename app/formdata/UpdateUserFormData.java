package formdata;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;

@Constraints.Validate
public class UpdateUserFormData implements Constraints.Validatable<List<ValidationError>> {

    private List<ValidationError> errors = new ArrayList<>();

    public String firstName;
    public String lastName;
    public String gender;
    public String dob;

    @Override
    public List<ValidationError> validate() {

        if (errors.isEmpty()) {
            return errors;
        } else {
            return null; //requirement of the inherited validate method (I think)
        }
    }

}
