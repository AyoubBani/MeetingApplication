package app.rencontre.com.rencontreapp;

import android.util.Log;

import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import app.rencontre.com.rencontreapp.entities.User;

/**
 * Created by famille on 6/3/2018.
 */

public class Access {
    public static String getImage(FirebaseUser user) {

        String facebookUserId = "";
        user = FirebaseAuth.getInstance().getCurrentUser();

        // find the Facebook profile and get the user's id
        for (UserInfo profile : user.getProviderData()) {
            // check if the provider id matches "facebook.com"
            if (FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                facebookUserId = profile.getUid();
            }
        }

        // construct the URL to the profile picture, with a custom height
        // alternatively, use '?type=small|medium|large' instead of ?height=
        String photoUrl = "https://graph.facebook.com/" + facebookUserId + "/picture?height=500";
        Log.d("URLP","PHT URL : "+photoUrl);
        return  photoUrl;
        // (optional) use Picasso to download and show to image
        //Picasso.with(this).load(photoUrl).into(profilePicture);
    }

    public static ArrayList<User> sortLocations(ArrayList<User> locations, final double myLatitude, final double myLongitude) {
        Comparator comp = new Comparator<User>() {
            @Override
            public int compare(User o, User o2) {
                float[] result1 = new float[3];
                    android.location.Location.distanceBetween(myLatitude, myLongitude, o.getLatitude(), o.getLongtitude(), result1);
                Float distance1 = result1[0];

                float[] result2 = new float[3];
                android.location.Location.distanceBetween(myLatitude, myLongitude, o2.getLatitude(), o2.getLongtitude(), result2);
                Float distance2 = result2[0];

                return distance1.compareTo(distance2);
            }
        };


        Collections.sort(locations, comp);
        return locations;
    }

}
