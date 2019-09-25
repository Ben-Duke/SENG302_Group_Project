package models;

import accessors.EventAccessor;
import accessors.EventResponseAccessor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import controllers.ApplicationManager;
import io.ebean.Finder;
import io.ebean.Model;
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
    @Formats.DateTime(pattern = DATE_PATTERN)
    //date was protected not public/private for some reason
    private LocalDateTime startTime;


    @Temporal(TemporalType.TIMESTAMP)
    @Formats.DateTime(pattern = DATE_PATTERN)
    private LocalDateTime endTime;

    private String name;
    private String url;
    private String address;
    private String type;

    private String imageUrl;
    private double latitude;
    private double longitude;

    @Column(columnDefinition = "TEXT")
    private String description;


    @JsonIgnore
    @OneToMany(mappedBy = "event")
    private List<Album> albums;
    public static final Finder<Integer, Event> find = new Finder<>(Event.class, ApplicationManager.getDatabaseName());


    public Event(Integer externalId, LocalDateTime startTime, LocalDateTime endTime,
                 String name, String type, String url, String imageUrl, double latitude,
                 double longitude, String address, String description) {
        this.externalId = externalId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.type = type;
        this.url = url;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.description = description;
        this.albums = new ArrayList<>();
    }

    public Event() {
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", externalId=" + externalId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", address='" + address + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public List<Album> getAlbums() {
        return albums;
    }

    public Album getPrimaryAlbum() {
        // add the primary album if it is missing
        if (this.albums.isEmpty()) {
            this.albums.add(new Album(this, this.getName(), false));
        }
        return albums.get(0);
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    /**
     * Get the first 10 responses for the event where the first response is for the user
     * if they have responded to the event and the rest are people the user is following who have responded
     * to the event
     */
    public List<EventResponse> getLimitedResponses(User user) {
        // get the user if they are going
        List<EventResponse> responses = new ArrayList<>();
        EventResponse userResponse = EventResponseAccessor.getByUserAndEvent(user, this);
        if (userResponse != null) {
            responses.add(userResponse); // add the user response
        }
        // add follower responses
        responses.addAll(EventResponseAccessor.getEventResponsesOfFollowing(user, 5));

        return responses;
    }
}
