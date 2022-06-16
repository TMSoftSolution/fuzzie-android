package sg.com.fuzzie.android.items.topup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.ui.payments.TopUpPaymentActivity_;

import static sg.com.fuzzie.android.utils.Constants.REQUEST_TOP_UP_PAYMENT;

/**
 * Created by mac on 10/9/17.
 */

public class TopUpItem extends AbstractItem<TopUpItem, TopUpItem.ViewHolder>{

    private Context context;
    private int position;
    private int value;

    public TopUpItem(int position, int value){
        this.position = position;
        this.value = value;
    }

    @Override
    public int getType() {
        return R.id.item_top_up_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_top_up;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        context = holder.itemView.getContext();

        if (position == 0){
            holder.ivTopUp.setImageResource(R.drawable.icon_top_up_1);
        } else if (position == 1){
            holder.ivTopUp.setImageResource(R.drawable.icon_top_up_2);
        } else if (position == 2){
            holder.ivTopUp.setImageResource(R.drawable.icon_top_up_3);
        }

        if (position != 2){
            holder.divider2.setVisibility(View.GONE);
            holder.divider1.setVisibility(View.VISIBLE);
        } else {
            holder.divider2.setVisibility(View.VISIBLE);
            holder.divider1.setVisibility(View.GONE);
        }

        holder.tvPrice.setText("S$" + String.valueOf(value));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TopUpPaymentActivity_.intent(context).topUpPrice(value).startForResult(REQUEST_TOP_UP_PAYMENT);
            }
        });

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivTopUp;
        TextView tvPrice;
        TextView tvBuy;
        View divider1;
        View divider2;

        public ViewHolder(View itemView) {
            super(itemView);

            ivTopUp = (ImageView) itemView.findViewById(R.id.ivTopUp);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvBuy = (TextView) itemView.findViewById(R.id.tvBuy);
            divider1 = (View) itemView.findViewById(R.id.divider1);
            divider2 = (View) itemView.findViewById(R.id.divider2);
        }
    }

}
