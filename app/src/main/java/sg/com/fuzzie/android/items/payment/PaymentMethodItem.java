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
import sg.com.fuzzie.android.api.models.PaymentMethod;
import sg.com.fuzzie.android.utils.FuzzieData_;

/**
 * Created by nurimanizam on 22/12/16.
 */

public class PaymentMethodItem extends AbstractItem<PaymentMethodItem, PaymentMethodItem.ViewHolder> {

    private PaymentMethod paymentMethod;
    private boolean deleteMode;

    public PaymentMethodItem(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        this.deleteMode = false;
    }

    public PaymentMethodItem(PaymentMethod paymentMethod, boolean deleteMode) {
        this.paymentMethod = paymentMethod;
        this.deleteMode = deleteMode;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.item_payment_method_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_payment_method;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(PaymentMethodItem.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        Context context = viewHolder.itemView.getContext();
        FuzzieData_ dataManager = FuzzieData_.getInstance_(context);

        if (paymentMethod.getCardType().equals("ENETS")){

            viewHolder.ivPaymentCard.setImageResource(R.drawable.ic_nets);
            viewHolder.tvCardNumber.setText("NETSPay");

        } else {

            Picasso.get().load(paymentMethod.getImageUrl()).into(viewHolder.ivPaymentCard);
            viewHolder.tvCardNumber.setText("●●●●●●● " + paymentMethod.getLast4());

        }

        if (deleteMode) {

            viewHolder.ivCheck.setImageResource(R.drawable.payment_method_delete);

            if (paymentMethod.getCardType().equals("ENETS")){

                viewHolder.ivCheck.setVisibility(View.GONE);

            } else {

                viewHolder.ivCheck.setVisibility(View.VISIBLE);
            }

        } else {

            viewHolder.ivCheck.setVisibility(View.VISIBLE);

            if (dataManager.getSelectedPaymentMethod() != null && dataManager.getSelectedPaymentMethod().getToken().equals(paymentMethod.getToken())){

                viewHolder.ivCheck.setImageResource(R.drawable.payment_method_tick);

            } else {

                viewHolder.ivCheck.setImageResource(R.drawable.payment_method_untick);
            }
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected ImageView ivPaymentCard;
        protected ImageView ivCheck;
        protected TextView tvCardNumber;

        public ViewHolder(View view) {
            super(view);
            this.ivPaymentCard = (ImageView) view.findViewById(R.id.ivPaymentCard);
            this.ivCheck = (ImageView) view.findViewById(R.id.ivCheck);
            this.tvCardNumber = (TextView) view.findViewById(R.id.tvCardNumber);
        }
    }

}
