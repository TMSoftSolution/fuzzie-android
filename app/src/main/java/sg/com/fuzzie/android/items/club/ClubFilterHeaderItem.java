package sg.com.fuzzie.android.items.club;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;

public class ClubFilterHeaderItem extends AbstractItem<ClubFilterHeaderItem, ClubFilterHeaderItem.ViewHolder>{

    private String title;

    public ClubFilterHeaderItem(String title){
        this.title = title;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_club_filter_header_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_club_filter_header;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        holder.tvHeader.setText(title);
    }

    protected final class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvHeader;

        public ViewHolder(View itemView) {
            super(itemView);

            tvHeader = itemView.findViewById(R.id.tvHeader);
        }
    }
}
