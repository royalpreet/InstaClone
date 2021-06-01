package com.example.instaclone;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.instaclone.util.FirebaseMethods;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Map;
import com.example.instaclone.R;

public class NextActivity extends AppCompatActivity {
    private static final String TAG = "NextActivity";
    private ImageView back;
    private Bitmap bitmap;
    private FirebaseFirestore db;
    private TextView description;
    private FirebaseMethods firebaseMethods;
    private ImageView image;
    private int imgCount = 0;
    private String imgUrl;
    private FirebaseAuth mAuth;
    private AuthStateListener mAuthStateListener;
    private TextView share;

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_next);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onCreate: selected image: ");
        String str = "selectedImage";
        stringBuilder.append(getIntent().getStringExtra(str));
        Log.d(TAG, stringBuilder.toString());
        this.imgUrl = getIntent().getStringExtra(str);
        this.bitmap = (Bitmap) getIntent().getParcelableExtra("selectedBitmap");
        this.firebaseMethods = new FirebaseMethods(this);
        this.back = (ImageView) findViewById(R.id.back);
        this.share = (TextView) findViewById(R.id.share);
        this.image = (ImageView) findViewById(R.id.shareImage);
        this.description = (TextView) findViewById(R.id.descriptionImage);
        this.back.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                NextActivity.this.finish();
            }
        });
        this.share.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(NextActivity.TAG, "onClick: sharing image");
                Toast.makeText(NextActivity.this.getApplicationContext(), "Uploading photo", 0).show();
                if (NextActivity.this.getIntent().hasExtra("selectedImage")) {
                    NextActivity.this.firebaseMethods.uploadPhoto("newPhoto", NextActivity.this.description.getText().toString(), NextActivity.this.imgCount, NextActivity.this.imgUrl, null);
                    return;
                }
                NextActivity.this.firebaseMethods.uploadPhoto("newPhoto", NextActivity.this.description.getText().toString(), NextActivity.this.imgCount, null, NextActivity.this.bitmap);
            }
        });
        setImage();
        setUpFirebaseAuth();
    }

    private void setImage() {
        if (getIntent().hasExtra("selectedImage")) {
            ((RequestBuilder) ((RequestBuilder) Glide.with((FragmentActivity) this).load(this.imgUrl).centerCrop()).placeholder((int) R.drawable.ic_android)).into(this.image);
        } else {
            this.image.setImageBitmap(this.bitmap);
        }
    }

    private void setUpFirebaseAuth() {
        Log.d(TAG, "setUpFirebaseAuth: setting up firebase auth");
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.mAuthStateListener = new AuthStateListener() {
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                String str = NextActivity.TAG;
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
        this.db.collection("user_photos").document(this.mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map data = documentSnapshot.getData();
                String str = NextActivity.TAG;
                if (data != null) {
                    Log.d(str, "onSuccess: documentSnapshot");
                    NextActivity.this.imgCount = documentSnapshot.getData().size();
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onSuccess: imgCount: ");
                stringBuilder.append(NextActivity.this.imgCount);
                Log.d(str, stringBuilder.toString());
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public void onStart() {
        super.onStart();
        this.mAuth.addAuthStateListener(this.mAuthStateListener);
    }

    /* Access modifiers changed, original: protected */
    public void onStop() {
        super.onStop();
        this.mAuth.removeAuthStateListener(this.mAuthStateListener);
    }
}
