package com.applications.coffee.whatsappstatussaver.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ActionMode;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.OnDragInitiatedListener;
import androidx.recyclerview.selection.OnItemActivatedListener;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applications.coffee.whatsappstatussaver.FileManager;
import com.applications.coffee.whatsappstatussaver.R;
import com.applications.coffee.whatsappstatussaver.recyclerViewSelection.ActionModeController;
import com.applications.coffee.whatsappstatussaver.recyclerViewSelection.CustomItemDetailsLookup;
import com.applications.coffee.whatsappstatussaver.recyclerViewSelection.CustomItemKeyProvider;
import com.applications.coffee.whatsappstatussaver.recyclerViewSelection.RecyclerViewAdapter;


import java.io.File;
import java.util.Iterator;
import java.util.List;

public class GalleryFragment extends Fragment {

    Context context;
    Menu menu;
    View root;
    TextView emptyMessage;

    SelectionTracker selectionTracker;
    private ActionMode actionMode;
    RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_gallery, container, false);
        setHasOptionsMenu(true);
        context = root.getContext();
        emptyMessage = root.findViewById(R.id.emptyMessage);
        File f = new File(FileManager.SAVED_STATUS_DIR);
        if (!f.exists()){
            f.mkdir();
        }

        FileManager.fetchFilesFromDir(FileManager.savedFiles, FileManager.SAVED_STATUS_DIR);

        if(FileManager.savedFiles.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
        }
        else{
            emptyMessage.setVisibility(View.INVISIBLE);
        }
        setUpRecyclerView(FileManager.savedFiles);
        return root;
    }

    void refreshRecyclerView(){
        FileManager.fetchFilesFromDir(FileManager.savedFiles, FileManager.SAVED_STATUS_DIR);
        recyclerViewAdapter.cleanThumbnails();
        setUpRecyclerView(FileManager.savedFiles);
        recyclerViewAdapter.notifyDataSetChanged();
        if(FileManager.whatsAppFiles.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
        }
        else{
            emptyMessage.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerViewAdapter.cleanThumbnails();
    }

    @Override
    public void onPause() {
        super.onPause();
        recyclerViewAdapter.cleanThumbnails();
    }

    @Override
    public void onStop() {
        super.onStop();
        recyclerViewAdapter.cleanThumbnails();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            FileManager.delete(selectionTracker.getSelection());
            Toast.makeText(context,R.string.deleted,Toast.LENGTH_SHORT).show();
            recyclerViewAdapter.notifyDataSetChanged();
            selectionTracker.clearSelection();
        }
        if (item.getItemId() == R.id.refresh){
            refreshRecyclerView();
            Toast.makeText(context,R.string.refreshed,Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == R.id.share){
            FileManager.share(context,FileManager.savedFiles,selectionTracker.getSelection());
        }
        if (item.getItemId() == R.id.select_all){
            recyclerViewAdapter.selectAll();
        }
        return super.onOptionsItemSelected(item);
    }
    public void setUpRecyclerView(List<String> data){
        recyclerViewAdapter = new RecyclerViewAdapter(data,context);
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context,3));
        recyclerViewAdapter.setHasStableIds(true);
        recyclerView.setAdapter(recyclerViewAdapter);
        selectionTracker = new SelectionTracker.Builder<>(
                "my-selection-id",
                recyclerView,
                new CustomItemKeyProvider(1, data),
                new CustomItemDetailsLookup(recyclerView),
                StorageStrategy.createLongStorage()
        )
                .withOnItemActivatedListener(new OnItemActivatedListener<Long>() {
                    @Override
                    public boolean onItemActivated(@NonNull ItemDetailsLookup.ItemDetails<Long> item, @NonNull MotionEvent e) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        Uri photoURI = FileProvider.getUriForFile(context, context.getPackageName(), new File(recyclerViewAdapter.data.get(item.getPosition())));
                        intent.setDataAndType(photoURI, context.getContentResolver().getType(photoURI));
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);

                        Log.d("tag", "Selected ItemId: " +item);
                        return true;
                    }
                })
                .withOnDragInitiatedListener(new OnDragInitiatedListener() {
                    @Override
                    public boolean onDragInitiated(@NonNull MotionEvent e) {
                        Log.d("tag", "onDragInitiated");
                        return true;
                    }

                })
                .build();
        recyclerViewAdapter.setSelectionTracker(selectionTracker);

        selectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
            @Override
            public void onItemStateChanged(@NonNull Object key, boolean selected) {
                super.onItemStateChanged(key, selected);
            }

            @Override
            public void onSelectionRefresh() {
                super.onSelectionRefresh();
            }

            @Override
            public void onSelectionChanged() {

                super.onSelectionChanged();
                if (selectionTracker.hasSelection() && actionMode == null) {
                    menu.findItem(R.id.delete).setVisible(true);
                    menu.findItem(R.id.share).setVisible(true);
                    menu.findItem(R.id.select_all).setVisible(true);
                    menu.findItem(R.id.refresh).setVisible(false);
                } else if (!selectionTracker.hasSelection() && actionMode != null) {
                    actionMode.finish();
                    actionMode = null;
                } else {
                    menu.findItem(R.id.delete).setVisible(false);
                    menu.findItem(R.id.share).setVisible(false);
                    menu.findItem(R.id.select_all).setVisible(false);
                    menu.findItem(R.id.refresh).setVisible(true);
                }
                Iterator<String> itemIterable = selectionTracker.getSelection().iterator();
                while (itemIterable.hasNext()) {
                    Log.i("tag", itemIterable.next());
                }
            }

            @Override
            public void onSelectionRestored() {
                super.onSelectionRestored();
            }
        });
    }

}