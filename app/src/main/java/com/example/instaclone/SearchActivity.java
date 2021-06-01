package com.example.instaclone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.instaclone.Home.MainActivity;
import com.example.instaclone.Profile.ProfileActivity;
import com.example.instaclone.Share.ShareActivity;
import com.example.instaclone.models.UserAccountSettings;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.instaclone.R;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private BottomNavigationView bottomNavigationView;
    private Context context;
    private FirebaseFirestore db;
    private TextView displayName;
    private ImageView profilePhoto;
    private RelativeLayout relativeLayout;
    private ImageView searchImage;
    private EditText searchText;
    private UserAccountSettings userAccountSettings;
    private TextView username;

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_search);
        this.username = (TextView) findViewById(R.id.username);
        this.context = this;
        this.displayName = (TextView) findViewById(R.id.display_name);
        this.searchText = (EditText) findViewById(R.id.searchText);
        this.profilePhoto = (ImageView) findViewById(R.id.profilePhoto);
        this.searchImage = (ImageView) findViewById(R.id.searchImage);
        this.relativeLayout = (RelativeLayout) findViewById(R.id.relLayout2);
        setUpSearchText();
        setUpBottomNavigationView();
        this.searchImage.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SearchActivity.this.searchText.requestFocus();
                ((InputMethodManager) SearchActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(SearchActivity.this.searchText, 1);
            }
        });
        this.relativeLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this.context, ProfileActivity.class);
                intent.putExtra("searchActivity", 1);
                intent.putExtra("user_account_settings", SearchActivity.this.userAccountSettings);
                SearchActivity.this.startActivity(intent);
            }
        });
    }

    private void setUpSearchText() {
        Log.d(TAG, "setUpSearchText: ");
        this.searchText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                String str = "";
                if (SearchActivity.this.searchText.getText().toString().equals(str)) {
                    SearchActivity.this.username.setText(str);
                    SearchActivity.this.displayName.setText(str);
                    SearchActivity.this.profilePhoto.setImageResource(0);
                    return;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("afterTextChanged: ");
                stringBuilder.append(SearchActivity.this.searchText.getText().toString());
                Log.d(SearchActivity.TAG, stringBuilder.toString());
                SearchActivity searchActivity = SearchActivity.this;
                searchActivity.searchForUsername(searchActivity.searchText.getText().toString());
            }
        });
    }

    private void searchForUsername(String usernameSearch) {
        Log.d(TAG, "searchForUsername: ");
        FirebaseFirestore instance = FirebaseFirestore.getInstance();
        this.db = instance;
        instance.collection("user_account_settings").whereEqualTo("username", (Object) usernameSearch).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    Log.d(SearchActivity.TAG, "onSuccess: setting");
                    SearchActivity.this.userAccountSettings = (UserAccountSettings) ((DocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0)).toObject(UserAccountSettings.class);
                    SearchActivity.this.username.setText((String) ((DocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0)).get("username"));
                    SearchActivity.this.displayName.setText((String) ((DocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0)).get("display_name"));
                    ((RequestBuilder) ((RequestBuilder) Glide.with(SearchActivity.this).load((String) ((DocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0)).get("profile_photo")).centerCrop()).placeholder((int) R.drawable.ic_android)).into(SearchActivity.this.profilePhoto);
                }
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
                    SearchActivity.this.context.startActivity(new Intent(SearchActivity.this.context, ShareActivity.class));
                } else if (itemId == R.id.android) {
                    SearchActivity.this.context.startActivity(new Intent(SearchActivity.this.context, ProfileActivity.class));
                } else if (itemId == R.id.home) {
                    SearchActivity.this.context.startActivity(new Intent(SearchActivity.this.context, MainActivity.class));
                }
                return false;
            }
        });
        this.bottomNavigationView.getMenu().getItem(1).setChecked(true);
    }

    /* Access modifiers changed, original: protected */
    public void onResume() {
        super.onResume();
        this.bottomNavigationView.getMenu().getItem(1).setChecked(true);
    }
}
