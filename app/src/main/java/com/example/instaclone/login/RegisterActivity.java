package com.example.instaclone.login;

import android.content.Context;
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

import com.example.instaclone.util.FirebaseMethods;
import com.example.instaclone.util.StringManipulation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.instaclone.R;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private String Email;
    private String Password;
    private String Username;
    private Context context;
    private FirebaseFirestore db;
    private EditText email;
    private FirebaseMethods firebaseMethods;
    private FirebaseAuth mAuth;
    private AuthStateListener mAuthStateListener;
    private EditText password;
    private ProgressBar progressBar;
    private Button registerButton;
    private EditText username;
    private TextView wait;

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_register);
        this.context = this;
        this.firebaseMethods = new FirebaseMethods(this.context);
        this.email = (EditText) findViewById(R.id.input_email);
        this.username = (EditText) findViewById(R.id.input_username);
        this.password = (EditText) findViewById(R.id.input_password);
        this.registerButton = (Button) findViewById(R.id.register_button);
        this.wait = (TextView) findViewById(R.id.wait);
        this.progressBar = (ProgressBar) findViewById(R.id.registerProgressBar);
        init();
        setUpFirebaseAuth();
    }

    private void init() {
        this.progressBar.setVisibility(View.GONE);
        this.wait.setVisibility(View.GONE);
        this.registerButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(RegisterActivity.TAG, "onClick: Attempting to register");
                RegisterActivity.this.closeKeyboard();
                RegisterActivity registerActivity = RegisterActivity.this;
                registerActivity.Email = registerActivity.email.getText().toString();
                registerActivity = RegisterActivity.this;
                registerActivity.Password = registerActivity.password.getText().toString();
                registerActivity = RegisterActivity.this;
                registerActivity.Username = registerActivity.username.getText().toString();
                registerActivity = RegisterActivity.this;
                if (!registerActivity.isStringNull(registerActivity.Email)) {
                    registerActivity = RegisterActivity.this;
                    if (!registerActivity.isStringNull(registerActivity.Password)) {
                        registerActivity = RegisterActivity.this;
                        if (!registerActivity.isStringNull(registerActivity.Username)) {
                            RegisterActivity.this.progressBar.setVisibility(View.VISIBLE);
                            RegisterActivity.this.wait.setVisibility(View.VISIBLE);
                            RegisterActivity.this.firebaseMethods.registerNewEmail(RegisterActivity.this.Email, RegisterActivity.this.Password, RegisterActivity.this.Username, RegisterActivity.this.progressBar, RegisterActivity.this.wait);
                            return;
                        }
                    }
                }
                Toast.makeText(RegisterActivity.this.context, "You must fill all the fields", Toast.LENGTH_SHORT).show();
            }
        });
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

    private void setUpFirebaseAuth() {
        Log.d(TAG, "setUpFirebaseAuth: setting up firebase auth");
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.mAuthStateListener = new AuthStateListener() {
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                String str = RegisterActivity.TAG;
                if (user != null) {
                    final String userID = user.getUid();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("onAuthStateChanged: ");
                    stringBuilder.append(user.getUid());
                    stringBuilder.append(" signed in");
                    Log.d(str, stringBuilder.toString());
                    RegisterActivity.this.db.collection("users").whereEqualTo("username", (Object) StringManipulation.condenseUsername(RegisterActivity.this.Username)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            String str = RegisterActivity.TAG;
                            Log.d(str, "onSuccess:");
                            String displayName = RegisterActivity.this.Username;
                            if (!queryDocumentSnapshots.isEmpty()) {
                                RegisterActivity registerActivity = RegisterActivity.this;
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(RegisterActivity.this.Username);
                                stringBuilder.append(userID.substring(3, 10));
                                registerActivity.Username = stringBuilder.toString();
                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("onSuccess: Username already exists. New username: ");
                                stringBuilder2.append(RegisterActivity.this.Username);
                                Log.d(str, stringBuilder2.toString());
                            }
                            RegisterActivity.this.Username = StringManipulation.condenseUsername(RegisterActivity.this.Username);
                            RegisterActivity.this.firebaseMethods.addNewUser(RegisterActivity.this.Email, RegisterActivity.this.Username, " ", " ", displayName);
                        }
                    });
                    RegisterActivity.this.finish();
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
