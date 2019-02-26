package app.rencontre.com.rencontreapp.entities;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by famille on 6/8/2018.
 */

public class User {
    private String name;
    private int age;
    private String url;
    private double latitude;
    private double longtitude;

    public User() {
    }

    public User(String name, int age, String url, double latitude, double longtitude) {
        this.name = name;
        this.age = age;
        this.url = url;
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("age", age);
        result.put("url", url);
        result.put("latitude", latitude);
        result.put("longtitude", longtitude);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (age != user.age) return false;
        if (Double.compare(user.latitude, latitude) != 0) return false;
        if (Double.compare(user.longtitude, longtitude) != 0) return false;
        if (!name.equals(user.name)) return false;
        return url.equals(user.url);
    }

}
