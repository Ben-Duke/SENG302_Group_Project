package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "nationality",
        uniqueConstraints = @UniqueConstraint(columnNames = "nationalityName")
)
public class Nationality extends Model {
    @Id
    public Integer natid;

    @Column
    public String nationalityName;

    @ManyToMany(mappedBy = "nationality")
    @JsonIgnore
    public List<User> users;

    public static Finder<Integer,Nationality> find = new Finder<>(Nationality.class);


    // --------------------- methods below here--------------------------------
    public Nationality(String nationality){
        this.nationalityName = nationality;
    }

    public String getNationalityName() { return nationalityName; }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }


}
