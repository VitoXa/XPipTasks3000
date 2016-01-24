package vitalitysoft.xpiptasks3000;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import vitalitysoft.xpiptasks3000.dialogs.MainTaskDialog;
import vitalitysoft.xpiptasks3000.dialogs.SubTaskDialog;
import vitalitysoft.xpiptasks3000.task.types.MainTask;
import vitalitysoft.xpiptasks3000.task.types.SubTask;
import vitalitysoft.xpiptasks3000.task.types.TaskState;


public class MainActivity extends ActionBarActivity {

    // это будет именем файла настроек
    public static final String APP_PREFERENCES = "mysettings";

    public static final String ACTIVE_MAIN_TASK_ID = "ACTIVE_MAIN_TASK_ID";
    public static final String SELECTED_MAIN_TASK_ID = "SELECTED_MAIN_TASK_ID";

    private SharedPreferences mSettings;
    private TasksDataSource dataSource;
    private List<MainTask> mainTasks;
    private List<SubTask> subTasks;

    MainTaskArrayAdapter mainTaskAdapter;
    MainTaskDialog mainTaskDialog;

    SubTaskArrayAdapter subTaskAdapter;
    SubTaskDialog subTaskDialog;

    // задача для которой идет просмотр подзадач
    private int selectedMainTaskId = -1;

    // задача выполнение которой идет
    //private int activeMainTaskId = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        // загрузили данные о последних выбранных задачах
        LoadState();

        dataSource = new TasksDataSource(this);
        dataSource.open();

        // получаем данные из БД
        mainTasks = dataSource.getAllMainTasks();
        // эти подзадачи будут показанныsubTasks = new ArrayList<SubTask>();
        // выбрать подзадачи выбранной задачи
        for (int i = 0; i < mainTasks.size(); i++) {
            MainTask mainTask = mainTasks.get(i);
            if (mainTask.id == selectedMainTaskId) {
                subTasks = mainTask.subTasks;
            }
        }
        // если в бд совсем ничего нету
        if(mainTasks.size() == 0) {
            subTasks = new ArrayList<SubTask>();
        }

        // получить данные о последней выбранной задаче

        // бла бла бла

        // =========================================================================================
        // инициализация списка подзадач

        subTaskAdapter = new SubTaskArrayAdapter(this, subTasks);


        final ListView subTasksListView = (ListView) findViewById(R.id.SubTasksListView);

        subTasksListView.setAdapter(subTaskAdapter);
        subTasksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SubTaskArrayAdapter adapter = ((SubTaskArrayAdapter) parent.getAdapter());
                SubTask selectedSubTask = (adapter).getItem(position);

