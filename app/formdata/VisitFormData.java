package formdata;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Constraints.Validate
public class VisitFormData implements Constraints.Validatable<List<ValidationError>> {

    public String destName;
    public String arrival;
    public String departure;
    public String visitName;


    public VisitFormData(String destName, String arrival, String departure, String visitName) {
        this.destName = destName;
        this.arrival = arrival;
        this.departure = departure;
        this.visitName = visitName;
    }

    public VisitFormData() {

    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        if (destName == null || destName.length() == 0) {
            errors.add(new ValidationError("destName", "Please enter a Destination to add"));

        }
        if ((!arrival.isEmpty()) && (!departure.isEmpty())) {
            LocalDate now = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate arrivalDate = LocalDate.parse(arrival, formatter);
            LocalDate departureDate = LocalDate.parse(departure, formatter);
            if ((arrivalDate.compareTo(now) > 0) || (departureDate.compareTo(now) > 0)) {
                errors.add(new ValidationError("arrival", "Please provide a date before the present date"));
                errors.add(new ValidationError("departure", "Please provide a date before the present date"));
            }
            if((arrivalDate.compareTo(departureDate)) > 0) {
                errors.add(new ValidationError("arrival", "Please provide an arrival date after the departure date"));
                errors.add(new ValidationError("departure", "Please provide an arrival date after the departure date"));
            }
        } else if ((!arrival.isEmpty())) {
            LocalDate now = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate arrivalDate = LocalDate.parse(arrival, formatter);
            if ((arrivalDate.compareTo(now) > 0)) {
                errors.add(new ValidationError("arrival", "Please provide a date before the present date"));
            }
        } else if ((!departure.isEmpty())) {
            LocalDate now = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate departureDate = LocalDate.parse(departure, formatter);
            if ((departureDate.compareTo(now) > 0)) {
                errors.add(new ValidationError("departure", "Please provide a date before the present date"));
            }
        }


        if (! errors.isEmpty()) {
            return errors;
        }

        return null;
    }
}

