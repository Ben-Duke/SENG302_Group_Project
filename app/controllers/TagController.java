package controllers;

import accessors.DestinationAccessor;
import accessors.TagAccessor;
import accessors.TripAccessor;
import accessors.UserPhotoAccessor;
import models.*;
import org.slf4j.Logger;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import utilities.UtilityFunctions;
import views.html.users.tag.*;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static play.mvc.Results.*;

public class TagController {

    Logger logger = UtilityFunctions.getLogger();

    /**
     * Displays the tag page for a particular tag
     * @param request The http request, with a logged in user
     * @param tagName the name of the tag to display
     * @return Ok if the user is logged in, otherwise unauthorized
     */
    public Result displayTags(Http.Request request, String tagName) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized();
        }
        return ok(displayTag.render(TagAccessor.getTagByName(tagName), user));
    }

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
        logger.debug("getPhotoTags");
        User user = User.getCurrentUser(request);
        if (user != null) {
            UserPhoto photo = UserPhotoAccessor.getUserPhotoById(photoId);
            if (photo == null) {
                return notFound();
            }
            if (!photo.isPublic()
                    && !user.userIsAdmin()
                    && user.getUserid() != photo.getUser().getUserid()) {
                return forbidden();
            }
            Set<Tag> tags = photo.getTags();
            logger.debug("tags = ", Json.toJson(tags).toString());
            return ok(Json.toJson(tags));
        } else {
            return unauthorized();
        }
    }

    public Result getTagDetails(Http.Request request, int tagId) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized();
        }
        Tag tag = TagAccessor.getTagById(tagId);
        Set<TaggableModel> taggedItems = TagAccessor.findTaggedItems(tag);
        if (!user.userIsAdmin()) {
            Predicate<TaggableModel> accessFilter = item -> item.isPublic() || item.getUser().equals(user);
            taggedItems = taggedItems.stream().
                    filter(accessFilter)
                    .collect(Collectors.toSet());
        }

        return ok(Json.toJson(taggedItems));
    }

    /**
     * Adds a tag to the database but does not assign it to any item.
     * This tag is pending under the user and once it is no longer pending it will be deleted if it not being used.
     * @param request the request containing json of the tag name under attribute tag.
     * @return OK if the tag already exists,
     *         Created if the tag has been created and is now pending
     *         Unauthorized if the user is not logged in
     *         Bad request if the tag name is invalid
     */
    public Result addRawTag(Http.Request request) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized();
        }
        String tagName = request.body().asJson().get("tag").asText();
        if (tagName.isEmpty()) {
            return badRequest();
        }
        Tag tag = TagAccessor.getTagByName(tagName);
        if(tag != null) {
            tag.getPendingUsers().add(user);
            TagAccessor.update(tag);
            return ok();
        }
        tag = new Tag(tagName);
        tag.getPendingUsers().add(user);
        TagAccessor.insert(tag);
        return created();
    }

    /**
     * Get all tags for a given item
     * @param request the request containing the taggableType, the name of the tag and the logged in user.
     * @param taggableId the id of the item that contains tags
     * @return Result from the associated getTags method
     */
    public Result getTaggablesTags(Http.Request request, int taggableId) {
        String taggableType = request.body().asJson().get("taggableType").asText();
        switch (taggableType) {
            case "trip":
                return getTripTags(request,taggableId);
            case "destination":
                return getDestTags(request, taggableId);
            case "photo":
                return getPhotoTags(request, taggableId);
            default:
                return null;
        }
    }
    /**
     * Adds a tag to any taggable item. Request specifies the type of the taggable model
     * @param request contains the session and a json containing the taggableType and tag
     * @param taggableId the id of the item to be tagged
     * @return Result from the associated tag add method
     */
    public Result addTagToTaggable(Http.Request request, int taggableId) {
        String taggableType = request.body().asJson().get("taggableType").asText();
        switch (taggableType){
            case "trip":
                return addTripTag(request, taggableId);
            case "destination":
                return addDestTag(request, taggableId);
            case "photo":
                return addPhotoTag(request, taggableId);
            default:
                return null;
        }
    }

    /**
     * Removes a tag from a taggable item
     * @param request request containing the taggableType and the name of the tag
     * @param taggableId the id of the taggable item
     * @return Result from the associated tag remove method
     */
    public Result removeTagFromTaggable(Http.Request request, int taggableId) {
        String taggableType = request.body().asJson().get("taggableType").asText();
        switch (taggableType){
            case "trip":
                return removeTripTag(request, taggableId);
            case "destination":
                return removeDestTag(request, taggableId);
            case "photo":
                return removePhotoTag(request, taggableId);
            default:
                return null;
        }
    }

    /**
     * Removes the given pending tag from a user and clears all unused tags
     * @param request the request containing the logged in user and the name of the tag to clear
     * @return unauthorized if no user logged in, otherwise ok if successful
     */
    public Result removeUsersPendingTags(Http.Request request) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized();
        }
        String tagName = request.body().asJson().get("tag").asText();
        TagAccessor.removePendingTagsWithName(user.getUserid(), tagName);
        return ok();
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
        TagAccessor.clearUnneededTags();
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

    /**
     *
     * @param request
     * @param destId
     * @return
     */
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
        TagAccessor.clearUnneededTags();
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
    public Result getTripTags(Http.Request request, int tripId){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Trip trip = TripAccessor.getTripById(tripId);
            if (trip == null) {
                return notFound("No trip with that id exists");
            }

            Set<Tag> tags = trip.getTags();

            if (tags == null) {
                return ok("");
            }
            return ok(Json.toJson(tags));
        }

        return unauthorized();

    }

    public Result addTag(Http.Request request, TaggableModel itemToTag) {
        if (itemToTag.getClass() == Destination.class) {
            Destination destination = (Destination) itemToTag;
            return addDestTag(request, destination.getDestId());
        } else if (itemToTag.getClass() == Trip.class) {
            Trip trip = (Trip) itemToTag;
            return addTripTag(request, trip.getTripid());
        } else if (itemToTag.getClass() == UserPhoto.class) {
            UserPhoto userPhoto = (UserPhoto) itemToTag;
            return addPhotoTag(request, userPhoto.getMediaId());
        } else {
            return badRequest();
        }
    }

    /**
     * This function is used to do a request to add a tag to a trip
     * @param request
     * @param tripId

     * @return
     */
    public Result addTripTag(Http.Request request, int tripId) {
        User user = User.getCurrentUser(request);
        if (user != null) {
            Trip trip = TripAccessor.getTripById(tripId);
            if (trip == null) {
                return notFound();
            }
            if (!user.userIsAdmin() && user.getUserid() != trip.getUser().getUserid()){
                return forbidden();
            }
            String tagName = request.body().asJson().get("tag").asText();

            if (tagName.isEmpty()) {
                return badRequest("Tag name cannot be empty");
            }
            Tag tagEbeans = TagAccessor.getTagByName(tagName);

            if (tagEbeans != null) {
                trip.addTag(tagEbeans);
                TripAccessor.update(trip);
                TagAccessor.update(tagEbeans);
                return ok("The tag " + tagName + " appears to already be on this trip");
            }
            Tag tag = new Tag(tagName);
            TagAccessor.insert(tag);
            trip.addTag(tag);
            TripAccessor.update(trip);
            TagAccessor.update(tag);
            return ok("Added new tag " + tagName);
        }

        return unauthorized();
    }

    /**
     * This function is used to do a request to remove a tag to a destination
     * @param request
     * @param tripId
     * @return
     */
    public Result removeTripTag(Http.Request request, int tripId) {
        User user = User.getCurrentUser(request);
        if (user != null) {
            Trip trip = TripAccessor.getTripById(tripId);
            if (trip == null) {
                return notFound();
            }
            if (!user.userIsAdmin() && user.getUserid() != trip.getUser().getUserid()) {
                return forbidden();
            }
            String tagName = request.body().asJson().get("tag").asText();

            if (tagName.isEmpty()) {
                return badRequest("Tag name cannot be empty");
            }
            Tag tagEbeans = TagAccessor.getTagByName(tagName);
            if (tagEbeans == null) {
                return notFound("That tag does not exist");
            }
            if (trip.getTags().contains(tagEbeans)) {
                trip.removeTag(tagEbeans);
                TripAccessor.update(trip);
                TagAccessor.update(tagEbeans);
                TagAccessor.clearUnneededTags();
                return ok("Tag removed from trip");
            }
            else{
                return notFound("Tag was not on this trip");
            }

        }
        return unauthorized("Need to be logged in to perform that action");
    }
}
