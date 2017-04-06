package com.lyh.eyescare.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyh.eyescare.CustomContract;
import com.lyh.eyescare.CustomPresenter;
import com.lyh.eyescare.R;
import com.lyh.eyescare.base.BaseActivity;
import com.lyh.eyescare.bean.AppInfo;
import com.lyh.eyescare.fragment.SysAppFragment;
import com.lyh.eyescare.fragment.UserAppFragment;
import com.lyh.eyescare.utils.SystemBarHelper;
import com.lyh.eyescare.view.DialogSearch;

import java.util.ArrayList;
import java.util.List;

public class CustomSettingActivity extends BaseActivity
implements CustomContract.View,View.OnClickListener{

    private RelativeLayout mTopLayout;
    private TextView mEditSearch;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private customPagerAdapter mPagerAdapter;
    private CustomPresenter mCustomPresenter;
    private DialogSearch mDialogSearch;

    private List<String> titles ;
    private List<Fragment> fragmentList ;

    @Override
    public int getLayoutId() {
        return R.layout.activity_custom_setting;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        mEditSearch = (TextView) findViewById(R.id.edit_search);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mTopLayout = (RelativeLayout) findViewById(R.id.top_layout);
        mTopLayout.setPadding(0, SystemBarHelper.getStatusBarHeight(this), 0, 0);
        mCustomPresenter = new CustomPresenter(this,this);
        mCustomPresenter.loadAppInfo(this);
    }

    @Override
    protected void initData() {
        mDialogSearch = new DialogSearch(this);
    }

    @Override
    protected void initAction() {
        mEditSearch.setOnClickListener(this);
        mDialogSearch.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mCustomPresenter.loadAppInfo(CustomSettingActivity.this);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.edit_search:
                mDialogSearch.show();
                break;
        }
    }

    @Override
    public void loadAppInfoSuccess(List<AppInfo> list) {
        int sysNum = 0;
        int userNum = 0;
        for (AppInfo appInfo:list){
            if (appInfo.isSysApp()) {
                sysNum++;
            } else {
                userNum++;
            }
        }
        titles = new ArrayList<>();
        titles.add("第三方应用" + " (" + userNum + ")");
        titles.add("系统应用" + " (" + sysNum + ")");
        SysAppFragment sysAppFragment = SysAppFragment.newInstance(list);
        UserAppFragment userAppFragment = UserAppFragment.newInstance(list);
        fragmentList = new ArrayList<>();
        fragmentList.add(userAppFragment);
        fragmentList.add(sysAppFragment);
        mPagerAdapter = new customPagerAdapter(getSupportFragmentManager(), fragmentList, titles);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }


    public class customPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragmentList = new ArrayList<>();
        private List<String> titles = new ArrayList<>();


        public customPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titles) {
            super(fm);
            this.fragmentList = fragmentList;
            this.titles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public int getCount() {
            return titles.size();
        }
    }
}
