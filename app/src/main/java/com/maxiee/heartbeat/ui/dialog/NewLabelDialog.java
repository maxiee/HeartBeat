package com.maxiee.heartbeat.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.maxiee.heartbeat.R;

/**
 * Created by maxiee on 15-6-16.
 */
public class NewLabelDialog extends AppCompatDialog{

    private Toolbar mToolbar;
    private String mTextLabel;
    private EditText mEditLabel;
    private OnAddFinishedListener mCallback;

    public interface OnAddFinishedListener {
        public void addLabel(String label);
    }

    public NewLabelDialog(Context context) {
        super(context, R.style.AppTheme_Dialog);
    }

    public void setOnAddFinishedListener(OnAddFinishedListener callback) {
        mCallback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_new_label);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mEditLabel = (EditText) findViewById(R.id.edit_label);

        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mToolbar.inflateMenu(R.menu.dialog_new_thought);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.add) {
                    mTextLabel = mEditLabel.getText().toString();
                    if (mTextLabel.isEmpty()) {
                        Toast.makeText(
                                getContext(),
                                R.string.notempty,
                                Toast.LENGTH_LONG).show();
                        return true;
                    }

                    mCallback.addLabel(mTextLabel);
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        String title = getContext().getString(R.string.new_tag);
        mToolbar.setTitle(title);
    }
}
