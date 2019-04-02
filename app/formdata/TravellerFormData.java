package formdata;

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
public class TravellerFormData implements Constraints.Validatable<List<ValidationError>> {
    public String username;
    public String firstName;
    public String lastName;
    public String password;
    public String gender;
    public List<String> passports;
    public List<String> nationalities;
    public String dob;

    /** Required for form instantiation. */
    public TravellerFormData(){}

    /**
     * Creates an initialized form instance. Assumes the passed data is valid.
     */
    public TravellerFormData(String username, String password, String firstName, String lastName, String gender, List<String> passports, List<String> nationalities, String dob) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.passports = new ArrayList<>(passports);
        this.nationalities = new ArrayList<>(nationalities);
        this.dob = dob;
    }

    /**
     * A function that is called when the TravellerFormData is submitted and returns a list of errors if any or null.
     * @return if there are errors a list of errors will be returned if there aren't any then then
     * it will return null.
     */
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();


        if (firstName == null || firstName.length() == 0) {
            errors.add(new ValidationError("firstName", "No first name was given"));
        }

        if (lastName == null || lastName.length() == 0) {
            errors.add(new ValidationError("lastName", "No last name was given"));
        }

        if (username == null || username.length() == 0) {
            errors.add(new ValidationError("username", "No email was given"));
        }

        if (password.length() == 0) {
            errors.add(new ValidationError("password", "No password was given"));
        }


        // Check dates are valid

        if (dob.isEmpty()) {
            errors.add(new ValidationError("dob", "Please enter a date"));

        } else {
            LocalDate now = LocalDate.now();
            String min = "1900-01-01";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate minDate = LocalDate.parse(min, formatter);
            LocalDate userDate = LocalDate.parse(dob, formatter);

            if (userDate.compareTo(now) > 0) {
                errors.add(new ValidationError("dob", "Please select a valid year under the current year"));
            }
            if (userDate.compareTo(minDate) < 0) {
                errors.add(new ValidationError("dob", "Please select a date after 1/1/1900"));
            }
        }

        if (gender == null) {
            errors.add(new ValidationError("gender", "No gender was given"));
        }

        //if (nationalities == null || nationalities.size() == 0) {
        //    errors.add(new ValidationError("nationalities", "No nationality was given"));
        //}

        if (errors.size() > 0) {
            return errors;
        }
        return null;

    }

    @Override
    public String toString() {
        return "TravellerFormData{" +
                "username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", gender='" + gender + '\'' +
                ", passports=" + passports +
                ", nationalities=" + nationalities +
                '}';
    }
}