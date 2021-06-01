package com.example.instaclone.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import com.example.instaclone.Profile.ProfileActivity;
import com.example.instaclone.Profile.ViewCommentsFragment;
import com.example.instaclone.SearchActivity;
import com.example.instaclone.Share.ShareActivity;
import com.example.instaclone.login.LoginActivity;
import com.example.instaclone.models.Photo;
import com.example.instaclone.util.SectionsPageAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.instaclone.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static RelativeLayout relativeLayout;
    BottomNavigationView bottomNavigationView;
    Context context = this;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private AuthStateListener mAuthStateListener;

    public void onCommentThreadSelected(Photo photo) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onCommentThreadSelected: ");
        stringBuilder.append(photo);
        Log.d(TAG, stringBuilder.toString());
        ViewCommentsFragment viewCommentsFragment = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable("selected_photo", photo);
        args.putInt("main_activity", 1);
        viewCommentsFragment.setArguments(args);
        relativeLayout.setVisibility(View.GONE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, viewCommentsFragment);
        transaction.addToBackStack("View Comments Fragment");
        transaction.commit();
    }

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main);
        relativeLayout = (RelativeLayout) findViewById(R.id.relLayout);
        if (getIntent().hasExtra("end")) {
            finish();
        }
        setUpFirebaseAuth();
        setUpBottomNavigationView();
        setupViewPager();
    }

    private void setupViewPager() {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment());
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_feed);
    }

    private void setUpBottomNavigationView() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        this.bottomNavigationView = bottomNavigationView;
        bottomNavigationView.setOnNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.add) {
                    MainActivity.this.context.startActivity(new Intent(MainActivity.this.context, ShareActivity.class));
                } else if (itemId == R.id.android) {
                    MainActivity.this.context.startActivity(new Intent(MainActivity.this.context, ProfileActivity.class));
                } else if (itemId == R.id.search) {
                    MainActivity.this.context.startActivity(new Intent(MainActivity.this.context, SearchActivity.class));
                }
                return false;
            }
        });
        this.bottomNavigationView.getMenu().getItem(0).setChecked(true);
    }

    /* Access modifiers changed, original: protected */
    public void onResume() {
        super.onResume();
        this.bottomNavigationView.getMenu().getItem(0).setChecked(true);
    }

    private void setUpFirebaseAuth() {
        Log.d(TAG, "setUpFirebaseAuth: setting up firebase auth");
        FirebaseAuth instance = FirebaseAuth.getInstance();
        this.mAuth = instance;
        if (instance.getCurrentUser() == null) {
            startActivity(new Intent(this.context, LoginActivity.class));
            finish();
        }
        this.mAuthStateListener = new AuthStateListener() {
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                String str = MainActivity.TAG;
                if (user != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("onAuthStateChanged: ");
                    stringBuilder.append(user.getUid());
                    stringBuilder.append(" signed in");
                    Log.d(str, stringBuilder.toString());
                    return;
                }
                Log.d(str, "onAuthStateChanged: signed out");
            }
        };
    }

    /* Access modifiers changed, original: protected */
    public void onStart() {
        super.onStart();
        this.mAuth.addAuthStateListener(this.mAuthStateListener);
        this.db = FirebaseFirestore.getInstance();
        if (this.mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this.context, LoginActivity.class));
            finish();
        }
    }

    /* Access modifiers changed, original: protected */
    public void onStop() {
        super.onStop();
        this.mAuth.removeAuthStateListener(this.mAuthStateListener);
    }

    public void onBackPressed() {
        super.onBackPressed();
        if (relativeLayout.getVisibility() == View.GONE) {
            relativeLayout.setVisibility(View.VISIBLE);
        }
    }

}
