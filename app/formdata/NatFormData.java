package formdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import java.util.ArrayList;
import java.util.List;

import factories.UserFactory;

@Constraints.Validate
/**A form class used ot validate user inputs on the nationality form page */
public class NatFormData implements Constraints.Validatable<List<ValidationError>>{

    private static Logger logger = LoggerFactory.getLogger("application");
    public int userId = -1;
    private int natcount;
    private int nationality;
    public String nationalitydelete;


    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        NatFormData.logger = logger;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getNatcount() {
        return natcount;
    }

    public void setNatcount(int natcount) {
        this.natcount = natcount;
    }

    public int getNationality() {
        return nationality;
    }

    public void setNationality(int nationality) {
        this.nationality = nationality;
    }

    public String getNationalitydelete() {
        return nationalitydelete;
    }

    public void setNationalitydelete(String nationalitydelete) {
        this.nationalitydelete = nationalitydelete;
    }

    public NatFormData(List<String> nats, String delNat, int id){
        this.nationality = nats.size();
        this.nationalitydelete = delNat;
        this.userId = id;
    }
    /** Required for form instantiation. */
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
            natcount = UserFactory.getNatsForUserbyId(userId);
        }
        if (natcount < 2) {
            errors.add(
                    new ValidationError("nationalitydelete", "Need at least one nationality, " +
                            "please add another nationality before deleting the one you selected."));
        }
            return errors;

    }

}
