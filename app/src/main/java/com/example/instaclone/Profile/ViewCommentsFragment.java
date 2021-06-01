package com.example.instaclone.Profile;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.instaclone.Home.MainActivity;
import com.example.instaclone.R;
import com.example.instaclone.models.Comment;
import com.example.instaclone.models.Photo;
import com.example.instaclone.util.CommentsAdapter;
import com.example.instaclone.util.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class ViewCommentsFragment extends Fragment {
    private static final String TAG = "ViewCommentsFragment";
    private CommentsAdapter adapter;
    private EditText addComment;
    private ImageView back;
    private ImageView check;
    private ArrayList<Comment> comments = new ArrayList();
    private Context context;
    private FirebaseFirestore db;
    private FirebaseMethods firebaseMethods;
    private FirebaseAuth mAuth;
    private AuthStateListener mAuthStateListener;
    private Photo photo;
    private RecyclerView recyclerView;

    public ViewCommentsFragment() {
        setArguments(new Bundle());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_comments, container, false);
        this.check = (ImageView) view.findViewById(R.id.check);
        this.back = (ImageView) view.findViewById(R.id.back);
        this.addComment = (EditText) view.findViewById(R.id.addComment);
        this.recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        this.context = getActivity();
        setUpFirebaseAuth();
        this.photo = getPhotoFromBundle();
        setUpRecyclerView();
        this.back.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(ViewCommentsFragment.TAG, "onClick: navigating back to view post fragment");
                MainActivity.relativeLayout.setVisibility(View.VISIBLE);
                ViewCommentsFragment.this.getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        this.check.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String str = "";
                if (ViewCommentsFragment.this.addComment.getText().toString().equals(str)) {
                    Toast.makeText(ViewCommentsFragment.this.context, "Comment should not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                ViewCommentsFragment.this.addNewComment();
                ViewCommentsFragment.this.addComment.setText(str);
                ViewCommentsFragment.this.closeKeyboard();
            }
        });
        return view;
    }

    private void closeKeyboard() {
        View view = ((FragmentActivity) requireActivity()).getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void addNewComment() {
        Log.d(TAG, "addNewComment: ");
        Comment comment = new Comment();
        comment.setComment(this.addComment.getText().toString());
        comment.setDate_created(getTimeStamp());
        comment.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        this.comments.add(comment);
        this.adapter.notifyDataSetChanged();
        String str = "comments";
        this.db.collection("photos").document(this.photo.getPhoto_id()).update(str, FieldValue.arrayUnion(comment), new Object[0]);
        DocumentReference document = this.db.collection("user_photos").document(this.photo.getUser_id());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.photo.getPhoto_id());
        stringBuilder.append(".comments");
        document.update(stringBuilder.toString(), FieldValue.arrayUnion(comment), new Object[0]);
    }

    private String getTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        return sdf.format(new Date());
    }

    private void setUpRecyclerView() {
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this.context));
        this.comments = this.photo.getComments();
        CommentsAdapter commentsAdapter = new CommentsAdapter(this.context, this.comments);
        this.adapter = commentsAdapter;
        this.recyclerView.setAdapter(commentsAdapter);
    }

    private Photo getPhotoFromBundle() {
        Log.d(TAG, "getPhotoFromBundle: ");
        Bundle bundle = getArguments();
        if (bundle != null) {
            return (Photo) bundle.getParcelable("selected_photo");
        }
        return null;
    }

    private void setUpFirebaseAuth() {
        Log.d(TAG, "setUpFirebaseAuth: setting up firebase auth");
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.mAuthStateListener = new AuthStateListener() {
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                String str = ViewCommentsFragment.TAG;
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
