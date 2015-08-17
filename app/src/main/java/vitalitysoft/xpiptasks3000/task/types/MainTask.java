package vitalitysoft.xpiptasks3000.task.types;


import android.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vitaliy on 29.03.2015.
 */
public class MainTask {
    public String text;
    //public int state;
    public int id;
    public List<SubTask> subTasks;

    //public ArrayList<SubTask> subTasks;

    public MainTask(int id, String text) {
        this.text = text;
        this.id = id;
        // this.state = state;

        this.subTasks = new ArrayList<SubTask>();
    }

    public MainTask(int id, String text,  List<SubTask> subTasks) {
        this.text = text;
        this.id = id;
        // this.state = state;

        this.subTasks = subTasks;
    }

    public int GetState() {
        //
        if (subTasks==null) {
            return TaskState.IN_PROGRESS;
        }

        if (subTasks.size() == 0){
            return TaskState.IN_PROGRESS;
        }
        // если хотя бы 1 задача не выполненна то вся основная задача в процессе
        for (int i = 0; i < subTasks.size(); i++) {
            if (subTasks.get(i).state == TaskState.IN_PROGRESS){
                return TaskState.IN_PROGRESS;
            }
        }
        return TaskState.DONE;
    }

    public void SetSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }
}