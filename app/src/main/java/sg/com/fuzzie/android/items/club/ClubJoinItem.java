package sg.com.fuzzie.android.items.club;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.ui.club.ClubSubscribeActivity_;

public class ClubJoinItem extends AbstractItem<ClubJoinItem, ClubJoinItem.ViewHolder>{


    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_club_join_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_club_join;
    }

    @Override
    public void bindView(final ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClubSubscribeActivity_.intent(holder.itemView.getContext()).start();
            }
        });
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);

        }
    }
}
