package models;

import controllers.ApplicationManager;
import io.ebean.Finder;
import io.ebean.Model;
import io.ebean.annotation.CreatedTimestamp;
import play.data.format.Formats;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Model for events
 */
@Entity
public class Event extends Model {

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
    private String description;

    @ManyToOne
    private Destination destination;

    private static final Finder<Integer,Event> find = new Finder<>(Event.class, ApplicationManager.getDatabaseName());

    public Event(LocalDateTime startTime, LocalDateTime endTime, String description, Destination destination) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.destination = destination;
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

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
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

}
