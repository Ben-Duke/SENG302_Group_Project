package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.Finder;
import javax.persistence.*;
import java.util.*;

@Entity
public class Destination extends TaggableModel {

    @Id
    public Integer destid;

    public String destName;
    public String destType;
    public String district;
    public String country;
    public boolean isCountryValid;
    public double latitude;
    public double longitude;



    @ManyToOne
    public UserPhoto primaryPhoto;


    @JsonIgnore
    @OneToMany(mappedBy = "destination")
    public List<Visit> visits;

    @JsonIgnore
    @ManyToMany(mappedBy = "destinations")
    public List<UserPhoto> userPhotos;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    public Set<TravellerType> travellerTypes;

    public static Finder<String,Destination> findString = new Finder<>(Destination.class);
    public static Finder<Integer,Destination> find = new Finder<>(Destination.class);

    /**
     * Destination constructor with isPublic method
     * @param destName
     * @param destType
     * @param district
     * @param country
     * @param latitude
     * @param longitude
     * @param user
     * @param isPublic
     */
    public Destination(String destName, String destType, String district, String country, double latitude, double longitude, User user, boolean isPublic){
        this.destName = destName;
        this.user = user;
        this.destType = destType;
        this.district = district;
        this.country = country;
        this.isCountryValid = true;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isPublic = isPublic;
    }


    public Destination(Destination destination){
        this.destName = destination.getDestName();
        this.user = destination.getUser();
        this.destType = destination.getDestType();
        this.district = destination.getDistrict();
        this.country = destination.getCountry();
        this.isCountryValid = destination.getIsCountryValid();
        this.latitude = destination.getLatitude();
        this.longitude = destination.getLongitude();
        this.isPublic = destination.getIsPublic();
    }

    /**
     * Destination constructor without isPublic method (isPublic defaults to false)
     * @param destName
     * @param destType
     * @param district
     * @param country
     * @param latitude
     * @param longitude
     * @param user
     */
    public Destination(String destName, String destType, String district, String country, double latitude, double longitude, User user){
        this(destName, destType, district, country, latitude, longitude, user, false);
        this.isCountryValid = true;
    }
    
    public Destination(Destination destination, List<Visit> visits) {
        this(destination.destName, destination.destType, destination.district,
                destination.country, destination.latitude, destination.longitude,
                destination.user, destination.isPublic);
        this.visits = visits;
    }

    /**
     * Destination constructor
     */
    public Destination(){}

    /**
     * A function that is called when creating a destination to the the types
     *
     * @return A map of all destination types and a boolean set to false
     */
    public static Map<String, Boolean> getTypeList() {
        Map<String, Boolean> typeMap = new TreeMap<>();
        typeMap.put("Accomodation", false);
        typeMap.put("Town", false);
        typeMap.put("Country", false);
        typeMap.put("Monument", false);
        typeMap.put("Cafe/Restaurant", false);
        typeMap.put("Attraction", false);
        typeMap.put("Event", false);
        typeMap.put("Natural Spot", false);
        return typeMap;
    }

    /**
     * A function that is called when any form needs a list of countries
     *
     * @return A map of all countries and a boolean set to false
     */


    //GETTERS
    public Integer getDestId() { return destid; }
    public String getDestName() { return destName; }
    public String getDestType() { return destType; }
    public String getDistrict() { return district; }
    public String getCountry() { return country; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public List<UserPhoto> getUserPhotos() {
        return userPhotos;
    }
    public UserPhoto getPrimaryPhoto() {
        return primaryPhoto;
    }
    public List<Visit> getVisits() {
        return visits;
    }
    public boolean getIsCountryValid() { return isCountryValid; }


    public Set<TravellerType> getTravellerTypes() {
        return travellerTypes;
    }

    //SETTERS
    public void setDestId(int destId) { this.destid = destId; }
    public void setDestName(String destName) { this.destName = destName; }
    public void setDestType(String destType) { this.destType = destType; }
    public void setDistrict(String district) { this.district = district; }
    public void setCountry(String country) { this.country = country; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setTravellerTypes(Set<TravellerType> travellerTypes) {
        this.travellerTypes = travellerTypes;
    }
    public void setUserPhotos(List<UserPhoto> userPhotos) {
        this.userPhotos = userPhotos;
    }
    public void setPrimaryPhoto(UserPhoto primaryPhoto) {
        this.primaryPhoto = primaryPhoto;
    }
    public void setVisits(List<Visit> visits) {
        this.visits = visits;
    }
    public void setCountryValid(boolean isCountryValid) {
        this.isCountryValid = isCountryValid;
    }


    public void deleteTravellerType(TravellerType travellerType){
        this.travellerTypes.remove(travellerType);
    }

    public void addTravellerType(TravellerType travellerType){
        this.travellerTypes.add(travellerType);
    }

    public void addVisit(Visit visit) { this.visits.add(visit);}
    public void removeVisit(Visit visit) { this.visits.remove(visit);}


    @Override
    public String toString() {
        return "Destination{" +
                "destid=" + destid +
                ", destName='" + destName + '\'' +
                ", destType='" + destType + '\'' +
                ", district='" + district + '\'' +
                ", country='" + country + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", isPublic=" + isPublic +
                ", primaryPhoto=" + primaryPhoto +
                ", user=" + user +
                ", visits=" + visits +
                ", userPhotos=" + userPhotos +
                ", travellerTypes=" + travellerTypes +
                ", tags=" + tags +
                '}';
    }

    /**
     * The equals method compares two Destination objects for equality. The criteria
     * is all attributes, except isPublic.
     *
     * @param o the other Destination object which is being compared for equality
     * @return true if destinations are equal, false if not.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (! (o instanceof Destination)) {
            return false;
        }

        Destination other = (Destination) o;

        if (!this.destName.equals(other.getDestName())) {
            return false;
        }
        if (!this.country.equals(other.getCountry())) {
            return false;
        }
        if (!this.district.equals(other.getDistrict())) {
            return false;
        }
        if (Math.round(this.latitude*1000) != Math.round(other.getLatitude()*1000)) {
            return false;
        }
        if (Math.round(this.longitude*1000) != Math.round(other.getLongitude()*1000)) {
            return false;
        }
        if (!this.destType.equals(other.getDestType())) {
            return false;
        }
        /*Can not currently compare traveller types as this will let an identical destination be
           created as long as it has a different traveller type which would be the case due to
            how destinations are created the traveller type is not asked till after creation.
         */
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + destid;
        hash = 31 * hash + destName.hashCode();
        hash = 31 * hash + destType.hashCode();
        hash = 31 * hash + country.hashCode();
        hash = 31 * hash + district.hashCode();
        hash = 31 * hash + ((Double) latitude).hashCode();
        hash = 31 * hash + ((Double) longitude).hashCode();
        hash = 31 * hash + travellerTypes.hashCode();
        return hash;
    }

    /**
     * Checks if the given user id (of the currently logged in user) is the same as the owner of the entity.
     * @param userid the user id to be tested
     * @return true if userid is the owner of the entity, false if owner has a different user id.
     */
    public boolean isUserOwner(Integer userid){
        return this.user.getUserid() == userid;
    }

    /** Modifies the fields of this Destination which are included in the
     *   destination editing form to be equal to those fields of the destination
     *   passed in */
    public void applyEditChanges(Destination newDestination) {
        this.destName = newDestination.getDestName();
        this.country = newDestination.getCountry();
        this.district = newDestination.getDistrict();
        this.longitude = newDestination.getLongitude();
        this.latitude = newDestination.getLatitude();
        this.destType = newDestination.getDestType();
    }
}
