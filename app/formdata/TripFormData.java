package formdata;

import models.Destination;
import models.Trip;
import models.User;
import models.Visit;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Constraints.Validate
/** A form class used to validate user inputs on the create trip form page */
public class TripFormData implements Constraints.Validatable<List<ValidationError>> {
    public String tripName;
    public User user;
    public String tags;

    /** Constructor */
    public TripFormData(String tripName, User user, String tags) {
        this.tripName = tripName;
        this.user = user;
        this.tags = tags;
    }

    /** Required for form instantiation. */
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

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
