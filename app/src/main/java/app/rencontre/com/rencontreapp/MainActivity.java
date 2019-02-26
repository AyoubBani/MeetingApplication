package app.rencontre.com.rencontreapp;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import app.rencontre.com.rencontreapp.entities.Post;
import app.rencontre.com.rencontreapp.entities.User;
import app.rencontre.com.rencontreapp.fragments.HomeFragment;
import app.rencontre.com.rencontreapp.fragments.MatchesFragment;
import app.rencontre.com.rencontreapp.fragments.MessageFragment;
import app.rencontre.com.rencontreapp.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    //    private TextView mTextMessage;
//    private TextView logIn;
    android.support.v4.app.FragmentManager fragmentManager;
    android.support.v4.app.FragmentTransaction fragmentTransaction;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean first = true;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference ref;
    Double latitude = 0.0, longtitude = 0.0;
    ProfileFragment selectedFragment = null;
    HomeFragment homeFragment = null;
    MatchesFragment mtchFrg = null;
    MessageFragment msgFrg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("posts");
        ref = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);

        fragmentManager = getSupportFragmentManager();

        fragmentTransaction = fragmentManager.beginTransaction();

        homeFragment = new HomeFragment();
        homeFragment.setmContext(MainActivity.this);
//        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.contentLayout, homeFragment);
        fragmentTransaction.commit();

        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");



        if (AccessToken.getCurrentAccessToken() != null) {

            System.out.println(AccessToken.getCurrentAccessToken().getToken());

            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            // Application code
                            try {
                                Log.d("FACEBOOKDATA", "INF: " + object.toString());
                                String id = object.getString("id");
                                String email = object.getString("email");
                                String gender = object.getString("gender");
                                String birthday = object.getString("birthday");
                                Log.d("FACEBOOKDATA", "email: " + email + " gender: " + gender + " id: " + id + " birthday: " + birthday);
                            } catch (JSONException e) {
                                Log.d("FACEBOOKDATA", "error: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,birthday");
//            parameters.putString("fields", "id,name,email,gender,birthday");
            request.setParameters(parameters);
            request.executeAsync();

        } else {
            System.out.println("Access Token NULL");
        }
        //User u = new User(mFirebaseAuth.getCurrentUser().getDisplayName(), 23, Access.getImage(FirebaseAuth.getInstance().getCurrentUser()), 0.0, 0.0);
        //sRef.child(mFirebaseAuth.getCurrentUser().getUid()).push().setValue(u);
        updateLocation();
        userTest();
//        User u1=new User("Sofia",20,"https://pbs.twimg.com/profile_images/572905100960485376/GK09QnNG.jpeg",40.73,-73.93);
//        User u2=new User("Roma",22,"http://cdn.cavemancircus.com//wp-content/uploads/images/2015/june/pretty_girls_3/pretty_girls_15.jpg",36.77,-119.41);
//        User u3=new User("Zoya",23,"http://i.imgur.com/N6SaAlZ.jpg",60.19,24.94);
//        User u4=new User("Carol",19,"http://cdn.cavemancircus.com//wp-content/uploads/images/2015/january/pretty_girls_2/pretty_girls_5.jpg",43.65,-79.38);
//        User u5=new User("Monica",22,"http://informationng.com/wp-content/uploads/2014/08/Selfie-featured.jpg",41.39,2.15);
//        ref.child("users").push().setValue(u1);
//        ref.child("users").push().setValue(u2);
//        ref.child("users").push().setValue(u3);
//        ref.child("users").push().setValue(u4);
//        ref.child("users").push().setValue(u5);
//  Post p1 = new Post("Post Ahmed Num Dt",mFirebaseAuth.getCurrentUser().getDisplayName(),"05/06/18");
//        Post p2 = new Post("HellO world",mFirebaseAuth.getCurrentUser().getDisplayName(),"05/07/18");
//        Post p3 = new Post("I'm new user here everyone!!!",mFirebaseAuth.getCurrentUser().getDisplayName(),"15/10/18");
//        Post p4 = new Post("what's upp homies",mFirebaseAuth.getCurrentUser().getDisplayName(),"25/06/18");
//        Post p5 = new Post("Hey guys!!",mFirebaseAuth.getCurrentUser().getDisplayName(),"05/06/18");
//        Post p6 = new Post("Wow that's cool",mFirebaseAuth.getCurrentUser().getDisplayName(),"19/03/18");
//        Post p7 = new Post("Hello World How are you doing??",mFirebaseAuth.getCurrentUser().getDisplayName(),"17/06/18");
//        Post p8 = new Post("anyone wanna talk",mFirebaseAuth.getCurrentUser().getDisplayName(),"20/01/18");
//        myRef.child(mFirebaseAuth.getCurrentUser().getUid()).push().setValue(p4);
//        myRef.child(mFirebaseAuth.getCurrentUser().getUid()).push().setValue(p5);
//        myRef.child(mFirebaseAuth.getCurrentUser().getUid()).push().setValue(p6);
//        myRef.child(mFirebaseAuth.getCurrentUser().getUid()).push().setValue(p7);
//        myRef.child(mFirebaseAuth.getCurrentUser().getUid()).push().setValue(p8);

//        mTextMessage = (TextView) findViewById(R.id.message);
//        logIn= (TextView) findViewById(R.id.login);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void userTest() {
        ref.child("users").child(mFirebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // use "username" already exists
                    // Let the user know he needs to pick another username.
                    User u = getUser();
                    ref.child("users").child((mFirebaseAuth.getCurrentUser().getUid())).child("latitude").setValue(u.getLatitude());
                    ref.child("users").child((mFirebaseAuth.getCurrentUser().getUid())).child("longtitude").setValue(u.getLongtitude());
                } else {
                    // User does not exist. NOW call createUserWithEmailAndPassword
                    //mAuth.createUserWithPassword(...);
                    // Your previous code here.
                    ref.child("users").child((mFirebaseAuth.getCurrentUser().getUid())).setValue(getUser());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //myRef.addValueEventListener(userListner);
    }

    private User getUser() {
        return new User(mFirebaseAuth.getCurrentUser().getDisplayName(), 23, Access.getImage(FirebaseAuth.getInstance().getCurrentUser()), latitude, longtitude);
    }


    private void updateLocation() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(7500); //use a value fo about 10 to 15s for a real app
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        startLocationUpdates();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
//                        latitude.setText(String.valueOf(location.getLatitude()));
//                        longitude.setText(String.valueOf(location.getLongitude()));
//                        accuracy.setText(String.valueOf(location.getAccuracy()));
                        updateMyLocation(location.getLatitude(), location.getLongitude());
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String postalCode = addresses.get(0).getPostalCode();
                            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

//                            addresse.setText("address: "+address+" city: "+city+" state: "+state+" country: "+country+" postalCode: "+postalCode+" knownName: "+knownName);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (location.hasAltitude()) {
//                            altitude.setText(String.valueOf(location.getAltitude()));
                        } else {
//                            altitude.setText("No altitude available");
                        }
                        if (location.hasSpeed()) {
//                            speed.setText(String.valueOf(location.getSpeed()) + "m/s");
                        } else {
//                            speed.setText("No speed available");
                        }

                    }
                }
            });
        } else {
            // request permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
            }
        }

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    //Update UI with location data
                    if (location != null) {
//                        latitude.setText(String.valueOf(location.getLatitude()));
//                        longitude.setText(String.valueOf(location.getLongitude()));
                        updateMyLocation(location.getLatitude(), location.getLongitude());
//                        accuracy.setText(String.valueOf(location.getAccuracy()));
                        if (location.hasAltitude()) {
//                            altitude.setText(String.valueOf(location.getAltitude()));
                        } else {
//                            altitude.setText("No altitude available");
                        }
                        if (location.hasSpeed()) {
//                            speed.setText(String.valueOf(location.getSpeed()) + "m/s");
                        } else {
//                            speed.setText("No speed available");
                        }

                    }
                }
            }
        };


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSION_REQUEST_FINE_LOCATION:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted do nothing and carry on

                } else {
                    Toast.makeText(getApplicationContext(), "This app requires location permissions to be granted", Toast.LENGTH_SHORT).show();
                    finish();
                }

                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (fusedLocationProviderClient != null)
