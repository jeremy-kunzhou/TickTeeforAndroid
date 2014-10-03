package com.huhukun.tickteeforandroid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.huhukun.utils.FormatHelper;

import java.util.Calendar;
import java.util.concurrent.Executors;

/**
 * Created by kun on 8/09/2014.
 */
public class ProjectSharingFragment extends Fragment {

    public static final String ARG_SHARING_ID = "sharing_id";
    private static final String TAG = App_Constants.APP_TAG + "ProjectSharingFragment";
    private String sharingString = "";
    private EditText etSharing;
    Button btFacebook;
    Button btTwitter;
    Button btWeibo;
    Button btWeixin;
    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        if (getArguments() != null && getArguments().containsKey(ARG_SHARING_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            sharingString = getArguments().getString(ARG_SHARING_ID);


        }

    }

    private View.OnClickListener sharingListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.project_sharing_bt_twitter:
                    break;
                default:
                    Toast.makeText(getActivity(), "Support soon.", Toast.LENGTH_SHORT);
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_project_sharing, container, false);
        etSharing = (EditText) rootView.findViewById(R.id.project_sharing_content);
        btFacebook = (Button) rootView.findViewById(R.id.project_sharing_bt_facebook);
        btFacebook.setOnClickListener(sharingListener);
        btTwitter = (Button) rootView.findViewById(R.id.project_sharing_bt_twitter);
        btTwitter.setOnClickListener(sharingListener);
        btWeibo = (Button) rootView.findViewById(R.id.project_sharing_bt_weibo);
        btWeibo.setOnClickListener(sharingListener);
        btWeixin = (Button) rootView.findViewById(R.id.project_sharing_bt_weixin);
        btWeixin.setOnClickListener(sharingListener);

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        etSharing.setText(sharingString);
    }

}
