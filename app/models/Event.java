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

    @Temporal(TemporalType.TIMESTAMP)
    @Formats.DateTime(pattern=DATE_PATTERN)
    //date was protected not public/private for some reason
    @CreatedTimestamp
    private LocalDateTime startTime;


    @Temporal(TemporalType.TIMESTAMP)
    @Formats.DateTime(pattern=DATE_PATTERN)
    @CreatedTimestamp
    private LocalDateTime endTime;

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

}
