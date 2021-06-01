package com.example.instaclone.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.instaclone.Home.MainActivity;
import com.example.instaclone.Profile.ProfileActivity;
import com.example.instaclone.R;
import com.example.instaclone.models.Photo;
import com.example.instaclone.models.UserAccountSettings;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainFeedAdapter extends Adapter<MainFeedAdapter.ViewHolder> {
    private static final String TAG = "CommentsAdapter";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context mContext;
    private ArrayList<Photo> photos;

    public class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        TextView comments;
        ImageView commentsImage;
        TextView description;
        TextView likes;
        ImageView outlineHeart;
        SquareImageView postImage;
        ImageView profilePhoto;
        ImageView redHeart;
        TextView timestamp;
        TextView username;

        public ViewHolder(View view) {
            super(view);
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
        }
    }

    public MainFeedAdapter(Context context, ArrayList<Photo> photos) {
        this.photos = photos;
        this.mContext = context;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_view_main_feed_post, parent, false));
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final long[] likesCount = new long[1];
        holder.username.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MainFeedAdapter.this.db.collection("user_account_settings").document(((Photo) MainFeedAdapter.this.photos.get(position)).getUser_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserAccountSettings userAccountSettings = (UserAccountSettings) documentSnapshot.toObject(UserAccountSettings.class);
                        Intent intent = new Intent(MainFeedAdapter.this.mContext, ProfileActivity.class);
                        intent.putExtra("searchActivity", 1);
                        intent.putExtra("user_account_settings", userAccountSettings);
                        MainFeedAdapter.this.mContext.startActivity(intent);
                    }
                });
            }
        });
        holder.profilePhoto.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MainFeedAdapter.this.db.collection("user_account_settings").document(((Photo) MainFeedAdapter.this.photos.get(position)).getUser_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserAccountSettings userAccountSettings = (UserAccountSettings) documentSnapshot.toObject(UserAccountSettings.class);
                        Intent intent = new Intent(MainFeedAdapter.this.mContext, ProfileActivity.class);
                        intent.putExtra("searchActivity", 1);
                        intent.putExtra("user_account_settings", userAccountSettings);
                        MainFeedAdapter.this.mContext.startActivity(intent);
                    }
                });
            }
        });
        holder.commentsImage.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ((MainActivity) MainFeedAdapter.this.mContext).onCommentThreadSelected((Photo) MainFeedAdapter.this.photos.get(position));
            }
        });
        holder.comments.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ((MainActivity) MainFeedAdapter.this.mContext).onCommentThreadSelected((Photo) MainFeedAdapter.this.photos.get(position));
            }
        });
        String timeDifference = getTimeStampDifference(position);
        TextView textView;
        StringBuilder stringBuilder;
        if (timeDifference.equals("0")) {
            holder.timestamp.setText("Today");
        } else if (timeDifference.equals("1")) {
            textView = holder.timestamp;
            stringBuilder = new StringBuilder();
            stringBuilder.append(timeDifference);
            stringBuilder.append(" day ago");
            textView.setText(stringBuilder.toString());
        } else {
            textView = holder.timestamp;
            stringBuilder = new StringBuilder();
            stringBuilder.append(timeDifference);
            stringBuilder.append(" days ago");
            textView.setText(stringBuilder.toString());
        }
        ((RequestBuilder) ((RequestBuilder) Glide.with(this.mContext).load(((Photo) this.photos.get(position)).getImage_path()).centerCrop()).placeholder((int) R.drawable.ic_android)).into(holder.postImage);
        this.db.collection("user_account_settings").document(((Photo) this.photos.get(position)).getUser_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onSuccess: documentSnapshot: ");
                stringBuilder.append(documentSnapshot);
                Log.d(MainFeedAdapter.TAG, stringBuilder.toString());
                UserAccountSettings userAccountSettings = (UserAccountSettings) documentSnapshot.toObject(UserAccountSettings.class);
                ((RequestBuilder) ((RequestBuilder) Glide.with(MainFeedAdapter.this.mContext).load(userAccountSettings.getProfile_photo()).centerCrop()).placeholder((int) R.drawable.ic_android)).into(holder.profilePhoto);
                holder.username.setText(userAccountSettings.getUsername());
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception e) {
                Log.d(MainFeedAdapter.TAG, "onFailure: couldn't get UserAccountSettingsInfo ");
                e.printStackTrace();
            }
        });
        String str = TAG;
        Log.d(str, "setLikesAndHeartColor: ");
        try {
            likesCount[0] = (long) ((Photo) this.photos.get(position)).getLikes().size();
            if (((Photo) this.photos.get(position)).getLikes().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                holder.redHeart.setVisibility(View.VISIBLE);
                holder.outlineHeart.setVisibility(View.GONE);
            } else {
                holder.outlineHeart.setVisibility(View.VISIBLE);
                holder.redHeart.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("setLikesAndHeartColor: Exception: ");
            stringBuilder2.append(e.getMessage());
            Log.d(str, stringBuilder2.toString());
            holder.outlineHeart.setVisibility(View.VISIBLE);
            holder.redHeart.setVisibility(View.GONE);
            likesCount[0] = 0;
        }
        TextView textView2;
        StringBuilder stringBuilder3;
        if (likesCount[0] == 0 || likesCount[0] == 1) {
            textView2 = holder.likes;
            stringBuilder3 = new StringBuilder();
            stringBuilder3.append(String.valueOf(likesCount[0]));
            stringBuilder3.append(" like");
            textView2.setText(stringBuilder3.toString());
        } else {
            textView2 = holder.likes;
            stringBuilder3 = new StringBuilder();
            stringBuilder3.append(String.valueOf(likesCount[0]));
            stringBuilder3.append(" likes");
            textView2.setText(stringBuilder3.toString());
        }
        Log.d(str, "setUpHeart: ");
        holder.redHeart.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (holder.redHeart.getVisibility() == View.VISIBLE) {
                    holder.redHeart.setVisibility(View.GONE);
                    holder.outlineHeart.setVisibility(View.VISIBLE);
                    Log.d(MainFeedAdapter.TAG, "removeLike: ");
                    String str = "likes";
                    MainFeedAdapter.this.db.collection("photos").document(((Photo) MainFeedAdapter.this.photos.get(position)).getPhoto_id()).update(str, FieldValue.arrayRemove(FirebaseAuth.getInstance().getCurrentUser().getUid()), new Object[0]);
                    DocumentReference document = MainFeedAdapter.this.db.collection("user_photos").document(((Photo) MainFeedAdapter.this.photos.get(position)).getUser_id());
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(((Photo) MainFeedAdapter.this.photos.get(position)).getPhoto_id());
                    stringBuilder.append(".likes");
                    document.update(stringBuilder.toString(), FieldValue.arrayRemove(FirebaseAuth.getInstance().getCurrentUser().getUid()), new Object[0]);
                    long[] jArr = likesCount;
                    jArr[0] = jArr[0] - 1;
                    TextView textView;
                    StringBuilder stringBuilder2;
                    if (jArr[0] == 0 || jArr[0] == 1) {
                        textView = holder.likes;
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(String.valueOf(likesCount[0]));
                        stringBuilder2.append(" like");
                        textView.setText(stringBuilder2.toString());
                        return;
                    }
                    textView = holder.likes;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(String.valueOf(likesCount[0]));
                    stringBuilder2.append(" likes");
                    textView.setText(stringBuilder2.toString());
                }
            }
        });
        holder.outlineHeart.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (holder.outlineHeart.getVisibility() == View.VISIBLE) {
                    holder.outlineHeart.setVisibility(View.GONE);
                    holder.redHeart.setVisibility(View.VISIBLE);
                    Log.d(MainFeedAdapter.TAG, "addLike: ");
                    String str = "likes";
                    MainFeedAdapter.this.db.collection("photos").document(((Photo) MainFeedAdapter.this.photos.get(position)).getPhoto_id()).update(str, FieldValue.arrayUnion(FirebaseAuth.getInstance().getCurrentUser().getUid()), new Object[0]);
                    DocumentReference document = MainFeedAdapter.this.db.collection("user_photos").document(((Photo) MainFeedAdapter.this.photos.get(position)).getUser_id());
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(((Photo) MainFeedAdapter.this.photos.get(position)).getPhoto_id());
                    stringBuilder.append(".likes");
                    document.update(stringBuilder.toString(), FieldValue.arrayUnion(FirebaseAuth.getInstance().getCurrentUser().getUid()), new Object[0]);
                    long[] jArr = likesCount;
                    jArr[0] = jArr[0] + 1;
                    TextView textView;
                    StringBuilder stringBuilder2;
                    if (jArr[0] == 0 || jArr[0] == 1) {
                        textView = holder.likes;
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(String.valueOf(likesCount[0]));
                        stringBuilder2.append(" like");
                        textView.setText(stringBuilder2.toString());
                        return;
                    }
                    textView = holder.likes;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(String.valueOf(likesCount[0]));
                    stringBuilder2.append(" likes");
                    textView.setText(stringBuilder2.toString());
                }
            }
        });
        holder.description.setText(((Photo) this.photos.get(position)).getCaption());
        String str2 = "No comments";
        if (((Photo) this.photos.get(position)).getComments() == null) {
            holder.comments.setText(str2);
        } else if (((Photo) this.photos.get(position)).getComments().size() == 0) {
            holder.comments.setText(str2);
        } else if (((Photo) this.photos.get(position)).getComments().size() == 1) {
            holder.comments.setText("View 1 comment");
        } else {
            TextView textView3 = holder.comments;
            StringBuilder stringBuilder4 = new StringBuilder();
            stringBuilder4.append("View ");
            stringBuilder4.append(((Photo) this.photos.get(position)).getComments().size());
            stringBuilder4.append(" comments");
            textView3.setText(stringBuilder4.toString());
        }
    }

    public int getItemCount() {
        return this.photos.size();
    }

    private String getTimeStampDifference(int position) {
        String difference = TAG;
        Log.d(difference, "getTimeStampDifference: ");
        String difference2 = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        Date today = c.getTime();
        sdf.format(today);
        String photoTimestamp = ((Photo) this.photos.get(position)).getDate_created();
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

    private void setLikesAndHeartColor(ViewHolder holder, int position, long likesCount) {
        String str = TAG;
        Log.d(str, "setLikesAndHeartColor: ");
        try {
            likesCount = (long) ((Photo) this.photos.get(position)).getLikes().size();
            if (((Photo) this.photos.get(position)).getLikes().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                holder.redHeart.setVisibility(View.VISIBLE);
                holder.outlineHeart.setVisibility(View.GONE);
            } else {
                holder.outlineHeart.setVisibility(View.VISIBLE);
                holder.redHeart.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("setLikesAndHeartColor: Exception: ");
            stringBuilder.append(e.getMessage());
            Log.d(str, stringBuilder.toString());
            holder.outlineHeart.setVisibility(View.VISIBLE);
            holder.redHeart.setVisibility(View.GONE);
            likesCount = 0;
        }
        TextView textView;
        StringBuilder stringBuilder2;
        if (likesCount == 0 || likesCount == 1) {
            textView = holder.likes;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(String.valueOf(likesCount));
            stringBuilder2.append(" like");
            textView.setText(stringBuilder2.toString());
            return;
        }
        textView = holder.likes;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(String.valueOf(likesCount));
        stringBuilder2.append(" likes");
        textView.setText(stringBuilder2.toString());
    }

    private void setUpHeart(ViewHolder holder, int position, long likesCount) {
        Log.d(TAG, "setUpHeart: ");
        final ViewHolder viewHolder = holder;
        final int i = position;
        final long j = likesCount;
        holder.redHeart.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (viewHolder.redHeart.getVisibility() == View.VISIBLE) {
                    viewHolder.redHeart.setVisibility(View.GONE);
                    viewHolder.outlineHeart.setVisibility(View.VISIBLE);
                    Log.d(MainFeedAdapter.TAG, "removeLike: ");
                    String str = "likes";
                    MainFeedAdapter.this.db.collection("photos").document(((Photo) MainFeedAdapter.this.photos.get(i)).getPhoto_id())
                            .update(str, FieldValue.arrayRemove(FirebaseAuth.getInstance().getCurrentUser().getUid()), new Object[0]);
                    DocumentReference document = MainFeedAdapter.this.db.collection("user_photos").document(((Photo) MainFeedAdapter.this.photos.get(i)).getUser_id());
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(((Photo) MainFeedAdapter.this.photos.get(i)).getPhoto_id());
                    stringBuilder.append(".likes");
                    document.update(stringBuilder.toString(), FieldValue.arrayRemove(FirebaseAuth.getInstance().getCurrentUser().getUid()), new Object[0]);
                    long j = likesCount-1;
                    TextView textView;
                    StringBuilder stringBuilder2;
                    if (j == 0 || j == 1) {
                        textView = viewHolder.likes;
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(String.valueOf(j));
                        stringBuilder2.append(" like");
                        textView.setText(stringBuilder2.toString());
                        return;
                    }
                    textView = viewHolder.likes;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(String.valueOf(j));
                    stringBuilder2.append(" likes");
                    textView.setText(stringBuilder2.toString());
                }
            }
        });
        holder.outlineHeart.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (viewHolder.outlineHeart.getVisibility() == View.VISIBLE) {
                    viewHolder.outlineHeart.setVisibility(View.GONE);
                    viewHolder.redHeart.setVisibility(View.VISIBLE);
                    Log.d(MainFeedAdapter.TAG, "addLike: ");
                    String str = "likes";
                    MainFeedAdapter.this.db.collection("photos").document(((Photo) MainFeedAdapter.this.photos.get(i)).getPhoto_id())
                            .update(str, FieldValue.arrayUnion(FirebaseAuth.getInstance().getCurrentUser().getUid()), new Object[0]);
                    DocumentReference document = MainFeedAdapter.this.db.collection("user_photos")
                            .document(((Photo) MainFeedAdapter.this.photos.get(i)).getUser_id());
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(((Photo) MainFeedAdapter.this.photos.get(i)).getPhoto_id());
                    stringBuilder.append(".likes");
                    document.update(stringBuilder.toString(), FieldValue.arrayUnion(FirebaseAuth.getInstance().getCurrentUser().getUid()), new Object[0]);
                    long j = likesCount+1;
                    TextView textView;
                    StringBuilder stringBuilder2;
                    if (j == 0 || j == 1) {
                        textView = viewHolder.likes;
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(String.valueOf(j));
                        stringBuilder2.append(" like");
                        textView.setText(stringBuilder2.toString());
                        return;
                    }
                    textView = viewHolder.likes;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(String.valueOf(j));
                    stringBuilder2.append(" likes");
                    textView.setText(stringBuilder2.toString());
                }
            }
        });
    }

    private void removeLike(ViewHolder holder, int position, long likesCount) {
        Log.d(TAG, "removeLike: ");
        String str = "likes";
        this.db.collection("photos").document(((Photo) this.photos.get(position)).getPhoto_id()).update(str, FieldValue.arrayRemove(FirebaseAuth.getInstance().getCurrentUser().getUid()), new Object[0]);
        DocumentReference document = this.db.collection("user_photos").document(((Photo) this.photos.get(position)).getUser_id());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(((Photo) this.photos.get(position)).getPhoto_id());
        stringBuilder.append(".likes");
        document.update(stringBuilder.toString(), FieldValue.arrayRemove(FirebaseAuth.getInstance().getCurrentUser().getUid()), new Object[0]);
        likesCount--;
        TextView textView;
        StringBuilder stringBuilder2;
        if (likesCount == 0 || likesCount == 1) {
            textView = holder.likes;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(String.valueOf(likesCount));
            stringBuilder2.append(" like");
            textView.setText(stringBuilder2.toString());
            return;
        }
        textView = holder.likes;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(String.valueOf(likesCount));
        stringBuilder2.append(" likes");
        textView.setText(stringBuilder2.toString());
    }

    private void addLike(ViewHolder holder, int position, long likesCount) {
        Log.d(TAG, "addLike: ");
        String str = "likes";
        this.db.collection("photos").document(((Photo) this.photos.get(position)).getPhoto_id()).update(str, FieldValue.arrayUnion(FirebaseAuth.getInstance().getCurrentUser().getUid()), new Object[0]);
        DocumentReference document = this.db.collection("user_photos").document(((Photo) this.photos.get(position)).getUser_id());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(((Photo) this.photos.get(position)).getPhoto_id());
        stringBuilder.append(".likes");
        document.update(stringBuilder.toString(), FieldValue.arrayUnion(FirebaseAuth.getInstance().getCurrentUser().getUid()), new Object[0]);
        likesCount++;
        TextView textView;
        StringBuilder stringBuilder2;
        if (likesCount == 0 || likesCount == 1) {
            textView = holder.likes;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(String.valueOf(likesCount));
            stringBuilder2.append(" like");
            textView.setText(stringBuilder2.toString());
            return;
        }
        textView = holder.likes;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(String.valueOf(likesCount));
        stringBuilder2.append(" likes");
        textView.setText(stringBuilder2.toString());
    }
}
