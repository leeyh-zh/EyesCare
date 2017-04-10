package com.lyh.eyescare;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.flipboard.bottomsheet.commons.BottomSheetFragment;

/**
 * Created by lyh on 2017/4/10.
 */
public class MyFragment extends BottomSheetFragment {

    private Button mButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_custom_setting, container, false);
        mButton = (Button) view.findViewById(R.id.btn_filter_blue);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPOP();
            }


        });

    }

    private void showPOP() {
        PopupWindow pop = new PopupWindow(getContext());
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.pop_color, null);
        pop.setContentView(contentView);
        pop.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        pop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        pop.showAsDropDown(mButton, 0, 0, Gravity.TOP);
    }

}


