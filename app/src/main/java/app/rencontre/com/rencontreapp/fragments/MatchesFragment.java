package app.rencontre.com.rencontreapp.fragments;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import java.util.ArrayList;
import java.util.List;

import app.rencontre.com.rencontreapp.Access;
import app.rencontre.com.rencontreapp.Profile;
import app.rencontre.com.rencontreapp.R;
import app.rencontre.com.rencontreapp.TinderCard;
import app.rencontre.com.rencontreapp.Utils;
import app.rencontre.com.rencontreapp.entities.Post;
import app.rencontre.com.rencontreapp.entities.User;

/**
 * Created by famille on 6/5/2018.
 */

public class MatchesFragment extends Fragment {
    Context mContext;
    private SwipePlaceHolderView mSwipeView;
    private ArrayList<User> usersList;
    FirebaseAuth mFirebaseAuth;
    private ArrayList<String> keys;
    private double latitude;
    private double longtitude;

    public MatchesFragment() {
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
        keys = new ArrayList<>();
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.matches, container, false);
//        postListView = (ListView) result.findViewById(R.id.postlist);
        mFirebaseAuth = FirebaseAuth.getInstance();
        usersList = new ArrayList<>();
        mSwipeView = (SwipePlaceHolderView) result.findViewById(R.id.swipeView);

        int bottomMargin = Utils.dpToPx(160);
        Point windowSize = Utils.getDisplaySize(getActivity().getWindowManager());
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setIsUndoEnabled(true)
                .setHeightSwipeDistFactor(10)
                .setWidthSwipeDistFactor(5)
                .setSwipeDecor(new SwipeDecor()
                        .setViewWidth(windowSize.x)
                        .setViewHeight(windowSize.y - bottomMargin)
                        .setViewGravity(Gravity.TOP)
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeMaxChangeAngle(2f)
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));

        fetchNearbyUsers();
//
//        for (Profile profile : Utils.loadProfiles(mContext)) {
//            mSwipeView.addView(new TinderCard(mContext, profile, mSwipeView));
//        }



/*
        result.findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
            }
        });

        result.findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
            }
        });

        result.findViewById(R.id.undoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.undoLastSwipe();
            }
        });
*/
        return result;
    }

    private void fetchNearbyUsers() {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference usersDatabaseReference = mFirebaseDatabase.getReference().child("users");
        usersDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    usersList.add(data.getValue(User.class));
                    if (!data.getKey().equals(mFirebaseAuth.getCurrentUser().getUid())) {
                        User user = data.getValue(User.class);
//                        Log.d("MTCHTAG", "Username: " + user.getName() + " Age: " + user.getAge() + " Key: " + data.getKey());
                        //                   if(mSwipeView==null) Log.d("MTCHTAG", "It's NULLL Username: " + user.getName() + " Age: " + user.getAge() + " Key: " + data.getKey());
                        usersList.add(user);
                        keys.add(data.getKey());

                    }
                }
                populateCard();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    void populateCard() {
        ArrayList<User> list = new ArrayList<>();
        ArrayList<String> ky = new ArrayList<>();
        for (int i = 0; i < usersList.size(); i++) list.add(usersList.get(i));
        usersList = Access.sortLocations(usersList, latitude, longtitude);
        for (int i = 0; i < list.size(); i++) {
            //Log.d("TGSORT","")
            //int j = usersList.indexOf(list.get(i));
            int j = list.indexOf(usersList.get(i));
            ky.add(keys.get(j));
        }
        for (int i = 0; i < usersList.size(); i++) {
            mSwipeView.addView(new TinderCard(mContext, usersList.get(i), mSwipeView, ky.get(i)));
            Log.d("TGSORT", "User: " + usersList.get(i).getName() + " Key: " + ky.get(i));
        }
    }

}
