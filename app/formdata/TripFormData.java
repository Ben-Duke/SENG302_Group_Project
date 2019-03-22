package formdata;

import models.Destination;
import models.User;
import models.Visit;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Constraints.Validate
public class TripFormData implements Constraints.Validatable<List<ValidationError>> {
    public String tripName;
    public User user;

    public TripFormData(String tripName, User user) {
        this.tripName = tripName;
        this.user = user;
    }

    public TripFormData() {

    }

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (tripName == null || tripName.length() == 0) {
            errors.add(new ValidationError("tripName","Trip name field is empty"));
        }

        if (errors.size() > 0) {
            return errors;
        }

        return null;

    }


}
