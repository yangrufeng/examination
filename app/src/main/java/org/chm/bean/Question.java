package org.chm.bean;

/**
 * Created by pc on 2016/7/4.
 */
public class Question {
    private String id;
    private String text;
    private String type;

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }
}
