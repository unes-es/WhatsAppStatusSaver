package com.applications.coffee.whatsappstatussaver.recyclerViewSelection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;

import java.util.List;

public class CustomItemKeyProvider extends ItemKeyProvider {

    private final List<String> data;

    public CustomItemKeyProvider(int scope, List<String> data){
        super(scope);
        this.data = data;
    }

    @Nullable
    @Override
    public Object getKey(int position) {
        return data.get(position);
    }

    @Override
    public int getPosition(@NonNull Object key) {
        return data.indexOf(key);
    }
}
