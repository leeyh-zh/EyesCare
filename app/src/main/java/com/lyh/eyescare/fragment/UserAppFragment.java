package com.lyh.eyescare.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lyh.eyescare.CustomContract;
import com.lyh.eyescare.CustomPresenter;
import com.lyh.eyescare.R;
import com.lyh.eyescare.activity.CustomItemSettingActivity;
import com.lyh.eyescare.adapter.CustomAdapter;
import com.lyh.eyescare.base.BaseFragment;
import com.lyh.eyescare.bean.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class UserAppFragment extends BaseFragment implements CustomContract.View {

    public static UserAppFragment newInstance(List<AppInfo> list) {
        UserAppFragment userAppFragment = new UserAppFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data", (ArrayList<? extends Parcelable>) list);
        userAppFragment.setArguments(bundle);
        return userAppFragment;
    }

    private RecyclerView mRecyclerView;
    private List<AppInfo> data, list,updateList;
    private CustomAdapter mCustomAdapter;
    private CustomPresenter mCustomPresenter;

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
        mCustomPresenter = new CustomPresenter(this,getContext());
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
            public void onItemClick(View view, AppInfo data) {
                Intent intent = new Intent(UserAppFragment.this.getContext(), CustomItemSettingActivity.class);
                intent.putExtra("appInfo", data);
                startActivityForResult(intent, 2);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            mCustomPresenter.loadAppInfo(getContext());
        }
    }

    @Override
    public void loadAppInfoSuccess(List<AppInfo> list) {
        updateList = new ArrayList<>();
        for (AppInfo info : list) {
            if (!info.isSysApp()) {
                updateList.add(info);
            }
        }
        mCustomAdapter.setInfos(updateList);
    }

    @Override
    public void showProgressBar() {

    }
}
