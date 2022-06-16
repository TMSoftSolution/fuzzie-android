package sg.com.fuzzie.android.items.brand;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Service;
import sg.com.fuzzie.android.ui.shop.BrandServiceActivity_;

/**
 * Created by nurimanizam on 24/12/16.
 */

public class ServiceListItem extends AbstractItem<ServiceListItem, ServiceListItem.ViewHolder> {

    Brand brand;
    Service service;

    public ServiceListItem(Brand brand, Service service) {
        this.brand = brand;
        this.service = service;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.item_service_list_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_service_list;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ServiceListItem.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        final Context context = viewHolder.itemView.getContext();
        viewHolder.tvStrikePrice.setPaintFlags(viewHolder.tvStrikePrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        viewHolder.tvPackageName.setText(service.getName());

        if (service.isSoldOut() != null && service.isSoldOut()) {
            viewHolder.tvSoldOut.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvSoldOut.setVisibility(View.GONE);
        }

        if(service.getDiscountedPrice() > 0) {

            viewHolder.tvPrice.setText(service.getPrice().getCurrencySymbol() + String.format("%.0f", service.getDiscountedPrice()) );

            if (service.getDiscountedPrice() == Float.valueOf(service.getPrice().getValue())) {
                viewHolder.tvStrikePrice.setText("");
            } else {
                viewHolder.tvStrikePrice.setText(service.getPrice().getCurrencySymbol() + service.getPrice().getValue() );
            }

        } else {
            viewHolder.tvPrice.setText(service.getPrice().getCurrencySymbol() + service.getPrice().getValue() );
            viewHolder.tvStrikePrice.setText("");
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrandServiceActivity_.intent(context).brandId(brand.getId()).serviceExtra(Service.toJSON(service)).start();
            }
        });


    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvPackageName;
        protected TextView tvSoldOut;
        protected TextView tvPrice;
        protected TextView tvStrikePrice;

        public ViewHolder(View view) {
            super(view);
            this.tvPackageName = (TextView) view.findViewById(R.id.tvPackageName);
            this.tvSoldOut = (TextView) view.findViewById(R.id.tvSoldOut);
            this.tvPrice = (TextView) view.findViewById(R.id.tvPrice);
            this.tvStrikePrice = (TextView) view.findViewById(R.id.tvStrikePrice);
        }
    }
}
