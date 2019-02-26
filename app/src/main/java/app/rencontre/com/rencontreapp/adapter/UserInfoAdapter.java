package app.rencontre.com.rencontreapp.adapter;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import app.rencontre.com.rencontreapp.R;
import app.rencontre.com.rencontreapp.entities.Configuration;

import static java.security.AccessController.getContext;

/**
 * Created by famille on 6/12/2018.
 */

public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.ViewHolder>{
    private List<Configuration> profileConfig;

    public UserInfoAdapter(List<Configuration> profileConfig){
        this.profileConfig = profileConfig;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_info_item_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Configuration config = profileConfig.get(position);
        holder.label.setText(config.getLabel());
        holder.value.setText(config.getValue());
        holder.icon.setImageResource(config.getIcon());
    }



    @Override
    public int getItemCount() {
        return profileConfig.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView label, value;
        public ImageView icon;
        public ViewHolder(View view) {
            super(view);
            label = (TextView)view.findViewById(R.id.tv_title);
            value = (TextView)view.findViewById(R.id.tv_detail);
            icon = (ImageView)view.findViewById(R.id.img_icon);
        }
    }
}
