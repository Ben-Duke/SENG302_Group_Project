package controllers;

import accessors.UserPhotoAccessor;
import models.Tag;
import models.User;
import models.UserPhoto;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import java.util.Set;

import static play.mvc.Results.*;

public class TagController {
    public Result getPhotoTag(Http.Request request, int photoId) {
        User user = User.getCurrentUser(request);
        if (user != null) {
            UserPhoto photo = UserPhotoAccessor.getUserPhotoById(photoId);
            if (photo == null) {
                return notFound();
            }
            if (!photo.isPublic
                    && !user.userIsAdmin()
                    && user.getUserid() != photo.getUser().getUserid()) {
                return forbidden();
            }
            Set<Tag> tags = photo.getPhotoTags();
            if (tags == null) {
                return ok("looser");
            }
            return ok(Json.toJson(tags));
        } else {
            return unauthorized();
        }
    }

    public Result addPhotoTag(Http.Request request, int photoId) {
        User user = User.getCurrentUser(request);
        if (user != null) {
            UserPhoto photo = UserPhotoAccessor.getUserPhotoById(photoId);
            if (photo == null) {
                return notFound();
            }
            String tag = request.body().toString();
            photo.addTag(tag);
            UserPhotoAccessor.update(photo);
            return ok();
        } else {
            return unauthorized();
        }
    }

//    public Result removePhotoTag(Http.Request request, int photoId) {
//    }
}
