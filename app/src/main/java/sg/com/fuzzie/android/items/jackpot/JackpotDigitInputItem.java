package sg.com.fuzzie.android.items.jackpot;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import me.philio.pinentry.PinEntryView;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Coupon;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieData_;

/**
 * Created by joma on 10/15/17.
 */

public class JackpotDigitInputItem extends AbstractItem<JackpotDigitInputItem, JackpotDigitInputItem.ViewHolder> {

    private DigitInputListener listener;
    private int count;
    private int index;

    public JackpotDigitInputItem(int count, int index, DigitInputListener listener){
        this.count = count;
        this.index = index;
        this.listener = listener;
    }

    @Override
    public int getType() {
        return R.id.item_jackpot_digit_input_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_jackpot_digit_input;
    }

    @Override
    public void bindView(final ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        Context context = holder.itemView.getContext();

        holder.pinEntryView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pin = s.toString();
                if (pin.length() == 4) {
                    holder.itemView.setBackgroundResource(R.drawable.bg_jackpot_digit_input_active);
                } else {
                    holder.itemView.setBackgroundResource(R.drawable.bg_jackpot_digit_input_inactive);
                }

                if (listener != null)
                listener.fullDigitEntered(pin.length() == 4, pin, index);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        FuzzieData dataManager = FuzzieData_.getInstance_(context);
        holder.pinEntryView.setText(dataManager.digits[index]);

        holder.tvSequence.setText((index + 1) + "/" + count);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    public void setListener(DigitInputListener listener) {
        this.listener = listener;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        PinEntryView pinEntryView;
        TextView tvSequence;

        public ViewHolder(View itemView) {
            super(itemView);

            pinEntryView = (PinEntryView) itemView.findViewById(R.id.pinEntry);
            tvSequence = (TextView) itemView.findViewById(R.id.tvSequence);
         }
    }

    public interface DigitInputListener{
        void fullDigitEntered(boolean filled, String digit, int index);
    }

}
