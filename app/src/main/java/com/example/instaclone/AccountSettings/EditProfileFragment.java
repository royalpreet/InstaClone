package com.example.instaclone.AccountSettings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.instaclone.Share.ShareActivity;
import com.example.instaclone.models.User;
import com.example.instaclone.models.UserAccountSettings;
import com.example.instaclone.util.ConfirmPasswordDialog;
import com.example.instaclone.util.ConfirmPasswordDialog.OnConfirmPasswordListener;
import com.example.instaclone.util.FirebaseMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;
import com.example.instaclone.R;
import java.util.Objects;

public class EditProfileFragment extends Fragment implements OnConfirmPasswordListener {
    private static final String TAG = "EditProfileFragment";
    public static ProgressBar progressBar;
    private ImageView back;
    private TextView changeImage;
    private ImageView check;
    private Context context;
    private FirebaseFirestore db;
    private TextView description;
    private String description_;
    private TextView displayName;
    private String displayName_;
    private TextView email;
    private String email_;
    private FirebaseMethods firebaseMethods;
    private FirebaseAuth mAuth;
    private AuthStateListener mAuthStateListener;
    private TextView phone;
    private long phone_;
    private ImageView profilePhoto;
    private User user;
    private UserAccountSettings userAccountSettings;
    private TextView username;
    private String username_;