//            startLocationUpdates();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    homeFragment = new HomeFragment();
                    homeFragment.setmContext(MainActivity.this);
//                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.contentLayout, homeFragment);
                    fragmentTransaction.commit();
//                    fragmentManager.executePendingTransactions();
//                    mTextMessage.setText(R.string.acceuil);
                    return true;
                case R.id.message:
                    msgFrg = new MessageFragment();
                    msgFrg.setLayout(R.layout.inbox);
                    msgFrg.setmContext(MainActivity.this);
//                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.contentLayout, msgFrg);
                    fragmentTransaction.commit();


                    //                    mTextMessage.setText(R.string.message);
                    return true;
                case R.id.matches:
//                    mTextMessage.setText(R.string.matches);


                    mtchFrg = new MatchesFragment();
                    mtchFrg.setmContext(MainActivity.this);
                    mtchFrg.setLatitude(latitude);
                    mtchFrg.setLongtitude(longtitude);
//                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.contentLayout, mtchFrg);
                    fragmentTransaction.commit();

                    return true;
                case R.id.profile:
                    selectedFragment = new ProfileFragment();
                    selectedFragment.setmContext(MainActivity.this);
//                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.contentLayout, selectedFragment);
                    fragmentTransaction.commit();
//                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                    if (user != null) {
//                        logIn.setText(user.getDisplayName());
//                    }

//                    mTextMessage.setText(R.string.profile);
                    return true;
            }


//                    ph = new PassangerHome();
            //AddNewPost addNewPost = new AddNewPost();
            //profileFragment.setInfo(passanger, mFirebaseAuth.getCurrentUser().getDisplayName(), mFirebaseAuth.getCurrentUser().getEmail());
/* LATER

            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(layout, selectedFragment);
            fragmentTransaction.commit();
*/
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logOut() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        LoginManager.getInstance().logOut();
//        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(MainActivity.this, Auth.class);
        startActivity(i);
        finish();
//        AuthUI.getInstance()
//                .signOut(this)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Intent i =new Intent(MainActivity.this,Auth.class);
//                        startActivity(i);
//                    }
//                });


    }

    void updateMyLocation(Double latitude, Double longtitude) {
        if (first) {
            this.latitude = latitude;
            this.longtitude = longtitude;
            Log.d("INSUPDTEST", "xxlatitude: " + latitude + " longtitude: " + longtitude);
            //insert or Update UserData


            first = false;
        }
    }

}
