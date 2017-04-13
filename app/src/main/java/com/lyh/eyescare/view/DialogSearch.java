package com.lyh.eyescare.view;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.lyh.eyescare.CustomContract;
import com.lyh.eyescare.CustomPresenter;
import com.lyh.eyescare.R;
import com.lyh.eyescare.adapter.CustomAdapter;
import com.lyh.eyescare.base.BaseDialog;
import com.lyh.eyescare.bean.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyh on 2017/4/5.
 */

public class DialogSearch extends BaseDialog implements CustomContract.View {

    private Activity mContext;
    private EditText mEditSearch;
    private RecyclerView mRecyclerView;
    private CustomAdapter mCustomAdapter;
    private CustomPresenter mCustomPresenter;

    public DialogSearch(@NonNull Activity context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getAttributes().gravity = Gravity.TOP;

    }

    @Override
    public void loadAppInfoSuccess(List<AppInfo> list) {

    }

    @Override
    public void showProgressBar(boolean show) {

    }

    @Override
    protected float setWidthScale() {
        return 1;
    }

    @Override
    protected AnimatorSet setEnterAnim() {
        return null;
    }

    @Override
    protected AnimatorSet setExitAnim() {
        return null;
    }

    private OnDialogSearchListener mOnDialogSearchListener;

    //define interface
    public static interface OnDialogSearchListener {
        void onDialogSearchClick(AppInfo data);
    }

    public void setOnDialogSearchListener(OnDialogSearchListener listener) {
        this.mOnDialogSearchListener = listener;
    }

    @Override
    protected void init() {
        mCustomPresenter = new CustomPresenter(this, mContext);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mEditSearch = (EditText) findViewById(R.id.edit_search);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mCustomAdapter = new CustomAdapter(mContext);
        mRecyclerView.setAdapter(mCustomAdapter);
        mCustomAdapter.setOnItemClickListener(new CustomAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, AppInfo data) {
                if (mOnDialogSearchListener !=null){
                    mOnDialogSearchListener.onDialogSearchClick(data);
                }
            }
        });
        setCanceledOnTouchOutside(true);
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                mEditSearch.setFocusable(true);
                mEditSearch.setFocusableInTouchMode(true);
                mEditSearch.requestFocus();
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mEditSearch, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        mEditSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    mCustomAdapter.setInfos(new ArrayList<AppInfo>());
                } else {
                    mCustomPresenter.searchAppInfo(s.toString(), new CustomPresenter.ISearchResultListener() {
                        @Override
                        public void onSearchResult(List<AppInfo> appInfos) {
                            mCustomAdapter.setInfos(appInfos);
                        }
                    });
                }
            }
        });
    }

    public void updateDialogStatus(){
        Log.d("1111","mEditSearch = " + mEditSearch.getText().toString());
        if (mEditSearch.length() == 0) {
            mCustomAdapter.setInfos(new ArrayList<AppInfo>());
        } else {
            mCustomPresenter.searchAppInfo(mEditSearch.getText().toString(), new CustomPresenter.ISearchResultListener() {
                @Override
                public void onSearchResult(List<AppInfo> appInfos) {
                    mCustomAdapter.setInfos(appInfos);
                }
            });
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.dialog_search;
    }
}
