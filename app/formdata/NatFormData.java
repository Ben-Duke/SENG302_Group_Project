package formdata;
import factories.NatFactory;
import factories.UserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Constraints.Validate
public class NatFormData implements Constraints.Validatable<List<ValidationError>>{

    public List<String> nationality;
    public String nationalitydelete;
    public static Logger logger = LoggerFactory.getLogger("application");

    public NatFormData(List<String> nats, String delNat){
        this.nationality = nats;
        this.nationalitydelete = delNat;
    }

    /**
     * Required for form instantiation.
     */
    public NatFormData(){

    }

    /**
     * A function that is called when the UserFormData is submitted and returns a list of errors if any or null.
     *
     * @return if there are errors a list of errors will be returned if there aren't any then then
     * it will return null.
     */
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        logger.debug("Got to validiate");

        logger.debug("nats " + nationality);
        if (nationality == null) {
            errors.add(new ValidationError("nationalitydelete", "Need at least one nationality"));
        }
        errors.add(new ValidationError("nationalitydelete", "Need at least one nationality"));
        if (errors.size() > 0) {
            return errors;
        }

        return null;
    }
}
