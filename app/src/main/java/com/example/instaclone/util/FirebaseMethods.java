package com.example.instaclone.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.instaclone.AccountSettings.AccountSettingsActivity;
import com.example.instaclone.Home.MainActivity;
import com.example.instaclone.models.Photo;
import com.example.instaclone.models.User;
import com.example.instaclone.models.UserAccountSettings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask.TaskSnapshot;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private AuthStateListener mAuthStateListener;
    private Context mContext;
    private StorageReference mStorageRef;
    private double photoUploadProgress = 0.0d;
    private String userID;

    public FirebaseMethods(Context context) {
        this.mContext = context;
        this.db = FirebaseFirestore.getInstance();
        this.mStorageRef = FirebaseStorage.getInstance().getReference();
        if (this.mAuth.getCurrentUser() != null) {
            this.userID = this.mAuth.getCurrentUser().getUid();
        }
    }

    public void sendVerificationEmail() {
        Log.d(TAG, "sendVerificationEmail: ");
        FirebaseUser user = this.mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                public void onComplete(Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(FirebaseMethods.this.mContext, "Verification email sent.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FirebaseMethods.this.mContext, "Couldn't send email.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void sendVerificationEmailAgain(final Context context) {
        Log.d(TAG, "sendVerificationEmail: ");
        FirebaseUser user = this.mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                public void onComplete(Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(FirebaseMethods.this.mContext, "Verification email sent.", Toast.LENGTH_SHORT).show();
                        FirebaseMethods.this.mAuth.signOut();
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //67108864
                        intent.putExtra("end", 1);
                        context.startActivity(intent);
                        return;
                    }
                    Toast.makeText(FirebaseMethods.this.mContext, "Couldn't send email.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void registerNewEmail(String email, String password, String username, final ProgressBar progressBar, final TextView wait) {
        this.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((Activity) this.mContext, new OnCompleteListener<AuthResult>() {
            public void onComplete(Task<AuthResult> task) {
                boolean isSuccessful = task.isSuccessful();
                String str = FirebaseMethods.TAG;
                if (isSuccessful) {
                    Log.d(str, "createUserWithEmail:success");
                    FirebaseMethods.this.userID = FirebaseMethods.this.mAuth.getCurrentUser().getUid();
                    progressBar.setVisibility(View.GONE);
                    wait.setVisibility(View.GONE);
                    FirebaseMethods.this.sendVerificationEmail();
                    FirebaseMethods.this.mAuth.signOut();
                    return;
                }
                Log.w(str, "createUserWithEmail:failure", task.getException());
                Toast.makeText(FirebaseMethods.this.mContext, "Error: Couldn't Register", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                wait.setVisibility(View.GONE);
            }
        });
    }

    public void addNewUser(String email, String username, String description, String profilePhoto, String displayName) {
        String str = "addNewUser: adding new user";
        String str2 = TAG;
        Log.d(str2, str);
        this.db.collection("users").document(this.userID).set(new User(email, 0, this.userID, username)).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                Log.d(FirebaseMethods.TAG, "DocumentSnapshot successfully written!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception e) {
                Log.w(FirebaseMethods.TAG, "Error writing document", e);
            }
        });
        Log.d(str2, str);
        this.db.collection("user_account_settings").document(this.userID).set(new UserAccountSettings(description, displayName, 0, 0, 0, profilePhoto, this.userID, username)).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                Log.d(FirebaseMethods.TAG, "DocumentSnapshot successfully written!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception e) {
                Log.w(FirebaseMethods.TAG, "Error writing document", e);
            }
        });
    }

    public User getUserInfo() {
        Log.d(TAG, "getUserInfo: getting user info ");
        final User[] user = new User[1];
        this.db.collection("users").document(this.userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onSuccess: documentSnapshot: ");
                stringBuilder.append(documentSnapshot);
                Log.d(FirebaseMethods.TAG, stringBuilder.toString());
                user[0] = (User) documentSnapshot.toObject(User.class);
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception e) {
                Log.d(FirebaseMethods.TAG, "onFailure: couldn't get UserAccountSettingsInfo ");
                e.printStackTrace();
            }
        });
        return user[0];
    }

    public UserAccountSettings getUserAccountSettingsInfo() {
        Log.d(TAG, "getUserAccountSettingsInfo: getting UserAccountSettings info ");
        final UserAccountSettings[] userAccountSettings = new UserAccountSettings[1];
        this.db.collection("user_account_settings").document(this.userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onSuccess: documentSnapshot: ");
                stringBuilder.append(documentSnapshot);
                String stringBuilder2 = stringBuilder.toString();
                String str = FirebaseMethods.TAG;
                Log.d(str, stringBuilder2);
                userAccountSettings[0] = (UserAccountSettings) documentSnapshot.toObject(UserAccountSettings.class);
                stringBuilder = new StringBuilder();
                stringBuilder.append("setInfo: userAccountSettings[0]: ");
                stringBuilder.append(userAccountSettings[0]);
                Log.d(str, stringBuilder.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception e) {
                Log.d(FirebaseMethods.TAG, "onFailure: couldn't get UserAccountSettingsInfo ");
                e.printStackTrace();
            }
        });
        return userAccountSettings[0];
    }

    public void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: ");
        this.db.collection("users").whereEqualTo("username", (Object) username).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                boolean isEmpty = queryDocumentSnapshots.isEmpty();
                String str = FirebaseMethods.TAG;
                if (isEmpty) {
                    Log.d(str, "onSuccess: unique username");
                    FirebaseMethods.this.updateUsername(username);
                    return;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onSuccess: username already exists ");
                stringBuilder.append(queryDocumentSnapshots.getDocuments());
                Log.d(str, stringBuilder.toString());
                Toast.makeText(FirebaseMethods.this.mContext, "Username already exists", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateUsername(String username) {
        Log.d(TAG, "updateUsername: saving username");
        String str = "username";
        this.db.collection("user_account_settings").document(this.userID).update(str, (Object) username, new Object[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                Log.d(FirebaseMethods.TAG, "onSuccess: username saved");
                Toast.makeText(FirebaseMethods.this.mContext, "Username updated", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception e) {
                Log.d(FirebaseMethods.TAG, "onFailure: username couldn't be saved");
            }
        });
        this.db.collection("users").document(this.userID).update(str, (Object) username, new Object[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                Log.d(FirebaseMethods.TAG, "onSuccess: username saved");
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception e) {
                Log.d(FirebaseMethods.TAG, "onFailure: username couldn't be saved");
            }
        });
    }

    public void uploadPhoto(String photoType, final String description, int imgCount, String imgUrl, Bitmap bm) {
        String str = TAG;
        Log.d(str, "uploadPhoto: uploading photo");
        FilePaths filePaths = new FilePaths();
        String str2 = "/";
        StorageReference storageReference;
        StringBuilder stringBuilder;
        if (photoType.equals("newPhoto")) {
            storageReference = this.mStorageRef;
            stringBuilder = new StringBuilder();
            stringBuilder.append(filePaths.FIREBASE_IMAGE_STORAGE);
            stringBuilder.append(str2);
            stringBuilder.append(this.userID);
            stringBuilder.append("/photo");
            stringBuilder.append(imgCount + 1);
            storageReference = storageReference.child(stringBuilder.toString());
            if (bm == null) {
                bm = ImageManager.getBitmap(imgUrl);
            }
            storageReference.putBytes(ImageManager.getBytesFromBitmap(bm, 100)).addOnSuccessListener((OnSuccessListener) new OnSuccessListener<TaskSnapshot>() {
                public void onSuccess(TaskSnapshot taskSnapshot) {
                    Toast.makeText(FirebaseMethods.this.mContext, "Photo upload success", Toast.LENGTH_SHORT).show();
                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        public void onSuccess(Uri uri) {
                            FirebaseMethods.this.addPhotoToDatabase(description, uri.toString());
                            FirebaseMethods.this.mContext.startActivity(new Intent(FirebaseMethods.this.mContext, MainActivity.class));
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                public void onFailure(Exception e) {
                    Log.d(FirebaseMethods.TAG, "onFailure: Photo upload failed");
                    Toast.makeText(FirebaseMethods.this.mContext, "Photo upload failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<TaskSnapshot>() {
                public void onProgress(TaskSnapshot taskSnapshot) {
                    double progress = (double) ((taskSnapshot.getBytesTransferred() * 100) / taskSnapshot.getTotalByteCount());
                    if (progress - 15.0d > FirebaseMethods.this.photoUploadProgress) {
                        Context access$000 = FirebaseMethods.this.mContext;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Photo upload progress: ");
                        stringBuilder.append(String.format("%.0f", new Object[]{Double.valueOf(progress)}));
                        stringBuilder.append("%");
                        Toast.makeText(access$000, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
                        FirebaseMethods.this.photoUploadProgress = progress;
                    }
                }
            });
            return;
        }
        Log.d(str, "uploadPhoto: uploading profile photo");
        storageReference = this.mStorageRef;
        stringBuilder = new StringBuilder();
        stringBuilder.append(filePaths.FIREBASE_IMAGE_STORAGE);
        stringBuilder.append(str2);
        stringBuilder.append(this.userID);
        stringBuilder.append("/profile_photo");
        storageReference = storageReference.child(stringBuilder.toString());
        if (bm == null) {
            bm = ImageManager.getBitmap(imgUrl);
        }
        storageReference.putBytes(ImageManager.getBytesFromBitmap(bm, 100)).addOnSuccessListener((OnSuccessListener) new OnSuccessListener<TaskSnapshot>() {
            public void onSuccess(TaskSnapshot taskSnapshot) {
                Toast.makeText(FirebaseMethods.this.mContext, "Photo upload success", Toast.LENGTH_SHORT).show();
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    public void onSuccess(Uri uri) {
                        FirebaseMethods.this.addProfilePhotoToDatabase(uri.toString());
                        ((AccountSettingsActivity) FirebaseMethods.this.mContext).setUpViewPager(((AccountSettingsActivity) FirebaseMethods.this.mContext).sectionsStatePageAdapter.getFragmentNumber("Edit Profile"));
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception e) {
                Log.d(FirebaseMethods.TAG, "onFailure: Photo upload failed");
                Toast.makeText(FirebaseMethods.this.mContext, "Photo upload failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<TaskSnapshot>() {
            public void onProgress(TaskSnapshot taskSnapshot) {
                double progress = (double) ((taskSnapshot.getBytesTransferred() * 100) / taskSnapshot.getTotalByteCount());
                if (progress - 15.0d > FirebaseMethods.this.photoUploadProgress) {
                    Context access$000 = FirebaseMethods.this.mContext;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Photo upload progress: ");
                    stringBuilder.append(String.format("%.0f", new Object[]{Double.valueOf(progress)}));
                    stringBuilder.append("%");
                    Toast.makeText(access$000, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
                    FirebaseMethods.this.photoUploadProgress = progress;
                }
            }
        });
    }

    private String getTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        return sdf.format(new Date());
    }

    private void addProfilePhotoToDatabase(String url) {
        this.db.collection("user_account_settings").document(this.userID).update("profile_photo", (Object) url, new Object[0]);
    }

    private void addPhotoToDatabase(String description, String url) {
        Log.d(TAG, "addPhotoToDatabase: ");
        String photoKey = RandomString.getAlphaNumericString();
        Photo photo = new Photo();
        photo.setCaption(description);
        photo.setImage_path(url);
        photo.setPhoto_id(photoKey);
        photo.setUser_id(this.userID);
        photo.setDate_created(getTimeStamp());
        Map<String, Object> newPhoto = new HashMap();
        newPhoto.put(photoKey, photo);
        this.db.collection("user_photos").document(this.userID).set(newPhoto, SetOptions.merge());
        this.db.collection("photos").document(photoKey).set(photo);
        this.db.collection("user_account_settings").document(this.userID).update("posts", FieldValue.increment(1), new Object[0]);
    }
}
