package com.example.instaclone.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.instaclone.R;
import java.util.ArrayList;

public class GridImageAdapter extends ArrayAdapter<String> {
    private ArrayList<String> imgUrls;
    private Context mContext;
    private int mResource;

    public static class ViewHolder {
        SquareImageView image;
    }

    public GridImageAdapter(Context context, int resource, ArrayList<String> imgUrls) {
        super(context, resource, imgUrls);
        this.mContext = context;
        this.imgUrls = imgUrls;
        this.mResource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(this.mResource, parent, false);
            holder = new ViewHolder();
            holder.image = (SquareImageView) convertView.findViewById(R.id.gridImageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ((RequestBuilder) ((RequestBuilder) Glide.with(this.mContext).load((String) getItem(position)).centerCrop()).placeholder((int) R.drawable.ic_android)).into(holder.image);
        return convertView;
    }
}
