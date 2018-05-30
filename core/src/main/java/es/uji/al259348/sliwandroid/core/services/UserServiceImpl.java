package es.uji.al259348.sliwandroid.core.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;

import es.uji.al259348.sliwandroid.core.model.Config;
import es.uji.al259348.sliwandroid.core.model.User;
import rx.Observable;
import rx.schedulers.Schedulers;

public class UserServiceImpl extends AbstractService implements UserService {

    private static final String SHARED_PREFERENCES_NAME = "UserServiceSharedPreferences";
    private static final String SHARED_PREFERENCES_KEY_USER = "user";

    private static final String MESSAGING_CONFIGURE_REQUEST_TOPIC = "users/%s/configure";
    private static final String MESSAGING_REGISTER_RESPONSE_OK = "200 OK";

    private MessagingService messagingService;

    private ObjectMapper objectMapper;
    private SharedPreferences sharedPreferences;

    public UserServiceImpl(Context context) {
        super(context);
        this.messagingService = new MessagingServiceImpl(context);
        this.objectMapper = new ObjectMapper();
        this.sharedPreferences = getSharedPreferences();
    }

    public UserServiceImpl(Context context, MessagingService messagingService) {
        super(context);
        this.messagingService = messagingService;
        this.objectMapper = new ObjectMapper();
        this.sharedPreferences = getSharedPreferences();
    }

    private SharedPreferences getSharedPreferences() {
        return getContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public User getCurrentLinkedUser() {
        User res = null;

        if (sharedPreferences.contains(SHARED_PREFERENCES_KEY_USER)) {
            String value = sharedPreferences.getString(SHARED_PREFERENCES_KEY_USER, "");
            if (!value.isEmpty()) {
                try {
                    res = objectMapper.readValue(value, User.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return res;
    }

    @Override
    public boolean setCurrentLinkedUser(User user) {

        String value = null;

        if (user != null) {
            try {
                value = objectMapper.writeValueAsString(user);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return false;
            }
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_KEY_USER, value);
        return editor.commit();
    }

    @Override
    public String getCurrentLinkedUserId() {
        User user = getCurrentLinkedUser();
        return (user == null) ? "" : user.getId();
    }

    @Override
    public Observable<User> getUserLinkedTo(final String deviceId) {
        return Observable.create(subscriber -> {
            String topic = "devices/" + deviceId + "/user";
            String msg = (new Date()).toString();

            messagingService.request(topic, msg)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.newThread())
                    .subscribe(s -> {
                        try {
                            User user = objectMapper.readValue(s, User.class);
                            subscriber.onNext(user);
                        } catch (IOException e) {
                            subscriber.onError(new Throwable(s));
                        }
                    }, subscriber::onError);
        });
    }

    @Override
    public Observable<Void> configureUser(User user, Config config) {
        return Observable.create(subscriber -> {

            try {
                String msg = objectMapper.writeValueAsString(config.getSamples());

                String topic = String.format(MESSAGING_CONFIGURE_REQUEST_TOPIC, user.getId());
                messagingService.request(topic, msg)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(Schedulers.newThread())
                        .subscribe(response -> {
                            if (response.equals(MESSAGING_REGISTER_RESPONSE_OK)) {
                                subscriber.onCompleted();
                            } else {
                                subscriber.onError(new Throwable(response));
                            }
                        }, subscriber::onError);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        });
    }

}
