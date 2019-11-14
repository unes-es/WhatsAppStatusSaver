package com.applications.coffee.whatsappstatussaver.recyclerViewSelection;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;


import com.applications.coffee.whatsappstatussaver.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    public List<String> data;
    Context context;
    public List<Uri> thumbnails = new ArrayList<>();

    private SelectionTracker selectionTracker;

    public SelectionTracker getSelectionTracker() {
        return selectionTracker;
    }
    public void setSelectionTracker(SelectionTracker selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements ViewHolderWithDetails {
        public ImageView imageView;
        public boolean charged = false;
        public RecyclerViewHolder(View v){
            super(v);
            imageView = v.findViewById(R.id.imageView);
        }

        public final void bind(boolean isActive) {
            itemView.findViewById(R.id.mask).setActivated(isActive);
        }

        @Override
        public CustomItemDetail getItemDetails( ){
            return new CustomItemDetail(getAdapterPosition(),data.get(getAdapterPosition()));
        }

    }

    public RecyclerViewAdapter(List<String> data,Context context){
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_image_view,parent,false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position) {
        if(!holder.charged) {
            Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName(), new File(data.get(position)));
            if (context.getContentResolver().getType(uri) == "video/mp4") {
                new AsyncTask<String, String, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(String[] objects) {
                        return ThumbnailUtils.createVideoThumbnail(objects[0], MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                    }
                    @Override
                    protected void onPostExecute(Bitmap o) {
                        super.onPostExecute(o);
                        Uri uri = getImageUri(context, o);
                        Picasso.get().load(uri).into((holder.imageView));
                        thumbnails.add(uri);
                    }
                }.execute(data.get(position));
                holder.imageView.setForeground(context.getDrawable(R.drawable.ic_play_circle_outline_24px));
            } else {
                Picasso.get().load(new File(data.get(position))).into(holder.imageView);
                holder.imageView.setForeground(null);
            }
        }
        holder.bind(selectionTracker.isSelected(data.get(position)));
        holder.charged = true;
    }

    public void cleanThumbnails(){
        for (Uri thumbnail:thumbnails) {
            context.getContentResolver().delete(thumbnail, null, null);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void selectAll(){
        if(selectionTracker.getSelection().size() == data.size()) {
            selectionTracker.setItemsSelected(data, false);
        }
        else {
            selectionTracker.setItemsSelected(data, true);
        }
        notifyDataSetChanged();
    }
}
