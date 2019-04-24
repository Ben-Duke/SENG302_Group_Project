package formdata;

import com.sun.xml.bind.v2.TODO;
import models.Destination;
import models.User;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Backing class for the Traveller data form.
 * Requirements:
 * <ul>
 * <li> All fields are public,
 * <li> All fields are of type String or List[String].
 * <li> A public no-arg constructor.
 * <li> A validate() method that returns null or a List[ValidationError].
 * </ul>
 */
@Constraints.Validate
public class TreasureHuntFormData implements Constraints.Validatable<List<ValidationError>> {
    public String title;
    public String riddle;
    public Destination destination;
    public String startDate;
    public String endDate;
    public List<User> users;
    public User user;

    @Override
    public String toString() {
        return "TreasureHuntFormData{" +
                "title='" + title + '\'' +
                ", riddle='" + riddle + '\'' +
                ", destination=" + destination +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", users=" + users +
                ", user=" + user +
                '}';
    }

    /** Required for form instantiation. */
    public TreasureHuntFormData(){}

    /**
     * Creates an initialized form instance. Assumes the passed data is valid.
     */
    public TreasureHuntFormData(String title, String riddle, Destination destination, String startDate, String endDate) {
        this.title = title;
        this.riddle = riddle;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * A function that is called when the TreasureHuntFormData is submitted and returns a list of errors if any or null.
     * @return if there are errors a list of errors will be returned if there aren't any then it will return null.
     */
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (title == null || title.length() == 0) {
            errors.add(new ValidationError("title", "No title was given"));
        }

        if (riddle == null || riddle.length() == 0) {
            errors.add(new ValidationError("riddle", "No riddle was given"));
        }

        // Check dates are valid
        // TODO: Implement validation for the dates below and destination.
        if (startDate.isEmpty()) {
            errors.add(new ValidationError("startDate", "Please enter a start date"));
        }
        else {
            LocalDate now = LocalDate.now();
            String min = "1900-01-01";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate minDate = LocalDate.parse(min, formatter);
            LocalDate userDate = LocalDate.parse(startDate, formatter);

            if (userDate.compareTo(now) > 0) {
                errors.add(new ValidationError("startDate", "Please select a valid year under the current year"));
            }
            if (userDate.compareTo(minDate) < 0) {
                errors.add(new ValidationError("startDate", "Please select a date after 1/1/1900"));
            }
        }

        if (endDate.isEmpty()) {
            errors.add(new ValidationError("startDate", "Please enter a start date"));
        }
        else {
            LocalDate now = LocalDate.now();
            String min = "1900-01-01";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate minDate = LocalDate.parse(min, formatter);
            LocalDate userDate = LocalDate.parse(endDate, formatter);

            if (userDate.compareTo(now) > 0) {
                errors.add(new ValidationError("endDate", "Please select a valid year under the current year"));
            }
            if (userDate.compareTo(minDate) < 0) {
                errors.add(new ValidationError("endDate", "Please select a date after 1/1/1900"));
            }
        }

        if (destination == null) {
            errors.add(new ValidationError("destination", "No destination was given"));
        }

        if (errors.size() > 0) {
            return errors;
        }
        return null;

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRiddle() {
        return riddle;
    }

    public void setRiddle(String riddle) {
        this.riddle = riddle;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}