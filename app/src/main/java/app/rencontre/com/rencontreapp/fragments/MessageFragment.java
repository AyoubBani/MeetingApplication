package app.rencontre.com.rencontreapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import app.rencontre.com.rencontreapp.R;
import app.rencontre.com.rencontreapp.adapter.CustomInboxAdapter;
import app.rencontre.com.rencontreapp.adapter.MessageAdapter;
import app.rencontre.com.rencontreapp.entities.ChatMessage;
import app.rencontre.com.rencontreapp.entities.Data;
import app.rencontre.com.rencontreapp.entities.Message;
import app.rencontre.com.rencontreapp.entities.Post;
import app.rencontre.com.rencontreapp.entities.User;

/**
 * Created by famille on 6/9/2018.
 */

public class MessageFragment extends Fragment {
    Context mContext;
    ListView msg;
    ArrayList<Data> msgTitles;
    ArrayList<ChatMessage> msgList;
    CustomInboxAdapter customListAdapter;
    private int layout;
    private MessageAdapter adapter;
    private String ruid; // reciever uid
    private ChildEventListener mChildEventListener;
    private ChildEventListener nChl;

    public MessageFragment() {
        msgTitles = new ArrayList<>();
        msgList = new ArrayList<>();
    }

    ArrayList<String> ruidList;

    public void setmContext(Context mContext) {
        this.mContext = mContext;
        customListAdapter = new CustomInboxAdapter(mContext, R.layout.inbox_item, msgTitles);
        adapter = new MessageAdapter(mContext, R.layout.message, msgList);
        Log.d("DISIM","RUID : "+ruid);
        ruidList = new ArrayList<>();
    }

    public void setRuid(String ruid) {
        this.ruid = ruid;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        adapter.setUid(ruid);
        View result = inflater.inflate(layout, container, false);
        if (layout == R.layout.inbox) {

            msg = (ListView) result.findViewById(R.id.messageslist);
            fetch();
            msg.setAdapter(customListAdapter);
            msg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                Toast.makeText(mContext, "" + position, Toast.LENGTH_SHORT).show();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    MessageFragment mg = new MessageFragment();
                    mg.setLayout(R.layout.chat);
                    mg.setmContext(mContext);
                    Log.d("DISIM","adding this one ruid: "+ruidList.get(position));
                    mg.setRuid(ruidList.get(position));
                    ft.replace(R.id.contentLayout, mg);
                    ft.commit();
                }
            });
        } else {
            // chat layout
            ListView listOfMessages = (ListView) result.findViewById(R.id.list_of_messages);
            fetchMessages();
            listOfMessages.setAdapter(adapter);
            /*
            adapter = new FirebaseListAdapter<ChatMessage>(getActivity(), ChatMessage.class,
                    R.layout.message, FirebaseDatabase.getInstance().getReference().child("chat").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("messages").child(ruid)) {
                @Override
                protected void populateView(View v, ChatMessage model, int position) {
                    // Get references to the views of message.xml
                    TextView messageText = (TextView) v.findViewById(R.id.message_text);
                    TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                    TextView messageTime = (TextView) v.findViewById(R.id.message_time);

                    if (model.getMessageUser().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
                        // green and hide the other one
                        Log.d("GONETAG","-------SENDER-------");
                        ConstraintLayout receiver = (ConstraintLayout) v.findViewById(R.id.recv);
                        receiver.setVisibility(View.GONE);
                        ConstraintLayout general = (ConstraintLayout) v.findViewById(R.id.general);
                        general.setVisibility(View.GONE);
                        messageText = (TextView) v.findViewById(R.id.text_message_body);
                        messageUser = (TextView) v.findViewById(R.id.message_users);
                        messageTime = (TextView) v.findViewById(R.id.text_message_time);
                    } else if (model.getMessageUser().equals("System")) {
                        Log.d("GONETAG","-------System-------");
                        ConstraintLayout receiver = (ConstraintLayout) v.findViewById(R.id.recv);
                        receiver.setVisibility(View.GONE);
                        ConstraintLayout sender = (ConstraintLayout) v.findViewById(R.id.send);
                        sender.setVisibility(View.GONE);
                        TextView gen=(TextView) v.findViewById(R.id.textgeneral);
                        gen.setText(model.getMessageText()+" at "+DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                                model.getMessageTime()));
                    } else {
                        //receiver
                        Log.d("GONETAG","-------Receiver-------");
                        ConstraintLayout sender = (ConstraintLayout) v.findViewById(R.id.send);
                        sender.setVisibility(View.GONE);
                        ConstraintLayout general = (ConstraintLayout) v.findViewById(R.id.general);
                        general.setVisibility(View.GONE);
                    }
                    // Set their text
                    messageText.setText(model.getMessageText());
                    messageUser.setText(model.getMessageUser());

                    // Format the date before showing it
                    messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                            model.getMessageTime()));


                }
            };
*/
            listOfMessages.setAdapter(adapter);


            //send new message


//            FloatingActionButton fab = (FloatingActionButton) result.findViewById(R.id.fab);
            ImageButton fab = (ImageButton) result.findViewById(R.id.fab);
            final View res = result;
//            Button fab=(Button) result.findViewById(R.id.button_chatbox_send);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    EditText input = (EditText) res.findViewById(R.id.input);
//                    EditText input = (EditText) res.findViewById(R.id.edittext_chatbox);

                    // Read the input field and push a new instance
                    // of ChatMessage to the Firebase database
/*
        ref.child("chat").child(mFirebaseAuth.getCurrentUser().getUid()).push().setValue(ch1);
        ref.child("chat").child(uid).push().setValue(ch2);
         */

                    FirebaseDatabase.getInstance()
                            .getReference().child("chat").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("messages").child(ruid)
                            .push()
                            .setValue(new ChatMessage(input.getText().toString(),
                                    FirebaseAuth.getInstance()
                                            .getCurrentUser()
                                            .getDisplayName())
                            );

                    FirebaseDatabase.getInstance()
                            .getReference().child("chat").child(ruid).child("messages").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .push()
                            .setValue(new ChatMessage(input.getText().toString(),
                                    FirebaseAuth.getInstance()
                                            .getCurrentUser()
                                            .getDisplayName())
                            );
                    FirebaseDatabase.getInstance().getReference().child("messages").push().setValue(new Message(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), input.getText().toString()));
                    // Clear the input
                    input.setText("");
                }
            });
        }
        return result;
    }

    //private void fetchMessages() {
