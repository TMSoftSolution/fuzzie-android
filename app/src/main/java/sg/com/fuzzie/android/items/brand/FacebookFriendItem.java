package sg.com.fuzzie.android.items.brand;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
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
import sg.com.fuzzie.android.api.models.Friend;

/**
 * Created by mac on 6/19/17.
 */

public class FacebookFriendItem extends StatelessSection{

    Context context;
    private String title;
    private List<Friend> friends;

    public FacebookFriendItem(String title, List<Friend> friends){
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_facebook_friend)
                .headerResourceId(R.layout.item_friend_section)
                .build());
        this.title = title;
        this.friends= friends;
    }

    @Override
    public int getContentItemsTotal() {
        return friends.size();
    }


    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {

        final ItemViewHolder itemHolder = (ItemViewHolder) holder;
        context = itemHolder.itemView.getContext();

        final Friend friend = friends.get(position);
        if (friend.getAvatar() != null && !friend.getAvatar().equals("")){
            Picasso.get()
                    .load(friend.getAvatar())
                    .placeholder(R.drawable.avatar_bear_1)
                    .fit()
                    .into(itemHolder.avatar);
        }

        itemHolder.tvName.setText(friend.getName());

    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
        headerHolder.tvTitle.setText(title);
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;

        public HeaderViewHolder(View view) {
            super(view);

            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        CircleImageView avatar;
        ImageView facebook;
        AppCompatTextView tvName;

        ItemViewHolder(View view) {
            super(view);
            this.avatar = (CircleImageView) view.findViewById(R.id.avatar);
            this.facebook = (ImageView) view.findViewById(R.id.liker_facebook);
            this.tvName = (AppCompatTextView) view.findViewById(R.id.tvName);
        }
    }

}
