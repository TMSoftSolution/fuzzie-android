package sg.com.fuzzie.android.items.shop;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.ui.jackpot.JackpotLearnMoreActvity_;
import sg.com.fuzzie.android.utils.FuzzieUtils;

/**
 * Created by joma on 11/7/17.
 */

public class ShopJackpotTeasingItem extends AbstractItem<ShopJackpotTeasingItem, ShopJackpotTeasingItem.ViewHolder> {

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_shop_jackpot_teasing_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_shop_jackpot_teasing;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        final Context context = holder.itemView.getContext();

        String htmlText = "<font color='#FA3E3F'>WIN BIG. </font><font color='#FFFFFF'>NEVER LOSE MONEY, EVER</font>";
        holder.tvTitle.setText(FuzzieUtils.fromHtml(htmlText));

        holder.cvLearnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JackpotLearnMoreActvity_.intent(context).start();
            }
        });
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        CardView cvLearnMore;

        public ViewHolder(View view) {
            super(view);

            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            cvLearnMore = (CardView) view.findViewById(R.id.cvLearnMore);
        }
    }
}
