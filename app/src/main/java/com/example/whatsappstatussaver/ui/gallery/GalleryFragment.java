package com.example.whatsappstatussaver.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.whatsappstatussaver.FileManager;
import com.example.whatsappstatussaver.ImageAdapter;
import com.example.whatsappstatussaver.R;


import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;

    Set<Integer> selectedFiles = new HashSet<>();
    Context context;
    GridView gridview;
    ImageAdapter imageAdapter;
    Menu menu;
    View root;
    TextView emptyMessage;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        root = inflater.inflate(R.layout.fragment_gallery, container, false);
        setHasOptionsMenu(true);
        context = root.getContext();
        emptyMessage = root.findViewById(R.id.emptyMessage);
        /*final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/


        File f = new File(FileManager.SAVED_STATUS_DIR);
        if (!f.exists()){
            f.mkdir();
        }

        FileManager.fetchFilesFromDir(FileManager.savedFiles, FileManager.SAVED_STATUS_DIR);

        imageAdapter = new ImageAdapter(context,FileManager.savedFiles);
        gridview = root.findViewById(R.id.gridview1);
        gridview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        gridview.setAdapter(imageAdapter);
        setGridViewListener();
        if(FileManager.savedFiles.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
        }
        else{
            emptyMessage.setVisibility(View.INVISIBLE);
        }
        return root;
    }
    void refreshGridView(){
        FileManager.fetchFilesFromDir(FileManager.savedFiles, FileManager.SAVED_STATUS_DIR);
        imageAdapter = new ImageAdapter(context,FileManager.savedFiles,selectedFiles);
        imageAdapter.notifyDataSetChanged();
        gridview.setAdapter(imageAdapter);
        if(FileManager.savedFiles.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
        }
        else{
            emptyMessage.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            FileManager.delete(selectedFiles);
            Toast.makeText(context,R.string.deleted,Toast.LENGTH_SHORT).show();
            selectedFiles.clear();
            refreshGridView();
            menu.findItem(R.id.delete).setVisible(false);
            menu.findItem(R.id.share).setVisible(false);
            menu.findItem(R.id.refresh).setVisible(true);
            menu.findItem(R.id.select_all).setVisible(false);
        }
        if (item.getItemId() == R.id.refresh){
            refreshGridView();
            Toast.makeText(context,R.string.refreshed,Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == R.id.share){
            FileManager.share(context,FileManager.savedFiles,selectedFiles);
            //Toast.makeText(context,"Shared",Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == R.id.select_all){
            Toast.makeText(context,"Select ALL",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setGridViewListener(){
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedFiles.isEmpty()) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName(), new File(FileManager.savedFiles.get(position)));
                    intent.setDataAndType(photoURI, context.getContentResolver().getType(photoURI));
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                }
                else {
                    if(selectedFiles.contains(position)) {
                        selectedFiles.remove(position);
                        view.setAlpha(1f);
                    }else
                    {
                        view.setAlpha(0.3f);
                        selectedFiles.add(position);
                    }
                }
                if (selectedFiles.isEmpty()) {
                    menu.findItem(R.id.delete).setVisible(false);
                    menu.findItem(R.id.share).setVisible(false);
                    menu.findItem(R.id.select_all).setVisible(false);
                    menu.findItem(R.id.refresh).setVisible(true);

                }
                else {
                    menu.findItem(R.id.delete).setVisible(true);
                    menu.findItem(R.id.share).setVisible(true);
                    menu.findItem(R.id.select_all).setVisible(true);
                    menu.findItem(R.id.refresh).setVisible(false);

                }
            }
        });
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                view.setAlpha(0.3f);
                selectedFiles.add(position);
                if (selectedFiles.isEmpty()) {
                    menu.findItem(R.id.delete).setVisible(false);
                    menu.findItem(R.id.share).setVisible(false);
                    menu.findItem(R.id.select_all).setVisible(false);
                    menu.findItem(R.id.refresh).setVisible(true);
                }
                else {
                    menu.findItem(R.id.delete).setVisible(true);
                    menu.findItem(R.id.share).setVisible(true);
                    menu.findItem(R.id.select_all).setVisible(true);
                    menu.findItem(R.id.refresh).setVisible(false);
                }
                return true;
            }
        });
    }
}