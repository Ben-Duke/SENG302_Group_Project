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

    /**
     * Gets all current tags of a given photo
     * @param request An authenticated http request
     * @param photoId the id of the photo to retrieve the tags of
     * @return a http response with the list of tags
     */
    public Result getPhotoTags(Http.Request request, int photoId) {
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
            return ok(Json.toJson(tags));
        } else {
            return unauthorized();
        }
    }

    /**
     * Adds a tag to a photo
     * @param request An authenticated http request containing the new tag
     * @param photoId the id of the photo to add a tag to
     * @return Http response detailing the success or failure
     */
    public Result addPhotoTag(Http.Request request, int photoId) {
        User user = User.getCurrentUser(request);
        if (user != null) {
            UserPhoto photo = UserPhotoAccessor.getUserPhotoById(photoId);
            if (photo == null) {
                return notFound();
            }
            String tagName = request.body().toString();
            Tag tag = new Tag(tagName);
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
