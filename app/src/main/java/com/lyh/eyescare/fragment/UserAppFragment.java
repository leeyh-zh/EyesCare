package com.lyh.eyescare.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.lyh.eyescare.R;
import com.lyh.eyescare.activity.CustomItemSettingActivity;
import com.lyh.eyescare.adapter.CustomAdapter;
import com.lyh.eyescare.base.BaseFragment;
import com.lyh.eyescare.bean.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xian on 2017/3/1.
 */

public class UserAppFragment extends BaseFragment {

    public static UserAppFragment newInstance(List<AppInfo> list) {
        UserAppFragment userAppFragment = new UserAppFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data", (ArrayList<? extends Parcelable>) list);
        userAppFragment.setArguments(bundle);
        return userAppFragment;
    }

    private RecyclerView mRecyclerView;
    private List<AppInfo> data, list;
    private CustomAdapter mCustomAdapter;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_app_list;
    }

    @Override
    protected void init(View rootView) {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        data = getArguments().getParcelableArrayList("data");
        mCustomAdapter = new CustomAdapter(getActivity());
        mRecyclerView.setAdapter(mCustomAdapter);
        list = new ArrayList<>();
        for (AppInfo info : data) {
            if (!info.isSysApp()) {
                list.add(info);
            }
        }
        mCustomAdapter.setInfos(list);
        mCustomAdapter.setOnItemClickListener(new CustomAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position, AppInfo data) {
                mPosition = position;
                Log.d("11113", data.toString());
                Log.d("11113", "position = " + position);
                Intent intent = new Intent(UserAppFragment.this.getContext(), CustomItemSettingActivity.class);
                intent.putExtra("appInfo", data);
                Log.d("1111", "onItemClick  position = " + position);
                startActivityForResult(intent, 2);
            }
        });
    }
   int mPosition;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            AppInfo appInfo = data.getParcelableExtra("appInfo");
            list.set(mPosition,appInfo);
            Log.d("1111","set" + list.get(mPosition).toString());
            mCustomAdapter.setInfos(list);
            mCustomAdapter.notifyItemChanged(mPosition);
        }
    }
}
