package com.example.whatsappstatussaver.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    Set<Integer> selectedFiles = new HashSet<>();
    Context context;
    GridView gridview;
    ImageAdapter imageAdapter;
    Menu menu;
    View root;
    TextView emptyMessage;


    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        /*final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        emptyMessage = root.findViewById(R.id.emptyMessage);

        context = root.getContext();
        File f = new File(FileManager.SAVED_STATUS_DIR);
        if (f.exists()){
            FileManager.fetchFilesFromDir(FileManager.whatsAppFiles, FileManager.WHATSAPP_STATUS_DIR);
        }
        imageAdapter = new ImageAdapter(context,FileManager.whatsAppFiles);
        gridview = root.findViewById(R.id.gridview);
        gridview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        gridview.setAdapter(imageAdapter);
        setGridViewListeners();

        if(FileManager.whatsAppFiles.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
        }
        else{
            emptyMessage.setVisibility(View.INVISIBLE);
        }

        //gridview.setColumnWidth(100);
        //gridview.setPadding(4,4,4,4);
        //gridview.setHorizontalSpacing(50);

        return root;
    }



    void refreshGridView(){
        FileManager.fetchFilesFromDir(FileManager.whatsAppFiles, FileManager.WHATSAPP_STATUS_DIR);
        imageAdapter.notifyDataSetChanged();
        gridview.setAdapter(imageAdapter);
        if(FileManager.whatsAppFiles.isEmpty()) {
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
        if (item.getItemId() == R.id.save) {
            FileManager.saveToGallery(selectedFiles);
            Toast.makeText(context,R.string.saved_to_gallery,Toast.LENGTH_SHORT).show();
            selectedFiles.clear();
            refreshGridView();
            menu.findItem(R.id.save).setVisible(false);
            menu.findItem(R.id.share).setVisible(false);
            menu.findItem(R.id.select_all).setVisible(false);
            menu.findItem(R.id.refresh).setVisible(true);
        }
        if (item.getItemId() == R.id.refresh){
            refreshGridView();
            Toast.makeText(context,R.string.refreshed,Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == R.id.share){
            FileManager.share(context,FileManager.whatsAppFiles,selectedFiles);
            //Toast.makeText(context,"Shared",Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == R.id.select_all){
            Toast.makeText(context,"Select ALL",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setGridViewListeners(){
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedFiles.isEmpty()) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName(), new File(FileManager.whatsAppFiles.get(position)));
                    //String mimeType = getContentResolver().getType(photoURI);
                    intent.setDataAndType(photoURI, context.getContentResolver().getType(photoURI));
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                    //FileManager.saveToGallery(FileManager.files.get(position));
                }
                else {
                    if(selectedFiles.contains(position)) {
                        selectedFiles.remove(position);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            view.setAlpha(1f);
                        } else {
                            AlphaAnimation alphaAnim = new AlphaAnimation(1f, 1f);
                            alphaAnim.setDuration(0);
                            alphaAnim.setFillAfter(true);
                            view.startAnimation(alphaAnim);
                        }
                    }else
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            view.setAlpha(0.3f);
                        } else {
                            AlphaAnimation alphaAnim = new AlphaAnimation(0.3f, 0.3f);
                            alphaAnim.setDuration(0);
                            alphaAnim.setFillAfter(true);
                            view.startAnimation(alphaAnim);
                        }
                        selectedFiles.add(position);
                    }
                }
                if (selectedFiles.isEmpty()) {
                    menu.findItem(R.id.save).setVisible(false);
                    menu.findItem(R.id.share).setVisible(false);
                    menu.findItem(R.id.select_all).setVisible(false);
                    menu.findItem(R.id.refresh).setVisible(true);
                }
                else {
                    menu.findItem(R.id.save).setVisible(true);
                    menu.findItem(R.id.share).setVisible(true);
                    menu.findItem(R.id.select_all).setVisible(true);
                    menu.findItem(R.id.refresh).setVisible(false);
                }
            }
        });
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    view.setAlpha(0.3f);
                } else {
                    AlphaAnimation alphaAnim = new AlphaAnimation(0.3f, 0.3f);
                    alphaAnim.setDuration(0);
                    alphaAnim.setFillAfter(true);
                    view.startAnimation(alphaAnim);
                }
                selectedFiles.add(position);
                if (selectedFiles.isEmpty()) {
                    menu.findItem(R.id.save).setVisible(false);
                    menu.findItem(R.id.share).setVisible(false);
                    menu.findItem(R.id.select_all).setVisible(false);
                    menu.findItem(R.id.refresh).setVisible(true);
                }
                else {
                    menu.findItem(R.id.save).setVisible(true);
                    menu.findItem(R.id.share).setVisible(true);
                    menu.findItem(R.id.select_all).setVisible(true);
                    menu.findItem(R.id.refresh).setVisible(false);
                }
                return true;
            }
        });
    }
}