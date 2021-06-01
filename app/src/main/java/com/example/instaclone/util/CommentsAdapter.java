package com.example.instaclone.util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.instaclone.R;
import com.example.instaclone.models.Comment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CommentsAdapter extends Adapter<CommentsAdapter.ViewHolder> {
    private static final String TAG = "CommentsAdapter";
    private ArrayList<Comment> comments;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context mContext;

    public class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        TextView comment;
        ImageView like;
        TextView likes;
        ImageView profilePhoto;
        TextView reply;
        TextView timestamp;
        TextView username;

        public ViewHolder(View itemView) {
            super(itemView);
            this.profilePhoto = (ImageView) itemView.findViewById(R.id.profilePhoto);
            this.comment = (TextView) itemView.findViewById(R.id.comment);
            this.username = (TextView) itemView.findViewById(R.id.commentUsername);
            this.timestamp = (TextView) itemView.findViewById(R.id.timeStamp);
        }
    }

    public CommentsAdapter(Context context, ArrayList<Comment> comments) {
        this.comments = comments;
        this.mContext = context;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment, parent, false));
    }

    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.comment.setText(((Comment) this.comments.get(position)).getComment());
        String timeDifference = getTimeStampDifference((Comment) this.comments.get(position));
        if (timeDifference.equals("0")) {
            holder.timestamp.setText("today");
        } else {
            TextView textView = holder.timestamp;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(timeDifference);
            stringBuilder.append("d");
            textView.setText(stringBuilder.toString());
        }
        this.db.collection("user_account_settings").document(((Comment) this.comments.get(position)).getUser_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.username.setText((String) documentSnapshot.get("username"));
                ((RequestBuilder) ((RequestBuilder) Glide.with(CommentsAdapter.this.mContext).load((String) documentSnapshot.get("profile_photo")).centerCrop()).placeholder((int) R.drawable.ic_android)).into(holder.profilePhoto);
            }
        });
    }

    public int getItemCount() {
        return this.comments.size();
    }

    private String getTimeStampDifference(Comment comment) {
        String difference = TAG;
        Log.d(difference, "getTimeStampDifference: ");
        String difference2 = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        Date today = c.getTime();
        sdf.format(today);
        String photoTimestamp = comment.getDate_created();
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
}
