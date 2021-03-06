package formdata;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Constraints.Validate
/**A form class used ot validate user inputs for creating visits for a trip */
public class VisitFormData implements Constraints.Validatable<List<ValidationError>> {

    public String arrival;
    public String departure;

    /** Class constructor */
    public VisitFormData(String arrival, String departure) {
        this.arrival = arrival;
        this.departure = departure;
    }

    /** Required for form instantiation. */
    public VisitFormData() {

    }

    /**
     * Method to validate the Form data.
     *
     * Validation requirements:<br>
     *  *      Departure date is not before arrival date
     *
     * @return A List<E> containing all the validation errors. Or null if no
     *         errors exist.
     */
    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if ((!arrival.isEmpty()) && (!departure.isEmpty())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate arrivalDate = LocalDate.parse(arrival, formatter);
            LocalDate departureDate = LocalDate.parse(departure, formatter);
            if ((arrivalDate.compareTo(departureDate)) > 0) {
                errors.add(new ValidationError("arrival", "Please provide an arrival date after the departure date"));
                errors.add(new ValidationError("departure", "Please provide an arrival date after the departure date"));
            }
        }


        if (! errors.isEmpty()) {
            return errors;
        }

        return null;
    }



    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

}
