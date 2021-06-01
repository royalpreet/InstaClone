package com.example.instaclone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import com.example.instaclone.Home.MainActivity;
import com.example.instaclone.Profile.ProfileActivity;
import com.example.instaclone.Share.ShareActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;

public class LikeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Context context = this;

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_like);
        setUpBottomNavigationView();
    }

    public void setUpBottomNavigationView() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        this.bottomNavigationView = bottomNavigationView;
        bottomNavigationView.setOnNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add /*2131230783*/:
                        LikeActivity.this.context.startActivity(new Intent(LikeActivity.this.context, ShareActivity.class));
                        break;
                    case R.id.android /*2131230790*/:
                        LikeActivity.this.context.startActivity(new Intent(LikeActivity.this.context, ProfileActivity.class));
                        break;
                    case R.id.home /*2131230888*/:
                        LikeActivity.this.context.startActivity(new Intent(LikeActivity.this.context, MainActivity.class));
                        break;
                    case R.id.search /*2131231016*/:
                        LikeActivity.this.context.startActivity(new Intent(LikeActivity.this.context, SearchActivity.class));
                        break;
                }
                return false;
            }
        });
        this.bottomNavigationView.getMenu().getItem(3).setChecked(true);
    }

    /* Access modifiers changed, original: protected */
    public void onResume() {
        super.onResume();
        this.bottomNavigationView.getMenu().getItem(3).setChecked(true);
    }
}
