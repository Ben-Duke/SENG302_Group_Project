package formdata;

import models.Destination;
import models.Trip;
import models.Visit;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Constraints.Validate
public class VisitFormData implements Constraints.Validatable<List<ValidationError>> {

    public VisitFormData(String destName,String arrival, String departure, String visitName) {
        this.destName = destName;
        this.arrival = arrival;
        this.departure = departure;
        this.visitName = visitName;
    }
    public String destName;
    public String arrival;
    public String departure;
    public String visitName;

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        if (destName == null || destName.length() == 0) {
            errors.add(new ValidationError("destName", "Please enter a Destination to add"));

        }

        if (errors.size() > 0) {
            return errors;
        }

        return null;
    }
}

