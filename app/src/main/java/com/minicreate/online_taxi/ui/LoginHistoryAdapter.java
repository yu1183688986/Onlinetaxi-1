package com.minicreate.online_taxi.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.minicreate.online_taxi.R;
import com.minicreate.online_taxi.entity.UserInfoEntity;

import java.util.List;

public class LoginHistoryAdapter extends RecyclerView.Adapter {
    List<UserInfoEntity> userInfos;

    public void setUserInfos(List<UserInfoEntity> userInfos) {
        this.userInfos = userInfos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View thisItemsView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_history,
                viewGroup, false);
        return new LoginHistoryHolder(thisItemsView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        UserInfoEntity entity = userInfos.get(i);
        ((LoginHistoryHolder) viewHolder).mUserNameView.setText(entity.getName());
        ((LoginHistoryHolder) viewHolder).mOperationView.setText("签到时间");
        ((LoginHistoryHolder) viewHolder).mTvTimeView.setText(entity.getSignInTime());
    }

    @Override
    public int getItemCount() {
        return userInfos.size();
    }

    private class LoginHistoryHolder extends RecyclerView.ViewHolder {
        public TextView mUserNameView;
        public TextView mOperationView;
        public TextView mTvTimeView;

        public LoginHistoryHolder(View itemView) {
            super(itemView);
            mUserNameView = itemView.findViewById(R.id.tv_user_name);
            mOperationView = itemView.findViewById(R.id.tv_operation);
            mTvTimeView = itemView.findViewById(R.id.tv_time);
        }
    }
}
