package sg.com.fuzzie.android.ui.jackpot;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Coupon;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.TimeUtils;

/**
 * Created by joma on 10/14/17.
 */

@EActivity(R.layout.activity_jackpot_choose)
public class JackpotChooseActivity extends BaseActivity {

    @Extra
    boolean fromPowerUpPack;

    @Extra
    String couponId;

    @Bean
    FuzzieData dataManager;

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.tvCount)
    TextView tvCount;

    @ViewById(R.id.tvDate)
    TextView tvDate;

    @ViewById(R.id.tvBody)
    TextView tvBody;

    @AfterViews
    public void calledAfterViewInjection() {

        Coupon coupon ;

        coupon = dataManager.getCouponById(couponId);

        if (coupon == null) return;

//        Bitmap background = ((FuzzieApplication_) getApplication()).lastActivityBitmap;
//        if (background != null) {
//            findViewById(R.id.content).setBackground(new BitmapDrawable(getResources(), background));
//        } else {
//            findViewById(R.id.content).setBackgroundColor(Color.parseColor("#D0000000"));
//        }

        tvCount.setText("X" + coupon.getTicketCount());
        if (coupon.getTicketCount() > 1){
            tvBody.setText(FuzzieUtils.fromHtml("You are entitled to <b>"  + String.format("%d", coupon.getTicketCount())  +" free Jackpot tickets</b> with this purchase."));
        } else {
            tvBody.setText(FuzzieUtils.fromHtml("You are entitled to <b>"  + String.format("%d", coupon.getTicketCount())  +" free Jackpot ticket</b> with this purchase."));
        }

        String expirationDate = currentUser.getCurrentUser().getJackpotTicketsExpirationDate();

        if (expirationDate != null && !expirationDate.equals("")){
            tvDate.setText("Valid till " + TimeUtils.dateTimeFormat(expirationDate, "yyyy-MM-dd", "d MMMM yyyy"));
        } else {
            tvDate.setVisibility(View.GONE);
        }

    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.cvCheckout)
    void checkOutButtonClicked(){
        JackpotPaymentActivity_.intent(context).couponId(couponId).fromPowerUpPack(fromPowerUpPack).start();
    }
}
