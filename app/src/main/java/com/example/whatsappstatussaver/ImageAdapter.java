package com.example.whatsappstatussaver;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    List<String> files = new ArrayList<>();


    // Constructor
    public ImageAdapter(Context c, List<String> files) {
        mContext = c;
        this.files = files;
    }

    public int getCount() {
        return files.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }


    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView = new ImageView(mContext);
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.grid_image_view, null);
            imageView = convertView.findViewById(R.id.imageView);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Log.d("tag", "getView: "+imageView.getMeasuredWidth());
            //imageView.setLayoutParams(new GridView.LayoutParams(300, 300));
        }



        /*if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(300, 300));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //imageView.setPadding(8, 8, 8, 8);
            GradientDrawable border = new GradientDrawable();
            border.setStroke(6, 0xFFFF0000);
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                imageView.setBackgroundDrawable(border);
            }
            else {
                imageView.setBackground(border);
            }
        }
        else
        {
            imageView = (ImageView) convertView;
        }*/

        //files.get(position)
        //Bitmap myBitmap = BitmapFactory.decodeFile(files.get(position));

        Bitmap myBitmap = ThumbnailUtils.createVideoThumbnail(files.get(position), MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);

        if (myBitmap == null)
        {
            myBitmap = BitmapFactory.decodeFile(files.get(position));
        }

        imageView.setImageBitmap(myBitmap);


        return convertView;
    }

}