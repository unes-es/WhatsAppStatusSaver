package com.applications.coffee.statussaver.recyclerViewSelection;

import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;

public class CustomItemDetail extends ItemDetailsLookup.ItemDetails {

    private final int adapterPosition;
    private final String selectionKey;

    public CustomItemDetail(int adapterPosition,String selectionKey){
        this.adapterPosition = adapterPosition;
        this.selectionKey = selectionKey;
    }

    @Override
    public int getPosition() {
        return adapterPosition;
    }

    @Nullable
    @Override
    public String getSelectionKey() {
        return selectionKey;
    }
}
