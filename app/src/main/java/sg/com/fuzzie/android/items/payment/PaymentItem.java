package sg.com.fuzzie.android.items.payment;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.utils.FuzzieUtils;

/**
 * Created by nurimanizam on 29/12/16.
 */

public class PaymentItem extends AbstractItem<PaymentItem, PaymentItem.ViewHolder> {

    private String giftName;
    private double giftPrice;
    private double cashback;

    public PaymentItem(String giftName, double giftPrice, double cashback) {
        this.giftName = giftName;
        this.giftPrice = giftPrice;
        this.cashback = cashback;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.item_payment_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_payment;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(PaymentItem.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        viewHolder.tvGiftName.setText(giftName);
        viewHolder.tvGiftPrice.setText(FuzzieUtils.getFormattedValue(giftPrice, 0.75f));
        SpannableString cashbackString = new SpannableString(" cashback");
        viewHolder.tvCashback.setText(TextUtils.concat(FuzzieUtils.getFormattedValue(cashback, 0.75f), cashbackString));
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvGiftName;
        protected TextView tvGiftPrice;
        protected TextView tvCashback;

        public ViewHolder(View view) {
            super(view);
            this.tvGiftName = (TextView) view.findViewById(R.id.tvGiftName);
            this.tvGiftPrice = (TextView) view.findViewById(R.id.tvGiftPrice);
            this.tvCashback = (TextView) view.findViewById(R.id.tvCashback);
        }
    }


}
