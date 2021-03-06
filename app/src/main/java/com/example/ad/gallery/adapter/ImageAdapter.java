package com.example.ad.gallery.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ad.gallery.activity.ListPhotoActivity;
import com.example.ad.gallery.model.ImageItem;
import com.example.ad.gallery.R;
import com.example.ad.gallery.activity.ViewPhotoActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ImageAdapter extends ArrayAdapter<ImageItem> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<ImageItem> data = new ArrayList<ImageItem>();
    public ArrayList<ImageItem> selectedItem = new ArrayList<>();

    public boolean checked = false;

    public ImageAdapter(Context context, int layoutResourceId, ArrayList<ImageItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        selectedItem.clear();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(layoutResourceId, parent, false);
        //
        final ImageItem item = data.get(position);
        //
        //TextView imageTitle = (TextView) row.findViewById(R.id.text);
        ImageView image = (ImageView) row.findViewById(R.id.imageView);
        final CheckBox cb = (CheckBox) row.findViewById(R.id.cbImage);
        //
        Log.i("ImageAdapter", "Item : " +position + ":\n\t"+ item.getPath());
        String extention = ".jpg";
        if (item.getPath().endsWith(extention)) {
            image.setImageBitmap(resizeBitmap(item.getPath(),image.getWidth(),image.getHeight()));
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewPhotoActivity.class);
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                }
            });

        } else {
            // Set Image of Video :
            try {
                Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(item.getPath(),
                        MediaStore.Images.Thumbnails.MINI_KIND);
                image.setImageBitmap(thumbnail);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(item.getPath())), "video/*");
                        v.getContext().startActivity(intent);
                    }
                });

                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                //use one of overloaded setDataSource() functions to set your data source
                retriever.setDataSource(context, Uri.fromFile(new File(item.getPath())));
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long timeInMillisec = Long.parseLong(time );
                TextView txtVideo = (TextView) row.findViewById(R.id.txtVideo);
                txtVideo.setText((new SimpleDateFormat("mm:ss:SS")).format(new Date(timeInMillisec)));
                txtVideo.setVisibility(View.VISIBLE);
            } catch (Exception ex) {
                Log.e("ImageAdapter", "", ex);
            }
        }
        if(checked){
            cb.setVisibility(View.VISIBLE);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cb.setChecked(!cb.isChecked());
                }
            });
        }else {
            cb.setVisibility(View.INVISIBLE);
        }
        //
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        selectedItem.add(item);
                        Log.i("ImageAdapter", "Select item : " + item);
                    } catch (Exception e) {
                        Log.e("ImageAdapter", "Add selected arraylist fail" , e);
                    }
                } else {
                    try {
                        selectedItem.remove(item);
                        Log.i("ImageAdapter", "Remove item : " + item);
                    } catch (Exception e) {
                        Log.e("ImageAdapter", "Remove item from arraylist fail" , e);
                    }
                }
            }
        });
        //
        return row;
    }
    public Bitmap resizeBitmap(String photoPath, int targetW, int targetH) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 2;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true; //Deprecated API 21

        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }
}