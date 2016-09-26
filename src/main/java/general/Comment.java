package main.java.general;

/**
 * Created by sirius-.- on 2015/9/14.
 */
public class Comment {
    String text;
    String time;
    String id;
    String gender;
    String region;
    String url;

    public int getSourceId() {
        return sourceId;
    }

    int sourceId;
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getUrl() {
        return url;
    }

    public Comment(String text, String time, String id, String gender, String region,String url,int sourceId) {
        this.text = text;
        this.time = time;
        this.id = id;
        this.gender = gender;
        this.region = region;
        this.url=url;
        this.sourceId=sourceId;
    }
}