                if (GetEditorState()) {
                    subTaskDialog = new SubTaskDialog(view.getContext(), selectedSubTask, selectedMainTaskId);
                    Button save = (Button) subTaskDialog.findViewById(R.id.SubTaskDialog_SaveButton);
                    save.setOnClickListener(onSaveSubTaskDialogClick);
                    Button delete = (Button) subTaskDialog.findViewById(R.id.SubTaskDialog_delete);
                    delete.setOnClickListener(onDeleteSubTaskDialogClick);
                    subTaskDialog.show();
                }
            }
        });
        subTasksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SubTaskArrayAdapter adapter = ((SubTaskArrayAdapter) parent.getAdapter());
                SubTask subTask = (adapter).getItem(position);
                dataSource.SetSubTaskDone(subTask);

                subTask.state =  TaskState.DONE;
                adapter.notifyDataSetChanged();

                /*
                //subTask.state = TaskState.DONE;
                int mpos = GetMainTaskPositionById(selectedMainTaskId);

                for (int i = 0; i < mainTasks.get(mpos).subTasks.size(); i++){
                    if (mainTasks.get(mpos).subTasks.get(i).id == subTask.id){
                        mainTasks.get(mpos).subTasks.get(i).state = TaskState.DONE;
                    }
                }

                SelectMainTaskById(selectedMainTaskId);
                */


                return false;
            }
        });


        // =========================================================================================
        // инициализация списка основных задач
        mainTaskAdapter = new MainTaskArrayAdapter(this, mainTasks);
        // они отметятся как выделенные 1 раз при 1-вой отрисовке

        mainTaskAdapter.SetSelectedId(selectedMainTaskId);
        //mainTaskAdapter.SetActive(activeMainTaskId);

        final ListView mainTasksListView = (ListView) findViewById(R.id.mainTasksListView);

        mainTasksListView.setAdapter(mainTaskAdapter);
        // тап на основной задаче
        mainTasksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainTaskArrayAdapter adapter = (MainTaskArrayAdapter) parent.getAdapter();

                MainTask selectedMainTask = mainTasks.get(position);
                //MainTask selectedMainTask = adapter.getItem(position); //альтернативный вариант

                SelectMainTask(selectedMainTask);

                if (GetEditorState()) {
                    mainTaskDialog = new MainTaskDialog(view.getContext(), selectedMainTask);
                    Button save = (Button) mainTaskDialog.findViewById(R.id.MainTaskDialog_SaveButton);
                    save.setOnClickListener(onSaveMainTaskDialogClick);
                    Button delete = (Button) mainTaskDialog.findViewById(R.id.MainTaskDialog_delete);
                    delete.setOnClickListener(onDeleteMainTaskDialogClick);
                    mainTaskDialog.show();
                } else {

                }
            }
        });
        // долгий тап
        mainTasksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                return false;
            }
        });

        // после того как все инициализировано можно и отметить текущие задачи
        // SelectMainTaskById(selectedMainTaskId);


    }


    private boolean GetEditorState() {
        ToggleButton editorMode = (ToggleButton)findViewById(R.id.editModeToggle);
        return editorMode.isChecked();
    }

    private int GetActiveMainTaskPosition(List<MainTask> theMainTasks) {
        for (int i = 0; i < theMainTasks.size(); i++) {
            if (theMainTasks.get(i).GetState() == TaskState.ACTIVE) {
                return i;
            }
        }
        return -1;
    }
    // отметить данную задачу активной, отобразить подзадачи данной задачи
    private void SelectMainTask(MainTask mainTask) {
        SelectMainTaskById(mainTask.id);
    }

    // возвращение вьюхи к обычному виду
    private void UnselectMainTaskById(int selectedMainTaskId) {
        if (selectedMainTaskId == -1) {return;}
        ListView mainTasksListView   = (ListView) findViewById(R.id.mainTasksListView);
        View unselectedView = GetViewFromListByPosition(
                GetMainTaskPositionById(selectedMainTaskId), mainTasksListView);

        if (unselectedView!=null) {
            ((CheckBox) unselectedView.findViewById(R.id.mainTaskActiveBox)).setChecked(false);
        }

    }
    // возвращение вьюхи к обычному виду
    private void SelectMainTaskById(int mainTaskId) {

        // снять выделение с последнего выделенного
        if (selectedMainTaskId != -1) {
            UnselectMainTaskById(selectedMainTaskId);
        }
        if (mainTaskId == -1){
            return;
        }

        ListView mainTasksListView   = (ListView) findViewById(R.id.mainTasksListView);
        int selectedPosition = GetMainTaskPositionById(mainTaskId);

        View selectedView = GetViewFromListByPosition(selectedPosition, mainTasksListView);
        // отрендерить изменения
        ((CheckBox)selectedView.findViewById(R.id.mainTaskActiveBox)).setChecked(true);
        selectedMainTaskId = mainTaskId;
        mainTaskAdapter.SetSelectedId(mainTaskId);
        SetSubTasksFor(mainTaskAdapter.getItem(selectedPosition));
    }



    private int GetMainTaskPositionById(int mainTaskId) {
        // ListView mainTasksListView   = ((ListView) findViewById(R.id.mainTasksListView));
        // MainTaskArrayAdapter mainTaskAdapter = (MainTaskArrayAdapter)mainTasksListView.getAdapter();
        for (int i = 0; i < mainTaskAdapter.getCount(); i++) {
            if(mainTaskAdapter.getItem(i).id == mainTaskId)
                return i;
        }
        return -1;
    }
    private MainTask GetMainTaskById(int mainTaskId){
        return  mainTaskAdapter.getItem(GetMainTaskPositionById(selectedMainTaskId));
    }


    // отобразить подзадачи для задачи с id
    private void SetSubTasksFor(MainTask mainTask) {
        subTaskAdapter = new SubTaskArrayAdapter(this, mainTask.subTasks);
        ListView subTasksListView = (ListView)findViewById(R.id.SubTasksListView);
        subTasksListView.setAdapter(subTaskAdapter);
    }

    // сделанна для того что бы безопасно получить view по позиции (без ошибки)
    public View GetViewFromListByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return null;
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    // add new or edit
    public void newMainTask_click(View v){
        mainTaskDialog = new MainTaskDialog (this);
        Button save = (Button)mainTaskDialog.findViewById(R.id.MainTaskDialog_SaveButton);
        save.setOnClickListener(onSaveMainTaskDialogClick);
        Button delete = (Button) mainTaskDialog.findViewById(R.id.MainTaskDialog_delete);
        delete.setOnClickListener(onDeleteMainTaskDialogClick);
        mainTaskDialog.show();
    }
    public void newSubTask_click(View v){
        subTaskDialog = new SubTaskDialog(this, selectedMainTaskId);
        Button save = (Button)subTaskDialog.findViewById(R.id.SubTaskDialog_SaveButton);
        save.setOnClickListener(onSaveSubTaskDialogClick);
        Button delete = (Button) subTaskDialog.findViewById(R.id.SubTaskDialog_delete);
        delete.setOnClickListener(onDeleteSubTaskDialogClick);
        subTaskDialog.show();
    }

    View.OnClickListener onSaveMainTaskDialogClick = new View.OnClickListener(){
        @Override
        public void onClick(View v)
        {
            MainTask currentTask = mainTaskDialog.getMainTask();
            if (currentTask.id != -1) {
                mainTasks.set(GetMainTaskPositionById(currentTask.id), currentTask);
                mainTaskAdapter.notifyDataSetChanged();
                dataSource.UpdateMainTask(currentTask);
            }
            else {

                ListView mainTasksListView = (ListView) findViewById(R.id.mainTasksListView);
                MainTaskArrayAdapter adapter = (MainTaskArrayAdapter) mainTasksListView.getAdapter();

                EditText mainTaskEditText = (EditText) mainTaskDialog.findViewById(R.id.MainTaskEditText);
                String text = mainTaskEditText.getText().toString();
                MainTask newMainTask = dataSource.CreateMainTask(text);
                adapter.add(newMainTask);
            }
            mainTaskDialog.dismiss();
        }

    };
    View.OnClickListener onDeleteMainTaskDialogClick = new View.OnClickListener(){
        @Override
        public void onClick(View v)
        {
            // may be 1 more dialog?
            // Select 1-st MainTask
            // TODO NOT SELECTED !!! BUT THAT ONE THAT IS EDITED BY DIALOG
            MainTask mainTask = mainTaskDialog.getMainTask();

            if (mainTask.id == selectedMainTaskId) {
                selectedMainTaskId = -1;
            }
            dataSource.DeleteMainTask(mainTask);
            mainTaskAdapter.remove(mainTask);
            mainTaskDialog.dismiss();

            if ((mainTaskAdapter.getCount() > 0)&&(selectedMainTaskId == -1)) {
                SelectMainTaskById(mainTaskAdapter.getItem(0).id);
            }
        }

    };
    View.OnClickListener onSaveSubTaskDialogClick = new View.OnClickListener(){
        @Override
        public void onClick(View v)
        {
            /*
            ListView subTasksListView = (ListView)findViewById(R.id.SubTasksListView);
            SubTaskArrayAdapter adapter = (SubTaskArrayAdapter)subTasksListView.getAdapter();

            EditText mainTaskEditText = (EditText)subTaskDialog.findViewById(R.id.SubTaskEditText);
            String text = mainTaskEditText.getText().toString();
            SubTask newSubTask = dataSource.CreateSubTask(text, selectedMainTaskId);
            //adapter.add(newSubTask);
            */

            SubTask currentSubTask = subTaskDialog.getSubTask();
            MainTask parentTask = GetMainTaskById(currentSubTask.mainTaskID);



            if (currentSubTask.id !=-1){
                // get position of subTask
                int position = -1;
                for (int i = 0; i<parentTask.subTasks.size(); i++) {
                    if (parentTask.subTasks.get(i).id == currentSubTask.id) {
                        position = i;
                        break;
                    }
                }

                if (position == -1)
                    // TODO may be exception
                    return; // no main task for sub task - smth go wrong
                parentTask.subTasks.set(position, currentSubTask);
                subTaskAdapter.notifyDataSetChanged();
                dataSource.UpdateSubTask(currentSubTask);
            }
            else {
                ListView subTasksListView = (ListView) findViewById(R.id.SubTasksListView);
                SubTaskArrayAdapter adapter = (SubTaskArrayAdapter) subTasksListView.getAdapter();

                EditText subTaskEditText = (EditText) subTaskDialog.findViewById(R.id.SubTaskEditText);
                String text = subTaskEditText.getText().toString();
                SubTask newSubTask = dataSource.CreateSubTask(text, selectedMainTaskId);
                adapter.add(newSubTask);
            }
            /*
            int mainTaskPosition = GetMainTaskPositionById(selectedMainTaskId);
            MainTask mainTask = mainTasks.get(mainTaskPosition);
            mainTask.subTasks.add(currentSubTask);
            mainTasks.set(mainTaskPosition, mainTask);

            ListView mainTasksListView = (ListView)findViewById(R.id.mainTasksListView);
            MainTaskArrayAdapter mainAdapter = (MainTaskArrayAdapter)mainTasksListView.getAdapter();
            mainAdapter.notifyDataSetChanged();
            adapter.notifyDataSetChanged();
            */
            subTaskDialog.dismiss();

            //RefreshAllTasks();
        }

    };

    private void RefreshAllTasks() {
        // получаем данные из БД
        mainTasks = dataSource.getAllMainTasks();
        // эти подзадачи будут показанны
        subTasks = new ArrayList<SubTask>();
        // выбрать подзадачи выбранной задачи
        for (int i = 0; i < mainTasks.size(); i++) {
            MainTask mainTask = mainTasks.get(i);
            if (mainTask.id == selectedMainTaskId) {
                subTasks = mainTask.subTasks;
            }
        }
        // mainTaskAdapter = new MainTaskArrayAdapter(this, mainTasks);
        // mainTaskAdapter.SetSelectedId(selectedMainTaskId);
        // mainTaskAdapter.SetActive(activeMainTaskId);

        // subTaskAdapter = new SubTaskArrayAdapter(this, subTasks);

        ListView mainTasksListView = (ListView) findViewById(R.id.mainTasksListView);
        mainTasksListView.invalidate();
        ((BaseAdapter)mainTasksListView.getAdapter()).notifyDataSetChanged();
        // mainTasksListView.setAdapter(mainTaskAdapter);
        //final ListView subTasksListView = (ListView) findViewById(R.id.SubTasksListView);
        //subTasksListView.setAdapter(subTaskAdapter);

    }


    View.OnClickListener onDeleteSubTaskDialogClick = new View.OnClickListener(){
        @Override
        public void onClick(View v)
        {
            SubTask currentSubTask = subTaskDialog.getSubTask();
            MainTask parentTask = GetMainTaskById(currentSubTask.mainTaskID);

            int position = -1;
            for (int i = 0; i<parentTask.subTasks.size(); i++) {
                if (parentTask.subTasks.get(i).id == currentSubTask.id) {
                    position = i;
                    break;
                }
            }
            if (position == -1)
                // TODO may be exception
                return; // no main task for sub task - smth go wrong
            parentTask.subTasks.remove(position);
            subTaskAdapter.notifyDataSetChanged();

            dataSource.DeleteSubTask(currentSubTask);
            subTaskDialog.dismiss();

        }

    };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop(){
        super.onStop();
        SaveState();

    }
    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
    private void LoadState() {
        SharedPreferences settings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        //activeMainTaskId    = settings.getInt(ACTIVE_MAIN_TASK_ID, -1);
        selectedMainTaskId  = settings.getInt(SELECTED_MAIN_TASK_ID, -1);
    }

    private void SaveState() {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(APP_PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        //editor.putInt(ACTIVE_MAIN_TASK_ID, this.activeMainTaskId);
        editor.putInt(SELECTED_MAIN_TASK_ID, this.selectedMainTaskId);
        // editor.putInt(SELECTED_MAIN_TASK_STATE);

        // Commit the edits!
        editor.commit();
    }
}
