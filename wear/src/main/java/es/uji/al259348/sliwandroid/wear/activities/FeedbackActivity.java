package es.uji.al259348.sliwandroid.wear.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;

import java.util.List;

import es.uji.al259348.sliwandroid.core.model.Location;
import es.uji.al259348.sliwandroid.core.model.User;
import es.uji.al259348.sliwandroid.core.services.UserService;
import es.uji.al259348.sliwandroid.core.services.UserServiceImpl;
import es.uji.al259348.sliwandroid.wear.R;
import es.uji.al259348.sliwandroid.wear.fragments.FeedbackFragment;

public class FeedbackActivity extends Activity implements FeedbackFragment.OnFragmentInteractionListener {

    private View fragmentContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        UserService userService = new UserServiceImpl(this);
        User user = userService.getCurrentLinkedUser();
        if (user == null) {
            Intent i = getIntent();
            setResult(RESULT_CANCELED, i);
            finish();
        }
        List<Location> locations = user.getLocations();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                fragmentContent = stub.findViewById(R.id.fragmentContent);
                setFragment(FeedbackFragment.newInstance(locations));
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(fragmentContent.getId(), fragment);
        transaction.commit();
    }

    @Override
    public void onLocationSelected(String location) {
        Intent i = getIntent();
        i.putExtra("location", location);
        setResult(RESULT_OK, i);
        finish();
    }
}
