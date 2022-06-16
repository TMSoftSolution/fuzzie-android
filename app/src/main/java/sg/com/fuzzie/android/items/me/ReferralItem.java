package sg.com.fuzzie.android.items.me;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.User;
import sg.com.fuzzie.android.ui.likes.LikerProfileActivity_;
import sg.com.fuzzie.android.utils.ViewUtils;

/**
 * Created by mac on 6/4/17.
 */

public class ReferralItem extends StatelessSection {

    private String title;
    private List<User> referrals;
    private Context context;
    private boolean isClubReferral;

    public ReferralItem(String title, List<User> referrals, boolean isClubReferral){
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_referral)
                .headerResourceId(R.layout.item_friend_section)
                .build());

        this.title = title;
        this.referrals = referrals;
        this.isClubReferral = isClubReferral;
    }

    @Override
    public int getContentItemsTotal() {
        return referrals.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {

        final ItemViewHolder itemHolder = (ItemViewHolder) holder;
        context = holder.itemView.getContext();

        final User user = referrals.get(position);

        TypedArray avatarBear = context.getResources().obtainTypedArray(R.array.avatar_bears);
        if (user.getAvatar() != null && !user.getAvatar().equals("")){
            Picasso.get()
                    .load(user.getAvatar())
                    .placeholder(R.drawable.avatar_bear_1)
                    .into(itemHolder.avatar);
        } else {
            Picasso.get()
                    .load(avatarBear.getResourceId(user.getAvatarBear(), 0))
                    .placeholder(R.drawable.avatar_bear_1)
                    .into(itemHolder.avatar);
        }
        avatarBear.recycle();

        if (user.getFriend()){
            ViewUtils.visible(itemHolder.facebook);
        } else {
            ViewUtils.invisible(itemHolder.facebook);
        }

        String formattedName = "";
        if (user.getFirstName() != null && user.getFirstName().length() > 0){
            if (user.getFirstName().length() == 1){
                formattedName += user.getFirstName().toUpperCase();
            } else {
                formattedName += user.getFirstName().substring(0,1).toUpperCase() + user.getFirstName().substring(1);
            }
        }

        if (formattedName.length() != 0){
            formattedName += " ";
        }
        if (user.getLastName() != null && user.getLastName().length() > 0){
            if (user.getLastName().length() == 1){
                formattedName += user.getLastName().toUpperCase();
            } else {
                formattedName += user.getLastName().substring(0,1).toUpperCase() + user.getLastName().substring(1);
            }
        }
        itemHolder.tvName.setText(formattedName);

        if (user.getStatus().equals("referer_awarded_bonus")){

            if (isClubReferral){

                itemHolder.tvStatus.setText("S$10 Earned");

            } else {

                itemHolder.tvStatus.setText("S$5 Earned");
            }

            itemHolder.tvStatus.setTextColor(Color.parseColor("#FA3E3F"));
        } else {
            itemHolder.tvStatus.setText("Pending...");
            itemHolder.tvStatus.setTextColor(Color.parseColor("#B9B9B9"));
        }

        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LikerProfileActivity_.intent(context).userExtra(User.toJSON(user)).start();

            }
        });
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP){
            headerHolder.tvTitle.setLetterSpacing(0.1f);
        }
        headerHolder.tvTitle.setText(title);
    }

    private class  HeaderViewHolder extends RecyclerView.ViewHolder{

        private TextView tvTitle;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        }
    }

    protected static class ItemViewHolder extends RecyclerView.ViewHolder {

        CircleImageView avatar;
        ImageView facebook;
        TextView tvName;
        TextView tvStatus;

        public ItemViewHolder(View view) {
            super(view);
            this.avatar = (CircleImageView) view.findViewById(R.id.avatar);
            this.facebook = (ImageView) view.findViewById(R.id.liker_facebook);
            this.tvName = (TextView) view.findViewById(R.id.tvName);
            this.tvStatus = (TextView) view.findViewById(R.id.tvStatus);
        }
    }

}