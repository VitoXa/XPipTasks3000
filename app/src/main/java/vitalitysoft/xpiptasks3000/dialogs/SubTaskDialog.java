package vitalitysoft.xpiptasks3000.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import vitalitysoft.xpiptasks3000.R;
import vitalitysoft.xpiptasks3000.task.types.MainTask;
import vitalitysoft.xpiptasks3000.task.types.SubTask;
import vitalitysoft.xpiptasks3000.task.types.TaskState;

/**
 * Created by Vitaliy on 11.05.2015.
 */
public class SubTaskDialog extends Dialog{
    private Context _context;
    private SubTask _subTask;



    public SubTaskDialog(Context context, int selectedMainTaskId) {
        super(context);
        this._context = context;
        this._subTask = new SubTask(-1, selectedMainTaskId, "", TaskState.IN_PROGRESS);

        this.setContentView(R.layout.new_subtask_dialog);
        this.setTitle(R.string.sub_task_dialog_title);
        ((EditText)this.findViewById(R.id.SubTaskEditText)).setText(_subTask.text);
        Button bCancel = (Button)this.findViewById(R.id.SubTaskDialog_cancel);
        final Dialog _this = this;
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _this.cancel();
            }
        });

    }

    public SubTaskDialog(Context context, SubTask subTask, int selectedMainTaskId) {
        super(context);
        this._context = context;
        this._subTask = subTask;

        this.setContentView(R.layout.new_subtask_dialog);
        this.setTitle(R.string.sub_task_dialog_title);

        ((EditText)this.findViewById(R.id.SubTaskEditText)).setText(_subTask.text);
        boolean isDone = false;
        if (_subTask.state == TaskState.DONE) isDone = true;

        ((CheckBox)this.findViewById(R.id.subTaskDoneCheckBox)).setChecked(isDone);
        Button bCancel = (Button)this.findViewById(R.id.SubTaskDialog_cancel);
        final Dialog _this = this;
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _this.cancel();
            }
        });
    }

    public SubTask getSubTask() {
        // resave all attributes
        this._subTask.text = ((EditText)this.findViewById(R.id.SubTaskEditText))
                .getText().toString();
        if (((CheckBox)this.findViewById(R.id.subTaskDoneCheckBox)).isChecked()) {
            this._subTask.state = TaskState.DONE;
        }
        else this._subTask.state = TaskState.IN_PROGRESS;

        //

        return _subTask;
    }

    public void SetSubTask(SubTask subTask) {
        this._subTask = subTask;
    }
}
