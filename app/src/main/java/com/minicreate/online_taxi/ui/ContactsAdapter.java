package com.minicreate.online_taxi.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.minicreate.online_taxi.R;

public class ContactsAdapter extends RecyclerView.Adapter {

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View thisItemsView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contacts_info,
                viewGroup, false);
        return new CustomViewHolder(thisItemsView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView mContactsNameView;
        ImageView mCallContactVIew;
        TextView mPhoneNumberView;

        public CustomViewHolder(View itemView) {
            super(itemView);
            mContactsNameView = itemView.findViewById(R.id.tv_contact_name);
            mCallContactVIew = itemView.findViewById(R.id.tv_call_contact);
            mPhoneNumberView = itemView.findViewById(R.id.tv_phone_number);
        }
    }
}

