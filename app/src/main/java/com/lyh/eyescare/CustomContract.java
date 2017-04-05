package com.lyh.eyescare;

import android.content.Context;

import com.lyh.eyescare.base.BasePresenter;
import com.lyh.eyescare.base.BaseView;
import com.lyh.eyescare.bean.AppInfo;

import java.util.List;

/**
 * Created by lyh on 2017/4/1.
 */

public interface CustomContract {
    interface View extends BaseView<Presenter> {
        void loadAppInfoSuccess(List<AppInfo> list);
    }

    interface Presenter extends BasePresenter {
        void loadAppInfo(Context context);

        void searchAppInfo(String search, CustomPresenter.ISearchResultListener listener);

        void onDestroy();
    }
}
