package controllers;

import accessors.TagAccessor;
import accessors.TripAccessor;
import accessors.UserPhotoAccessor;
import models.Tag;
import models.Trip;
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


    /**
     * This Function will get all tags on a trip.
     * If the user is logged in then will return the tags that are on a destination,
     * will return not found if a trip with the requested id does not exist.
     * @param request
     * @param tripId
     * @return
     */
    public Result getTags(Http.Request request, int tripId){
        User user = User.getCurrentUser(request);
        if(user != null){
            Trip trip = TripAccessor.getTripById(tripId);
            if(trip == null){
                return notFound("No trip with that id exists");
            }

            Set<Tag> tags = trip.getTags();

            if(tags == null){
                return ok("");
            }
            return ok(Json.toJson(tags));
        }

        return unauthorized();

    }

    /**
     * This function is used to do a request to add tag to a destination
     * @param request
     * @param tripId
     * @param newTag
     * @return
     */
    public Result addTripTag(Http.Request request, int tripId, String newTag){
        User user = User.getCurrentUser(request);
        if(user != null) {
            Trip trip = TripAccessor.getTripById(tripId);
            if(trip == null){
                return notFound();
            }
            if(!user.userIsAdmin() && user.getUserid() != trip.getUser().getUserid()){
                return forbidden();
            }
            Tag tag = new Tag(newTag);
            if(trip.addTag(tag)){
                return ok("Added new tag " + newTag);
            }else{
                return ok("The tag " + newTag + " appears to already be on this trip");
            }

        }

        return unauthorized();
    }

}
