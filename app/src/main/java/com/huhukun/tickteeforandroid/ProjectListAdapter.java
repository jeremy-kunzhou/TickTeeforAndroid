package com.huhukun.tickteeforandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.huhukun.tickteeforandroid.model.Project;

import java.util.List;

/**
 * Created by kun on 18/08/2014.
 */
public class ProjectListAdapter extends ArrayAdapter<Project> {

    List<Project> values;

    public ProjectListAdapter(Context context, int resource, List<Project> values)
    {
        super(context, resource, values);
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.row_project_list, null);

        }

        Project p = values.get(position);

        if (p != null) {

            TextView tt = (TextView) v.findViewById(R.id.row_project_title);


            if (tt != null) {
                tt.setText(p.getName());
            }

        }

        return v;
    }
}
