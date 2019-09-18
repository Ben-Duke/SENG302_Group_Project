package models;

import accessors.AlbumAccessor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import controllers.ApplicationManager;
import io.ebean.Finder;
import io.ebean.Model;
import models.media.MediaOwner;

import javax.persistence.*;
import java.util.*;

@Entity
public class Destination extends TaggableModel implements AlbumOwner, MediaOwner  {

    @Id
    private Integer destid;

    private String destName;
    private String destType;
    private String district;
    private String country;
    private boolean isCountryValid;
    private double latitude;
    private double longitude;
    private boolean destIsPublic;

    @ManyToOne
    private UserPhoto primaryPhoto;

    @JsonIgnore
    @OneToMany(mappedBy = "destination")
    private List<Album> albums;

    @ManyToMany
    public List<Media> mediaList;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userid")
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "destination")
    private List<Visit> visits;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<TravellerType> travellerTypes;


    private static Finder<Integer,Destination> find = new Finder<>(Destination.class, ApplicationManager.getDatabaseName());

    /**
     * Destination constructor with destIsPublic method
     * @param destName The name of the destination
     * @param destType The type of the destination
     * @param district The district of the destination
     * @param country The country of the destination
     * @param latitude The latitude of the destination
     * @param longitude The longitude of the destination
     * @param user The user that owns the destination
     * @param destIsPublic Is the destination public
     */
    public Destination(String destName, String destType, String district, String country, double latitude, double longitude, User user, boolean destIsPublic){
        this.destName = destName;
        this.user = user;
        this.destType = destType;
        this.district = district;
        this.country = country;
        this.isCountryValid = true;
        this.latitude = latitude;
        this.longitude = longitude;
        this.destIsPublic = destIsPublic;
    }

    /**
     * Destination construction given a chosen destination
     *
     * @param destination The Destination object
     */
    public Destination(Destination destination){
        this.destName = destination.getDestName();
        this.user = destination.getUser();
        this.destType = destination.getDestType();
        this.district = destination.getDistrict();
        this.country = destination.getCountry();
        this.isCountryValid = destination.getIsCountryValid();
        this.latitude = destination.getLatitude();
        this.longitude = destination.getLongitude();
        this.destIsPublic = destination.getIsPublic();
    }

    /**
     * Destination constructor without destIsPublic method (destIsPublic defaults to false)
     * @param destName The name of the destination
     * @param destType The type of the destination
     * @param district The district of the destination
     * @param country The country of the destination
     * @param latitude The latitude of the destination
     * @param longitude The longitude of the destination
     * @param user The user that owns the destination
     */
    public Destination(String destName, String destType, String district, String country, double latitude, double longitude, User user){
        this(destName, destType, district, country, latitude, longitude, user, false);
        this.isCountryValid = true;
    }

    /**
     * Destination constructor given a destination and a list of visits
     * @param destination The destination object being made
     * @param visits The list of visits for this destination
     */
    public Destination(Destination destination, List<Visit> visits) {
        this(destination.destName, destination.destType, destination.district,
                destination.country, destination.latitude, destination.longitude,
                destination.user, destination.destIsPublic);
        this.visits = visits;
    }

    /**
     * Destination constructor
     */
    public Destination(){}

    public static Finder<Integer,Destination> find() {
        return find;
    }

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

    /**
     * Weird scala html bug fix.
     * Without this, tests will give this error for some reason:
     * Uncaught error from thread [application-akka.actor.default-dispatcher-4]:
     * models.Destination.getDestid()Ljava/lang/Integer;
     * @return the destination id
     */
    public Integer getDestid() { return destid; }
    public String getDestName() { return destName; }
    public String getDestType() { return destType; }
    public String getDistrict() { return district; }
    public String getCountry() { return country; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public boolean getIsPublic() { return destIsPublic; }
    public List<Visit> getVisits() {
        return visits;
    }
    public boolean getIsCountryValid() { return isCountryValid; }

    public boolean hasPrimaryPhoto() { return primaryPhoto != null; }


    public User getUser() { return user; }

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
    public void setIsPublic(boolean isPublic) { this.destIsPublic = isPublic; }
    public void setTravellerTypes(Set<TravellerType> travellerTypes) {
        this.travellerTypes = travellerTypes;
    }
    public void setVisits(List<Visit> visits) {
        this.visits = visits;
    }
    public void setCountryValid(boolean isCountryValid) {
        this.isCountryValid = isCountryValid;
    }

    public void setUser(User user) { this.user = user; }

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
                ", destIsPublic=" + destIsPublic +
                ", user=" + user +
                ", visits=" + visits +
                ", albums=" + albums +
                ", travellerTypes=" + travellerTypes +
                ", tags=" + tags +
                '}';
    }

    /**
     * The equals method compares two Destination objects for equality. The criteria
     * is all attributes, except destIsPublic.
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

    public boolean isSame(Destination other) {

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
        if (!this.getTravellerTypes().equals(other.getTravellerTypes())) {
            return false;
        }

        return true;
    }

    public boolean isSimilar(Destination other) {
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

        return true;
    }

    /**
     *The unique hashcode of a destination given it's attributes
     *
     * @return The full hash code of the destination
     */
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
    public boolean isUserOwner(User user) { return this.user.getUserid() == user.getUserid(); }

    /** Modifies the fields of this Destination which are included in the
     *   destination editing form to be equal to those fields of the destination
     *   passed in
     *
     * @param newDestination The new destination after updating
     */
    public void applyEditChanges(Destination newDestination) {
        this.destName = newDestination.getDestName();
        this.country = newDestination.getCountry();
        this.district = newDestination.getDistrict();
        this.longitude = newDestination.getLongitude();
        this.latitude = newDestination.getLatitude();
        this.destType = newDestination.getDestType();
        this.travellerTypes = newDestination.getTravellerTypes();
        this.tags = newDestination.getTags();
    }

    @Override
    public List<Album> getAlbums() {
        return albums;
    }

    /**
     * Returns the first album in the destination's list of albums.
     * Currently, a destination should only have one album so this should return
     * the only album the destination has.
     * @return the destination's album
     */
    @JsonIgnore
    public Album getPrimaryAlbum() {
        if (albums.isEmpty()) {
            Album album = new Album(this, this.getDestName(), false);
            AlbumAccessor.insert(album);
        }
        return AlbumAccessor.getAlbumsByOwner(this).get(0);
    }
}
