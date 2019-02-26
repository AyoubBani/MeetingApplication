package app.rencontre.com.rencontreapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeHead;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;
import com.mindorks.placeholderview.annotations.swipe.SwipeView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import app.rencontre.com.rencontreapp.entities.ChatMessage;
import app.rencontre.com.rencontreapp.entities.User;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by janisharali on 19/08/16.
 */
@Layout(R.layout.tinder_card_view)
public class TinderCard {

    @View(R.id.profileImageView)
     ImageView profileImageView;

    @View(R.id.nameAgeTxt)
     TextView nameAgeTxt;

    @View(R.id.locationNameTxt)
     TextView locationNameTxt;

    @SwipeView
     android.view.View cardView;

     Profile mProfile;
     Context mContext;
     SwipePlaceHolderView mSwipeView;
     User muser;
    DatabaseReference ref;
    FirebaseAuth mFirebaseAuth;
     String uid;

//    public TinderCard(Context context, Profile profile, SwipePlaceHolderView swipeView) {
//        mContext = context;
//        mProfile = profile;
//        mSwipeView = swipeView;
//    }

    public TinderCard(Context context, User user, SwipePlaceHolderView swipeView, String uid) {
        Log.d("TAGERRDE","TinderCard Constructor");
        mContext = context;
        muser = user;
        mSwipeView = swipeView;
        this.uid = uid;
        ref = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }


    @Resolve
     void onResolved() {
        Log.d("TAGERRDE","TinderCard onResolved");
        MultiTransformation multi = new MultiTransformation(
                new BlurTransformation(mContext, 30),
                new RoundedCornersTransformation(
                        mContext, Utils.dpToPx(7), 0,
                        RoundedCornersTransformation.CornerType.TOP));

//        Glide.with(mContext).load(mProfile.getImageUrl())
        Glide.with(mContext).load(muser.getUrl())

                .bitmapTransform(multi)
                .into(profileImageView);
//        nameAgeTxt.setText(mProfile.getName() + ", " + mProfile.getAge());
        nameAgeTxt.setText(muser.getName() + ", " + muser.getAge());
//        locationNameTxt.setText(mProfile.getLocation());


        if (muser.getLatitude() == 0 && muser.getLongtitude() == 0) {
            locationNameTxt.setText("Unknown Location");
        } else {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(muser.getLatitude(), muser.getLongtitude(), 1);
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

//                            addresse.setText("address: "+address+" city: "+city+" state: "+state+" country: "+country+" postalCode: "+postalCode+" knownName: "+knownName);
                locationNameTxt.setText(address);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SwipeHead
     void onSwipeHeadCard() {
//        Glide.with(mContext).load(mProfile.getImageUrl())
        Log.d("TAGERRDE","TinderCard onSwipeHeadCard");
        Glide.with(mContext).load(muser.getUrl())
                .bitmapTransform(new RoundedCornersTransformation(
                        mContext, Utils.dpToPx(7), 0,
                        RoundedCornersTransformation.CornerType.TOP))
                .into(profileImageView);
        cardView.invalidate();
    }

    @Click(R.id.profileImageView)
     void onClick() {
        Log.d("TAGERRDE", "TinderCard click");
//        mSwipeView.addView(this);
    }

    @SwipeOut
     void onSwipedOut() {
        //Reject
        Log.d("TAGERRDE", "onSwipedOut");
//        mSwipeView.addView(this);
    }

    @SwipeCancelState
     void onSwipeCancelState() {
        Log.d("TAGERRDE", "onSwipeCancelState");
    }

    @SwipeIn
     void onSwipeIn() {
        //Accept
        newLike();
        Log.d("TAGERRDE", "onSwipedIn");
    }

    @SwipeInState
     void onSwipeInState() {
        Log.d("TAGERRDE", "onSwipeInState");
    }

    @SwipeOutState
     void onSwipeOutState() {
        Log.d("TAGERRDE", "onSwipeOutState");
    }

     void newLike() {
        Log.d("TAGERRDE", "TinderCard NewLike");
        ref.child("users").child(mFirebaseAuth.getCurrentUser().getUid()).child("likes").push().setValue(uid);
        //test other node if exists
        ref.child("users").child(uid).child("likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    Log.d("TINMATCH","data contains"+data.getKey()+" VALUEEX: "+data.getValue());
                    if (data.getValue().equals(mFirebaseAuth.getCurrentUser().getUid())) {
                        // new Match in Both sides
                        Toast.makeText(mContext, "It's a match say Hello!", Toast.LENGTH_LONG).show();
                        ref.child("users").child(mFirebaseAuth.getCurrentUser().getUid()).child("matches").push().setValue(uid);
                        ref.child("users").child(uid).child("matches").push().setValue(mFirebaseAuth.getCurrentUser().getUid());
                        newMessage();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

     void newMessage() {
        Log.d("TAGERRDE", "TinderCard newMessage");
        ChatMessage ch1= new ChatMessage("You're matched with " + muser.getName(),"System");
        ChatMessage ch2= new ChatMessage("You're matched with " + mFirebaseAuth.getCurrentUser().getDisplayName(),"System");
        ref.child("chat").child(mFirebaseAuth.getCurrentUser().getUid()).child("messages").child(uid).push().setValue(ch1);
        ref.child("chat").child(uid).child("messages").child(mFirebaseAuth.getCurrentUser().getUid()).push().setValue(ch2);
    }
}
