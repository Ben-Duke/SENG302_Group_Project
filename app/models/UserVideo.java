package models;

import javax.persistence.Entity;

@Entity
public class UserVideo extends Media {

    public UserVideo(String url, boolean isPublic, User user) {
        super(url, isPublic, user);
    }

    public UserVideo(UserVideo userVideo) {
        super(userVideo.getUrl(), userVideo.getIsPublic(), userVideo.getUser());
    }
}
