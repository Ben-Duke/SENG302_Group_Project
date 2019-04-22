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


    public String getDestName() {
        return destName;
    }

    public void setDestName(String destName) {
        this.destName = destName;
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

    public String getVisitName() {
        return visitName;
    }

    public void setVisitName(String visitName) {
        this.visitName = visitName;
    }
}

