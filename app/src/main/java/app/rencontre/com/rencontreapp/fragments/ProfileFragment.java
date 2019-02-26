package app.rencontre.com.rencontreapp.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.ArrayList;
import java.util.List;

import app.rencontre.com.rencontreapp.Access;
import app.rencontre.com.rencontreapp.R;
import app.rencontre.com.rencontreapp.adapter.UserInfoAdapter;
import app.rencontre.com.rencontreapp.entities.Configuration;

/**
 * Created by famille on 5/30/2018.
 */

public class ProfileFragment extends Fragment {
    Context mContext;
    FirebaseUser user;
    private List<Configuration> listConfig = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserInfoAdapter infoAdapter;


    private static final String USERNAME_LABEL = "Username";
    private static final String EMAIL_LABEL = "Email";
    private static final String SIGNOUT_LABEL = "Sign out";
    private static final String RESETPASS_LABEL = "Change Password";

    public ProfileFragment() {

    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.profile, container, false);
        TextView username = result.findViewById(R.id.profileusername);
//        TextView useremail = result.findViewById(R.id.profileemail);
        final ImageView img = (ImageView) result.findViewById(R.id.profileImg);
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Uri url = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();

            Glide.with(mContext).load(Access.getImage(user)).asBitmap().centerCrop().into(new BitmapImageViewTarget(img) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    img.setImageDrawable(circularBitmapDrawable);
                }
            });



            //Glide.with(mContext).load(Access.getImage(user)).apply(RequestOptions.circleCropTransform()).into(img);
            username.setText(user.getDisplayName());
//            useremail.setText(user.getEmail());
        }
        setupArrayListInfo(user);

        recyclerView = (RecyclerView) result.findViewById(R.id.info_recycler_view);
        infoAdapter = new UserInfoAdapter(listConfig);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(result.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(infoAdapter);


        return result;
    }

    public void setupArrayListInfo(FirebaseUser myAccount){
        listConfig.clear();
        Configuration userNameConfig = new Configuration(USERNAME_LABEL, myAccount.getDisplayName(), R.mipmap.ic_account_box);
        listConfig.add(userNameConfig);

        Configuration emailConfig = new Configuration(EMAIL_LABEL, myAccount.getEmail(), R.mipmap.ic_email);
        listConfig.add(emailConfig);

        Configuration resetPass = new Configuration(RESETPASS_LABEL, "", R.mipmap.ic_restore);
        listConfig.add(resetPass);

        Configuration signout = new Configuration(SIGNOUT_LABEL, "", R.mipmap.ic_power_settings);
        listConfig.add(signout);
    }



}
