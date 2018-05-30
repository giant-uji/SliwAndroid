package es.uji.al259348.sliwandroid.wear.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.uji.al259348.sliwandroid.wear.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoadingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoadingFragment extends Fragment {

    private static final String ARG_MSG = "ARG_MSG";

    private String msg;

    public LoadingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param msg Message to be shown.
     * @return A new instance of fragment LoadingFragment.
     */
    public static LoadingFragment newInstance(String msg) {
        LoadingFragment fragment = new LoadingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MSG, msg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            msg = getArguments().getString(ARG_MSG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_loading, container, false);

        TextView tvMsg = (TextView) rootView.findViewById(R.id.tvMsg);
        tvMsg.setText(msg);

        return rootView;
    }

}
