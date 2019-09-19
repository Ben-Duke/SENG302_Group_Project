package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import controllers.ApplicationManager;
import io.ebean.Finder;
import io.ebean.Model;
import io.ebean.annotation.CreatedTimestamp;
import play.data.format.Formats;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

/** The model class for EventResponse construction */
@Entity
@Table(name = "event_response")
public class EventResponse extends BaseModel{

    private static final String DATE_PATTERN = "dd-MM-yyyy HH:mm:ss";

    private static Finder<Integer,EventResponse> find = new Finder<>(EventResponse.class,
            ApplicationManager.getDatabaseName());
    public static Finder<Integer, EventResponse> find() {
        return find;
    }


    /**
     * Constructor for event response
     * @param responseType The name of the event response type being created
     */
    public EventResponse(String responseType){
        this.responseType = responseType;
    }

    /**
     * Constructor for event response
     * @param responseType The name of the event response type being created
     */
    public EventResponse(String responseType, Event event, User user){
        this.responseType = responseType;
        this.event = event;
        this.user = user;
    }

    @Id
    private Integer eventResponseId;

    private String responseType;

    @JsonIgnore
    @ManyToOne
    private User user;

    @JsonIgnore
    @ManyToOne
    private Event event;

    @Temporal(TemporalType.TIMESTAMP)
    @Formats.DateTime(pattern=DATE_PATTERN)
    @CreatedTimestamp
    private LocalDateTime responseDateTime;

    public LocalDateTime getResponseDateTime() {
        return responseDateTime;
    }

    public void setResponseDateTime(LocalDateTime responseDateTime) {
        this.responseDateTime = responseDateTime;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getEventResponseId() {
        return eventResponseId;
    }

    public void setEventResponseId(Integer eventResponseId) {
        this.eventResponseId = eventResponseId;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    @Override
    public String toString() {
        return "EventResponse{" +
                "eventResponseId=" + eventResponseId +
                ", responseType='" + responseType + '\'' +
                ", user=" + user +
                ", event=" + event +
                ", responseDateTime=" + responseDateTime +
                '}';
    }
}