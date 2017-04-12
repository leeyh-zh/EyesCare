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
import com.lyh.eyescare.manager.AppInfoManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xian on 2017/3/1.
 */

public class SysAppFragment extends BaseFragment {

    public static SysAppFragment newInstance(List<AppInfo> list) {
        SysAppFragment sysAppFragment = new SysAppFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data", (ArrayList<? extends Parcelable>) list);
        sysAppFragment.setArguments(bundle);
        return sysAppFragment;
    }

    private RecyclerView mRecyclerView;
    private List<AppInfo> data, list, updateList;
    private CustomAdapter mCustomAdapter;
    private AppInfoManager mAppInfoManager;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_app_list;
    }

    @Override
    protected void init(View rootView) {
        mAppInfoManager = new AppInfoManager(getContext());
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        data = getArguments().getParcelableArrayList("data");
        mCustomAdapter = new CustomAdapter(getActivity());
        mRecyclerView.setAdapter(mCustomAdapter);
        showRecycleView(data);
        mCustomAdapter.setOnItemClickListener(new CustomAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position, AppInfo data) {
                mPosition = position;
                Intent intent = new Intent(SysAppFragment.this.getContext(), CustomItemSettingActivity.class);
                intent.putExtra("appInfo", data);
                startActivityForResult(intent, 2);
            }
        });
    }

    private void showRecycleView(List<AppInfo> infoList) {
        Log.d("11112","list infoList = " + infoList.size());
        list = new ArrayList<>();
        for (AppInfo info : infoList) {
            if (info.isSysApp()) {
                list.add(info);
            }
        }
        Log.d("11112","list size = " + list.size());
        mCustomAdapter.setInfos(list);
    }

    int mPosition;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            updateList = new ArrayList<>();
            updateList = mAppInfoManager.getAllAppInfos();
            List<AppInfo> newList = new ArrayList<>();
            //showRecycleView(updateList);
//            mCustomAdapter.setInfos(list);
//            mCustomAdapter.notifyDataSetChanged();
            //mCustomAdapter.notifyItemChanged(mPosition);
        }
    }

}
