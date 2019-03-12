package models;

import formdata.DestinationFormData;

public class Destination {
    private String name;
    private String district;
    private String city;
    private String latitude;
    private String longitude;
    private String type;
    private String date;
    public String country;

    public Destination() {
    }

    public String getName() {
        return name;
    }

    public String getDistrict() {
        return district;
    }

    public String getCity() {
        return city;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getCountry() {
        return country;
    }


    /**
     * A function that is called when the DestinationFormData is submitted and returns an Instance of a destination with the form data passed to it
     * @return Destination object formed using the DestinationFormData
     */
    public static Destination makeInstance(DestinationFormData formData) {
        Destination destination = new Destination();
        destination.name = formData.name;
        destination.district = formData.district;
        destination.city = formData.city;
        destination.latitude = formData.latitude;
        destination.longitude = formData.longitude;
        destination.type = formData.type;
        destination.date = formData.date;
        destination.country = formData.country;
        return destination;
    }
}
