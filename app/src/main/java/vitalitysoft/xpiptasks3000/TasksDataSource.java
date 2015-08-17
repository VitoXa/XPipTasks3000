package vitalitysoft.xpiptasks3000;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import vitalitysoft.xpiptasks3000.task.types.MainTask;
import vitalitysoft.xpiptasks3000.task.types.SubTask;
import vitalitysoft.xpiptasks3000.task.types.TaskState;

/**
 * Created by Vitaliy on 30.03.2015.
 */
public class TasksDataSource {
    private SQLiteDatabase database;
    private TaskDateBaseHelper dbHelper;

    public TasksDataSource(Context context){
        dbHelper = new TaskDateBaseHelper(context);
    }
    public void open(){
        database = dbHelper.getWritableDatabase();
    }
    public void close(){
        dbHelper.close();
    }




    public MainTask CreateMainTask(String taskText) {
        ContentValues values = new ContentValues();
        values.put("Text", taskText);
        values.put("State", TaskState.IN_PROGRESS);

        long insertId = database.insert(TaskDateBaseHelper.TABLE_TASKS, null,
                values);
        Cursor cursor = database.query(TaskDateBaseHelper.TABLE_TASKS,
                null, "id" + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        MainTask newTask = cursorToMainTask(cursor);
        cursor.close();
        return newTask;
    }
    public SubTask CreateSubTask(String text, int mainTaskID) {
        ContentValues values = new ContentValues();
        values.put("Text", text);
        values.put("Task_id", mainTaskID);
        long insertId = database.insert(TaskDateBaseHelper.TABLE_SUBTASKS, null,
                values);
        Cursor cursor = database.query(TaskDateBaseHelper.TABLE_SUBTASKS,
                null, "id" + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        SubTask newSubTask = cursorToSubTask(cursor);
        cursor.close();
        return newSubTask;
    }

    public boolean SetMainTaskState(int MainTaskID, int state) {
        ContentValues args = new ContentValues();
        args.put("State", state);
        return database.update(TaskDateBaseHelper.TABLE_TASKS, args, "id="+ MainTaskID,null) > 0;
    }

    public void deleteComment(MainTask task) {
        long id = task.id;
        System.out.println("Comment deleted with id: " + id);
        database.delete(dbHelper.TABLE_TASKS, "id = " + id, null);
    }

    public List<MainTask> getAllMainTasks() {
        List<MainTask> mainTasks = new ArrayList<MainTask>();

        Cursor cursor = database.query(dbHelper.TABLE_TASKS, null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MainTask task = cursorToMainTask(cursor);
            mainTasks.add(task);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return mainTasks;
    }



    public void DeleteMainTask(MainTask task) {
        long id = task.id;
        System.out.println("Comment deleted with id: " + id);
        database.delete(dbHelper.TABLE_TASKS, "id = " + id, null);

    }

    public List<SubTask> GetSubTasksForMainTask(int mainTaskId) {
        List<SubTask> subTasks = new ArrayList<SubTask>();

        Integer ID = new Integer(mainTaskId); // сделано для корректности запроса
        Cursor cursor = database.query(dbHelper.TABLE_SUBTASKS, null, "Task_id=?",
                new String[]{ID.toString()}, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            SubTask task = cursorToSubTask(cursor);
            subTasks.add(task);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return subTasks;
    }

    private SubTask cursorToSubTask(Cursor cursor) {
        SubTask subTask = new SubTask(cursor.getInt(0), cursor.getInt(1), cursor.getString(2),
                cursor.getInt(3));
        return subTask;
    }
    private MainTask cursorToMainTask(Cursor cursor) {
        MainTask task = new MainTask(cursor.getInt(0), cursor.getString(1));
        List<SubTask> subTasks = GetSubTasksForMainTask(task.id);
        task.SetSubTasks(subTasks);
        return task;
    }
/*
    public boolean SelectMainTask(int mainTaskID, boolean b) {
        ContentValues args = new ContentValues();
        int selected = 0;
        if (b) selected = 1;
        args.put("Selected", selected);
        return database.update(TaskDateBaseHelper.TABLE_TASKS, args, "id="+ mainTaskID, null) > 0;
    }
*/

    public boolean SetSubTaskDone(SubTask subTask) {
        ContentValues args = new ContentValues();
        args.put("State", TaskState.DONE);
        return database.update(TaskDateBaseHelper.TABLE_SUBTASKS, args, "id="+ subTask.id, null) > 0;
    }

    public void UpdateMainTask(MainTask task) {
        long id = task.id;
        ContentValues values = new ContentValues();
        values.put("Text", task.text);
        values.put("State", task.GetState());
        database.update(dbHelper.TABLE_TASKS, values, "id = " + id, null);

    }

    public void UpdateSubTask(SubTask subTask) {
        long id = subTask.id;
        ContentValues values = new ContentValues();
        values.put("Text", subTask.text);
        values.put("State", subTask.state);
        database.update(dbHelper.TABLE_SUBTASKS, values, "id = " + id, null);
    }

    public void DeleteSubTask(SubTask subTask) {
        long id = subTask.id;
        database.delete(dbHelper.TABLE_SUBTASKS, "id = " + id, null);
    }
}
