package factories;

import accessors.*;
import formdata.DestinationFormData;
import models.*;
import org.slf4j.Logger;
import utilities.UtilityFunctions;
import utilities.exceptions.EbeanDateParseException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class to handle interactions with  the database involving the Destination class.
 */
public class DestinationFactory {

    private final Logger logger = UtilityFunctions.getLogger();

    /**
     * Gets a List of all public destinations.
     *
     * @return a List<Destination> of all public Destinations.
     */
    public List<Destination> getPublicDestinations() {
        return Destination.find().query()
                .where().eq("destIsPublic", true).findList();
    }


    public ArrayList<Destination> getMatching(Destination destination){
        ArrayList<Destination> destinations = new ArrayList<Destination>();

        for(Destination dest : Destination.find().query().where().eq("destName", destination.getDestName()).findList()){
            if((dest.getDestName().equals(destination.getDestName())) &&
                    (dest.getDistrict().equals(destination.getDistrict())) &&
                    (dest.getCountry().equals(destination.getCountry()))){
                destinations.add(dest);
            }
        }
        return destinations;
    }

    /**
     * Gets the destination's primary photo
     * @param destID the id of the destination
     * @return the primary photo
     */
    public static UserPhoto getPrimaryPicture(int destID) {
        Destination destination = Destination.find().byId(destID);
        if (destination != null) {
            return destination.getPrimaryAlbum().getPrimaryPhoto();
        }
       return null;
    }

    /**
     * Gets a List of all a users private destinations (excluding their own public
     * destinations).
     *
     * @param userId An int representing the users userId
     *
     * @return a List<Destination> of all the user's private Destinations.
     */
    public List<Destination> getUsersPrivateDestinations(int userId) {
        User user = UserFactory.getUserFromId(userId);

        List<Destination> privateDestinations = Destination.find().query()
                .where().eq("user", user).and().eq("destIsPublic", false)
                .findList();

        return privateDestinations;
    }

