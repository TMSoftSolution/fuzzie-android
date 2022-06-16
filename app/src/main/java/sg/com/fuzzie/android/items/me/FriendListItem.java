package sg.com.fuzzie.android.items.me;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.User;
import sg.com.fuzzie.android.ui.likes.LikerProfileActivity_;

/**
 * Created by mac on 6/9/17.
 */

public class FriendListItem extends StatelessSection {

    Context context;
    String title;
    List<User> friends;

    public FriendListItem(String title, List<User> friends){
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_friend)
                .headerResourceId(R.layout.item_friend_section)
                .build());
        this.title = title;
        this.friends = friends;
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

        final User friend = friends.get(position);
        TypedArray avatarBear = context.getResources().obtainTypedArray(R.array.avatar_bears);
        if (friend.getAvatar() != null && !friend.getAvatar().equals("")){
            Picasso.get()
                    .load(friend.getAvatar())
                    .placeholder(R.drawable.avatar_bear_1)
                    .into(itemHolder.avatar);
        } else {
            if (friend.getAvatarBear() != null && friend.getAvatarBear() != -1){
                Picasso.get()
                        .load(avatarBear.getResourceId(friend.getAvatarBear(), 0))
                        .into(itemHolder.avatar);
            } else {
                Picasso.get()
                        .load(R.drawable.avatar_bear_1)
                        .into(itemHolder.avatar);
            }
        }
        avatarBear.recycle();

        if (friend.getFriend()){
            itemHolder.facebook.setVisibility(View.VISIBLE);
        } else {
            itemHolder.facebook.setVisibility(View.INVISIBLE);
        }

        itemHolder.tvName.setText(friend.getName());

        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
        if (friend.getBirthdate() != null && !friend.getBirthdate().equals("")){
            try {
                Date dateObj = curFormater.parse(friend.getBirthdate());
                String birth = "Born " + new SimpleDateFormat("dd MMM").format(dateObj);
                itemHolder.tvBirthday.setText(birth);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        else {
            itemHolder.tvBirthday.setText("");
        }

        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LikerProfileActivity_.intent(context).userExtra(User.toJSON(friend)).start();
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
        AppCompatTextView tvBirthday;

        ItemViewHolder(View view) {
            super(view);
            this.avatar = (CircleImageView) view.findViewById(R.id.avatar);
            this.facebook = (ImageView) view.findViewById(R.id.liker_facebook);
            this.tvName = (AppCompatTextView) view.findViewById(R.id.tvName);
            this.tvBirthday = (AppCompatTextView) view.findViewById(R.id.tvBirth);
        }
    }
}
