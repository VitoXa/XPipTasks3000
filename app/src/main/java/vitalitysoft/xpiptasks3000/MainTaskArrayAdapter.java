package vitalitysoft.xpiptasks3000;

/**
 * Created by Vitaliy on 17.08.2015.
 */


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import vitalitysoft.xpiptasks3000.task.types.MainTask;
import vitalitysoft.xpiptasks3000.task.types.TaskState;

/**
 * Created by Vitaliy on 06.04.2015.
 */
public class MainTaskArrayAdapter extends ArrayAdapter<MainTask>{
    private int selectedId  = -1;
    private int activeId    = -1;

    public MainTaskArrayAdapter(Context context, List<MainTask> mainTasks) {
        super(context,R.layout.maintask_list_item , mainTasks);

    }
    public void SetSelectedId(int id){
        this.selectedId = id;
    }

    public void SetActive(int id){
        this.activeId = id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        MainTask mainTask = getItem(position);
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.maintask_list_item, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.mainTaskTextView);
        textView.setText(mainTask.text);

        if (mainTask.id == selectedId){
            ((CheckBox)rowView.findViewById(R.id.mainTaskActiveBox)).setChecked(true);
        }

        return rowView;
    }
}
