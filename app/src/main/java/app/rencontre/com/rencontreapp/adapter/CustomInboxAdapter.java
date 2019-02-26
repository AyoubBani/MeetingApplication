package app.rencontre.com.rencontreapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
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
import java.util.List;

import app.rencontre.com.rencontreapp.R;
import app.rencontre.com.rencontreapp.entities.Data;
import app.rencontre.com.rencontreapp.entities.Post;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by famille on 6/9/2018.
 */

public class CustomInboxAdapter extends ArrayAdapter<Data> {
    Context context;
    ArrayList<Data> messages;
    int resource;

    public CustomInboxAdapter(Context context, int resource, ArrayList<Data> messages) {
        super(context, resource, messages);
        this.context = context;
        this.messages = messages;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Data getItem(int i) {
        return messages.get(i);
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
            convertView = layoutInflater.inflate(R.layout.inbox_item, null, true);
        }
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView user = (TextView) convertView.findViewById(R.id.user);
        TextView date = (TextView) convertView.findViewById(R.id.datemessage);
        final ImageView img = (ImageView) convertView.findViewById(R.id.icon_av);
        title.setText(messages.get(position).getMessage().getMessageText());
        user.setText(messages.get(position).getMessage().getMessageUser());
        date.setText(new SimpleDateFormat("EEE HH:mm").format(messages.get(position).getMessage().getMessageTime()));

//        date.setText(DateFormat.format("HH:mm:ss",messages.get(position).getMessage().getMessageTime()));
        Glide.with(context).load(messages.get(position).getUser().getUrl()).asBitmap().centerCrop().into(new BitmapImageViewTarget(img) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                img.setImageDrawable(circularBitmapDrawable);
            }
        });
        return convertView;
    }
}
