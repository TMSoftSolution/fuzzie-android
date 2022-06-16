package sg.com.fuzzie.android.items.brand;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.RedeemDetail;

/**
 * Created by faruq on 03/02/17.
 */

public class RedeemDetailListItem extends AbstractItem<RedeemDetailListItem, RedeemDetailListItem.ViewHolder> {

    RedeemDetail redeemDetail;

    public RedeemDetailListItem(RedeemDetail redeemDetail) {
        this.redeemDetail = redeemDetail;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.item_redeem_details_list_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_redeem_details_list;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(RedeemDetailListItem.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        viewHolder.tvTitle.setText(redeemDetail.getTitle());
        viewHolder.tvBody.setText(redeemDetail.getBody());

        if (redeemDetail.getImage() != null && !redeemDetail.getImage().equals("")) {
            Picasso.get()
                    .load(redeemDetail.getImage())
                    .placeholder(R.drawable.brands_placeholder)
                    .into(viewHolder.ivImage);
            viewHolder.ivImage.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivImage.setVisibility(View.GONE);
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;

        protected TextView tvBody;

        protected ImageView ivImage;

        public ViewHolder(View view) {
            super(view);
            this.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            this.tvBody = (TextView) view.findViewById(R.id.tvBody);
            this.ivImage = (ImageView) view.findViewById(R.id.ivImage);
        }
    }
}
