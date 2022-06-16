package sg.com.fuzzie.android.items.me;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

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
 * Created by mac on 4/14/17.
 */

public class LikersItem extends AbstractItem <LikersItem, LikersItem.ViewHolder>{

    User liker;

    Context context;

    public LikersItem(User liker){
        this.liker = liker;
    }

    public User getLiker(){
        return liker;
    }

    @Override
    public int getType() {
        return R.id.item_likers_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_likers;
    }

    @Override
    public void bindView(LikersItem.ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        context = holder.itemView.getContext();

        TypedArray avatarBear = context.getResources().obtainTypedArray(R.array.avatar_bears);
        if (liker.getAvatar() != null && !liker.getAvatar().equals("")){
            Picasso.get()
                    .load(liker.getAvatar())
                    .placeholder(R.drawable.avatar_bear_1)
                    .into(holder.avatar);
        } else {
            if (liker.getAvatarBear() != null && liker.getAvatarBear() != -1){
                Picasso.get()
                        .load(avatarBear.getResourceId(liker.getAvatarBear(), 0))
                        .into(holder.avatar);
            } else {
                Picasso.get()
                        .load(R.drawable.avatar_bear_1)
                        .into(holder.avatar);
            }
        }
        avatarBear.recycle();

        if (liker.getFriend()){
            holder.facebook.setVisibility(View.VISIBLE);
        } else {
            holder.facebook.setVisibility(View.INVISIBLE);
        }

        holder.tvName.setText(liker.getName());

        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
        if (liker.getBirthdate() != null && !liker.getBirthdate().equals("")){
            try {
                Date dateObj = curFormater.parse(liker.getBirthdate());
                String birth = "Born " + new SimpleDateFormat("dd MMM").format(dateObj);
                holder.tvBirthday.setText(birth);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        else {
            holder.tvBirthday.setText("");
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView avatar;
        ImageView facebook;
        AppCompatTextView tvName;
        AppCompatTextView tvBirthday;

        public ViewHolder(View view) {
            super(view);
            this.avatar = (CircleImageView) view.findViewById(R.id.avatar);
            this.facebook = (ImageView) view.findViewById(R.id.liker_facebook);
            this.tvName = (AppCompatTextView) view.findViewById(R.id.tvName);
            this.tvBirthday = (AppCompatTextView) view.findViewById(R.id.tvBirth);
        }
    }

}
