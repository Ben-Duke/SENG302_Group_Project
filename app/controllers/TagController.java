package controllers;

import accessors.DestinationAccessor;
import accessors.TagAccessor;
import accessors.UserPhotoAccessor;
import models.Destination;
import models.Tag;
import models.User;
import models.UserPhoto;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import java.util.List;
import java.util.Set;

import static play.mvc.Results.*;

public class TagController {

    /**
     * Searches through the database and finds tags which names contain the search query.
     * @param request The http request with a logged in user and a json containing the search
     * @return a Json list of tags that match the search query
     */
    public Result searchTags(Http.Request request) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized();
        }
        String searchQuery = request.body().asJson().get("search").asText();
        Set<Tag> tags = TagAccessor.searchTags(searchQuery);
        if (tags.isEmpty()) {
            return notFound();
        }
        return ok(Json.toJson(tags));
    }

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
            Set<Tag> tags = photo.getTags();
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
            if (!photo.isPublic() && !photo.getUser().equals(user) && !user.userIsAdmin()) {
                return forbidden();
            } else {
                String tagName = request.body().asJson().get("tag").asText();
                if (tagName.isEmpty()) {
                    return badRequest();
                }
                return successfulAddPhotoTag(tagName, photo);
            }
        } else {
            return unauthorized();
        }
    }

    /**
     * Executed only when a photo tag adding is going to be successful.
     * Adds a tag to the photo
     * @param tagName The name of the tag to create or retrieve
     * @param photo the photo to add the tag to
     * @return ok if the tag already exists and created if the tag is new
     */
    private Result successfulAddPhotoTag(String tagName, UserPhoto photo) {
        Tag tag = TagAccessor.getTagByName(tagName);
        boolean exists = tag != null;
        if (!exists) {
            tag = new Tag(tagName);
            TagAccessor.insert(tag);
        }
        if (!photo.addTag(tag)) {
            // Tag is already linked to this photo
            return ok();
        }
        TagAccessor.update(tag);
        UserPhotoAccessor.update(photo);
        if (exists) return ok();
        else return created();
    }

    /**
     * Removes a tag from a photo by tag name
     * @param request a request containing a json like {tag: "tagName"}
     * @param photoId the id of the photo to remove the tag from
     * @return an http response detailing whether the removal was successful or not
     */
    public Result removePhotoTag(Http.Request request, int photoId) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized();
        }
        UserPhoto photo = UserPhotoAccessor.getUserPhotoById(photoId);
        if (photo == null) {
            return notFound("Photo not found");
        }
        if (!photo.isPublic() && !photo.getUser().equals(user) && !user.userIsAdmin()) {
            return forbidden();
        }
        String tagName = request.body().asJson().get("tag").asText();
        Tag tag = TagAccessor.getTagByName(tagName);
        if (tag == null) {
            return notFound("Tag " + tagName + " does not exist");
        }
        photo.removeTag(tag);
        photo.update();
        tag.update();
        return ok();
    }

    public Result getDestTags(Http.Request request, int destId) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized();
        }
        Destination destination = DestinationAccessor.getDestinationById(destId);
        if (destination == null) {
            return notFound();
        }
        if (!destination.getIsPublic()
                && !user.userIsAdmin()
                && user.getUserid() != destination.getUser().getUserid()) {
            return forbidden();
        }
        Set<Tag> tags = destination.getTags();
        return ok(Json.toJson(tags));
    }

    public Result addDestTag(Http.Request request, int destId) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized();
        }
        Destination destination = DestinationAccessor.getDestinationById(destId);
        if (destination == null) {
            return notFound();
        }
        if (!destination.getIsPublic() && !destination.getUser().equals(user) && !user.userIsAdmin()) {
            return forbidden();
        }
        String tagName = request.body().asJson().get("tag").asText();
        if (tagName.isEmpty()) {
            return badRequest();
        }
        Tag tag = TagAccessor.getTagByName(tagName);
        boolean exists = tag != null;
        if (!exists) {
            tag = new Tag(tagName);
            TagAccessor.insert(tag);
        }
        if (!destination.addTag(tag)) {
            // Tag is already linked to this destination
            return ok();
        }
        TagAccessor.update(tag);
        DestinationAccessor.update(destination);
        if (exists) {
            return ok();
        }
        return created();
    }

    public Result removeDestTag(Http.Request request, int destId) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized();
        }
        Destination destination = DestinationAccessor.getDestinationById(destId);
        if (destination == null) {
            return notFound("Photo not found");
        }
        if (!destination.getIsPublic() && !destination.getUser().equals(user) && !user.userIsAdmin()) {
            return forbidden();
        }
        String tagName = request.body().asJson().get("tag").asText();
        Tag tag = TagAccessor.getTagByName(tagName);
        if (tag == null) {
            return notFound("Tag " + tagName + " does not exist");
        }
        destination.removeTag(tag);
        destination.update();
        tag.update();
        return ok();
    }

}
