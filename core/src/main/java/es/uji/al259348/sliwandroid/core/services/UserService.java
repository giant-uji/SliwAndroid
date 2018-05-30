package es.uji.al259348.sliwandroid.core.services;

import es.uji.al259348.sliwandroid.core.model.Config;
import es.uji.al259348.sliwandroid.core.model.User;
import rx.Observable;

public interface UserService extends Service {

    /**
     * Get the current linked user.
     *
     * @return Returns the current linked user or null if there isn't.
     */
    User getCurrentLinkedUser();

    /**
     * Set the current linked user. It will be persisted.
     *
     * @param user The user to be linked or null to unlink the current user.
     * @return Returns true if the new value were successfully written.
     */
    boolean setCurrentLinkedUser(User user);

    /**
     * Get the current linked user.
     *
     * @return Returns the current linked user id or and empty string if there isn't.
     */
    String getCurrentLinkedUserId();

    Observable<User> getUserLinkedTo(String deviceId);
    Observable<Void> configureUser(User user, Config config);

}
