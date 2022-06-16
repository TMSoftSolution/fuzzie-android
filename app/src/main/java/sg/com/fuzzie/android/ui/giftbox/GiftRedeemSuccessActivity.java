package sg.com.fuzzie.android.ui.giftbox;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.MainActivity_;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

/**
 * Created by nurimanizam on 26/12/16.
 */

@EActivity(R.layout.activity_redeem_success)
public class GiftRedeemSuccessActivity extends BaseActivity {

    @Extra
    String orderNumberExtra;

    @ViewById(R.id.ivBackground)
    ImageView ivBackground;

    @ViewById(R.id.ivCircle)
    ImageView ivCircle;

    @ViewById(R.id.tvOrderNumber)
    TextView tvOrderNumber;

    @AfterViews
    public void calledAfterViewInjection() {
        if (orderNumberExtra != null) {
            tvOrderNumber.setText(orderNumberExtra.replaceAll("...", "$0 "));
        } else {
            tvOrderNumber.setText("");
        }

        Picasso.get()
                .load(R.drawable.code_success_red)
                .placeholder(R.drawable.code_success_red)
                .fit()
                .into(ivBackground);

        Picasso.get()
                .load(R.drawable.success_circle)
                .placeholder(R.drawable.success_circle)
                .fit()
                .into(ivCircle);
    }

    @Click(R.id.cvDone)
    void doneButtonClicked() {
        showFZAlert("READY TO LEAVE?", "Are you ready to leave this page?", "LEAVE PAGE", "CANCEL", R.drawable.ic_bear_baby_white);
    }

    @Override
    public void onBackPressed() {

    }

    /*
    * FZAlertViewListener
    * */

    @Override
    public void FZAlertOkButtonClicked() {
        super.FZAlertOkButtonClicked();
        Intent intent = new Intent(context, MainActivity_.class);
        intent.putExtra("go_gift_box", true);
        intent.putExtra("selected_tab", 1);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
