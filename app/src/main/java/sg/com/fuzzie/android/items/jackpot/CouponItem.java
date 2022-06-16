package sg.com.fuzzie.android.items.jackpot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Coupon;
import sg.com.fuzzie.android.ui.jackpot.JackpotCouponActivity_;
import sg.com.fuzzie.android.ui.jackpot.PowerUpCouponActivity_;
import sg.com.fuzzie.android.utils.FuzzieData_;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.ViewUtils;

/**
 * Created by joma on 10/13/17.
 */

public class CouponItem extends AbstractItem<CouponItem, CouponItem.ViewHolder> {

    public enum CouponItemMode{
        NORMAL,
        POWER_UP_PACK
    }

    private Context context;
    private Coupon coupon;
    private CouponItemMode mode;

    public CouponItem(Coupon coupon){
        this.coupon = coupon;
        if (coupon.getPowerUpPack() != null){
            this.mode = CouponItemMode.POWER_UP_PACK;
        } else {
            this.mode = CouponItemMode.NORMAL;
        }
    }

    public CouponItem(Coupon coupon, CouponItemMode mode){
        this.coupon = coupon;
        this.mode = mode;
    }

    @Override
    public int getType() {
        return R.id.item_coupon_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_coupon;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        context = holder.itemView.getContext();

        if (mode == CouponItemMode.POWER_UP_PACK){

            Picasso.get()
                    .load(R.drawable.power_up_pack_placeholder)
                    .placeholder(R.drawable.power_up_pack_placeholder)
                    .into(holder.ivBrand);

            holder.tvBrandName.setText("Fuzzie");
            ViewUtils.gone(holder.ivClubMember);

        } else {

            Picasso.get()
                    .load(coupon.getImage())
                    .placeholder(R.drawable.categories_placeholder)
                    .into(holder.ivBrand);

            holder.tvBrandName.setText(coupon.getBrandName());

            Brand brand = FuzzieData_.getInstance_(context).getBrandById(coupon.getBrandId());
            if (brand != null && brand.isClubOnly()){

                ViewUtils.visible(holder.ivClubMember);

            } else {

                ViewUtils.gone(holder.ivClubMember);
            }

        }

        holder.tvCouponName.setText(coupon.getName());

        if (coupon.getTicketCount() > 1){
            holder.tvTickets.setText("+" + coupon.getTicketCount() + " FREE JACKPOT TICKETS");
        } else {
            holder.tvTickets.setText("+" + coupon.getTicketCount() + " FREE JACKPOT TICKET");
        }

        double price = Double.parseDouble(coupon.getPrice().getValue());
        holder.tvPrice.setText(FuzzieUtils.getFormattedValue(price, 0.625f));

        if (coupon.isSoldOut()){

            holder.tvSoldOut.setVisibility(View.VISIBLE);
            holder.tvCashback.setVisibility(View.INVISIBLE);

        } else {

            holder.tvSoldOut.setVisibility(View.INVISIBLE);

            if (coupon.getCashBack().getPercentage() > 0){

                holder.tvCashback.setVisibility(View.VISIBLE);
                holder.tvCashback.setText(FuzzieUtils.getFormattedCashbackPercentage(coupon.getCashBack().getPercentage(), 1));

            } else {

                holder.tvCashback.setVisibility(View.INVISIBLE);

            }


        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mode == CouponItemMode.POWER_UP_PACK){

                    PowerUpCouponActivity_.intent(context).couponId(coupon.getId()).start();


                } else {

                    JackpotCouponActivity_.intent(context).couponId(coupon.getId()).start();
                }

            }
        });
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView ivBrand;
        TextView tvPrice;
        TextView tvBrandName;
        TextView tvCouponName;
        TextView tvTickets;
        TextView tvSoldOut;
        TextView tvCashback;
        ImageView ivClubMember;

        public ViewHolder(View view) {
            super(view);

            ivBrand = view.findViewById(R.id.ivBrand);
            tvPrice = view.findViewById(R.id.tvPrice);
            tvBrandName = view.findViewById(R.id.tvBrandName);
            tvCouponName = view.findViewById(R.id.tvCouponName);
            tvTickets = view.findViewById(R.id.tvTickets);
            tvSoldOut = view.findViewById(R.id.tvSoldOut);
            tvCashback = view.findViewById(R.id.tvCashback);
            ivClubMember = view.findViewById(R.id.ivClubMember);
        }
    }

}
