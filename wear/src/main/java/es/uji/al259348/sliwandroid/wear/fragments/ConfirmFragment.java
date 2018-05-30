package es.uji.al259348.sliwandroid.wear.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import es.uji.al259348.sliwandroid.wear.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConfirmFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConfirmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfirmFragment extends Fragment {

    private static final String ARG_MSG = "ARG_MSG";
    private static final String ARG_BTN_TEXT = "ARG_BTN_TEXT";

    private String msg;
    private String btnText;

    private OnFragmentInteractionListener mListener;

    public ConfirmFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param msg Message to be shown.
     * @param btnText Text for the confirm button.
     * @return A new instance of fragment ConfirmFragment.
     */
    public static ConfirmFragment newInstance(String msg, String btnText) {
        ConfirmFragment fragment = new ConfirmFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MSG, msg);
        args.putString(ARG_BTN_TEXT, btnText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            msg = getArguments().getString(ARG_MSG);
            btnText = getArguments().getString(ARG_BTN_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_confirm, container, false);

        TextView tvMsg = (TextView) rootView.findViewById(R.id.tvMsg);
        tvMsg.setText(msg);

        Button btnOk = (Button) rootView.findViewById(R.id.btnOk);
        btnOk.setText(btnText);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onConfirm();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onConfirm();
    }
}
