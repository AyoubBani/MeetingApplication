package app.rencontre.com.rencontreapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import app.rencontre.com.rencontreapp.R;
import app.rencontre.com.rencontreapp.adapter.CustomListAdapter;
import app.rencontre.com.rencontreapp.entities.ChatMessage;
import app.rencontre.com.rencontreapp.entities.Data;
import app.rencontre.com.rencontreapp.entities.Post;
import app.rencontre.com.rencontreapp.entities.User;

/**
 * Created by famille on 5/30/2018.
 */

public class HomeFragment extends Fragment {
    Context mContext;
    private ListView postListView;
    private ArrayList<Data> postList;
    CustomListAdapter customListAdapter;
    FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference postsDatabaseReference;
    private DatabaseReference userDatabaseReference;
    ArrayList<String> ruidList;
    private ChildEventListener mChildEventListener;

    public HomeFragment() {
        postList = new ArrayList<>();
        ruidList = new ArrayList<>();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        postsDatabaseReference = mFirebaseDatabase.getReference().child("posts");
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
        customListAdapter = new CustomListAdapter(mContext, R.layout.post_item, postList);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.home, container, false);
        postListView = (ListView) result.findViewById(R.id.postlist);

        postListView.setAdapter(customListAdapter);
        postListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                Toast.makeText(mContext, "" + position, Toast.LENGTH_SHORT).show();
                DatabaseReference mrf = FirebaseDatabase.getInstance().getReference().child("");
                ChatMessage ch1 = new ChatMessage(postList.get(position).getPost().getPost(), "System");
                ChatMessage ch2 = new ChatMessage(postList.get(position).getPost().getPost(), "System");
                mrf.child("chat").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("messages").child(ruidList.get(position)).push().setValue(ch1);
                mrf.child("chat").child(ruidList.get(position)).child("messages").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().setValue(ch2);

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                MessageFragment mg = new MessageFragment();
                mg.setLayout(R.layout.chat);
                mg.setmContext(mContext);
                mg.setRuid(ruidList.get(position));
                Log.d("TAGXH", "ruid: " + ruidList.get(position));
                ft.replace(R.id.contentLayout, mg);
                ft.commit();
            }
        });


        FloatingActionButton fab = result.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                ShowPopUp(view);
            }
        });
        fetchData();
        return result;
    }

    private void ShowPopUp(final View vw) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.new_post, null);
        dialogBuilder.setView(dialogView);

        final EditText editText = (EditText) dialogView.findViewById(R.id.postinput);
        Button btn = (Button) dialogView.findViewById(R.id.btn);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText.getText().toString().equals("")) {
                    //send new Post
                    Post p = new Post(editText.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("posts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    ref.push().setValue(p);
                    alertDialog.dismiss();
                    Snackbar.make(vw, "New Post is added", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    HomeFragment mg = new HomeFragment();
                    mg.setmContext(mContext);
                    ft.replace(R.id.contentLayout, mg);
                    ft.commit();
                }
            }
        });


    }

    private void fetchData() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    for (DataSnapshot dt : dataSnapshot.getChildren()) {
                        Log.d("USxTAG", "UserID: " + dataSnapshot.getKey());
                        ruidList.add(dataSnapshot.getKey());
                        Post post = dt.getValue(Post.class);
                        setUpData(post, dataSnapshot.getKey());
//                        customListAdapter.add(post);

                    }
                    customListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };

            postsDatabaseReference.addChildEventListener(mChildEventListener);
        }

//
//        postsDatabaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    customListAdapter.add(data.getValue(Post.class));
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

    }

    private void setUpData(final Post post, final String key) {
//        Log.d("USRKT", "setuser data key: " + key);



        userDatabaseReference = mFirebaseDatabase.getReference().child("users");
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Log.d("USRKT", "data "+data+" datakey: "+data.getKey());
                    if (data.getKey().equals(key)) {
                        User u = data.getValue(User.class);
//                        Log.d("USRKT", "datasnap: " + data.getKey() + " Key: " + key + " user: " + u.getName() + " post: " + post.getPost());
                        customListAdapter.add(new Data(post, u));
                        customListAdapter.notifyDataSetChanged();
                        Log.d("USRKT", "data inserted");
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        if (msChildEventListener == null) {
//            msChildEventListener = new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    for (DataSnapshot data : dataSnapshot.getChildren()) {
//                        Log.d("USRKT", "data "+data+" datakey: "+data.getKey());
//                        if (data.getKey().equals(key)) {
//                            User u = data.getValue(User.class);
//                            Log.d("USRKT", "datasnap: " + data.getKey() + " Key: " + key + " user: " + u.getName() + " post: " + post.getPost());
//                            customListAdapter.add(new Data(post, u));
//                        }
//                    }
//                }
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                }
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//                }
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                }
//            };
//
//            userDatabaseReference.addChildEventListener(msChildEventListener);
//        }


    }
}