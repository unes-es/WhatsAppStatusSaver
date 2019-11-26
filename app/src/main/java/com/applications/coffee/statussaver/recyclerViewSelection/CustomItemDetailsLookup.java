package com.applications.coffee.statussaver.recyclerViewSelection;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

public class CustomItemDetailsLookup extends ItemDetailsLookup {

    private final RecyclerView recyclerView;

    public CustomItemDetailsLookup(RecyclerView recyclerView){
        this.recyclerView = recyclerView;
    }

    @Nullable
    @Override
    public CustomItemDetail getItemDetails(@NonNull MotionEvent e) {
        View view = recyclerView.findChildViewUnder(e.getX(),e.getY());
        if(view != null){
            RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
            if (viewHolder instanceof RecyclerViewAdapter.RecyclerViewHolder){
                return ((RecyclerViewAdapter.RecyclerViewHolder)viewHolder).getItemDetails();
            }
        }
        return null;
    }
}
