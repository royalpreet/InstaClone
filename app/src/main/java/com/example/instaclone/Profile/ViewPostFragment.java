package com.example.instaclone.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.instaclone.Home.MainActivity;
import com.example.instaclone.R;
import com.example.instaclone.SearchActivity;
import com.example.instaclone.Share.ShareActivity;
import com.example.instaclone.models.Photo;
import com.example.instaclone.models.UserAccountSettings;
import com.example.instaclone.util.FirebaseMethods;
import com.example.instaclone.util.SquareImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ViewPostFragment extends Fragment {
    private static final String TAG = "ViewPostFragment";
    private ImageView back;
    private BottomNavigationView bottomNavigationView;
    private TextView comments;
    private ImageView commentsImage;
    private Context context;
    private FirebaseFirestore db;
    private TextView description;
    private FirebaseMethods firebaseMethods;
    private TextView likes;
    private long likesCount;
    private FirebaseAuth mAuth;
    private AuthStateListener mAuthStateListener;
    OnCommentThreadSelectedListener onCommentThreadSelectedListener;
    private ImageView outlineHeart;
    private Photo photo;
    private SquareImageView postImage;
    private ImageView profilePhoto;
    private ImageView redHeart;
    private TextView timestamp;
    private TextView username;

    public interface OnCommentThreadSelectedListener {
        void onCommentThreadSelected(Photo photo);
    }

    ViewPostFragment() {
        setArguments(new Bundle());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);
        this.context = getActivity();
        this.likesCount = 0;
        this.firebaseMethods = new FirebaseMethods(this.context);
        this.bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottom_navigation);
        this.postImage = (SquareImageView) view.findViewById(R.id.postImage);
        this.redHeart = (ImageView) view.findViewById(R.id.redHeart);
        this.outlineHeart = (ImageView) view.findViewById(R.id.outlineHeart);
        this.likes = (TextView) view.findViewById(R.id.likes);
        this.description = (TextView) view.findViewById(R.id.descriptionPhoto);
        this.comments = (TextView) view.findViewById(R.id.viewComments);
        this.timestamp = (TextView) view.findViewById(R.id.timeStamp);
        this.username = (TextView) view.findViewById(R.id.username);
        this.profilePhoto = (ImageView) view.findViewById(R.id.profilePhoto);
        this.commentsImage = (ImageView) view.findViewById(R.id.commentsImage);
        this.back = (ImageView) view.findViewById(R.id.back);
        setUpBottomNavigationView();
        setUpFirebaseAuth();
        this.photo = getPhotoFromBundle();
        setUpWidgets();
        setUpHeart();
        this.back.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(ViewPostFragment.TAG, "onClick: navigating back to profile fragment");
                ViewPostFragment.this.getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        this.commentsImage.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ViewPostFragment.this.onCommentThreadSelectedListener.onCommentThreadSelected(ViewPostFragment.this.photo);
            }
        });
        this.comments.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ViewPostFragment.this.onCommentThreadSelectedListener.onCommentThreadSelected(ViewPostFragment.this.photo);
            }
        });
        return view;
    }

    private void setUpHeart() {
        Log.d(TAG, "setUpHeart: ");
        this.redHeart.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ViewPostFragment.this.redHeart.getVisibility() == View.VISIBLE) {
                    ViewPostFragment.this.redHeart.setVisibility(View.GONE);
                    ViewPostFragment.this.outlineHeart.setVisibility(View.VISIBLE);
                    ViewPostFragment.this.removeLike();
                }
            }
        });
        this.outlineHeart.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ViewPostFragment.this.outlineHeart.getVisibility() == View.VISIBLE) {
                    ViewPostFragment.this.outlineHeart.setVisibility(View.GONE);
                    ViewPostFragment.this.redHeart.setVisibility(View.VISIBLE);
                    ViewPostFragment.this.addLike();
                }
            }
        });
    }

    private void removeLike() {
        Log.d(TAG, "removeLike: ");
        String str = "likes";
        this.db.collection("photos").document(this.photo.getPhoto_id()).update(str, FieldValue.arrayRemove(this.mAuth.getUid()), new Object[0]);
        DocumentReference document = this.db.collection("user_photos").document(this.photo.getUser_id());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.photo.getPhoto_id());
        stringBuilder.append(".likes");
        document.update(stringBuilder.toString(), FieldValue.arrayRemove(this.mAuth.getUid()), new Object[0]);
        long j = this.likesCount - 1;
        this.likesCount = j;
        TextView textView;
        StringBuilder stringBuilder2;
        if (j == 0 || j == 1) {
            textView = this.likes;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(String.valueOf(this.likesCount));
            stringBuilder2.append(" like");
            textView.setText(stringBuilder2.toString());
            return;
        }
        textView = this.likes;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(String.valueOf(this.likesCount));
        stringBuilder2.append(" likes");
        textView.setText(stringBuilder2.toString());
    }

    private void addLike() {
        Log.d(TAG, "addLike: ");
        String str = "likes";
        this.db.collection("photos").document(this.photo.getPhoto_id()).update(str, FieldValue.arrayUnion(this.mAuth.getUid()), new Object[0]);
        DocumentReference document = this.db.collection("user_photos").document(this.photo.getUser_id());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.photo.getPhoto_id());
        stringBuilder.append(".likes");
        document.update(stringBuilder.toString(), FieldValue.arrayUnion(this.mAuth.getUid()), new Object[0]);
        long j = this.likesCount + 1;
        this.likesCount = j;
        TextView textView;
        StringBuilder stringBuilder2;
        if (j == 0 || j == 1) {
            textView = this.likes;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(String.valueOf(this.likesCount));
            stringBuilder2.append(" like");
            textView.setText(stringBuilder2.toString());
            return;
        }
        textView = this.likes;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(String.valueOf(this.likesCount));
        stringBuilder2.append(" likes");
        textView.setText(stringBuilder2.toString());
    }

    private void setLikesAndHeartColor() {
        String str = TAG;
        Log.d(str, "setLikesAndHeartColor: ");
        try {
            this.likesCount = (long) this.photo.getLikes().size();
            if (this.photo.getLikes().contains(this.mAuth.getUid())) {
                this.redHeart.setVisibility(View.VISIBLE);
                this.outlineHeart.setVisibility(View.GONE);
            } else {
                this.outlineHeart.setVisibility(View.VISIBLE);
                this.redHeart.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("setLikesAndHeartColor: Exception: ");
            stringBuilder.append(e.getMessage());
            Log.d(str, stringBuilder.toString());
            this.outlineHeart.setVisibility(View.VISIBLE);
            this.redHeart.setVisibility(View.GONE);
            this.likesCount = 0;
        }
        long j = this.likesCount;
        TextView textView;
        StringBuilder stringBuilder2;
        if (j == 0 || j == 1) {
            textView = this.likes;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(String.valueOf(this.likesCount));
            stringBuilder2.append(" like");
            textView.setText(stringBuilder2.toString());
            return;
        }
        textView = this.likes;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(String.valueOf(this.likesCount));
        stringBuilder2.append(" likes");
        textView.setText(stringBuilder2.toString());
    }

    private void setUpWidgets() {
        TextView textView;
        StringBuilder stringBuilder;
        String timeDifference = getTimeStampDifference();
        if (timeDifference.equals("0")) {
            this.timestamp.setText("Today");
        } else if (timeDifference.equals("1")) {
            textView = this.timestamp;
            stringBuilder = new StringBuilder();
            stringBuilder.append(timeDifference);
            stringBuilder.append(" day ago");
            textView.setText(stringBuilder.toString());
        } else {
            textView = this.timestamp;
            stringBuilder = new StringBuilder();
            stringBuilder.append(timeDifference);
            stringBuilder.append(" days ago");
            textView.setText(stringBuilder.toString());
        }
        ((RequestBuilder) ((RequestBuilder) Glide.with(this.context).load(this.photo.getImage_path()).centerCrop()).placeholder((int) R.drawable.ic_android)).into(this.postImage);
        this.db.collection("user_account_settings").document(this.photo.getUser_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onSuccess: documentSnapshot: ");
                stringBuilder.append(documentSnapshot);
                Log.d(ViewPostFragment.TAG, stringBuilder.toString());
                UserAccountSettings userAccountSettings = (UserAccountSettings) documentSnapshot.toObject(UserAccountSettings.class);
                ((RequestBuilder) ((RequestBuilder) Glide.with(ViewPostFragment.this.context).load(userAccountSettings.getProfile_photo()).centerCrop()).placeholder((int) R.drawable.ic_android)).into(ViewPostFragment.this.profilePhoto);
                ViewPostFragment.this.username.setText(userAccountSettings.getUsername());
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception e) {
                Log.d(ViewPostFragment.TAG, "onFailure: couldn't get UserAccountSettingsInfo ");
                e.printStackTrace();
            }
        });
        setLikesAndHeartColor();
        this.description.setText(this.photo.getCaption());
        String str = "No comments";
        if (this.photo.getComments() == null) {
            this.comments.setText(str);
        } else if (this.photo.getComments().size() == 0) {
            this.comments.setText(str);
        } else if (this.photo.getComments().size() == 1) {
            this.comments.setText("View 1 comment");
        } else {
            textView = this.comments;
            stringBuilder = new StringBuilder();
            stringBuilder.append("View ");
            stringBuilder.append(this.photo.getComments().size());
            stringBuilder.append(" comments");
            textView.setText(stringBuilder.toString());
        }
    }

    private String getTimeStampDifference() {
        String difference = TAG;
        Log.d(difference, "getTimeStampDifference: ");
        String difference2 = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        Date today = c.getTime();
        sdf.format(today);
        String photoTimestamp = this.photo.getDate_created();
        try {
            Log.d(difference, "getTimeStampDifference: success");
            difference = String.valueOf(Math.round((float) ((today.getTime() - sdf.parse(photoTimestamp).getTime()) / 86400000)));
            return difference;
        } catch (ParseException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getTimeStampDifference: ParseException: ");
            stringBuilder.append(e.getMessage());
            Log.d(difference, stringBuilder.toString());
            return "0";
        }
    }

    private Photo getPhotoFromBundle() {
        Log.d(TAG, "getPhotoFromBundle: ");
        Bundle bundle = getArguments();
        if (bundle != null) {
            return (Photo) bundle.getParcelable("selected_photo");
        }
        return null;
    }

    public void setUpBottomNavigationView() {
        this.bottomNavigationView.setOnNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.add) {
                    ViewPostFragment.this.context.startActivity(new Intent(ViewPostFragment.this.context, ShareActivity.class));
                } else if (itemId == R.id.home) {
                    ViewPostFragment.this.context.startActivity(new Intent(ViewPostFragment.this.context, MainActivity.class));
                } else if (itemId == R.id.search) {
                    ViewPostFragment.this.context.startActivity(new Intent(ViewPostFragment.this.context, SearchActivity.class));
                }
                return false;
            }
        });
        this.bottomNavigationView.getMenu().getItem(3).setChecked(true);
    }

    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: resuming");
        this.bottomNavigationView.getMenu().getItem(3).setChecked(true);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.onCommentThreadSelectedListener = (OnCommentThreadSelectedListener) getActivity();
        } catch (ClassCastException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("onAttach: ClassCastException: ");
            stringBuilder.append(e.getMessage());
            Log.d(TAG, stringBuilder.toString());
        }
    }

    private void setUpFirebaseAuth() {
        Log.d(TAG, "setUpFirebaseAuth: setting up firebase auth");
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.mAuthStateListener = new AuthStateListener() {
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                String str = ViewPostFragment.TAG;
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

    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: starting");
        this.mAuth.addAuthStateListener(this.mAuthStateListener);
    }

    public void onStop() {
        super.onStop();
        this.mAuth.removeAuthStateListener(this.mAuthStateListener);
    }
}
