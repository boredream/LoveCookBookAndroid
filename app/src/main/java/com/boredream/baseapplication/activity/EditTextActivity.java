package com.boredream.baseapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.StringUtils;
import com.boredream.baseapplication.R;
import com.boredream.baseapplication.base.BaseActivity;
import com.boredream.baseapplication.view.EditTextWithClear;
import com.boredream.baseapplication.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditTextActivity extends BaseActivity {

    public static final int REQ_CODE_EDIT_TEXT = 40001;

    @BindView(R.id.title_bar)
    TitleBar titleBar;
    @BindView(R.id.etwc_content)
    EditTextWithClear etwc_content;

    public static void start(Fragment context, String title, String oldValue) {
        Intent intent = new Intent(context.getActivity(), EditTextActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("oldValue", oldValue);
        context.startActivityForResult(intent, REQ_CODE_EDIT_TEXT);
    }

    public static void start(Activity context, String title, String oldValue) {
        Intent intent = new Intent(context, EditTextActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("oldValue", oldValue);
        context.startActivityForResult(intent, REQ_CODE_EDIT_TEXT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        String title = getIntent().getStringExtra("title");
        String oldValue = getIntent().getStringExtra("oldValue");
        titleBar.setTitle(title).setLeftBack().setRight("完成", v -> commit());
        etwc_content.setHint("请输入" + title);
        if (oldValue != null) {
            etwc_content.setText(oldValue);
        }
    }

    private void commit() {
        String newValue = etwc_content.getText().toString();
        if (StringUtils.isEmpty(newValue)) {
            showTip("内容不能为空");
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("newValue", newValue);
        setResult(RESULT_OK, intent);
        finish();
    }

    public static String getActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK || requestCode != REQ_CODE_EDIT_TEXT) return null;
        return data.getStringExtra("newValue");
    }

}