package vitalitysoft.xpiptasks3000.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import vitalitysoft.xpiptasks3000.R;
import vitalitysoft.xpiptasks3000.task.types.MainTask;

/**
 * Created by Vitaliy on 05.04.2015.
 */
public class MainTaskDialog extends Dialog {
    private Context _context;
    private MainTask _mainTask;


    public MainTaskDialog(Context context){
        super(context);

        this._context = context;
        this._mainTask =  new MainTask(-1, "");

        this.setContentView(R.layout.new_main_task_dialog);
        this.setTitle(R.string.main_task_dialog_title);
        ((EditText)this.findViewById(R.id.MainTaskEditText)).setText(_mainTask.text);
        Button bCancel = (Button)this.findViewById(R.id.MainTaskDialog_cancel);
        final Dialog _this = this;
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _this.cancel();
            }
        });
    }
    public MainTaskDialog(Context context, MainTask mainTask){
        super(context);
        this._context = context;
        this._mainTask = mainTask;

        this.setContentView(R.layout.new_main_task_dialog);
        this.setTitle(R.string.main_task_dialog_title);

        ((EditText)this.findViewById(R.id.MainTaskEditText)).setText(_mainTask.text);
        Button bCancel = (Button)this.findViewById(R.id.MainTaskDialog_cancel);
        final Dialog _this = this;
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _this.cancel();
            }
        });
    }

    public MainTask getMainTask() {
        this._mainTask.text = ((EditText)this.findViewById(R.id.MainTaskEditText))
                .getText().toString();
        return _mainTask;
    }

    public void SetMainTask(MainTask mainTask) {
        this._mainTask = mainTask;
    }
}