    public void confirmPassword(String password) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("confirmPassword: password: ");
        stringBuilder.append(password);
        Log.d(TAG, stringBuilder.toString());
        this.mAuth.getCurrentUser().reauthenticate(EmailAuthProvider.getCredential((String) Objects.requireNonNull(this.mAuth.getCurrentUser().getEmail()), password)).addOnCompleteListener(new OnCompleteListener<Void>() {
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(EditProfileFragment.TAG, "onComplete: re-authenticated");
                    EditProfileFragment.this.mAuth.fetchSignInMethodsForEmail(EditProfileFragment.this.email.getText().toString()).addOnSuccessListener(new OnSuccessListener<SignInMethodQueryResult>() {
                        public void onSuccess(SignInMethodQueryResult signInMethodQueryResult) {
                            if (((List) Objects.requireNonNull(signInMethodQueryResult.getSignInMethods())).isEmpty()) {
                                Log.d(EditProfileFragment.TAG, "onSuccess: email available");
                                EditProfileFragment.this.mAuth.getCurrentUser().updateEmail(EditProfileFragment.this.email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(Task<Void> task) {
                                        boolean isSuccessful = task.isSuccessful();
                                        String str = EditProfileFragment.TAG;
                                        if (isSuccessful) {
                                            Log.d(str, "onComplete: email updated");
                                            Toast.makeText(EditProfileFragment.this.context, "Email updated", Toast.LENGTH_SHORT).show();
                                            String userId = EditProfileFragment.this.mAuth.getCurrentUser().getUid();
                                            EditProfileFragment.this.firebaseMethods.sendVerificationEmailAgain(EditProfileFragment.this.context);
                                            EditProfileFragment.progressBar.setVisibility(View.GONE);
                                            EditProfileFragment.this.db.collection("users").document(userId).update("email", EditProfileFragment.this.email.getText().toString(), new Object[0]);
                                            return;
                                        }
                                        Log.d(str, "onComplete: couldn't update email");
                                    }
                                });
                                return;
                            }
                            Toast.makeText(EditProfileFragment.this.context, "This email already in use", Toast.LENGTH_SHORT).show();
                            EditProfileFragment.progressBar.setVisibility(View.GONE);
                        }
                    });
                    return;
                }
                Toast.makeText(EditProfileFragment.this.context, "Re-authentication failed", Toast.LENGTH_SHORT).show();
                EditProfileFragment.progressBar.setVisibility(View.GONE);
            }
        });
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        this.context = getActivity();
        this.back = (ImageView) view.findViewById(R.id.back);
        this.changeImage = (TextView) view.findViewById(R.id.textChangeImage);
        this.check = (ImageView) view.findViewById(R.id.check);
        this.username = (TextView) view.findViewById(R.id.changeUsername);
        this.description = (TextView) view.findViewById(R.id.changeDescription);
        this.displayName = (TextView) view.findViewById(R.id.changeDisplayName);
        this.email = (TextView) view.findViewById(R.id.changeEmail);
        this.phone = (TextView) view.findViewById(R.id.changePhone);
        progressBar = (ProgressBar) view.findViewById(R.id.editProfileProgressBar);
        this.firebaseMethods = new FirebaseMethods(this.context);
        this.profilePhoto = (ImageView) view.findViewById(R.id.changeImage);
        this.back.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                EditProfileFragment.this.getActivity().finish();
            }
        });
        this.check.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                EditProfileFragment.this.saveInfo();
            }
        });
        this.changeImage.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(EditProfileFragment.TAG, "onClick: changing profile photo");
                Intent intent = new Intent(EditProfileFragment.this.context, ShareActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456
                EditProfileFragment.this.startActivity(intent);
                EditProfileFragment.this.getActivity().finish();
            }
        });
        setUpFirebaseAuth();
        setInfo();
        return view;
    }

    private void saveInfo() {
        Log.d(TAG, "saveInfo: saving info");
        this.username_ = this.username.getText().toString();
        this.description_ = this.description.getText().toString();
        this.displayName_ = this.displayName.getText().toString();
        this.phone_ = Long.parseLong(this.phone.getText().toString());
        this.email_ = this.email.getText().toString();
        boolean flag = false;
        if (!this.user.getUsername().equals(this.username.getText().toString())) {
            this.firebaseMethods.checkIfUsernameExists(this.username.getText().toString());
        }
        if (!this.user.getEmail().equals(this.email.getText().toString())) {
            ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
            dialog.show(getFragmentManager(), "ConfirmPasswordDialog");
            dialog.setTargetFragment(this, 1);
        }
        if (this.user.getPhone_number() != Long.parseLong(this.phone.getText().toString())) {
            this.db.collection("users").document(this.mAuth.getCurrentUser().getUid()).update("phone_number", Long.valueOf(this.phone_), new Object[0]);
            flag = true;
        }
        String str = "user_account_settings";
        if (!this.userAccountSettings.getDescription().equals(this.description.getText().toString())) {
            this.db.collection(str).document(this.mAuth.getCurrentUser().getUid()).update("description", this.description_, new Object[0]);
            flag = true;
        }
        if (!this.userAccountSettings.getDisplay_name().equals(this.displayName.getText().toString())) {
            this.db.collection(str).document(this.mAuth.getCurrentUser().getUid()).update("display_name", this.displayName_, new Object[0]);
            flag = true;
        }
        if (flag) {
            Toast.makeText(this.context, "Information Updated", Toast.LENGTH_SHORT).show();
        }
    }

    private void setInfo() {
        Log.d(TAG, "setInfo: setting info");
        this.db.collection("user_account_settings").document(this.mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onSuccess: documentSnapshot: ");
                stringBuilder.append(documentSnapshot);
                Log.d(EditProfileFragment.TAG, stringBuilder.toString());
                EditProfileFragment.this.userAccountSettings = (UserAccountSettings) documentSnapshot.toObject(UserAccountSettings.class);
                EditProfileFragment.this.username.setText(EditProfileFragment.this.userAccountSettings.getUsername());
                EditProfileFragment.this.displayName.setText(EditProfileFragment.this.userAccountSettings.getDisplay_name());
                EditProfileFragment.this.description.setText(EditProfileFragment.this.userAccountSettings.getDescription());
                ((RequestBuilder) ((RequestBuilder) Glide.with(EditProfileFragment.this.context).load(EditProfileFragment.this.userAccountSettings.getProfile_photo()).centerCrop()).placeholder((int) R.drawable.ic_android)).into(EditProfileFragment.this.profilePhoto);
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception e) {
                Log.d(EditProfileFragment.TAG, "onFailure: couldn't get UserAccountSettingsInfo ");
                e.printStackTrace();
            }
        });
        this.db.collection("users").document(this.mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onSuccess: documentSnapshot: ");
                stringBuilder.append(documentSnapshot);
                Log.d(EditProfileFragment.TAG, stringBuilder.toString());
                EditProfileFragment.this.user = (User) documentSnapshot.toObject(User.class);
                EditProfileFragment.this.phone.setText(String.valueOf(EditProfileFragment.this.user.getPhone_number()));
                EditProfileFragment.this.email.setText(EditProfileFragment.this.user.getEmail());
                EditProfileFragment.progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception e) {
                Log.d(EditProfileFragment.TAG, "onFailure: couldn't get UserAccountSettingsInfo ");
                e.printStackTrace();
            }
        });
    }

    private void setUpProfilePhoto() {
        ((RequestBuilder) ((RequestBuilder) Glide.with(getActivity()).load("https://photographycourse.net/wp-content/uploads/2014/11/Landscape-Photography-steps.jpg").centerCrop()).placeholder((int) R.drawable.ic_android)).into(this.profilePhoto);
    }

    private void setUpFirebaseAuth() {
        Log.d(TAG, "setUpFirebaseAuth: setting up firebase auth");
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.mAuthStateListener = new AuthStateListener() {
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                String str = EditProfileFragment.TAG;
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
        this.mAuth.addAuthStateListener(this.mAuthStateListener);
    }

    public void onStop() {
        super.onStop();
        this.mAuth.removeAuthStateListener(this.mAuthStateListener);
    }
}
