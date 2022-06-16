package sg.com.fuzzie.android.ui.me.history;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.GiftDetail;
import sg.com.fuzzie.android.api.models.Order;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.history.OrderItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.TimeUtils;

/**
 * Created by mac on 8/4/17.
 */

@EActivity(R.layout.activity_order_details)
public class OrderDetailsActivity extends BaseActivity {

    FastItemAdapter orderAdapter;
    private Order order;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvOrderDate)
    TextView tvOrderDate;

    @ViewById(R.id.tvOrderNumber)
    TextView tvOrderNumber;

    @ViewById(R.id.rvOrder)
    RecyclerView rvOrder;

    @ViewById(R.id.llCredits)
    View llCredits;

    @ViewById(R.id.tvCreditValue)
    TextView tvCreditValue;

    @ViewById(R.id.llCard)
    View llCard;

    @ViewById(R.id.ivPayment)
    ImageView ivPayment;

    @ViewById(R.id.tvCardNumber)
    TextView tvCardNumber;

    @ViewById(R.id.tvCardValue)
    TextView tvCardValue;


    @AfterViews
    public void calledAfterViewInjection() {

        order = dataManager.getSelectedOrder();
        if (order == null) return;

        setUI();
        setOrderItems();

    }

    private void setUI(){

        tvOrderDate.setText(TimeUtils.dateTimeForOrderHistory(order.getCreatedAt()));
        tvOrderNumber.setText("#" + order.getOrderNumber());

        if (order.getPaidWithCredits() > 0){

            llCredits.setVisibility(View.VISIBLE);
            tvCreditValue.setText(FuzzieUtils.getFormattedValue(order.getPaidWithCredits(), 0.75f));

        } else {

            llCredits.setVisibility(View.GONE);

        }

        if (order.getPaidWithCard() > 0){

            llCard.setVisibility(View.VISIBLE);

            if (order.getPaymentInstrumentType().equals("android_pay_card")){
                ivPayment.setImageResource(R.drawable.icon_android_pay);
                tvCardNumber.setText("Android Pay");
            } else if (order.getPaymentInstrumentType().equals("apple_pay_card")){
                ivPayment.setImageResource(R.drawable.icon_apple_pay);
                tvCardNumber.setText("Apple Pay");
            } else {
                ivPayment.setImageResource(R.drawable.ic_payment_uob);
                if (order.getCardNumber().length() > 4){
                    tvCardNumber.setText("●●●●●●● " + order.getCardNumber().substring(order.getCardNumber().length() - 4));
                } else {
                    tvCardNumber.setText(order.getCardNumber());
                }
            }

            tvCardValue.setText(FuzzieUtils.getFormattedValue(order.getTotalPrice(), 0.75f));

        } else {

            llCard.setVisibility(View.GONE);

        }

        orderAdapter = new FastItemAdapter();
        rvOrder.setLayoutManager(new LinearLayoutManager(context));
        rvOrder.setAdapter(orderAdapter);

    }

    private void setOrderItems(){

        if (orderAdapter == null){
            orderAdapter = new FastItemAdapter();
        } else {
            orderAdapter.clear();
        }

        if (order.getGiftDetails() != null && order.getGiftDetails().size() > 0){

            for (int i = 0 ; i < order.getGiftDetails().size() ; i ++){
                GiftDetail giftDetail = order.getGiftDetails().get(i);
                if (i == order.getGiftDetails().size() - 1){
                    orderAdapter.add(new OrderItem(giftDetail, false));
                } else {
                    orderAdapter.add(new OrderItem(giftDetail, true));
                }
            }

        } else {

            orderAdapter.add(new OrderItem(order));

        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }
}
