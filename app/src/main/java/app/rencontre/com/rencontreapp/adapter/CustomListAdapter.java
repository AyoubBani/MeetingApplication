package app.rencontre.com.rencontreapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import app.rencontre.com.rencontreapp.Access;
import app.rencontre.com.rencontreapp.R;
import app.rencontre.com.rencontreapp.entities.Data;
import app.rencontre.com.rencontreapp.entities.Post;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by famille on 5/30/2018.
 */

public class CustomListAdapter extends ArrayAdapter<Data> {
    Context context;
    ArrayList<Data> postsList;
    int resource;

    public CustomListAdapter(Context context, int resource, ArrayList<Data> postsList) {
        super(context, resource, postsList);
        this.postsList = postsList;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return postsList.size();
    }

    @Override
    public Data getItem(int i) {
        return postsList.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.post_item, null, true);
        }
        TextView username = (TextView) convertView.findViewById(R.id.user);
        TextView postText = (TextView) convertView.findViewById(R.id.post);
        TextView date = (TextView) convertView.findViewById(R.id.datepost);
        final ImageView profileimg = (ImageView) convertView.findViewById(R.id.icon_avata);
        username.setText(postsList.get(position).getPost().getUsername());
        postText.setText(postsList.get(position).getPost().getPost());
//        date.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", postsList.get(position).getPost().getDate()));
//        date.setText(DateFormat.format("HH:mm:ss", postsList.get(position).getPost().getDate()));
        date.setText(new SimpleDateFormat("EEE HH:mm").format(postsList.get(position).getPost().getDate()));


        Glide.with(context).load(postsList.get(position).getUser().getUrl()).asBitmap().centerCrop().into(new BitmapImageViewTarget(profileimg) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                profileimg.setImageDrawable(circularBitmapDrawable);
            }
        });


        return convertView;
    }
}
