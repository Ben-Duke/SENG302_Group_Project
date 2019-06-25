package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Table(
        uniqueConstraints=
                @UniqueConstraint(columnNames={"traveller_type_name"})
)
@Entity
public class TravellerType extends Model implements Comparable<TravellerType> {

    public TravellerType(String travellerTypeName){
        this.travellerTypeName = travellerTypeName;
    }

    @Id
    public Integer ttypeid;

    @Column(name="traveller_type_name")
    public String travellerTypeName;

    @Override
    public String toString() {
        return travellerTypeName;
    }

    @JsonIgnore
    @ManyToMany(mappedBy = "travellerTypes")
    public Set<User> users;

    @JsonIgnore
    @ManyToMany(mappedBy = "travellerTypes")
    public Set<Destination> destinations;

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

    public static Finder<Integer,TravellerType> find = new Finder<>(TravellerType.class);

    public static Map<String, Boolean> getTravellerTypeMap() {
        List<TravellerType> travellerTypes = TravellerType.find.all();
        Map<String, Boolean> travellerTypesMap = new TreeMap<>();
        for (TravellerType travellerType : travellerTypes) {
            travellerTypesMap.put(travellerType.getTravellerTypeName(), false);
        }
        return travellerTypesMap;
    }


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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + ttypeid;
        hash = 31 * hash + (travellerTypeName == null ? 0 : travellerTypeName.hashCode());
        return  hash;
    }

    public int compareTo(TravellerType o) {
        return this.travellerTypeName.compareTo(o.getTravellerTypeName());
    }
}
