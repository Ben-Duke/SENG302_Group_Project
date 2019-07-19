package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import controllers.ApplicationManager;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "nationality",
        uniqueConstraints = @UniqueConstraint(columnNames = "nationality_name")
)
public class Nationality extends CountryItem {
    @Id
    public Integer natid;

    @Column
    public String nationalityName;

    @ManyToMany(mappedBy = "nationality")
    @JsonIgnore
    public Set<User> users;

    public static Finder<Integer,Nationality> find = new Finder<>(Nationality.class, ApplicationManager.getDatabaseName());


    // --------------------- methods below here--------------------------------
    public Nationality(String nationality){
        super();
        this.nationalityName = nationality;
    }

    public String getNationalityName() { return nationalityName; }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }


}
