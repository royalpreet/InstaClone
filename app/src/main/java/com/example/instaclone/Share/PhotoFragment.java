package com.example.instaclone.Share;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import com.example.instaclone.AccountSettings.AccountSettingsActivity;
import com.example.instaclone.NextActivity;
import com.example.instaclone.R;

import java.util.Objects;

public class PhotoFragment extends Fragment {
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final String TAG = "PhotoFragment";
    private Button launchCamera;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: started");
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        Button button = (Button) view.findViewById(R.id.launchCamera);
        this.launchCamera = button;
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(PhotoFragment.TAG, "onClick: Launching camera");
                if (((ShareActivity) PhotoFragment.this.requireActivity()).getCurrentTabNumber() == 0) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraLauncher.launch(cameraIntent);
                    //PhotoFragment.this.startActivityForResult(cameraIntent, 1);
                }
            }
        });
        return view;
    }

    ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //result.getData().getExtras().get("data");
                        Intent data = result.getData();
                        Log.d(TAG, "onActivityResult: navigation to share screen");
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        if (isRootTask()) {
                            Intent intent = new Intent(getActivity(), NextActivity.class);
                            intent.putExtra("selectedBitmap", bitmap);
                            startActivity(intent);
                            return;
                        }
                        Intent intent2 = new Intent(getActivity(), AccountSettingsActivity.class);
                        intent2.putExtra("bitmap", bitmap);
                        intent2.putExtra("return_to_fragment", 1);
                        startActivity(intent2);
                        getActivity().finish();
                    }
                }
            });

    /*public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Log.d(TAG, "onActivityResult: navigation to share screen");
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            if (isRootTask()) {
                Intent intent = new Intent(getActivity(), NextActivity.class);
                intent.putExtra("selectedBitmap", bitmap);
                startActivity(intent);
                return;
            }
            Intent intent2 = new Intent(getActivity(), AccountSettingsActivity.class);
            intent2.putExtra("bitmap", bitmap);
            intent2.putExtra("return_to_fragment", 1);
            startActivity(intent2);
            getActivity().finish();
        }
    }*/

    public boolean isRootTask() {
        if (((ShareActivity) getActivity()).getTask() == 0) {
            return true;
        }
        return false;
    }
}
