package sg.com.fuzzie.android.items.shop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.ShoppingBagItem;
import sg.com.fuzzie.android.utils.Constants;
import sg.com.fuzzie.android.utils.FuzzieData_;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.ViewUtils;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_RELAYOUT_SHOPPING_BAG;

/**
 * Created by nurimanizam on 29/12/16.
 */

public class BagItem extends AbstractItem<BagItem, BagItem.ViewHolder> {

    private FuzzieData_ dataManager;
    private ShoppingBagItem item;
    private boolean isSelected = false;

    public BagItem(ShoppingBagItem item) {
        this.item = item;
    }

    public ShoppingBagItem getItem() {
        return item;
    }

    public void setItem(ShoppingBagItem item) {
        this.item = item;
    }

    @Override
    public int getType() {
        return R.id.item_bag_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_bag;
    }

    @Override
    public void bindView(final BagItem.ViewHolder viewHolder, List<Object> payloads) {

        super.bindView(viewHolder, payloads);
        viewHolder.setShoppingBagItem(item);

        dataManager = FuzzieData_.getInstance_(viewHolder.itemView.getContext());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataManager.isEdittingBags()){
                    isSelected = !isSelected;
                    if(isSelected) {
                        dataManager.addSelectedItem(item);
                        viewHolder.ivSelected.setImageResource(R.drawable.icn_selected);
                    }else{
                        dataManager.removeSelectedItem(item);
                        viewHolder.ivSelected.setImageResource(R.drawable.icn_unselected);
                    }
                    Intent intent = new Intent(Constants.BROADCAST_CHANGED_SELETED_SHOPPING_BAG);
                    LocalBroadcastManager.getInstance(viewHolder.itemView.getContext()).sendBroadcast(intent);
                }
            }
        });

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);

    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected ImageView ivBackground;
        protected TextView tvCashback;
        protected TextView tvBrandName;
        protected TextView tvGiftPrice;
        protected ImageView ivSelected;
        protected RelativeLayout rlCheckbox;
        ShoppingBagItem item;
        FuzzieData_ dataManager;

        public ViewHolder(View view) {
            super(view);
            this.ivBackground = (ImageView) view.findViewById(R.id.ivBackground);
            this.tvCashback = (TextView) view.findViewById(R.id.tvCashback);
            this.tvBrandName = (TextView) view.findViewById(R.id.tvBrandName);
            this.tvGiftPrice = (TextView) view.findViewById(R.id.tvGiftPrice);
            this.ivSelected = (ImageView) view.findViewById(R.id.ivSelected);
            this.rlCheckbox = (RelativeLayout)view.findViewById(R.id.rlCheckbox);

            dataManager = FuzzieData_.getInstance_(itemView.getContext());
            if(dataManager.isEdittingBags()) {
                this.rlCheckbox.setVisibility(View.VISIBLE);
            }else {
                this.rlCheckbox.setVisibility(View.GONE);
            }

            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(BROADCAST_RELAYOUT_SHOPPING_BAG)) {
                        refreshLayout();
                    }
                }
            };

            LocalBroadcastManager.getInstance(itemView.getContext()).registerReceiver(broadcastReceiver,
                    new IntentFilter(BROADCAST_RELAYOUT_SHOPPING_BAG));
        }

        private void refreshLayout() {

            if(dataManager.isEdittingBags()) {
                rlCheckbox.setVisibility(View.VISIBLE);
            }else {
                rlCheckbox.setVisibility(View.GONE);
            }
        }

        public void setShoppingBagItem(ShoppingBagItem bagItem) {

            this.item = bagItem;
            Brand brand = item.getBrand();

            final int cornerRadius = (int) ViewUtils.convertDpToPixel(5, itemView.getContext());
            Picasso.get()
                    .load(brand.getBackgroundImageUrl())
                    .placeholder(R.drawable.brands_placeholder)
                    .transform(new RoundedCornersTransformation(cornerRadius, 0, RoundedCornersTransformation.CornerType.ALL))
                    .into(this.ivBackground);

            String brandName = brand.getName();
            if(this.item.getItem().getGiftCard() != null) {
                brandName = brandName + "  E-Gift card";
            } else {
                brandName = brandName + "-" + this.item.getItem().getService().getName();
            }
            this.tvBrandName.setText(brandName);

            this.tvGiftPrice.setText(FuzzieUtils.getFormattedValue(item.getSellingPrice(), 0.75f));
            this.ivSelected.setImageResource(R.drawable.icn_unselected);

            if (brand.getCashBack().getPercentage() > 0) {

                this.tvCashback.setText(FuzzieUtils.getFormattedCashbackPercentage(brand.getCashBack().getPercentage(), 1));

            } else {

                this.tvCashback.setVisibility(View.GONE);

            }
        }
    }


}
