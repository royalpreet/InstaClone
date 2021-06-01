package com.example.instaclone.Home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;

import com.example.instaclone.models.Comment;
import com.example.instaclone.models.Photo;
import com.example.instaclone.util.MainFeedAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.primitives.Ints;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import com.example.instaclone.R;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private MainFeedAdapter adapter;
    private Context context;
    private FirebaseFirestore db;
    private ArrayList<String> followingList;
    private boolean isScrolling = false;
    private ArrayList<Photo> mPaginatedPhotos;
    private ArrayList<Photo> mPhotos;
    private LinearLayoutManager manager;
    private TextView noPosts;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private int scrolledOutItems;
    private int totalItems;
    private int totalPhotos = 0;
    private int visibleItems;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        this.recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        this.noPosts = (TextView) view.findViewById(R.id.noPosts);
        this.mPhotos = new ArrayList();
        this.mPaginatedPhotos = new ArrayList();
        this.followingList = new ArrayList();
        this.context = getActivity();
        getFollowing();
        return view;
    }

    public void onResume() {
        super.onResume();
        MainActivity.relativeLayout.setVisibility(View.VISIBLE);
    }

    private void getFollowing() {
        Log.d(TAG, "getFollowing: ");
        FirebaseFirestore instance = FirebaseFirestore.getInstance();
        this.db = instance;
        instance.collection("following").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                HomeFragment.this.followingList = (ArrayList) documentSnapshot.get("following");
                if (HomeFragment.this.followingList == null) {
                    HomeFragment.this.followingList = new ArrayList();
                }
                HomeFragment.this.followingList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                HomeFragment homeFragment = HomeFragment.this;
                homeFragment.getPhotos(homeFragment.followingList);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onSuccess: followingList[0]: ");
                stringBuilder.append((String) HomeFragment.this.followingList.get(0));
                stringBuilder.append(" ");
                stringBuilder.append(FirebaseAuth.getInstance().getCurrentUser().getUid());
                Log.d(HomeFragment.TAG, stringBuilder.toString());
            }
        });
    }

    private void getPhotos(final ArrayList<String> followingList) {
        Log.d(TAG, "getPhotos: ");
        if (followingList != null) {
            for (int i = 0; i < followingList.size(); i++) {
                final int index = i;
                this.db.collection("user_photos").document((String) followingList.get(i)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        StringBuilder stringBuilder;
                        boolean exists = documentSnapshot.exists();
                        String str = HomeFragment.TAG;
                        if (exists) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("onSuccess: totalPhotos documentSnapshot exists for index:");
                            stringBuilder.append(index);
                            Log.d(str, stringBuilder.toString());
                            Map<String, Object> photos = documentSnapshot.getData();
                            if (photos != null) {
                                for (Entry<String, Object> entry : photos.entrySet()) {
                                    Map<String, Object> singlePhoto = (Map) entry.getValue();
                                    Photo photo = new Photo();
                                    photo.setImage_path((String) singlePhoto.get("image_path"));
                                    String str2 = "date_created";
                                    photo.setDate_created((String) singlePhoto.get(str2));
                                    String str3 = "user_id";
                                    photo.setUser_id((String) singlePhoto.get(str3));
                                    photo.setPhoto_id((String) singlePhoto.get("photo_id"));
                                    photo.setCaption((String) singlePhoto.get("caption"));
                                    photo.setLikes((ArrayList) singlePhoto.get("likes"));
                                    ArrayList<Comment> allComments = new ArrayList();
                                    try {
                                        Iterator it = ((ArrayList) singlePhoto.get("comments")).iterator();
                                        while (it.hasNext()) {
                                            Map<String, Object> singleComment = (Map) it.next();
                                            Comment comment = new Comment();
                                            comment.setComment((String) singleComment.get("comment"));
                                            comment.setDate_created((String) singleComment.get(str2));
                                            comment.setUser_id((String) singleComment.get(str3));
                                            allComments.add(comment);
                                        }
                                    } catch (NullPointerException e) {
                                        StringBuilder stringBuilder2 = new StringBuilder();
                                        stringBuilder2.append("onSuccess: no comments: ");
                                        stringBuilder2.append(e.getMessage());
                                        Log.d(str, stringBuilder2.toString());
                                    }
                                    photo.setComments(allComments);
                                    HomeFragment.this.mPhotos.add(photo);
                                    HomeFragment.this.totalPhotos = HomeFragment.this.totalPhotos + 1;
                                    HomeFragment.this.noPosts.setVisibility(View.GONE);
                                    Log.d(str, "onSuccess: totalPhotos");
                                }
                            }
                        }
                        if (index == followingList.size() - 1) {
                            stringBuilder = new StringBuilder();
                            String str4 = "onSuccess: totalPhotos setting adapter: ";
                            stringBuilder.append(str4);
                            stringBuilder.append(HomeFragment.this.mPhotos.size());
                            Log.d(str, stringBuilder.toString());
                            HomeFragment.this.progressBar.setVisibility(View.GONE);
                            HomeFragment.this.adapter = new MainFeedAdapter(HomeFragment.this.context, HomeFragment.this.mPhotos);
                            HomeFragment.this.manager = new LinearLayoutManager(HomeFragment.this.context);
                            HomeFragment.this.recyclerView.setLayoutManager(HomeFragment.this.manager);
                            HomeFragment.this.recyclerView.setAdapter(HomeFragment.this.adapter);
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str4);
                            stringBuilder.append(HomeFragment.this.mPhotos.size());
                            Log.d(str, stringBuilder.toString());
                        }
                    }
                });
            }
        }
    }

    private void displayPosts() {
        String str = TAG;
        Log.d(str, "displayPosts: ");
        this.progressBar.setVisibility(View.GONE);
        this.adapter = new MainFeedAdapter(this.context, this.mPaginatedPhotos);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.context);
        this.manager = linearLayoutManager;
        this.recyclerView.setLayoutManager(linearLayoutManager);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onSuccess: totalPhotos: ");
        stringBuilder.append(this.totalPhotos);
        Log.d(str, stringBuilder.toString());
        int j = 0;
        while (j < 3) {
            try {
                this.mPaginatedPhotos.add(this.mPhotos.get(j));
                j++;
            } catch (Exception e) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("onSuccess: Exception: ");
                stringBuilder2.append(e.getMessage());
                Log.d(str, stringBuilder2.toString());
            }
        }
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.addOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                HomeFragment homeFragment = HomeFragment.this;
                homeFragment.visibleItems = homeFragment.manager.getChildCount();
                homeFragment = HomeFragment.this;
                homeFragment.totalItems = homeFragment.manager.getItemCount();
                homeFragment = HomeFragment.this;
                homeFragment.scrolledOutItems = homeFragment.manager.findFirstVisibleItemPosition();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onScrolled: totalPhotos:");
                stringBuilder.append(HomeFragment.this.totalPhotos);
                stringBuilder.append(" totalItems:");
                stringBuilder.append(HomeFragment.this.totalItems);
                Log.d(HomeFragment.TAG, stringBuilder.toString());
                if (HomeFragment.this.visibleItems + HomeFragment.this.scrolledOutItems == HomeFragment.this.totalItems && HomeFragment.this.totalItems < HomeFragment.this.totalPhotos) {
                    HomeFragment.this.getMorePhotos();
                }
            }
        });
    }

    private void getMorePhotos() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("getMorePhotos: visibleItems + scrolledOutItems: ");
        stringBuilder.append(this.visibleItems + this.scrolledOutItems);
        String stringBuilder2 = stringBuilder.toString();
        String str = TAG;
        Log.d(str, stringBuilder2);
        int lastIndex = Ints.min(new int[]{(this.visibleItems + this.scrolledOutItems) + 3, this.totalPhotos});
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append("getMorePhotos: last index: ");
        stringBuilder3.append(lastIndex);
        Log.d(str, stringBuilder3.toString());
        for (int j = this.visibleItems + this.scrolledOutItems; j < lastIndex; j++) {
            this.mPaginatedPhotos.add(this.mPhotos.get(j));
        }
        this.adapter.notifyDataSetChanged();
    }
}
