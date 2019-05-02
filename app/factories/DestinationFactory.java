package factories;

import models.Destination;
import models.User;
import models.UserPhoto;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to handle interactions with  the database involving the Destination class.
 */
public class DestinationFactory {

    /**
     * Gets a List of all public destinations.
     *
     * @return a List<Destination> of all public Destinations.
     */
    public List<Destination> getPublicDestinations() {
        List<Destination> allPublicDestinations = Destination.find.query()
                .where().eq("isPublic", true).findList();

        return allPublicDestinations;
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

        List<Destination> privateDestinations = Destination.find.query()
                .where().eq("user", user).and().eq("isPublic", false)
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
     * Finds the list of all destinations from other users that are private and match the given destination
     * @param userId the user's own ID
     * @param destination the destination to check if there are matches to
     * @return The list of destinations from other users that match the given destination.
     */
    public List<Destination> getOtherUsersMatchingPrivateDestinations(int userId, Destination destination) {
        User user = UserFactory.getUserFromId(userId);
        List<Destination> allDestinations = Destination.find.query()
                .where().eq("isPublic", false).and()
                .not().eq("user", user).findList();
        List<Destination> matchingDestinations = new ArrayList<>();
        int count = 0;
        for (Destination existingDestination : allDestinations) {
            if (destination.equals(existingDestination)) {
                matchingDestinations.add(destination);
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
        ArrayList<UserPhoto> photosToRemove = new ArrayList<UserPhoto>();
        for (UserPhoto photo : userPhotos) {
            if(!photo.isPublic && photo.getUser().getUserid() != viewerId) {
                photosToRemove.add(photo);
            }
        }
        userPhotos.removeAll(photosToRemove);
    }
}