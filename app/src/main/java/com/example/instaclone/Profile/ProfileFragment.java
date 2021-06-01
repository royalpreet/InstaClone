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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.instaclone.AccountSettings.AccountSettingsActivity;
import com.example.instaclone.Home.MainActivity;
import com.example.instaclone.R;
import com.example.instaclone.SearchActivity;
import com.example.instaclone.Share.ShareActivity;
import com.example.instaclone.models.Comment;
import com.example.instaclone.models.Photo;
import com.example.instaclone.models.UserAccountSettings;
import com.example.instaclone.util.FirebaseMethods;
import com.example.instaclone.util.GridImageAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    public static RelativeLayout mainLayout;
    BottomNavigationView bottomNavigationView;
    Context context;
    private FirebaseFirestore db;
    TextView description;
    TextView displayName;
    TextView editProfile;
    private FirebaseMethods firebaseMethods;
    TextView followers;
    TextView following;
    private GridView gridView;
    private FirebaseAuth mAuth;
    private AuthStateListener mAuthStateListener;
    private ImageView menu;
    OnGridImageSelectedListener onGridImageSelectedListener;
    TextView posts;
    ImageView profilePhoto;
    ProgressBar progressBar;
    TextView username;
    private ViewPostFragment viewPostFragment;

    public interface OnGridImageSelectedListener {
        void onGridImageSelected(Photo photo, int i);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        this.firebaseMethods = new FirebaseMethods(this.context);
        this.gridView = (GridView) view.findViewById(R.id.gridView);
        this.progressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);
        this.profilePhoto = (ImageView) view.findViewById(R.id.profile_image);
        this.username = (TextView) view.findViewById(R.id.name);
        this.context = getActivity();
        this.posts = (TextView) view.findViewById(R.id.posts);
        this.followers = (TextView) view.findViewById(R.id.followers);
        this.following = (TextView) view.findViewById(R.id.following);
        this.editProfile = (TextView) view.findViewById(R.id.editProfile);
        this.displayName = (TextView) view.findViewById(R.id.display_name);
        this.description = (TextView) view.findViewById(R.id.description);
        this.viewPostFragment = new ViewPostFragment();
        mainLayout = (RelativeLayout) view.findViewById(R.id.mainLayout);
        this.menu = (ImageView) view.findViewById(R.id.menu_image);
        this.bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottom_navigation);
        this.progressBar.setVisibility(View.VISIBLE);
        this.editProfile.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ProfileFragment.this.context, AccountSettingsActivity.class);
                intent.putExtra("editProfile", 1);
                ProfileFragment.this.startActivity(intent);
            }
        });
        setUpBottomNavigationView();
        setUpToolbar();
        setUpFirebaseAuth();
        setUpGridView();
        setInfo();
        return view;
    }

    private void setInfo() {
        Log.d(TAG, "setInfo: setting info");
        this.db.collection("user_account_settings").document(this.mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onSuccess: documentSnapshot: ");
                stringBuilder.append(documentSnapshot);
                Log.d(ProfileFragment.TAG, stringBuilder.toString());
                UserAccountSettings userAccountSettings = (UserAccountSettings) documentSnapshot.toObject(UserAccountSettings.class);
                ProfileFragment.this.username.setText(userAccountSettings.getUsername());
                ProfileFragment.this.posts.setText(String.valueOf(userAccountSettings.getPosts()));
                ProfileFragment.this.followers.setText(String.valueOf(userAccountSettings.getFollowers()));
                ProfileFragment.this.following.setText(String.valueOf(userAccountSettings.getFollowing()));
                ProfileFragment.this.displayName.setText(userAccountSettings.getDisplay_name());
                ProfileFragment.this.description.setText(userAccountSettings.getDescription());
                ((RequestBuilder) ((RequestBuilder) Glide.with(ProfileFragment.this.context).load(userAccountSettings.getProfile_photo()).centerCrop()).placeholder((int) R.drawable.ic_android)).into(ProfileFragment.this.profilePhoto);
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception e) {
                Log.d(ProfileFragment.TAG, "onFailure: couldn't get UserAccountSettingsInfo ");
                e.printStackTrace();
            }
        });
    }

    private void setUpGridView() {
        Log.d(TAG, "setUpGridView: setting up grid view");
        this.db.collection("user_photos").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                NullPointerException e;
                StringBuilder stringBuilder;
                if (documentSnapshot.exists()) {
                    ArrayList<String> photoPaths = new ArrayList();
                    final ArrayList<Photo> allPhotos = new ArrayList();
                    Map<String, Object> photos = documentSnapshot.getData();
                    if (photos != null) {
                        Log.i("Photos map size: ", String.valueOf(photos.size()));
                        Iterator comment = photos.entrySet().iterator();
                        while (comment.hasNext()) {
                            Map<String, Object> photos2;
                            Iterator it;
                            Map<String, Object> singlePhoto = (Map) ((Entry) comment.next()).getValue();
                            Photo photo = new Photo();
                            String str = "image_path";
                            photo.setImage_path((String) singlePhoto.get(str));
                            String str2 = "date_created";
                            photo.setDate_created((String) singlePhoto.get(str2));
                            String str3 = "user_id";
                            photo.setUser_id((String) singlePhoto.get(str3));
                            photo.setPhoto_id((String) singlePhoto.get("photo_id"));
                            photo.setCaption((String) singlePhoto.get("caption"));
                            photo.setLikes((ArrayList) singlePhoto.get("likes"));
                            ArrayList<Comment> allComments = new ArrayList();
                            try {
                                Iterator it2 = ((ArrayList) singlePhoto.get("comments")).iterator();
                                while (it2.hasNext()) {
                                    Map<String, Object> singleComment = (Map) it2.next();
                                    Comment comment2 = new Comment();
                                    photos2 = photos;
                                    try {
                                        it = comment;
                                        Comment comment3 = comment2;
                                        try {
                                            comment3.setComment((String) singleComment.get("comment"));
                                            comment3.setDate_created((String) singleComment.get(str2));
                                            comment3.setUser_id((String) singleComment.get(str3));
                                            allComments.add(comment3);
                                            photos = photos2;
                                            comment = it;
                                        } catch (NullPointerException e2) {
                                            e = e2;
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("onSuccess: no comments: ");
                                            stringBuilder.append(e.getMessage());
                                            Log.d(ProfileFragment.TAG, stringBuilder.toString());
                                            photo.setComments(allComments);
                                            allPhotos.add(photo);
                                            photoPaths.add((String) singlePhoto.get(str));
                                            photos = photos2;
                                            comment = it;
                                        }
                                    } catch (NullPointerException e3) {
                                        e = e3;
                                        it = comment;
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("onSuccess: no comments: ");
                                        stringBuilder.append(e.getMessage());
                                        Log.d(ProfileFragment.TAG, stringBuilder.toString());
                                        photo.setComments(allComments);
                                        allPhotos.add(photo);
                                        photoPaths.add((String) singlePhoto.get(str));
                                        photos = photos2;
                                        comment = it;
                                    }
                                }
                                photos2 = photos;
                                it = comment;
                            } catch (NullPointerException e4) {
                                e = e4;
                                photos2 = photos;
                                it = comment;
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("onSuccess: no comments: ");
                                stringBuilder.append(e.getMessage());
                                Log.d(ProfileFragment.TAG, stringBuilder.toString());
                                photos = photos2;
                                comment = it;
                            }
                            photo.setComments(allComments);
                            Log.i("All Photos size: ", String.valueOf(allPhotos.size()));
                            allPhotos.add(photo);
                            Log.i("All Photos size after: ", String.valueOf(allPhotos.size()));
                            photoPaths.add((String) singlePhoto.get(str));
                            photos = photos2;
                            comment = it;
                        }
                    }
                    Log.i("Photo paths size: ", String.valueOf(photoPaths.size()));
                    ProfileFragment.this.gridView.setAdapter(new GridImageAdapter(ProfileFragment.this.getActivity(), R.layout.layout_grid_imageview, photoPaths));
                    ProfileFragment.this.gridView.setColumnWidth(ProfileFragment.this.getResources().getDisplayMetrics().widthPixels / 3);
                    ProfileFragment.this.gridView.setOnItemClickListener(new OnItemClickListener() {
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            ProfileFragment.this.onGridImageSelectedListener.onGridImageSelected((Photo) allPhotos.get(position), 4);
                        }
                    });
                }
                ProfileFragment.this.progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void setUpToolbar() {
        this.menu.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ProfileFragment.this.startActivity(new Intent(ProfileFragment.this.getActivity(), AccountSettingsActivity.class));
            }
        });
    }

    public void setUpBottomNavigationView() {
        this.bottomNavigationView.setOnNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.add) {
                    ProfileFragment.this.context.startActivity(new Intent(ProfileFragment.this.context, ShareActivity.class));
                } else if (itemId == R.id.home) {
                    ProfileFragment.this.context.startActivity(new Intent(ProfileFragment.this.context, MainActivity.class));
                } else if (itemId == R.id.search) {
                    ProfileFragment.this.context.startActivity(new Intent(ProfileFragment.this.context, SearchActivity.class));
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
        mainLayout.setVisibility(View.VISIBLE);
    }

    public void onAttach(Context context) {
        String str = TAG;
        Log.d(str, "onAttach: ");
        try {
            this.onGridImageSelectedListener = (OnGridImageSelectedListener) getActivity();
        } catch (ClassCastException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("onAttach: ClassCastException");
            stringBuilder.append(e.getMessage());
            Log.d(str, stringBuilder.toString());
        }
        super.onAttach(context);
    }

    private void setUpFirebaseAuth() {
        Log.d(TAG, "setUpFirebaseAuth: setting up firebase auth");
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.mAuthStateListener = new AuthStateListener() {
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                String str = ProfileFragment.TAG;
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
