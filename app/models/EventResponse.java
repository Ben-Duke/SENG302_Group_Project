package models;

import controllers.ApplicationManager;
import io.ebean.Finder;
import io.ebean.annotation.CreatedTimestamp;
import io.ebean.annotation.EnumValue;
import play.data.format.Formats;

import javax.persistence.*;
import java.time.LocalDateTime;

/** The model class for EventResponse construction */
@Entity
@Table(name = "event_response",
        uniqueConstraints= @UniqueConstraint(columnNames={"user_userid", "event_event_id"}))
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
    public EventResponse(ResponseType responseType){
        this.responseType = responseType;
    }

    /**
     * Constructor for event response
     * @param responseType The name of the event response type being created
     */
    public EventResponse(ResponseType responseType, Event event, User user){
        this.responseType = responseType;
        this.event = event;
        this.user = user;
    }

    public EventResponse(){

    }

    @Id
    private Integer eventResponseId;

    private ResponseType responseType;

    @ManyToOne
    private User user;

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

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
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
