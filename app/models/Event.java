package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import controllers.ApplicationManager;
import io.ebean.Finder;
import io.ebean.Model;
import io.ebean.annotation.CreatedTimestamp;
import play.data.format.Formats;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Model for events
 */
@Entity
public class Event extends Model implements AlbumOwner {

    private static final String DATE_PATTERN = "dd-MM-yyyy HH:mm:ss";

    @Id
    private Integer eventId;

    private Integer externalId;

    @Temporal(TemporalType.TIMESTAMP)
    @Formats.DateTime(pattern=DATE_PATTERN)
    //date was protected not public/private for some reason
    @CreatedTimestamp
    private LocalDateTime startTime;


    @Temporal(TemporalType.TIMESTAMP)
    @Formats.DateTime(pattern=DATE_PATTERN)
    @CreatedTimestamp
    private LocalDateTime endTime;

    private String name;
    private String url;
    private double latitude;
    private double longitude;

    @Column(columnDefinition = "TEXT")
    private String description;


    @JsonIgnore
    @OneToMany(mappedBy = "event")
    private List<Album> albums;

    private static final Finder<Integer,Event> find = new Finder<>(Event.class, ApplicationManager.getDatabaseName());

    public Event(Integer externalId, LocalDateTime startTime, LocalDateTime endTime, String name, String url, double latitude, double longitude, String description) {
        this.externalId = externalId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.url = url;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.albums = new ArrayList<>();
    }

    public Event() {}

    public Event(String name) {
        this.name = name;
    }

    public Event(LocalDateTime startTime, LocalDateTime endTime, String description) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }

    public static Finder<Integer, Event> find() {
        return find;
    }

    public int getEventId() {
        return eventId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getExternalId() {
        return externalId;
    }

    public void setExternalId(Integer externalId) {
        this.externalId = externalId;
    }

    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", externalId=" + externalId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", description='" + description +
                '}';
    }
    @Override
    public List<Album> getAlbums() {
        return albums;
    }

    public Album getPrimaryAlbum() {return albums.get(0);}
}
