package sg.com.fuzzie.android.items.brand;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.GiftCard;
import sg.com.fuzzie.android.ui.shop.BrandGiftActivity_;
import sg.com.fuzzie.android.utils.ViewUtils;

/**
 * Created by nurimanizam on 25/12/16.
 */

public class ServiceListGiftCardItem extends AbstractItem<ServiceListGiftCardItem, ServiceListGiftCardItem.ViewHolder> {

    Brand brand;

    public ServiceListGiftCardItem(Brand brand) {
        this.brand = brand;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.item_service_list_giftcard_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_service_list_giftcard;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ServiceListGiftCardItem.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        final Context context = viewHolder.itemView.getContext();

        int cornerRadius = (int) ViewUtils.convertDpToPixel(4, context);
        Picasso.get()
                .load(brand.getBackgroundImageUrl())
                .placeholder(R.drawable.brands_placeholder)
                .transform(new RoundedCornersTransformation(cornerRadius, 0))
                .into(viewHolder.ivBrandImage);

        GiftCard firstCard = brand.getGiftCards().get(0);
        GiftCard lastCard = brand.getGiftCards().get(brand.getGiftCards().size()-1);

        viewHolder.tvGiftCardName.setText(String.format("%s E-Gift Card", brand.getName()));
        viewHolder.tvGiftCardPrice.setText(firstCard.getPrice().getCurrencySymbol() + firstCard.getPrice().getValue() + " - " + lastCard.getPrice().getCurrencySymbol() + lastCard.getPrice().getValue());

        viewHolder.backgroundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BrandGiftActivity_.intent(context).brandId(brand.getId()).start();
            }
        });

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected View backgroundView;
        protected ImageView ivBrandImage;
        protected TextView tvGiftCardName;
        protected TextView tvGiftCardPrice;

        public ViewHolder(View view) {
            super(view);
            this.backgroundView = (View) view.findViewById(R.id.item_service_list_giftcard_id);
            this.ivBrandImage = (ImageView) view.findViewById(R.id.ivBrandImage);
            this.tvGiftCardName = (TextView) view.findViewById(R.id.tvGiftCardName);
            this.tvGiftCardPrice = (TextView) view.findViewById(R.id.tvGiftCardPrice);
        }
    }
}