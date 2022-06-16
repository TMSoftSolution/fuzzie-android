package sg.com.fuzzie.android.items.club;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.ui.club.ClubSettingActivity_;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.CurrentUser_;
import sg.com.fuzzie.android.utils.FuzzieUtils;

public class ClubEarnItem extends AbstractItem<ClubEarnItem, ClubEarnItem.ViewHolder>{

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_club_earn_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_club_earn;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        final Context context = holder.itemView.getContext();
        CurrentUser currentUser = CurrentUser_.getInstance_(context);
        holder.tvEarn.setText(FuzzieUtils.getFormattedValue(context, currentUser.getCurrentUser().getFuzzieClub().getSavings(), 0.667f));

        holder.ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClubSettingActivity_.intent(context).start();
            }
        });
    }


    protected static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivSetting;
        TextView tvEarn;


        public ViewHolder(View itemView) {
            super(itemView);

            ivSetting = itemView.findViewById(R.id.ivSetting);
            tvEarn = itemView.findViewById(R.id.tvEarn);

        }
    }
}
