package vitalitysoft.xpiptasks3000;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import vitalitysoft.xpiptasks3000.task.types.SubTask;
import vitalitysoft.xpiptasks3000.task.types.TaskState;

/**
 * Created by Vitaliy on 04.05.2015.
 */
public class SubTaskArrayAdapter extends ArrayAdapter<SubTask> {

    public SubTaskArrayAdapter(Context context, List<SubTask> mainTasks) {
        super(context, R.layout.maintask_list_item, mainTasks);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SubTask subTask = getItem(position);
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.maintask_list_item, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.mainTaskTextView);
        textView.setText(subTask.text);

        // here is styling
        if (subTask.state == TaskState.DONE){
            textView.setTextColor(Color.parseColor("#900000"));
        }


        return rowView;
    }



}

