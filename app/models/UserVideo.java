package models;

public class UserVideo extends Media {



    public UserVideo(String url, boolean isPublic, boolean isProfile, User user) {
        super(url, isPublic, user);
    }
}
