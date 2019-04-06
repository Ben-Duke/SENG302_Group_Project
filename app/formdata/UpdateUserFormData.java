package formdata;

import factories.UserFactory;
import models.User;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import utilities.UtilityFunctions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static play.mvc.Results.unauthorized;

/**
 * A class to hold data from the client that is entered when updating a users basic
 * profile. Validates the data.
 */
@Constraints.Validate
public class UpdateUserFormData implements Constraints.Validatable<List<ValidationError>> {

    private List<ValidationError> errors = new ArrayList<>();

    public String firstName;
    public String lastName;
    public String gender;
    public String dateOfBirth;
    public String username;
    public String password;

    public String existingUsername;



    /**
     * Empty constructor, requirement of implementing Constraints.Validatable
     */
    public UpdateUserFormData() {

    }

    /**
     * Constructs a UpdateUserFormData object from a User object. Used to pre-load
     * a web page with existing User data.
     *
     * @param user The User whose data to enter into the form.
     */
    public UpdateUserFormData(User user) {
        this.firstName = user.getfName();
        this.lastName = user.getlName();
        this.gender = user.getGender();
        this.username = user.getEmail();
        this.existingUsername = user.getEmail();
        this.password = user.getPassword();
        if (user.getDateOfBirth() == null) {
            this.dateOfBirth = "null";
        } else {
            this.dateOfBirth = user.getDateOfBirth().toString();
        }
    }

    /**
     * A method to validate the entered data. Checks for nulls and that the data
     * is sensible.
     *
     * @return A List<ValidationError> of all the errors (empty if no errors).
     */
    @Override
    public List<ValidationError> validate() {

        if (firstName == null ||
                                     (! firstName.matches("[a-zA-Z\\s]+")) ||
                                        firstName.length() < 1) {
            errors.add(new ValidationError("firstName",
                                "First name should only contain alphabetical " +
                                        "letters."));
        }

        if (lastName == null ||
                                     (! lastName.matches("[a-zA-Z\\s]+")) ||
                                        lastName.length() < 1) {
            errors.add(new ValidationError("lastName",
                    "Last name should only contain alphabetical " +
                                                                  "letters."));
        }


        if (username == null || username.length() == 0) {
            errors.add(new ValidationError("username", "No username was given"));
        } else {
            if (! UtilityFunctions.isEmailValid(username)) {
                errors.add(new ValidationError("username", "Not an formEmail address"));
            }
        }


        if (!username.equals(existingUsername) && UserFactory.checkEmail(username) == 1) {
            errors.add(new ValidationError("username", "Username is taken"));
        }



        if (password.length() == 0) {
            errors.add(new ValidationError("password", "No password was given"));
        }

        String[] gendersArray = {"Male", "Female", "Other"};
        List gendersList = Arrays.asList(gendersArray);
        String genderErrorStr = "Please select a gender.";
        if (gender == null) {
            errors.add(new ValidationError("gender", genderErrorStr));
        } else if (! gendersList.contains(gender)) {
            errors.add(new ValidationError("gender", genderErrorStr));
        }

        String dateOfBirthErrorStr = "Please enter the date correctly.";
        if (dateOfBirth == null) {
            errors.add(new ValidationError("dateOfBirth", dateOfBirthErrorStr));
        } else {
            if (dateOfBirth.length() < 8) {
                errors.add(new ValidationError("dateOfBirth", dateOfBirthErrorStr));
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                //convert String to LocalDate
                LocalDate birthDate;
                try {
                    // not useless, if this fails it throws an error
                    birthDate = LocalDate.parse(dateOfBirth, formatter);

                    LocalDate dateNow = LocalDate.now();
                    LocalDate date150YearsAgo = LocalDate.now().minusYears(150);

                    if (! birthDate.isAfter(date150YearsAgo) ||
                        ! birthDate.isBefore(dateNow)) {
                        String dobError = "Birth date must be within the last 150 years.";
                        errors.add(new ValidationError("dateOfBirth", dobError));
                    }


                } catch (Exception e) {
                    errors.add(new ValidationError("dateOfBirth", dateOfBirthErrorStr));
                }
            }
        }

        return errors;
    }

}
