package com.example.instaclone.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.instaclone.Home.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import java.util.Arrays;
import com.example.instaclone.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private Context context;
    private EditText email;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private AuthStateListener mAuthStateListener;
    private EditText password;
    private ProgressBar progressBar;
    private TextView register;
    private TextView wait;

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_login);
        Log.d(TAG, "onCreate: started");
        this.register = (TextView) findViewById(R.id.link_signUp);
        this.context = this;
        this.progressBar = (ProgressBar) findViewById(R.id.loginProgressBar);
        this.wait = (TextView) findViewById(R.id.wait);
        this.email = (EditText) findViewById(R.id.input_email);
        this.password = (EditText) findViewById(R.id.input_password);
        this.loginButton = (Button) findViewById(R.id.login_button);
        this.progressBar.setVisibility(View.GONE);
        this.wait.setVisibility(View.GONE);
        setUpFirebaseAuth();
        init();
    }

    private boolean isStringNull(String string) {
        if (string.equals("")) {
            return true;
        }
        return false;
    }

    private void closeKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void init() {
        this.register.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        this.loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(LoginActivity.TAG, "onClick: attempting to log in");
                LoginActivity.this.closeKeyboard();
                String Email = LoginActivity.this.email.getText().toString();
                String Password = LoginActivity.this.password.getText().toString();
                if (LoginActivity.this.isStringNull(Email) || LoginActivity.this.isStringNull(Password)) {
                    Toast.makeText(LoginActivity.this.context, "You must fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                LoginActivity.this.progressBar.setVisibility(View.VISIBLE);
                LoginActivity.this.wait.setVisibility(View.VISIBLE);
                LoginActivity.this.mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener((Activity) LoginActivity.this.context, new OnCompleteListener<AuthResult>() {
                    public void onComplete(Task<AuthResult> task) {
                        boolean isSuccessful = task.isSuccessful();
                        String str = LoginActivity.TAG;
                        if (isSuccessful) {
                            Log.d(str, "signInWithEmail:success");
                            try {
                                if (LoginActivity.this.mAuth.getCurrentUser().isEmailVerified()) {
                                    Log.d(str, "onComplete: email verified");
                                    LoginActivity.this.startActivity(new Intent(LoginActivity.this.context, MainActivity.class));
                                    LoginActivity.this.finish();
                                } else {
                                    Log.d(str, "onComplete: Email not verified");
                                    Toast.makeText(LoginActivity.this.context, "Email not verified. Check your inbox.", Toast.LENGTH_SHORT).show();
                                    LoginActivity.this.mAuth.signOut();
                                }
                            } catch (Exception e) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("onComplete: error checking email ");
                                stringBuilder.append(Arrays.toString(e.getStackTrace()));
                                Log.d(str, stringBuilder.toString());
                            }
                            LoginActivity.this.progressBar.setVisibility(View.GONE);
                            LoginActivity.this.wait.setVisibility(View.GONE);
                            return;
                        }
                        Log.w(str, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this.context, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        LoginActivity.this.progressBar.setVisibility(View.GONE);
                        LoginActivity.this.wait.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void setUpFirebaseAuth() {
        Log.d(TAG, "setUpFirebaseAuth: setting up firebase auth");
        this.mAuth = FirebaseAuth.getInstance();
        this.mAuthStateListener = new AuthStateListener() {
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                String str = LoginActivity.TAG;
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
