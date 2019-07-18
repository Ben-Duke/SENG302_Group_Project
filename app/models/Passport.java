package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "passport",
        uniqueConstraints = @UniqueConstraint(columnNames = "passport_name")
)
public class Passport extends CountryItem {

    @Id
    private Integer passid;

    @Column(name="passport_name")
    private String passportName;

    @JsonIgnore
    @ManyToMany(mappedBy = "passports")
    private List<User> users;

    private final static Finder<Integer,Passport> find = new Finder<>(Passport.class);


    public Passport(String passportName){
        super();
        this.passportName = passportName;
    }

    /**
     * Method to get a finder object for Passport
     *
     * @return A Finder<Integer,Passport> object.
     */
    public static Finder<Integer,Passport> find() {
        return find;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Integer getPassportId() {
        return passid;
    }

    public String getName(){
        return passportName;
    }
}
