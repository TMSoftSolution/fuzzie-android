package sg.com.fuzzie.android.items.club;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;

public class ClubStoreFinePrintItem extends AbstractItem<ClubStoreFinePrintItem, ClubStoreFinePrintItem.ViewHolder>{

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_club_store_fine_print_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_club_store_fine_print;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvPrint;

        public ViewHolder(View itemView) {
            super(itemView);

            tvPrint = itemView.findViewById(R.id.tvPrint);
        }
    }
}
