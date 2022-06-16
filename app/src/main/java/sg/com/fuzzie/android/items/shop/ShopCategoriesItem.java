package sg.com.fuzzie.android.items.shop;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Category;
import sg.com.fuzzie.android.api.models.Home;
import sg.com.fuzzie.android.ui.shop.BrandsListActivity_;

/**
 * Created by nurimanizam on 16/12/16.
 */

public class ShopCategoriesItem extends AbstractItem<ShopCategoriesItem, ShopCategoriesItem.ViewHolder> {

    private Home home;

    public ShopCategoriesItem(Home home) {
        this.home = home;
    }

    public Home getHome() {
        return home;
    }

    public void setHome(Home home) {
        this.home = home;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.item_categories_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_home_categories;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ShopCategoriesItem.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        final Context context = viewHolder.itemView.getContext();

        FastItemAdapter<ShopCategoryItem> categoryAdapter = new FastItemAdapter<ShopCategoryItem>();
        categoryAdapter.withOnClickListener(new OnClickListener<ShopCategoryItem>() {
            @Override
            public boolean onClick(View v, IAdapter<ShopCategoryItem> adapter, ShopCategoryItem item, int position) {

                Category category = item.getCategory();
                if (category != null){
                    BrandsListActivity_.intent(context).titleExtra(category.getName()).categoryId(category.getId()).showFilter(true).start();
                }

                return true;
            }
        });

        viewHolder.rvCategories.setAdapter(categoryAdapter);
        viewHolder.rvCategories.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        categoryAdapter.clear();
        for (Category category : home.getCategories()) {
            categoryAdapter.add(new ShopCategoryItem(category));
        }

//        viewHolder.tvViewAll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
////                Gson gson = new GsonBuilder().create();
////                JsonArray brandJsonArray = gson.toJsonTree(brands).getAsJsonArray();
////
////                if (type == ShopBrandsItem.ITEM_TYPE.RECOMMENDED_BRANDS) {
////                    BrandsListActivity_.intent(context).titleExtra("RECOMMENDED BRANDS").brandsExtra(brandJsonArray.toString()).start();
////                } else if (type == ShopBrandsItem.ITEM_TYPE.TRENDING_BRANDS) {
////                    BrandsListActivity_.intent(context).titleExtra("TRENDING BRANDS").brandsExtra(brandJsonArray.toString()).start();
////                }
//            }
//        });

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
//        protected TextView tvViewAll;
        protected RecyclerView rvCategories;

        public ViewHolder(View view) {
            super(view);
            this.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
//            this.tvViewAll = (TextView) view.findViewById(R.id.tvViewAll);
            this.rvCategories = (RecyclerView) view.findViewById(R.id.rvCategories);
        }
    }

}
