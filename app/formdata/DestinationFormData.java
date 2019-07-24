package formdata;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Constraints.Validate
public class DestinationFormData implements Constraints.Validatable<List<ValidationError>> {

    public String destName;
    public String destType;
    public String district;
    public String country;
    public String latitude;
    public String longitude;
    public List<String> tags;


    /** Required for form instantiation. */
    public DestinationFormData() {
    }

    /** Constructor */
    public DestinationFormData(String destName, String destType, String district,
                               String country, Double latitude, Double longitude, List<String> tags) {
        this.destName = destName;
        this.destType = destType;
        this.district = district;
        this.country = country;
        this.latitude = latitude.toString();
        this.longitude = longitude.toString();
        this.tags = tags;
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
     * A function that is called when the DestinationFormData is submitted and
     * returns a list of errors if any or null.
     * @return if there are errors a list of errors will be returned if there
     * aren't any then then it will return null.
     */
    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        destName = destName.trim();
        district = district.trim();

        if (destName == null || destName.length() == 0) {
            errors.add(new ValidationError("destName",
                                    "Destination name  must not be empty"));
        }

        if (country == null || country.length() == 0) {
            errors.add(new ValidationError("country",
                                    "Destination country must not be empty"));
        }

        if (destType == null || destType.length() == 0) {
            errors.add(new ValidationError("destType",
                                        "Destination type must not be empty"));
        }

        if (district == null || district.length() == 0) {
            errors.add(new ValidationError("district",
                    "District must not be empty"));
        }


        try {
            if (latitude == null || latitude.length()==0) {
                errors.add(new ValidationError("latitude",
                            "Latitude must be a number between -90 and 90"));
            } else {
                Double latitudeDouble = Double.parseDouble(latitude);
                if (! (-90.0 <= latitudeDouble && latitudeDouble <= 90.0)) {
                    errors.add(new ValidationError("latitude",
                            "Latitude must be between -90 and 90"));
                }
            }
        } catch (NumberFormatException e) {
            errors.add(new ValidationError("latitude",
                            "Latitude must be a number between -90 and 90"));
        }

        try {
            if (longitude == null || longitude.length()==0) {
                errors.add(new ValidationError("longitude",
                                    "Longitude must be between -180 and 180"));
            } else {
                Double longitudeDouble = Double.parseDouble(longitude);
                if (! (-180.0 <= longitudeDouble && longitudeDouble <= 180.0)) {
                    errors.add(new ValidationError("longitude",
                            "Longitude must be between -180 and 180"));
                }
            }
        } catch (NumberFormatException e) {
            errors.add(new ValidationError("longitude",
                                    "Longitude must be between -180 and 180"));
        }
        return errors;
    }

    public String getDestName() {
        return destName;
    }

    public void setDestName(String destName) {
        this.destName = destName;
    }

    public String getDestType() {
        return destType;
    }

    public void setDestType(String destType) {
        this.destType = destType;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
