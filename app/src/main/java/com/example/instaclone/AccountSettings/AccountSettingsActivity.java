package com.example.instaclone.AccountSettings;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.example.instaclone.Home.MainActivity;
import com.example.instaclone.R;
import com.example.instaclone.SearchActivity;
import com.example.instaclone.Share.ShareActivity;
import com.example.instaclone.util.FirebaseMethods;
import com.example.instaclone.util.SectionsStatePageAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import java.util.ArrayList;

public class AccountSettingsActivity extends AppCompatActivity {
    private static final String TAG = "AccountSettingsActivity";
    BottomNavigationView bottomNavigationView;
    Context context = this;
    private FirebaseMethods firebaseMethods;
    private ListView listView;
    private RelativeLayout relativeLayout;
    public SectionsStatePageAdapter sectionsStatePageAdapter;
    public ViewPager viewPager;

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_account_settings);
        this.viewPager = (ViewPager) findViewById(R.id.viewPager);
        this.relativeLayout = (RelativeLayout) findViewById(R.id.relLayoutTop);
        this.listView = (ListView) findViewById(R.id.listAccountSettings);
        this.firebaseMethods = new FirebaseMethods(this.context);
        setUpToolbar();
        setUpSettingsList();
        setUpBottomNavigationView();
        setUpFragments();
        getIncomingIntent();
    }

    private void getIncomingIntent() {
        Log.d(TAG, "getIncomingIntent: ");
        Intent intent = getIntent();
        String str = "selectedImage";
        if (intent.hasExtra(str)) {
            this.firebaseMethods.uploadPhoto("profilePhoto", null, 0, intent.getStringExtra(str), null);
        }
        str = "bitmap";
        if (intent.hasExtra(str)) {
            this.firebaseMethods.uploadPhoto("profilePhoto", null, 0, null, (Bitmap) intent.getParcelableExtra(str));
        }
        if (intent.hasExtra("editProfile")) {
            setUpViewPager(this.sectionsStatePageAdapter.getFragmentNumber("Edit Profile"));
        }
    }

    private void setUpToolbar() {
        ((ImageView) findViewById(R.id.back)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AccountSettingsActivity.this.finish();
            }
        });
    }

    private void setUpFragments() {
        SectionsStatePageAdapter sectionsStatePageAdapter = new SectionsStatePageAdapter(getSupportFragmentManager());
        this.sectionsStatePageAdapter = sectionsStatePageAdapter;
        sectionsStatePageAdapter.addFragment(new EditProfileFragment(), "Edit Profile");
        this.sectionsStatePageAdapter.addFragment(new SignOutFragment(), "Sign Out");
    }

    public void setUpViewPager(Integer fragmentNumber) {
        this.relativeLayout.setVisibility(View.GONE);
        this.viewPager.setAdapter(this.sectionsStatePageAdapter);
        this.viewPager.setCurrentItem(fragmentNumber.intValue());
    }

    private void setUpSettingsList() {
        ArrayList<String> settingsList = new ArrayList();
        settingsList.add("Edit Profile");
        settingsList.add("Sign Out");
        this.listView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, settingsList));
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                AccountSettingsActivity.this.setUpViewPager(Integer.valueOf(position));
            }
        });
    }

    public void setUpBottomNavigationView() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        this.bottomNavigationView = bottomNavigationView;
        bottomNavigationView.setOnNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.add) {
                    AccountSettingsActivity.this.context.startActivity(new Intent(AccountSettingsActivity.this.context, ShareActivity.class));
                } else if (itemId == R.id.home) {
                    AccountSettingsActivity.this.context.startActivity(new Intent(AccountSettingsActivity.this.context, MainActivity.class));
                } else if (itemId == R.id.search) {
                    AccountSettingsActivity.this.context.startActivity(new Intent(AccountSettingsActivity.this.context, SearchActivity.class));
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
