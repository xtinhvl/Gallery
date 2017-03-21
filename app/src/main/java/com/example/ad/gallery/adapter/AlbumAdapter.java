package com.example.ad.gallery.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ad.gallery.activity.MainScreenActivity;
import com.example.ad.gallery.model.Album;
import com.example.ad.gallery.R;
import com.example.ad.gallery.activity.ListPhotoActivity;

import java.util.ArrayList;

/**
 * Created by AD on 08/03/2017.
 */

public class AlbumAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList<Album> arrayList = new ArrayList<>();

    public AlbumAdapter(Context context, int resource, ArrayList<Album> objects) {
        super(context, resource, objects);
        this.context = context;
        this.arrayList = objects;
        this.layoutResourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(layoutResourceId, parent, false);
        TextView imageTitle = (TextView) row.findViewById(R.id.text);
        ImageView image = (ImageView) row.findViewById(R.id.imageView);

        Album item = arrayList.get(position);
        imageTitle.setText(item.getName());
        image.setImageBitmap(resizeBitmap(item.getTitle(),image.getWidth(),image.getHeight()));
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListPhotoActivity.class);
                intent.putExtra(MainScreenActivity.ALBUM_NAME, item.getName());
                context.startActivity(intent);
            }
        });

        return row;
    }
    public Bitmap resizeBitmap(String photoPath, int targetW, int targetH) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true; //Deprecated API 21

        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }

}
