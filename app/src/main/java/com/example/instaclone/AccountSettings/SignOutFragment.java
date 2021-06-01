package com.example.instaclone.AccountSettings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.constraintlayout.widget.Constraints;
import androidx.fragment.app.Fragment;

import com.example.instaclone.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.example.instaclone.R;

public class SignOutFragment extends Fragment {
    private FirebaseAuth mAuth;
    private AuthStateListener mAuthStateListener;
    ProgressBar progressBar;
    Button signOut;
    TextView wait;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_out, container, false);
        this.signOut = (Button) view.findViewById(R.id.signOutButton);
        this.progressBar = (ProgressBar) view.findViewById(R.id.signOutProgressBar);
        TextView textView = (TextView) view.findViewById(R.id.wait);
        this.wait = textView;
        textView.setVisibility(View.GONE);
        this.progressBar.setVisibility(View.GONE);
        setUpFirebaseAuth();
        this.signOut.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(Constraints.TAG, "onClick: signing out");
                SignOutFragment.this.mAuth.signOut();
                SignOutFragment.this.wait.setVisibility(View.VISIBLE);
                SignOutFragment.this.progressBar.setVisibility(View.VISIBLE);
                SignOutFragment.this.getActivity().finish();
            }
        });
        return view;
    }

    private void setUpFirebaseAuth() {
        Log.d(Constraints.TAG, "setUpFirebaseAuth: setting up firebase auth");
        this.mAuth = FirebaseAuth.getInstance();
        this.mAuthStateListener = new AuthStateListener() {
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                String str = Constraints.TAG;
                if (user != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("onAuthStateChanged: ");
                    stringBuilder.append(user.getUid());
                    stringBuilder.append(" signed in");
                    Log.d(str, stringBuilder.toString());
                    return;
                }
                Log.d(str, "onAuthStateChanged: signed out");
                Intent intent = new Intent(SignOutFragment.this.getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK); //268468224
                SignOutFragment.this.startActivity(intent);
            }
        };
    }

    public void onStart() {
        super.onStart();
        this.mAuth.addAuthStateListener(this.mAuthStateListener);
    }

    public void onStop() {
        super.onStop();
        this.mAuth.removeAuthStateListener(this.mAuthStateListener);
    }
}
