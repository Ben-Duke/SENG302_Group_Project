package models;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

@Entity
public class Destination extends Model {

    public Destination(String destName, String destType, String district, String country, float latitude, float longitude, User user){
        this.destName = destName;
        this.user = user;
        this.destType = destType;
        this.district = district;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * A function that is called when creating a destination to the the types
     *
     * @return A map of all destination types and a boolean set to false
     */
    public static Map<String, Boolean> getTypeList() {
        Map<String, Boolean> typeMap = new TreeMap<>();
        typeMap.put("Accomodation", false);
        typeMap.put("Cafe/Restaurant", false);
        typeMap.put("Attraction", false);
        typeMap.put("Event", false);
        typeMap.put("Natural Spot", false);
        return typeMap;
    }

    @Id
    public Integer destid;

    public String destName;
    public String destType;
    public String district;
    public String country;
    public float latitude;
    public float longitude;

    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userid")
    public User user;

    @OneToMany(mappedBy = "destination")
    public List<Visit> visits;

    public static Finder<Integer,Destination> find = new Finder<>(Destination.class);


    //GETTERS
    public Integer getDestId() { return destid; }
    public String getDestName() { return destName; }
    public String getDestType() { return destType; }
    public String getDistrict() { return district; }
    public String getCountry() { return country; }
    public float getLatitude() { return latitude; }
    public float getLongitude() { return longitude; }

    public User getUser() { return user; }

    //SETTERS
    public void setDestId(int destId) { this.destid = destId; }
    public void setDestName(String destName) { this.destName = destName; }
    public void setDestType(String destType) { this.destType = destType; }
    public void setDistrict(String district) { this.district = district; }
    public void setCountry(String country) { this.country = country; }
    public void setLatitude(float latitude) { this.latitude = latitude; }
    public void setLongitude(float longitude) { this.longitude = longitude; }

    public void setUser(User user) { this.user = user; }

    /**
     * The equals method compares two Destination objects for equality. The criteria
     * is district and country.
     *
     * @param dest2 the other Destination object which is being compared for equality
     * @return true if destinations are equal, false if not.
     */

    public boolean equals(Destination dest2) {

        if (!district.equals(dest2.getDistrict())) {
            return false;
        }
        if (!country.equals(dest2.getCountry())) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the given user id (of the currently logged in user) is the same as the owner of the entity.
     * @param userid the user id to be tested
     * @return true if userid is the owner of the entity, false if owner has a different user id.
     */
    public boolean isUserOwner(Integer userid){
        return this.user.getUserid() == userid;
    }


}
