package sg.com.fuzzie.android.items.club;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Component;

public class ClubFilterTypeItem extends AbstractItem<ClubFilterTypeItem, ClubFilterTypeItem.ViewHolder>{

    private Component component;
    private boolean checked = false;

    public ClubFilterTypeItem(Component component){
        this.component = component;
    }

    public ClubFilterTypeItem(Component component, boolean checked){
        this.component = component;
        this.checked = checked;
    }


    public void setChecked(boolean checked){
        this.checked = checked;
    }

    public boolean isChecked(){
        return this.checked;
    }

    public Component getComponent() {
        return component;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_club_filter_type_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_club_filter_type;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        if (checked){

            holder.ivCheck.setImageResource(R.drawable.icn_selected);

        } else {

            holder.ivCheck.setImageResource(R.drawable.bg_filter_type_unselected);

        }

        holder.tvType.setText(component.getName());
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvType;
        ImageView ivCheck;

        public ViewHolder(View itemView) {
            super(itemView);

            tvType = itemView.findViewById(R.id.tvType);
            ivCheck = itemView.findViewById(R.id.ivCheck);
        }
    }
}
