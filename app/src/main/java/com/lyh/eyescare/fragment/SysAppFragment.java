package com.lyh.eyescare.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lyh.eyescare.R;
import com.lyh.eyescare.adapter.CustomAdapter;
import com.lyh.eyescare.base.BaseFragment;
import com.lyh.eyescare.bean.AppInfo;

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
            if (info.isSysApp()) {
                list.add(info);
            }
        }
        mCustomAdapter.setInfos(list);
    }


}
