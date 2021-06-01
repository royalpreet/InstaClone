package com.example.instaclone.Share;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.instaclone.AccountSettings.AccountSettingsActivity;
import com.example.instaclone.NextActivity;
import com.example.instaclone.util.FilePaths;
import com.example.instaclone.util.FileSearch;
import com.example.instaclone.util.GridImageAdapter;
import java.util.ArrayList;
import java.util.Objects;
import com.example.instaclone.R;

public class GalleryFragment extends Fragment {
    private static final String TAG = "GalleryFragment";
    private ImageView cancel;
    private ArrayList<String> directories;
    private Spinner directorySpinner;
    private ImageView galleryImage;
    private GridView gridView;
    private TextView next;
    private ProgressBar progressBar;
    private String selectedImage;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: started");
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        this.progressBar = (ProgressBar) view.findViewById(R.id.galleryProgressBar);
        this.next = (TextView) view.findViewById(R.id.next);
        this.galleryImage = (ImageView) view.findViewById(R.id.galleryImage);
        this.cancel = (ImageView) view.findViewById(R.id.cancel);
        this.gridView = (GridView) view.findViewById(R.id.gridViewGallery);
        this.directorySpinner = (Spinner) view.findViewById(R.id.directorySpinner);
        this.directories = new ArrayList();
        this.progressBar.setVisibility(View.GONE);
        this.cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                GalleryFragment.this.getActivity().finish();
            }
        });
        this.next.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(GalleryFragment.TAG, "onClick: navigating to share screen");
                String str = "selectedImage";
                Intent intent;
                if (GalleryFragment.this.isRootTask()) {
                    intent = new Intent(GalleryFragment.this.getActivity(), NextActivity.class);
                    intent.putExtra(str, GalleryFragment.this.selectedImage);
                    GalleryFragment.this.startActivity(intent);
                    return;
                }
                intent = new Intent(GalleryFragment.this.getActivity(), AccountSettingsActivity.class);
                intent.putExtra(str, GalleryFragment.this.selectedImage);
                intent.putExtra("return_to_fragment", 1);
                GalleryFragment.this.startActivity(intent);
                GalleryFragment.this.getActivity().finish();
            }
        });
        init();
        return view;
    }

    public boolean isRootTask() {
        if (((ShareActivity) getActivity()).getTask() == 0) {
            return true;
        }
        return false;
    }

    private void setUpGridView(String selectedDirectory) {
        String str = TAG;
        Log.d(str, "setUpGridView: ");
        final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);
        this.gridView.setColumnWidth(getResources().getDisplayMetrics().widthPixels / 4);
        this.gridView.setAdapter(new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, imgURLs));
        try {
            setImage((String) imgURLs.get(0), this.galleryImage);
            this.selectedImage = (String) imgURLs.get(0);
        } catch (Exception e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("setUpGridView: Exception: ");
            stringBuilder.append(e.getMessage());
            Log.d(str, stringBuilder.toString());
        }
        this.gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                GalleryFragment.this.setImage((String) imgURLs.get(position), GalleryFragment.this.galleryImage);
                GalleryFragment.this.selectedImage = (String) imgURLs.get(position);
            }
        });
    }

    private void setImage(String imgUrl, ImageView image) {
        ((RequestBuilder) Glide.with(getActivity()).load(imgUrl).placeholder((int) R.drawable.ic_android)).into(image);
    }

    private void init() {
        FilePaths filePaths = new FilePaths();
        /*if (FileSearch.getDirectoryPaths(filePaths.PICTURES) != null) {
            this.directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
        }*/
        this.directories.add(filePaths.CAMERA);
        ArrayList<String> directoryNames = new ArrayList();
        for (int i = 0; i < this.directories.size(); i++) {
            directoryNames.add(((String) this.directories.get(i)).substring(((String) this.directories.get(i)).lastIndexOf("/") + 1));
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("init: no. of directories ");
        stringBuilder.append(this.directories.size());
        Log.d(TAG, stringBuilder.toString());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter((Context) requireActivity(), android.R.layout.simple_spinner_item, directoryNames);//17367048
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //17367049
        this.directorySpinner.setAdapter(arrayAdapter);
        this.directorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onItemSelected: selected: ");
                stringBuilder.append((String) GalleryFragment.this.directories.get(position));
                Log.d(GalleryFragment.TAG, stringBuilder.toString());
                GalleryFragment galleryFragment = GalleryFragment.this;
                galleryFragment.setUpGridView((String) galleryFragment.directories.get(position));
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
}
