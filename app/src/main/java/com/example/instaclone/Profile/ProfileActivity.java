package com.example.instaclone.Profile;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.example.instaclone.Profile.ProfileFragment.OnGridImageSelectedListener;
import com.example.instaclone.Profile.ViewPostFragment.OnCommentThreadSelectedListener;
import com.example.instaclone.R;
import com.example.instaclone.models.Photo;

public class ProfileActivity extends AppCompatActivity implements OnGridImageSelectedListener, OnCommentThreadSelectedListener, ViewProfileFragment.OnGridImageSelectedListener {
    private static final String TAG = "ProfileActivity";
    private ViewPostFragment viewPostFragment;

    public void onCommentThreadSelected(Photo photo) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onCommentThreadSelected: ");
        stringBuilder.append(photo);
        Log.d(TAG, stringBuilder.toString());
        ViewCommentsFragment viewCommentsFragment = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable("selected_photo", photo);
        viewCommentsFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, viewCommentsFragment);
        transaction.addToBackStack("View Comments Fragment");
        transaction.commit();
    }

    public void onGridImageSelected(Photo photo, int activityNumber) {
        Log.d(TAG, "onGridImageSelected: selected an image from gridView");
        this.viewPostFragment = new ViewPostFragment();
        Bundle args = new Bundle();
        args.putParcelable("selected_photo", photo);
        args.putInt("calling_activity", activityNumber);
        this.viewPostFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, this.viewPostFragment);
        transaction.addToBackStack("View Post Fragment");
        transaction.commit();
    }

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_profile);
        Log.d(TAG, "onCreate: activity starting");
        if (getIntent().hasExtra("searchActivity")) {
            ViewProfileFragment viewProfileFragment = new ViewProfileFragment();
            Bundle args = new Bundle();
            String str = "user_account_settings";
            args.putParcelable(str, getIntent().getParcelableExtra(str));
            viewProfileFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, viewProfileFragment);
            transaction.commit();
            return;
        }
        ProfileFragment profileFragment = new ProfileFragment();
        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
        transaction2.replace(R.id.container, profileFragment);
        transaction2.commit();
    }
}
