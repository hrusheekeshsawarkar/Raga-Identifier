package com.shahdivya.ragaidentification.models;
import com.google.gson.annotations.SerializedName;
import java.net.URL;

public class Task
{
    @SerializedName("id")
    private int id;
    @SerializedName("audio")
    private URL audio;
    @SerializedName("date_created")
    private String date_created;

    public Task() { }

    public Task(int id, URL audio,String date_created) {
        this.id = id;
        this.audio = audio;
        this.date_created = date_created;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public URL getAudio() { return audio; }
    public void setAudio(URL audio) { this.audio = audio; }
    public String getDate_created() { return date_created; }
    public void setDate_created(String date_created) { this.date_created = date_created; }
}