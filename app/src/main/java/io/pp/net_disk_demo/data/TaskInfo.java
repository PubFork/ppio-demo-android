package io.pp.net_disk_demo.data;

public class TaskInfo {

    private String mId = "";
    private String mCreated = "";
    private String mType = "";
    private String mState = "";
    private String mFrom = "";
    private String mTo = "";
    private long mTotal = 0L;
    private long mFinished = 0L;
    private String mError = "";

    public void setId(String id) {
        mId = id;
    }

    public void setCreated(String created) {
        mCreated = created;
    }

    public void setType(String type) {
        mType = type;
    }

    public void setState(String state) {
        mState = state;
    }

    public void setFrom(String from) {
        mFrom = from;
    }

    public void setTo(String to) {
        mTo = to;
    }

    public void setTotal(long total) {
        mTotal = total;
    }

    public void setFinished(long finished) {
        mFinished = finished;
    }

    public void setError(String error) {
        mError = error;
    }


    public String getId() {
        return mId;
    }

    public String getCreated() {
        return mCreated;
    }

    public String getType() {
        return mType;
    }

    public String getState() {
        return mState;
    }

    public String getFrom() {
        return mFrom;
    }

    public String getTo() {
        return mTo;
    }

    public long getTotal() {
        return mTotal;
    }

    public long getFinished() {
        return mFinished;
    }

    public String getError() {
        return mError;
    }
}