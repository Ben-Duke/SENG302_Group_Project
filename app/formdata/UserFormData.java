package formdata;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import factories.UserFactory;

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
public class UserFormData implements Constraints.Validatable<List<ValidationError>> {
    public String username;
    public String firstName;
    public String lastName;
    public String password;
    public String gender;
    public List<String> passports;
    public List<String> nationalities;
    public List<String> travellerTypes;
    public String dob;

    /**
     * Required for form instantiation.
     */
    public UserFormData() {
    }

    /**
     * Creates an initialized form instance. Assumes the passed data is valid.
     */
    public UserFormData(String username, String password, String firstName, String lastName, String gender, List<String> passports, List<String> nationalities, List<String> tTypes, String dob) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.passports = new ArrayList<>(passports);
        this.nationalities = new ArrayList<>(nationalities);
        this.travellerTypes = new ArrayList<>(tTypes);
        this.dob = dob;
    }

    /**
     * A function that is called when the UserFormData is submitted and returns a list of errors if any or null.
     *
     * @return if there are errors a list of errors will be returned if there aren't any then then
     * it will return null.
     */
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();


        if (firstName == null || firstName.length() == 0) {
            errors.add(new ValidationError("firstName", "No first name was given"));
        }
        if (firstName.matches(".*\\d+.*") || firstName.length() < 1) {
            errors.add(new ValidationError("firstName", "First name needs to be only letters and be at least one letter long"));
        }


        if (lastName == null || lastName.length() == 0) {
            errors.add(new ValidationError("lastName", "No last name was given"));
        } else if (lastName.matches(".*\\d+.*") || lastName.length() < 1) {
            errors.add(new ValidationError("lastName", "Last name needs to be only letters and be at least one letter long"));
        }
        if (username == null || username.length() == 0) {
            errors.add(new ValidationError("username", "No username was given"));
        }
        if (UserFactory.checkUsername(username) == 1) {
            errors.add(new ValidationError("username", "Username is taken"));
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

        if (nationalities == null || nationalities.size() == 0) {
            errors.add(new ValidationError("nationalities", "No nationality was given"));
        }

        if (travellerTypes == null || travellerTypes.size() == 0) {
            errors.add(new ValidationError("travellerTypes", "No traveller types were given needs at least one"));
        }

        if (errors.size() > 0) {
            return errors;
        }

        return null;
    }



}