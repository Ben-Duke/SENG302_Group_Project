package formdata;

import models.Destination;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import utilities.UtilityFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Constraints.Validate
public class DestinationFormData implements Constraints.Validatable<List<ValidationError>> {

    public String name;
    public String district;
    public String city;
    public String latitude;
    public String longitude;
    public String type;
    public String date;
    public String country;

    public DestinationFormData() {}

    public DestinationFormData(Destination destination) {
        this.name = destination.destName;
        this.district = destination.district;
        this.latitude = String.valueOf(destination.latitude);
        this.longitude = String.valueOf(destination.longitude);
        this.type = destination.destType;
        this.country = destination.country;
    }

    /**
     * A function that is called when creating a destination to the the types
     *
     * @return A map of all destination types and a boolean set to false
     */
    public static Map<String, Boolean> getTypeList() {
        Map<String, Boolean> typeMap = new TreeMap<>();
        typeMap.put("monument", false);
        typeMap.put("town", false);
        typeMap.put("suburb", false);
        typeMap.put("country", false);
        typeMap.put("event", false);
        typeMap.put("activity", false);
        typeMap.put("natural feature", false);
        return typeMap;
    }


    /**
     * A method called when creating a destination to show the countries in a select box
     * @return A map of all countries in the world and a boolean set to false
     */
    public static Map<String, Boolean> getCountryList() {
        return UtilityFunctions.getIsoCountries();
    }


    /**
     * A function that is called when the DestinationFormData is submitted and returns a list of errors if any or null.
     * @return if there are errors a list of errors will be returned if there aren't any then then
     * it will return null.
     */
    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (name == null || name.length() == 0) {
            errors.add(new ValidationError("name","Destination Name field is empty"));
        }

        if (country == null || country.length() == 0) {
            errors.add(new ValidationError("country", "Country field is empty"));
        }

        if (type == null || type.length() == 0) {
            errors.add(new ValidationError("type", "Type field is empty"));
        }


        try {
            if (latitude == null || latitude.length()==0) {
                errors.add(new ValidationError("latitude","Latitude field is empty"));
            } else {
                Double.parseDouble(latitude);
            }
        } catch (NumberFormatException e) {
            errors.add(new ValidationError("latitude", "Latitude is not a valid number"));
        }

        try {
            if (longitude == null || longitude.length()==0) {
                errors.add(new ValidationError("longitude","Longitude field is empty"));
            } else {
                Double.parseDouble(longitude);
            }
        } catch (NumberFormatException e) {
            errors.add(new ValidationError("longitude", "Latitude is not a valid number"));
        }


        if (type.equals("event") && date.isEmpty()){
            errors.add(new ValidationError("date", "Please enter a date"));
        }


        if (errors.size() > 0) {
            return errors;
        }
        return null;
    }
}
