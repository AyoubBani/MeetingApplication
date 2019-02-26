package app.rencontre.com.rencontreapp.entities;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by famille on 5/30/2018.
 */

public class Post implements Serializable {
    private String post;
    private String username;
    private long date;

    public Post() {
    }

    public Post(String post, String username) {
        this.post = post;
        this.username = username;

        // Initialize to current time
        date = new Date().getTime();
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }


}
