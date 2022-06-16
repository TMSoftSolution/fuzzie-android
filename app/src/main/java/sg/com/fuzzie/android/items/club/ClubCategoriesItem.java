package sg.com.fuzzie.android.items.club;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.BrandType;

public class ClubCategoriesItem extends AbstractItem<ClubCategoriesItem, ClubCategoriesItem.ViewHolder>{

    private List<BrandType> brandTypes;

    public  ClubCategoriesItem(List<BrandType> brandTypes){
        this.brandTypes = brandTypes;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_club_categories_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_club_categories;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        Context context = holder.itemView.getContext();
        holder.rvCategory.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        FastItemAdapter adapter = new FastItemAdapter<>();
        holder.rvCategory.setAdapter(adapter);

        for (int i = 0 ; i < brandTypes.size() ; i ++){
            adapter.add(new ClubCategoryItem(brandTypes.get(i)));
        }
        adapter.add(new ClubCategoryMoreItem());
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        RecyclerView rvCategory;

        public ViewHolder(View itemView) {
            super(itemView);

            rvCategory = itemView.findViewById(R.id.rvCategories);
        }
    }
}
