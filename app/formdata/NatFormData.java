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

import factories.UserFactory;

@Constraints.Validate
public class NatFormData implements Constraints.Validatable<List<ValidationError>>{
    public static UserFactory userFactory;
    public int userId = -1;
    public int natcount;
    public int nationality;
    public String nationalitydelete;
    public static Logger logger = LoggerFactory.getLogger("application");

    public NatFormData(List<String> nats, String delNat, int id){
        this.nationality = nats.size();
        this.nationalitydelete = delNat;
        this.userId = id;
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
        if(userId != -1) {
            natcount = userFactory.getNatsForUserbyId(userId);
        }
        logger.debug("Got to validiate");

        if (natcount < 2) {
            logger.debug("im validating the nats");
            errors.add(
                    new ValidationError("nationalitydelete", "Need at least one nationality, " +
                            "please add another nationality before deleting the one you selected."));
        }

        if (errors.size() > 0) {
            return errors;
        }

        return null;
    }
}
