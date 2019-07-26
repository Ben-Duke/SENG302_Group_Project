package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/** The model class for nationality construction */
@Entity
@Table(name = "nationality",
        uniqueConstraints = @UniqueConstraint(columnNames = "nationality_name")
)
public class Nationality extends CountryItem {
    @Id
    private Integer natid;

    @Column
    private String nationalityName;

    @ManyToMany(mappedBy = "nationality")
    @JsonIgnore
    private Set<User> users;

    private static Finder<Integer,Nationality> find = new Finder<>(Nationality.class);


    /**
     * Constructor for nationalities
     * @param nationality The nationality object being created
     */
    // --------------------- methods below here--------------------------------
    public Nationality(String nationality){
        super();
        this.nationalityName = nationality;
    }

    /**
     * Method to get EBeans finder.
     *
     * @return Finder<Integer,Nationality> object
     */
    public static Finder<Integer,Nationality> find() {
        return find;
    }

    public String getNationalityName() { return nationalityName; }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Integer getNatId() {
        return this.natid;
    }


}
