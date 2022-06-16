package sg.com.fuzzie.android.ui.shop;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import sg.com.fuzzie.android.FuzzieApplication_;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;

/**
 * Created by nurimanizam on 26/12/16.
 */

@EActivity(R.layout.activity_cashback_info)
public class CashbackInfoActivity extends BaseActivity {

    @AfterViews
    public void calledAfterViewInjection() {
        Bitmap background = ((FuzzieApplication_) getApplication()).lastActivityBitmap;
        if (background != null) {
            findViewById(R.id.content).setBackground(new BitmapDrawable(getResources(), background));
        } else {
            findViewById(R.id.content).setBackgroundColor(Color.parseColor("#D0000000"));
        }
    }

    @Click(R.id.ivClose)
    void backButtonClicked() {
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slid_out_up);
    }

}
