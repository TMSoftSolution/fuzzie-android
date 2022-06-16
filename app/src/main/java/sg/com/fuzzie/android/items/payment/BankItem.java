package sg.com.fuzzie.android.items.payment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Bank;
import sg.com.fuzzie.android.ui.shop.CreditCardsActivity_;

/**
 * Created by nurimanizam on 6/10/17.
 */

public class BankItem extends AbstractItem<BankItem, BankItem.ViewHolder> {

    private Bank bank;

    public BankItem(Bank bank) {
        this.bank = bank;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.item_bank_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_bank;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(final BankItem.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        final Context context = viewHolder.itemView.getContext();

        Picasso.get()
                .load(bank.getBanner())
                .into(viewHolder.ivBank);

        viewHolder.itemView.setClickable(true);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreditCardsActivity_.intent(context).bankExtra(Bank.toJSON(bank)).start();
            }
        });

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected ImageView ivBank;

        public ViewHolder(View view) {
            super(view);
            this.ivBank = (ImageView) view.findViewById(R.id.ivBank);
        }
    }

}