    /**
     * Checks if a destination is already a private destination of a User.
     *
     * Only checks a users destinations that are private (not just owned ones).
     *
     * @param userId An int representing the User's userId.
     * @param newPrivateDestination The new Destination to check.
     * @return A boolean, true if the User does have an equal private Destination,
     *         false otherwise.
     */
    public boolean userHasPrivateDestination(int userId, Destination newPrivateDestination) {
        List<Destination> privateDestinations;
        privateDestinations = this.getUsersPrivateDestinations(userId);

        for (Destination existingDestination: privateDestinations) {
            if (newPrivateDestination.equals(existingDestination)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if a destination is already a public destination.
     *
     * Only checks public destinations (including the logged in users public destinations).
     *
     * @param destination The new Destination to check.
     * @return A boolean, true if an equivalent public destination exists, false
     * otherwise.
     */
    public boolean doesPublicDestinationExist(Destination destination) {
        List<Destination> allDestinations = this.getPublicDestinations();

        for (Destination existingDestination: allDestinations) {
            if (destination.equals(existingDestination)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return a DestinationFormData instance constructed from a destination instance
     *
     * @param dest The destination instance being converted
     * @return The FormData corresponding to the destination specified
     */
    public DestinationFormData makeDestinationFormData(Destination dest) {
        return new DestinationFormData(dest.getDestName(), dest.getDestType(),
                dest.getDistrict(), dest.getCountry(), dest.getLatitude(),
                dest.getLongitude(), null);
    }

    /**
     * Finds the list of all destinations from other users that are private and match the given destination
     * @param userId the user's own ID
     * @param destination the destination to check if there are matches to
     * @return The list of destinations from other users that match the given destination.
     */
    public List<Destination> getOtherUsersMatchingPrivateDestinations(int userId, Destination destination) {
        User user = UserFactory.getUserFromId(userId);
        // Get all destinations that are private and belong to another user
        List<Destination> allDestinations = Destination.find().query()
                .where().eq("destIsPublic", false).and()
                .not().eq("user", user).findList();

        List<Destination> matchingDestinations = new ArrayList<>();
        for (Destination existingDestination : allDestinations) {
            if (destination.equals(existingDestination)) {

                matchingDestinations.add(existingDestination);
            }
        }
        return matchingDestinations;
    }


    /**
     * Remove the private photos from a list of photos so that the list can be displayed publicly on a destination
     * Keeping the private content that the viewer owns
     * @param userPhotos the list of photos to display
     * @param viewerId the user Id of the person viewing the photos
     */
    public void removePrivatePhotos(List<UserPhoto> userPhotos, Integer viewerId) {
        ArrayList<UserPhoto> photosToRemove = new ArrayList<>();
        for (UserPhoto photo : userPhotos) {
            if(!photo.getIsPublic() && photo.getUser().getUserid() != viewerId) {
                photosToRemove.add(photo);
            }
        }
        userPhotos.removeAll(photosToRemove);
    }

    public void removePrivateMedia(List<Media> media, Integer viewerId) {
        ArrayList<Media> photosToRemove = new ArrayList<Media>();
        for (Media med : media) {
            if(!med.getIsPublic() && med.getUser().getUserid() != viewerId) {
                photosToRemove.add(med);
            }
        }
        media.removeAll(photosToRemove);
    }

    public Set<Tag> changedTags(Destination newDestination, int oldDestinationId) {
        Destination oldDestination = DestinationAccessor.getDestinationById(oldDestinationId);
        if (oldDestination.getTags() != newDestination.getTags()) {
            Set<Tag> newSet = new HashSet<>();
            newSet.addAll(newDestination.getTags());
            newSet.addAll(oldDestination.getTags());
            return newSet;
        }
        return oldDestination.getTags();
    }

    /**
     * Returns a list of all public destinations and all private destinations that the user can see
     * @param userId the user accessing the destinations
     * @return the list of all destinations that the user is authorized to see
     */
    public List<Destination> getAllVisibleDestinations(int userId) {
        User user = UserFactory.getUserFromId(userId);
        if (user.userIsAdmin()) {
            return Destination.find().all();
        }

        List<Destination> visibleDestinations = new ArrayList<>();
        visibleDestinations.addAll(getPublicDestinations());
        visibleDestinations.addAll(getUsersPrivateDestinations(userId));

        return visibleDestinations;
    }

    /**
     * Move the photos from one destination to being linked to another
     * @param destinationOne the original destination which will no longer hold photos
     * @param destinationTwo the new destination which will hold new photos
     */
    private void movePhotosToAnotherDestination(Destination destinationOne, Destination destinationTwo) {

        while(!AlbumAccessor.getAlbumsByOwner(destinationOne).get(0).getMedia().isEmpty()) {
            Media changingPhoto = AlbumAccessor.getAlbumsByOwner(destinationOne).get(0).getMedia().get(0);

            AlbumAccessor.getAlbumsByOwner(destinationOne).get(0).removeMedia(changingPhoto);
            changingPhoto.removeAlbum(AlbumAccessor.getAlbumsByOwner(destinationOne).get(0));
            MediaAccessor.update(changingPhoto);

            if (!AlbumAccessor.getAlbumsByOwner(destinationTwo).get(0).getMedia().contains(changingPhoto)) {

                AlbumAccessor.getAlbumsByOwner(destinationTwo).get(0).addMedia(changingPhoto);
                changingPhoto.addAlbum(AlbumAccessor.getAlbumsByOwner(destinationTwo).get(0));
                MediaAccessor.update(changingPhoto);

            }
        }



    }

    /**
     * Move the visits from one destination to being linked to another
     * @param destinationOne the original destination which will no longer hold visits
     * @param destinationTwo the new destination which will hold new visits
     */
    private void moveVisitsToAnotherDestination(Destination destinationOne, Destination destinationTwo){
        List<Visit> visitsFrom = Visit.find().query().where().eq("destination", destinationOne).findList();
        for(Visit visit : visitsFrom) {
            visit.delete();
            //Note: Update this if new attributes are ever added to visit
            Visit newVisit = new Visit(visit.getArrival(), visit.getDeparture(), visit.getTrip(), destinationTwo, visit.getVisitOrder());
            newVisit.save();
            try {
                visit.update();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Move the visits from one destination to being linked to another
     * @param destinationOne the original destination which will no longer hold visits
     * @param destinationTwo the new destination which will hold new visits
     */
    private void moveTagsToAnotherDestination(Destination destinationOne, Destination destinationTwo){
        Set<Tag> tagForm = destinationOne.getTags();
        if (!tagForm.isEmpty()) {
            for(Tag tag : tagForm) {
                destinationOne.removeTag(tag);
                DestinationAccessor.update(destinationOne);

                destinationTwo.addTag(tag);
                DestinationAccessor.update(destinationTwo);
                try {
                    tag.update();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Move the visits from one destination to being linked to another
     * @param destinationOne the original destination which will no longer hold visits
     * @param destinationTwo the new destination which will hold new visits
     */
    private void moveTreasureToAnotherDestination(Destination destinationOne, Destination destinationTwo){
        List<TreasureHunt> treasurehunts = TreasureHuntAccessor.getAllByDestination(destinationOne);
        if (!treasurehunts.isEmpty()) {
            for(TreasureHunt treasureHunt : treasurehunts) {
                TreasureHuntAccessor.delete(treasureHunt);
                destinationOne.update();
                TreasureHunt newTreasureHunt = null;
                newTreasureHunt = new TreasureHunt(treasureHunt.getTitle(),
                        treasureHunt.getRiddle(), treasureHunt.getDestination(),
                        treasureHunt.getStartDate(), treasureHunt.getEndDate(),
                        destinationTwo.getUser());
                newTreasureHunt.save();
                TreasureHuntAccessor.insert(newTreasureHunt);
                try {
                    destinationTwo.update();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public void editDestinationMerge(Destination publicDestination, Destination destination) {
        if(publicDestination.getUser() != destination.getUser()) {
            try {
                moveVisitsToAnotherDestination(destination, publicDestination);
                moveTagsToAnotherDestination(destination, publicDestination);
                moveTreasureToAnotherDestination(destination, publicDestination);
                destination.setVisits(new ArrayList<>());
                movePhotosToAnotherDestination(destination, publicDestination);
                AlbumAccessor.delete(AlbumAccessor.getAlbumsByOwner(destination).get(0));
                DestinationAccessor.delete(destination);
            } catch (Exception e) {
                logger.error("merge destinations 2", e);
            }
        }
    }



        /**
         * Merges all matching destination when one private destination is made public, will not merge if destination is used in trip
         * @param destinationList list of all matching private destinations
         * @param destination destination of user making private destination public
         */
    public void mergeDestinations(List<Destination> destinationList, Destination destination) {
        Admin defaultAdmin = Admin.find().query().where().eq("isDefault", true).findOne();
        User defaultAdminUser = User.find().query().where().eq("userid", defaultAdmin.getUserId()).findOne();
        destinationList.add(destination);
        for (Destination otherDestination : destinationList) {
            if(otherDestination.getUser() != destination.getUser()) {
                moveVisitsToAnotherDestination(otherDestination, destination);
                moveTagsToAnotherDestination(otherDestination, destination);
                moveTreasureToAnotherDestination(otherDestination, destination);
                otherDestination.setVisits(new ArrayList<>());
                try {
                    DestinationAccessor.update(otherDestination);
                } catch (Exception e) {
                    logger.error("merge destinations 1", e);
                }
                movePhotosToAnotherDestination(otherDestination, destination);
                try {
                    AlbumAccessor.delete(otherDestination.getPrimaryAlbum());
                    DestinationAccessor.delete(otherDestination);
                } catch (Exception e) {
                    logger.error("merge destinations 2", e);
                }
                try {
                    DestinationAccessor.update(otherDestination);
                } catch (Exception e) {
                    logger.error("merge destinations 3", e);
                }
            }
        }
        destination.setIsPublic(true);
        destination.setUser(defaultAdminUser);
        try {
            destination.update();
        } catch (Exception e) {
            logger.error("merge destination 4", e);
        }
    }
}
