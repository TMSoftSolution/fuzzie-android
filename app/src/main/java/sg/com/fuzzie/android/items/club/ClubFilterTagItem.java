package sg.com.fuzzie.android.items.club;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;

public class ClubFilterTagItem extends AbstractItem<ClubFilterTagItem, ClubFilterTagItem.ViewHolder>{

    private String tagName;
    private ClubFilterTagItemListener listener;

    public ClubFilterTagItem(String tagName, ClubFilterTagItemListener listener){
        this.tagName = tagName;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_club_filter_tag_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_club_filter_tag;
    }

    @Override
    public void bindView(final ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        holder.tvTag.setText(tagName);
        holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.tagRemove(holder.getAdapterPosition(), tagName);
                }
            }
        });
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTag;
        ImageView ivRemove;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTag = itemView.findViewById(R.id.tvTag);
            ivRemove= itemView.findViewById(R.id.ivRemove);
        }
    }

    public interface ClubFilterTagItemListener{

        void tagRemove(int position, String tagName);
    }
}
