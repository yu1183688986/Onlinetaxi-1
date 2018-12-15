package com.minicreate.online_taxi.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.minicreate.online_taxi.R;
import com.minicreate.online_taxi.entity.AboutUsEntity;

import java.util.List;

public class AboutUsAdapter extends RecyclerView.Adapter {
    List<AboutUsEntity> list;

    public AboutUsAdapter(List<AboutUsEntity> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View thisItemsView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_about_us,
                viewGroup, false);
        return new AboutUsHolder(thisItemsView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((AboutUsHolder) viewHolder).nameText.setText(list.get(i).propertyName);
        ((AboutUsHolder) viewHolder).valueText.setText(list.get(i).propertyValue);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class AboutUsHolder extends RecyclerView.ViewHolder {
        public TextView nameText;
        public TextView valueText;

        public AboutUsHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.txt_property_name);
            valueText = itemView.findViewById(R.id.txt_property_value);
        }
    }
}
