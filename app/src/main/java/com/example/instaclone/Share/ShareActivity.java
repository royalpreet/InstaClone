package com.example.instaclone.Share;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.instaclone.util.Permissions;
import com.example.instaclone.util.SectionsPageAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.example.instaclone.R;

public class ShareActivity extends AppCompatActivity {
    private static final String TAG = "ShareActivity";
    BottomNavigationView bottomNavigationView;
    Context context = this;
    private TabLayout tabLayout;
    public ViewPager viewPager;

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_share);
        this.viewPager = (ViewPager) findViewById(R.id.viewPager);
        this.tabLayout = (TabLayout) findViewById(R.id.tabsShare);
        if (checkPermissionArray(Permissions.PERMISSIONS)) {
            setUpViewPager();
        } else {
            verifyPermissions(Permissions.PERMISSIONS);
        }
    }

    public int getCurrentTabNumber() {
        return this.viewPager.getCurrentItem();
    }

    public void setUpViewPager() {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new PhotoFragment());
        //adapter.addFragment(new GalleryFragment());
        this.viewPager.setAdapter(adapter);
        this.tabLayout.setupWithViewPager(this.viewPager);
        this.tabLayout.getTabAt(0).setText((CharSequence) "Photo");
        //this.tabLayout.getTabAt(1).setText((CharSequence) "Gallery");
    }

    public int getTask() {
        return getIntent().getFlags();
    }

    private boolean checkPermissionArray(String[] permissions) {
        Log.d(TAG, "checkPermissionArray: checking permissions");
        for (String checkPermissions : permissions) {
            if (!checkPermissions(checkPermissions)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkPermissions(String permission) {
        //int permissionRequest = ContextCompat.checkSelfPermission(this, permission);
        String str = TAG;
        StringBuilder stringBuilder;
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("checkPermissions: permission granted for: ");
            stringBuilder.append(permission);
            Log.d(str, stringBuilder.toString());
            return true;
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append("checkPermissions: permission not granted for: ");
        stringBuilder.append(permission);
        Log.d(str, stringBuilder.toString());
        return false;
    }

    private void verifyPermissions(String[] permissions) {
        Log.d(TAG, "verifyPermissions: verifying permissions");
        ActivityCompat.requestPermissions(this, permissions, 1);
    }
}
