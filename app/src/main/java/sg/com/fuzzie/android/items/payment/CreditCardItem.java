package sg.com.fuzzie.android.items.payment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.CreditCard;
import sg.com.fuzzie.android.ui.shop.CreditCardActivity_;

/**
 * Created by nurimanizam on 23/6/17.
 */

public class CreditCardItem extends AbstractItem<CreditCardItem, CreditCardItem.ViewHolder> {

    private CreditCard creditCard;

    public CreditCardItem(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.item_creditcard;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_creditcard;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(final CreditCardItem.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        final Context context = viewHolder.itemView.getContext();

        Picasso.get()
                    .load(creditCard.getBanner())
                    .into(viewHolder.ivCreditCard);
        viewHolder.tvCreditCard.setText(creditCard.getTitle());
        viewHolder.tvDetails.setText(creditCard.getPreviewCopy());

        viewHolder.itemView.setClickable(true);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreditCardActivity_.intent(context).creditCardExtra(CreditCard.toJSON(creditCard)).start();
            }
        });

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected ImageView ivCreditCard;
        protected TextView tvCreditCard;
        protected TextView tvDetails;

        public ViewHolder(View view) {
            super(view);
            this.ivCreditCard = (ImageView) view.findViewById(R.id.ivCreditCard);
            this.tvCreditCard = (TextView) view.findViewById(R.id.tvCreditCard);
            this.tvDetails = (TextView) view.findViewById(R.id.tvDetails);
        }
    }


}

