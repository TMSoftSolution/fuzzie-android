package sg.com.fuzzie.android.items.club;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Category;
import sg.com.fuzzie.android.api.models.ClubStore;
import sg.com.fuzzie.android.api.models.Offer;
import sg.com.fuzzie.android.ui.club.ClubStoreListActivity_;
import sg.com.fuzzie.android.utils.ClubStoreType;
import sg.com.fuzzie.android.utils.FuzzieData_;
import sg.com.fuzzie.android.utils.ViewUtils;

public class ClubStoresItem extends AbstractItem<ClubStoresItem, ClubStoresItem.ViewHolder>{

    private ClubStoreType type;
    private String title;
    private List<Offer> offers;
    private List<ClubStore> stores;
    private List<Category> categories;
    private FuzzieData_ dataManager;

    private int cellCount;
    private final int DEFAULT_CELL_COUNT = 10;

    public ClubStoresItem(ClubStoreType type, @Nullable List<Offer> offers, @Nullable List<ClubStore> stores){

        this.type = type;
        this.offers = offers;
        this.stores = stores;
    }

    public ClubStoresItem(ClubStoreType type, List<Category> categories){

        this.type = type;
        this.categories = categories;
    }


    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_club_stores_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_club_stores;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        final Context context = holder.itemView.getContext();
        dataManager = FuzzieData_.getInstance_(context);

        switch (type){
                case NEAR:
                    title = "NEARBY STORES";
                    break;
                case TRENDING:
                    title = "TRENDING";
                    break;
                case NEW:
                    title = "NEW ON THE CLUB";
                    break;
                case RELATED:
                    title = "RELATED STORES";
                    break;
                case FLASH:
                    title = "FLASH SALES";
                    break;
                case POPULAR_CATEGORY:
                    title = "POPULAR CATEGORIES";
                    break;
                default:
                    break;
        }

        holder.tvTitle.setText(title);

        if (type == ClubStoreType.POPULAR_CATEGORY){

            ViewUtils.gone(holder.tvViewAll);
            holder.rvStores.setLayoutManager(new GridLayoutManager(context, 2));

        } else {

            ViewUtils.visible(holder.tvViewAll);
            holder.rvStores.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        }



        FastItemAdapter adapter = new FastItemAdapter();

        holder.tvViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dataManager.setTempStores(stores);
                dataManager.setOffers(offers);

                ClubStoreListActivity_.intent(context).extraTitle(title).flashMode(type == ClubStoreType.FLASH).start();
            }
        });

        holder.rvStores.setAdapter(adapter);

        if (type == ClubStoreType.FLASH){

            cellCount = Math.min(DEFAULT_CELL_COUNT, offers.size());

            for (int i = 0 ; i < cellCount ; i ++){
                adapter.add(new ClubFlashItem(offers.get(i)));
            }

        } else if (type == ClubStoreType.POPULAR_CATEGORY){

            cellCount = Math.min(DEFAULT_CELL_COUNT, categories.size());

            for (int i = 0 ; i < cellCount ; i ++){
                adapter.add(new ClubPopularCategoryItem(categories.get(i)));
            }
        } else {

            cellCount = Math.min(DEFAULT_CELL_COUNT, stores.size());

            for (int i = 0 ; i < cellCount ; i ++){
                adapter.add(new ClubStoreItem(stores.get(i), context));
            }
        }

    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle;
        TextView tvViewAll;
        RecyclerView rvStores;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvViewAll = itemView.findViewById(R.id.tvViewAll);
            rvStores = itemView.findViewById(R.id.rvStores);
        }
    }
}
