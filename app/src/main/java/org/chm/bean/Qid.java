package org.chm.bean;

/**
 * 题号类，用于标识题目所属及题目序号、答题情况
 * Created by pc on 2016/6/29.
 */
public class Qid {
    private String parentId;
    private String id;
    private boolean isFinished;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }
}
