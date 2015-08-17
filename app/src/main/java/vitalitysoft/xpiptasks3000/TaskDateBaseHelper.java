package vitalitysoft.xpiptasks3000;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Vitaliy on 29.03.2015.
 */
public class TaskDateBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "XPipTasksDB.db";
    private static final int DATABASE_VERSION = 7;
    public static final String TABLE_TASKS = "Task";
    public static final String TABLE_SUBTASKS = "Subtask";
    private static String SQL_CREATE_Task;
    private static String SQL_DROP_Task;
    private static String SQL_CREATE_Subtask;
    private static String SQL_DROP_Subtask;


    public TaskDateBaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQL_CREATE_Task = context.getString(R.string.CREATE_Task);
        SQL_DROP_Task = context.getString(R.string.DROP_Task);
        SQL_CREATE_Subtask = context.getString(R.string.CREATE_SubTask);
        SQL_DROP_Subtask = context.getString(R.string.DROP_SubTask);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_Task);
        db.execSQL(SQL_CREATE_Subtask);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("LOG_TAG", "База обновляется с " + oldVersion
                + " до новой" + newVersion + ", все что было то сотрется");

        db.execSQL(SQL_DROP_Subtask);
        db.execSQL(SQL_DROP_Task);

        onCreate(db);
    }

}
