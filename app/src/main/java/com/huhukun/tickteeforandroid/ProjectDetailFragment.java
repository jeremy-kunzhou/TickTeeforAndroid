package com.huhukun.tickteeforandroid;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.huhukun.tickteeforandroid.model.Project;
import com.huhukun.tickteeforandroid.model.ProjectsManagementImpl;
import com.huhukun.utils.FormatHelper;

import java.text.DateFormat;

/**
 * A fragment representing a single Project detail screen.
 * This fragment is either contained in a {@link ProjectListActivity}
 * in two-pane mode (on tablets) or a {@link ProjectDetailActivity}
 * on handsets.
 */
public class ProjectDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Project mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProjectDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = ProjectsManagementImpl.getInstance().getProjectById(Long.parseLong(getArguments().getString(ARG_ITEM_ID)));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_project_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.project_detail_name)).setText(mItem.getName());
            ((TextView) rootView.findViewById(R.id.project_detail_start_at)).setText(FormatHelper.shortDateFormat.format(mItem.getStartDate()));
            ((TextView) rootView.findViewById(R.id.project_detail_end_at)).setText(FormatHelper.shortDateFormat.format(mItem.getEndDate()));
            ((TextView) rootView.findViewById(R.id.project_detail_expected_progress)).setText(mItem.getExpectedProgress().toPlainString());
            ((TextView) rootView.findViewById(R.id.project_detail_current_progress)).setText(mItem.getCurrentProgress().toPlainString());
            ((TextView) rootView.findViewById(R.id.project_detail_created_at)).setText(FormatHelper.shortDateTimeFormat.format(mItem.getCreatedTime()));
            ((TextView) rootView.findViewById(R.id.project_detail_last_update_at)).setText(FormatHelper.shortDateTimeFormat.format(mItem.getLastUpdateTime()));
            ((TextView) rootView.findViewById(R.id.project_detail_description)).setText(mItem.getDescription());
        }

        return rootView;
    }
}
