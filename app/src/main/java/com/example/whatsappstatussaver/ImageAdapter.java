package com.example.whatsappstatussaver;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CancellationSignal;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public List<String> files = new ArrayList<>();
    Set<Integer> selectedFiles = new HashSet<>();

    public ImageAdapter(Context c, List<String> files) {
        mContext = c;
        this.files = files;
    }

    public ImageAdapter(Context c, List<String> files, Set<Integer> selectedFiles)
    {
        mContext = c;
        this.files = files;
        this.selectedFiles = selectedFiles;
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

    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            viewHolder.image = new ImageView(mContext);
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.grid_image_view, null);
            convertView.setTag(viewHolder);
        }

        if(selectedFiles.contains(position)) {
            convertView.setAlpha(0.3f);
        }else
        {
            convertView.setAlpha(1f);
        }


        viewHolder.image = convertView.findViewById(R.id.imageView);

        Uri photoURI = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName(), new File(files.get(position)));
        Bitmap myBitmap;
        if (mContext.getContentResolver().getType(photoURI) == "video/mp4")
        {
            myBitmap = ThumbnailUtils.createVideoThumbnail(files.get(position), MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
            viewHolder.image.setForeground(mContext.getDrawable(R.drawable.ic_play_circle_outline_24px));
        }
        else {
            myBitmap = BitmapFactory.decodeFile(files.get(position));
        }
        viewHolder.image.setImageBitmap(myBitmap);

        return convertView;
    }

    /*public Bitmap loadImageFromFile(Integer position){
        //Bitmap myBitmap = ThumbnailUtils.createVideoThumbnail(files.get(position), MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
        //if (myBitmap == null)
        /*{
            Bitmap myBitmap = BitmapFactory.decodeFile(files.get(position));
        }*/
        /*else {
            imageView.setForeground(mContext.getDrawable(R.drawable.ic_play_circle_outline_24px));
        }
        return BitmapFactory.decodeFile(files.get(position));
    }*/

    private static class ViewHolder{ public ImageView image;}

}