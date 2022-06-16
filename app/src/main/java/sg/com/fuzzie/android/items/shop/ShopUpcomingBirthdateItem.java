package sg.com.fuzzie.android.items.shop;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.User;

/**
 * Created by mac on 6/26/17.
 */

public class ShopUpcomingBirthdateItem extends AbstractItem<ShopUpcomingBirthdateItem, ShopUpcomingBirthdateItem.ViewHolder> {

    private User friend;
    private Context context;

    public ShopUpcomingBirthdateItem(User friend){
        this.friend = friend;
    }

    public User getFriend(){
        return this.friend;
    }

    @Override
    public int getType() {
        return R.id.item_upcoming_birthdate_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_upcoming_birthdate;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        context = holder.itemView.getContext();
        if (friend.getAvatar() != null && !friend.getAvatar().equals("")){
            Picasso.get()
                    .load(friend.getAvatar())
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .fit()
                    .into(holder.avatar);
        } else {

            if (friend.getAvatarBear() != null){
                holder.avatar.setImageResource(friend.getAvatarBear());
            }
        }

        if (friend.getName() != null){
            holder.tvName.setText(friend.getFirstName());
        }

        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
        if (friend.getBirthdate() != null && !friend.getBirthdate().equals("")){
            try {
                Date dateObj = curFormater.parse(friend.getBirthdate());
                String birth = new SimpleDateFormat("MMM dd").format(dateObj);
                holder.tvBirth.setText(birth);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView avatar;
        TextView tvName;
        TextView tvBirth;

        public ViewHolder(View itemView) {
            super(itemView);

            this.avatar = (CircleImageView) itemView.findViewById(R.id.avatar);
            this.tvName = (TextView) itemView.findViewById(R.id.tvName);
            this.tvBirth = (TextView) itemView.findViewById(R.id.tvBirth);
        }
    }
}
