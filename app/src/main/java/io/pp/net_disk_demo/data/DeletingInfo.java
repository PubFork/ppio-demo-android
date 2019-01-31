package io.pp.net_disk_demo.data;

public class DeletingInfo {

    private String mName;
    private String mTaskId;
    private double mProgress;
    private String mState;


    public DeletingInfo(String name, String taskId) {
        mName = name;
        mTaskId = taskId;
    }

    public void setProgress(double progress) {
        mProgress = progress;
    }

    public void setState(String state) {
        mState = state;
    }

    public String getName() {
        return mName;
    }

    public String getTaskId() {
        return mTaskId;
    }


    public double getProgress() {
        return mProgress;
    }

    public String getState() {
        return mState;
    }
}