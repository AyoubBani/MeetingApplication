package app.rencontre.com.rencontreapp.entities;

/**
 * Created by famille on 6/12/2018.
 */

public class Data {
    private Post post;
    private User user;
    private ChatMessage message;

    public Data(Post post, User user) {
        this.post = post;
        this.user = user;
    }

    public Data(User user, ChatMessage message) {
        this.user = user;
        this.message = message;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ChatMessage getMessage() {
        return message;
    }

    public void setMessage(ChatMessage message) {
        this.message = message;
    }
}