//        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("chat").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("messages").child(ruid);
//        mRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    adapter.add(data.getValue(ChatMessage.class));
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

    protected void fetchMessages() {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("chat").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("messages").child(ruid);
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    ChatMessage msg = dataSnapshot.getValue(ChatMessage.class);
                    adapter.add(msg);
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

            mRef.addChildEventListener(mChildEventListener);
        }

    }

    void fetch() {
        DatabaseReference msgRef = FirebaseDatabase.getInstance().getReference().child("chat").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("messages");
        msgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot dt : data.getChildren()) {
                        ChatMessage message = dt.getValue(ChatMessage.class);
                        //customListAdapter.add(message.getMessageText());
                        fetchData(message, data.getKey());
                        break;
                    }
                    ruidList.add(data.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        if (nChl == null) {
//            nChl = new ChildEventListener() {
//
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    for (DataSnapshot data : dataSnapshot.getChildren()) {
//                        for (DataSnapshot dt : data.getChildren()) {
//                            ChatMessage message = dt.getValue(ChatMessage.class);
//                            //customListAdapter.add(message.getMessageText());
//                            fetchData(message, data.getKey());
//                            break;
//                        }
//                        ruidList.add(data.getKey());
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
//            msgRef.addChildEventListener(nChl);
//        }

//        msgRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    for (DataSnapshot dt : data.getChildren()) {
//                        ChatMessage message = dt.getValue(ChatMessage.class);
//                        //customListAdapter.add(message.getMessageText());
//                        fetchData(message, data.getKey());
//                        break;
//                    }
//                    ruidList.add(data.getKey());
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


    }


    private void fetchData(final ChatMessage message, final String key) {
        DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.getKey().equals(key)) {
                        User u = data.getValue(User.class);
//                        Log.d("USRKT", "datasnap: " + data.getKey() + " Key: " + key + " user: " + u.getName() + " post: " + post.getPost());
                        customListAdapter.add(new Data(u, message));
                        customListAdapter.notifyDataSetChanged();
                        break;
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



//        userDatabaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    if (data.getKey().equals(key)) {
//                        User u = data.getValue(User.class);
////                        Log.d("USRKT", "datasnap: " + data.getKey() + " Key: " + key + " user: " + u.getName() + " post: " + post.getPost());
//                        customListAdapter.add(new Data(u, message));
//                        customListAdapter.notifyDataSetChanged();
//                    }
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

//        if (nChl == null) {
//            nChl = new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    for (DataSnapshot data : dataSnapshot.getChildren()) {
//                        if (data.getKey().equals(key)) {
//                            User u = data.getValue(User.class);
////                        Log.d("USRKT", "datasnap: " + data.getKey() + " Key: " + key + " user: " + u.getName() + " post: " + post.getPost());
//                            customListAdapter.add(new Data(u, message));
//                            customListAdapter.notifyDataSetChanged();
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
//            userDatabaseReference.addChildEventListener(nChl);
//        }


//customListAdapter.add(message.getMessageText());

//        DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
//        userDatabaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    Log.d("USRKT", "data " + data + " datakey: " + data.getKey());
//                    if (data.getKey().equals(key)) {
//                        User u = data.getValue(User.class);
////                        Log.d("USRKT", "datasnap: " + data.getKey() + " Key: " + key + " user: " + u.getName() + " post: " + post.getPost());
//                        //customListAdapter.add(new Data(post, u));
//                        customListAdapter.add(new Data(u, message));
//                        customListAdapter.notifyDataSetChanged();
//                        Log.d("USRKT", "data inserted");
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


        //        msgRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    for (DataSnapshot dt : data.getChildren()) {
//                        ChatMessage message = dt.getValue(ChatMessage.class);
//                        customListAdapter.add(message.getMessageText());
//                        break;
//                    }
//                    ruidList.add(data.getKey());
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

//
//        msgRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    for (DataSnapshot dt : data.getChildren()) {
//                        ChatMessage message = dt.getValue(ChatMessage.class);
//                        customListAdapter.add(message.getMessageText());
//                        break;
//                    }
//                    ruidList.add(data.getKey());
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

    }
}
