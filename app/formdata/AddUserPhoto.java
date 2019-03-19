package formdata;

public class AddUserphoto implements Constraints.Validatable<List<ValidationError>> {
    public String url;
    public boolean isPublic;


    public AddUserphoto() {

    }

    public List<ValidationError> validate() {
        return null;
    }
}