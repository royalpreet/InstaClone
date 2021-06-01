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
import com.example.instaclone.SearchActivity;
import com.example.instaclone.Share.ShareActivity;
import com.example.instaclone.models.Comment;
import com.example.instaclone.models.Photo;
import com.example.instaclone.models.UserAccountSettings;
import com.example.instaclone.util.FirebaseMethods;
import com.example.instaclone.util.GridImageAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import com.example.instaclone.R;

public class ViewProfileFragment extends Fragment {
    private static final String TAG = "ViewProfileFragment";
    public static RelativeLayout mainLayout;
    BottomNavigationView bottomNavigationView;
    Context context;
    private FirebaseFirestore db;
    TextView description;
    TextView displayName;
    TextView editProfile;
    private FirebaseMethods firebaseMethods;
    TextView follow;
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
    TextView unFollow;
    private UserAccountSettings userAccountSettings;
    TextView username;
    private ViewPostFragment viewPostFragment;

    public interface OnGridImageSelectedListener {
        void onGridImageSelected(Photo photo, int i);
    }

    ViewProfileFragment() {
        setArguments(new Bundle());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ViewProfileActivity starting");
        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);
        this.firebaseMethods = new FirebaseMethods(this.context);
        this.menu = (ImageView) view.findViewById(R.id.menu_image);
        this.follow = (TextView) view.findViewById(R.id.followButton);
        this.editProfile = (TextView) view.findViewById(R.id.editProfile);
        this.unFollow = (TextView) view.findViewById(R.id.unFollowButton);
        this.gridView = (GridView) view.findViewById(R.id.gridView);
        this.progressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);
        this.profilePhoto = (ImageView) view.findViewById(R.id.profile_image);
        this.username = (TextView) view.findViewById(R.id.name);
        this.context = getActivity();
        this.posts = (TextView) view.findViewById(R.id.posts);
        this.followers = (TextView) view.findViewById(R.id.followers);
        this.following = (TextView) view.findViewById(R.id.following);
        this.displayName = (TextView) view.findViewById(R.id.display_name);
        this.description = (TextView) view.findViewById(R.id.description);
        this.viewPostFragment = new ViewPostFragment();
        mainLayout = (RelativeLayout) view.findViewById(R.id.mainLayout);
        this.bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottom_navigation);
        this.progressBar.setVisibility(View.VISIBLE);
        this.userAccountSettings = getInfoFromBundle();
        this.menu.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ViewProfileFragment.this.startActivity(new Intent(ViewProfileFragment.this.getActivity(), AccountSettingsActivity.class));
            }
        });
        this.editProfile.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ViewProfileFragment.this.context, AccountSettingsActivity.class);
                intent.putExtra("editProfile", 1);
                ViewProfileFragment.this.startActivity(intent);
            }
        });
        setUpBottomNavigationView();
        setUpFirebaseAuth();
        setInfo();
        this.follow.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onClick: following user:");
                stringBuilder.append(ViewProfileFragment.this.userAccountSettings.getUsername());
                Log.d(ViewProfileFragment.TAG, stringBuilder.toString());
                HashMap<String, Object> update1 = new HashMap();
                String str = "following";
                update1.put(str, FieldValue.arrayUnion(ViewProfileFragment.this.userAccountSettings.getUser_id()));
                HashMap<String, Object> update2 = new HashMap();
                String str2 = "followers";
                update2.put(str2, FieldValue.arrayUnion(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                ViewProfileFragment.this.db.collection(str).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(update1, SetOptions.merge());
                ViewProfileFragment.this.db.collection(str2).document(ViewProfileFragment.this.userAccountSettings.getUser_id()).set(update2, SetOptions.merge());
                ViewProfileFragment.this.follow.setVisibility(View.GONE);
                ViewProfileFragment.this.unFollow.setVisibility(View.VISIBLE);
                ViewProfileFragment.this.followers.setText(String.valueOf(Long.parseLong(ViewProfileFragment.this.followers.getText().toString()) + 1));
                String str3 = "user_account_settings";
                ViewProfileFragment.this.db.collection(str3).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(str, FieldValue.increment(1), new Object[0]);
                ViewProfileFragment.this.db.collection(str3).document(ViewProfileFragment.this.userAccountSettings.getUser_id()).update(str2, FieldValue.increment(1), new Object[0]);
            }
        });
        this.unFollow.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onClick: UnFollowing user:");
                stringBuilder.append(ViewProfileFragment.this.userAccountSettings.getUsername());
                Log.d(ViewProfileFragment.TAG, stringBuilder.toString());
                String str = "following";
                ViewProfileFragment.this.db.collection(str).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(str, FieldValue.arrayRemove(ViewProfileFragment.this.userAccountSettings.getUser_id()), new Object[0]);
                String str2 = "followers";
                ViewProfileFragment.this.db.collection(str2).document(ViewProfileFragment.this.userAccountSettings.getUser_id()).update(str2, FieldValue.arrayRemove(FirebaseAuth.getInstance().getCurrentUser().getUid()), new Object[0]);
                ViewProfileFragment.this.follow.setVisibility(View.VISIBLE);
                ViewProfileFragment.this.unFollow.setVisibility(View.GONE);
                ViewProfileFragment.this.followers.setText(String.valueOf(Long.parseLong(ViewProfileFragment.this.followers.getText().toString()) - 1));
                String str3 = "user_account_settings";
                ViewProfileFragment.this.db.collection(str3).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(str, FieldValue.increment(-1), new Object[0]);
                ViewProfileFragment.this.db.collection(str3).document(ViewProfileFragment.this.userAccountSettings.getUser_id()).update(str2, FieldValue.increment(-1), new Object[0]);
            }
        });
        setUpGridView();
        setUpFollowOrUnFollowOrEditProfile();
        return view;
    }

    private void setUpFollowOrUnFollowOrEditProfile() {
        Log.d(TAG, "setUpFollowOrUnFollowOrEditProfile: ");
        if (this.userAccountSettings.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            this.editProfile.setVisibility(View.VISIBLE);
            this.menu.setVisibility(View.VISIBLE);
            return;
        }
        checkFollowOrUnFollow();
    }

    private void checkFollowOrUnFollow() {
        Log.d(TAG, "checkFollowOrUnFollow: ");
        this.db.collection("followers").document(this.userAccountSettings.getUser_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    try {
                        if (((ArrayList) documentSnapshot.get("followers")).contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            ViewProfileFragment.this.unFollow.setVisibility(View.VISIBLE);
                            return;
                        } else {
                            ViewProfileFragment.this.follow.setVisibility(View.VISIBLE);
                            return;
                        }
                    } catch (Exception e) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("onSuccess: Exception");
                        stringBuilder.append(e.getMessage());
                        Log.d(ViewProfileFragment.TAG, stringBuilder.toString());
                        ViewProfileFragment.this.follow.setVisibility(View.VISIBLE);
                        return;
                    }
                }
                ViewProfileFragment.this.follow.setVisibility(View.VISIBLE);
            }
        });
    }

    private UserAccountSettings getInfoFromBundle() {
        String str = "getInfoFromBundle: ";
        String str2 = TAG;
        Log.d(str2, str);
        Bundle bundle = getArguments();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        str = "user_account_settings";
        assert bundle != null;
        stringBuilder.append(String.valueOf(bundle.getParcelable(str)));
        Log.d(str2, stringBuilder.toString());
        if (bundle != null) {
            return (UserAccountSettings) bundle.getParcelable(str);
        }
        return null;
    }

    private void setInfo() {
        Log.d(TAG, "setInfo: setting info");
        this.username.setText(this.userAccountSettings.getUsername());
        this.posts.setText(String.valueOf(this.userAccountSettings.getPosts()));
        this.followers.setText(String.valueOf(this.userAccountSettings.getFollowers()));
        this.following.setText(String.valueOf(this.userAccountSettings.getFollowing()));
        this.displayName.setText(this.userAccountSettings.getDisplay_name());
        this.description.setText(this.userAccountSettings.getDescription());
        ((RequestBuilder) ((RequestBuilder) Glide.with(getActivity()).load(this.userAccountSettings.getProfile_photo()).centerCrop()).placeholder((int) R.drawable.ic_android)).into(this.profilePhoto);
    }

    private void setUpGridView() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("setUpGridView: setting up grid view of userId: ");
        stringBuilder.append(this.userAccountSettings.getUser_id());
        Log.d(TAG, stringBuilder.toString());
        this.db.collection("user_photos").document(this.userAccountSettings.getUser_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                NullPointerException e;
                StringBuilder stringBuilder;
                if (documentSnapshot.exists()) {
                    ArrayList<String> photoPaths = new ArrayList();
                    final ArrayList<Photo> allPhotos = new ArrayList();
                    Map<String, Object> photos = documentSnapshot.getData();
                    if (photos != null) {
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
                                            Log.d(ViewProfileFragment.TAG, stringBuilder.toString());
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
                                        Log.d(ViewProfileFragment.TAG, stringBuilder.toString());
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
                                Log.d(ViewProfileFragment.TAG, stringBuilder.toString());
                                photos = photos2;
                                comment = it;
                            }
                            photo.setComments(allComments);
                            allPhotos.add(photo);
                            photoPaths.add((String) singlePhoto.get(str));
                            photos = photos2;
                            comment = it;
                        }
                    }
                    ViewProfileFragment.this.gridView.setAdapter(new GridImageAdapter(ViewProfileFragment.this.getActivity(), R.layout.layout_grid_imageview, photoPaths));
                    ViewProfileFragment.this.gridView.setColumnWidth(ViewProfileFragment.this.getResources().getDisplayMetrics().widthPixels / 3);
                    ViewProfileFragment.this.gridView.setOnItemClickListener(new OnItemClickListener() {
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            ViewProfileFragment.this.onGridImageSelectedListener.onGridImageSelected((Photo) allPhotos.get(position), 4);
                        }
                    });
                }
                ViewProfileFragment.this.progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void setUpBottomNavigationView() {
        this.bottomNavigationView.setOnNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.add) {
                    ViewProfileFragment.this.context.startActivity(new Intent(ViewProfileFragment.this.context, ShareActivity.class));
                } else if (itemId == R.id.home) {
                    ViewProfileFragment.this.context.startActivity(new Intent(ViewProfileFragment.this.context, MainActivity.class));
                } else if (itemId == R.id.search) {
                    ViewProfileFragment.this.context.startActivity(new Intent(ViewProfileFragment.this.context, SearchActivity.class));
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
        super.onAttach(context);
        try {
            this.onGridImageSelectedListener = (OnGridImageSelectedListener) context;
        } catch (ClassCastException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("onAttach: ClassCastException");
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
                String str = ViewProfileFragment.TAG;
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
