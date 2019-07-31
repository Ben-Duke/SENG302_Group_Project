package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import controllers.ApplicationManager;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/** Model class for traveller type construction */
@Table(
        uniqueConstraints=
                @UniqueConstraint(columnNames={"traveller_type_name"})
)
@Entity
public class TravellerType extends BaseModel implements Comparable<TravellerType> {

    /**
     * Constructor for traveller types
     * @param travellerTypeName The name of the traveller type being created
     */
    public TravellerType(String travellerTypeName){
        this.travellerTypeName = travellerTypeName;
    }

    @Id
    private Integer ttypeid;

    @Column(name="traveller_type_name")
    private String travellerTypeName;

    @JsonIgnore
    @ManyToMany(mappedBy = "travellerTypes")
    private Set<User> users;

    @JsonIgnore
    @ManyToMany(mappedBy = "travellerTypes")
    private Set<Destination> destinations;

    private static Finder<Integer,TravellerType> find = new Finder<>(TravellerType.class,
            ApplicationManager.getDatabaseName());



    /**
     * Get's EBeans finder object for TravellerType
     *
     * @return A Finder<Integer,TravellerType> object.
     */
    public static Finder<Integer,TravellerType> find() {
        return find;
    }

    @Override
    public String toString() {
        return travellerTypeName;
    }

    public Integer getTtypeid() {
        return ttypeid;
    }

    public void setTtypeid(Integer ttypeid) {
        this.ttypeid = ttypeid;
    }

    public String getTravellerTypeName() {
        return travellerTypeName;
    }

    public void setTravellerTypeName(String travellerTypeName) {
        this.travellerTypeName = travellerTypeName;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }


    public static Map<String, Boolean> getTravellerTypeMap() {
        List<TravellerType> travellerTypes = TravellerType.find.all();
        Map<String, Boolean> travellerTypesMap = new TreeMap<>();
        for (TravellerType travellerType : travellerTypes) {
            travellerTypesMap.put(travellerType.getTravellerTypeName(), false);
        }
        return travellerTypesMap;
    }


    /**
     * Method to check equal traveller type objects
     *
     * @param obj The object being checked
     * @return True of the object is equal to this traveller type,
     * False otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (! (obj instanceof TravellerType)) {
            return false;
        }
        TravellerType other = (TravellerType) obj;
        return this.travellerTypeName.equals(other.travellerTypeName) && this.ttypeid.equals(other.ttypeid);
    }

    /**
     *The unique hashcode of a traveller type given it's attributes
     *
     * @return The full hash code of the traveller type
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + ttypeid;
        hash = 31 * hash + (travellerTypeName == null ? 0 : travellerTypeName.hashCode());
        return  hash;
    }

    /**
     * Method to compare a traveller type to this object
     * @param o The other traveller type
     * @return The int value when they are compared
     */
    public int compareTo(TravellerType o) {
        return this.travellerTypeName.compareTo(o.getTravellerTypeName());
    }
}
