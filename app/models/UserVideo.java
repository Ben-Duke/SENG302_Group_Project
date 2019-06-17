package models;

import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserVideo extends Model implements Media {

    @Id
    private Integer videoId;

    public Integer getMediaId() { return videoId; }
}
