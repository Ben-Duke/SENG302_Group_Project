package models;

import formdata.TravellerFormData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;


public class Traveller {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dob;
    private List<String> passports;
    private List<String> nationalities;

    private List<Destination> destinations = new ArrayList<Destination>();

    private final Logger logger = LoggerFactory.getLogger("application");

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDob(String dob) {
        this.dob = LocalDate.parse(dob);
    }

    public LocalDate getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<String> getPassports() {
        return passports;
    }

    public void setPassports(List<String> passports) {
        this.passports = passports;
    }

    public List<String> getNationalities() {
        return nationalities;
    }

    public List<Destination> getDestinations() {
        return destinations;
    }

    public static List<String> getGenderList() {
        String[] genderArray = {"Male", "Female", "Other"};
        return Arrays.asList(genderArray);
    }

    /**
     * A function that is called when the Create Profile Form is processed to show a list of countries
     *
     * @return A map of all countries and a boolean set to false
     * it will return null.
     */
    public static Map<String, Boolean> getCountryList() {
        Locale[] locales = Locale.getAvailableLocales();
        SortedMap<String, Boolean> countryMap = new TreeMap<>();
        for (Locale locale : locales) {
            String localeName;
            localeName = locale.getDisplayCountry();
            countryMap.put(localeName, false);
        }
        countryMap.remove("");
        return countryMap;
    }


    public Traveller(String username, String password, String firstName, String lastName, String gender, List<String> passports, List<String> nationalities, String dob) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.passports = passports;
        this.nationalities = nationalities;
        this.dob = LocalDate.parse(dob);
    }

    public Traveller(){

    }

    /**
     * A function that is called when the TravellerFormData is submitted and returns an Instance of a traveller with the form data passed to it
     * @return Traveller object formed using the TravellerFormData
     */
    public static Traveller makeInstance(TravellerFormData formData) {
        Traveller traveller = new Traveller();
        traveller.username = formData.username;
        traveller.password = formData.password;
        traveller.firstName = formData.firstName;
        traveller.lastName = formData.lastName;
        traveller.gender = formData.gender;
        traveller.passports = formData.passports;
        traveller.nationalities = formData.nationalities;
        traveller.dob = LocalDate.parse(formData.dob);

        return traveller;
    }

    @Override
    public String toString() {
        return "Traveller{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", passports=" + passports +
                ", nationalities=" + nationalities +
                '}' + "Date of birth: " + dob;
    }
}
