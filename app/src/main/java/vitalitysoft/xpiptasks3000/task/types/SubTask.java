package vitalitysoft.xpiptasks3000.task.types;


/**
 * Created by Vitaliy on 29.03.2015.
 */
public class SubTask {
    public String text;
    public int id;
    public int mainTaskID;
    public int state;  // 0-неготова активна
    // 1-неготова неактивна
    // 2-готова (готовая не может быть активна)


    public SubTask(int id, int mainTaskId, String text, int status) {
        this.id = id;
        this.text = text;
        this.state = status;
        this.mainTaskID = mainTaskId;

    }
}
