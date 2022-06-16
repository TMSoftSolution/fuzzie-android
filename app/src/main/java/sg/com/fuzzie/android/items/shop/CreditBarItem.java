package sg.com.fuzzie.android.items.shop;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.ui.shop.topup.TopUpActivity_;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieUtils;

import static sg.com.fuzzie.android.utils.Constants.REQUEST_TOP_UP_PAYMENT;

/**
 * Created by mac on 4/27/17.
 */

public class CreditBarItem extends AbstractItem<CreditBarItem, CreditBarItem.ViewHolder>{

    Context context;
    private CurrentUser currentUser;

    public CreditBarItem(CurrentUser currentUser){
        this.currentUser = currentUser;
    }

    @Override
    public int getType() {
        return R.id.item_credit_bar_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_credit_bar;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(final CreditBarItem.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        context = viewHolder.itemView.getContext();

        double balance = currentUser.getCurrentUser().getWallet().getBalance();
        viewHolder.tvCreditValue.setText(FuzzieUtils.getFormattedValue(context, balance, 0.75f));

        viewHolder.btnTopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TopUpActivity_.intent(context).startForResult(REQUEST_TOP_UP_PAYMENT);
            }
        });
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvCreditValue;
        Button btnTopUp;

        public ViewHolder(View view) {
            super(view);

            tvCreditValue = (TextView) view.findViewById(R.id.tvCredits);
            btnTopUp = (Button) view.findViewById(R.id.btnTopUp);

        }
    }

}
