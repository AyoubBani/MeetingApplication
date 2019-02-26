package app.rencontre.com.rencontreapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import app.rencontre.com.rencontreapp.Access;
import app.rencontre.com.rencontreapp.R;
import app.rencontre.com.rencontreapp.entities.ChatMessage;
import app.rencontre.com.rencontreapp.entities.User;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by famille on 6/11/2018.
 */

public class MessageAdapter extends ArrayAdapter<ChatMessage> {
    Context context;
    ArrayList<ChatMessage> list;
    int resource;
    String uid;
    User user;

    public MessageAdapter(Context context, int resource, ArrayList<ChatMessage> list) {
        super(context, resource, list);
        this.context = context;
        this.list = list;
        this.resource = resource;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ChatMessage getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage ch = list.get(position);
//        if (convertView == null) {

        if (ch.getMessageUser().equals("System")) {
            // system
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.message_general, null, true);

        } else if (ch.getMessageUser().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
            // sender
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.message, null, true);
        } else {
            // receiver
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.message_recv, null, true);
        }

//        }
        Log.d("NAMETG", "Username " + ch.getMessageUser() + " Message: " + ch.getMessageText());

        if (ch.getMessageUser().equals("System")) {
            TextView gen = (TextView) convertView.findViewById(R.id.textgeneral);
//            messageTime.setText(new SimpleDateFormat("EEE HH:mm").format(ch.getMessageTime()));
            gen.setText(ch.getMessageText() + " at " + new SimpleDateFormat("EEEE HH:mm").format(ch.getMessageTime()));
        } else if (ch.getMessageUser().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
            TextView messageText = (TextView) convertView.findViewById(R.id.text_message_body);
//            TextView messageUser = (TextView) convertView.findViewById(R.id.message_users);
            TextView messageTime = (TextView) convertView.findViewById(R.id.text_message_time);
            messageText.setText(ch.getMessageText());
//            messageUser.setText(ch.getMessageUser());

            // Format the date before showing it

            //new SimpleDateFormat("EEE").format(new Date());


//            messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", ch.getMessageTime()));
            messageTime.setText(new SimpleDateFormat("EEE HH:mm").format(ch.getMessageTime()));

            final ImageView img = (ImageView) convertView.findViewById(R.id.img);
            //getUserImg(img);
            Glide.with(context).load(Access.getImage(FirebaseAuth.getInstance().getCurrentUser())).asBitmap().centerCrop().into(new BitmapImageViewTarget(img) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    img.setImageDrawable(circularBitmapDrawable);
                }
            });

        } else {
            TextView messageText = (TextView) convertView.findViewById(R.id.message_text);
//            TextView messageUser = (TextView) convertView.findViewById(R.id.message_user);
            TextView messageTime = (TextView) convertView.findViewById(R.id.message_time);
            messageText.setText(ch.getMessageText());
//            messageUser.setText(ch.getMessageUser());

            // Format the date before showing it
            messageTime.setText(new SimpleDateFormat("EEE HH:mm").format(ch.getMessageTime()));
//            messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", ch.getMessageTime()));
            ImageView img = (ImageView) convertView.findViewById(R.id.imgus);
            getUserImg(img);
        }
//        TextView title = (TextView) convertView.findViewById(R.id.title);
//        title.setText(list.get(position));
        return convertView;
    }

    void getUserImg(final ImageView i) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.getKey().equals(uid)) {
                        user = data.getValue(User.class);
//                        Log.d("DISIM", "getUserImg  "+);
                        Log.d("DISIM", "getUserImg Img user " + user.getName() + " img: " + user.getUrl());
                        displayImg(i, user);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayImg(final ImageView img, User user) {
        Log.d("DISIM", "Img user " + user.getName() + " img: " + user.getUrl());

        Glide.with(context).load(user.getUrl()).asBitmap().centerCrop().into(new BitmapImageViewTarget(img) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                img.setImageDrawable(circularBitmapDrawable);
            }
        });
    }
}

