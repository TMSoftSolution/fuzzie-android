package sg.com.fuzzie.android.items.club;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.ArrayList;
import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.ClubStore;
import sg.com.fuzzie.android.api.models.TopBrand;
import sg.com.fuzzie.android.ui.club.ClubStoreActivity_;
import sg.com.fuzzie.android.ui.club.ClubStoreListActivity_;
import sg.com.fuzzie.android.utils.FuzzieData_;

public class ClubTopBrandsItem extends AbstractItem<ClubTopBrandsItem, ClubTopBrandsItem.ViewHolder>{

    private List<Brand> brands;
    private Context context;
    private FuzzieData_ dataManager;

    public ClubTopBrandsItem(List<TopBrand> topBrands, Context context){

        this.context = context;
        brands = new ArrayList<>();

        dataManager = FuzzieData_.getInstance_(context);

        for (TopBrand topBrand : topBrands){
            if (dataManager.getBrandById(topBrand.getBrandId()) != null){
                Brand brand = new Brand(dataManager.getBrandById(topBrand.getBrandId()));
                brand.setCustomImageUrl(topBrand.getImage());
                brand.setBrandLink(topBrand.getBrandLink());
                brands.add(brand);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_club_top_brands_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_club_top_brands;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        GridLayoutManager layoutManager = new GridLayoutManager(context, 4);

        FastItemAdapter<ClubTopBrandItem> adapter = new FastItemAdapter<>();
        holder.rvBrands.setLayoutManager(layoutManager);
        holder.rvBrands.setAdapter(adapter);

        adapter.withOnClickListener(new OnClickListener<ClubTopBrandItem>() {
            @Override
            public boolean onClick(@Nullable View v, IAdapter<ClubTopBrandItem> adapter, ClubTopBrandItem item, int position) {

                Brand brand = item.getBrand();

                if (brand.getBrandLink() != null && brand.getBrandLink().getType() != null && brand.getBrandLink().getType().equals("club")){

                    if (brand.getBrandLink().getClubStoreIds() != null && brand.getBrandLink().getClubStoreIds().size() > 0){

                        List<ClubStore> clubStores = new ArrayList<>();
                        for (String clubStoreId : brand.getBrandLink().getClubStoreIds()){

                            ClubStore  clubStore = dataManager.getClubStore(clubStoreId, dataManager.getClubHome().getStores());
                            if (clubStore != null){

                                clubStores.add(clubStore);
                            }
                        }

                        if (clubStores.size() > 0){

                            if (clubStores.size() == 1){

                                dataManager.clubStore = clubStores.get(0);
                                ClubStoreActivity_.intent(context).start();

                            } else {

                                dataManager.setTempStores(clubStores);
                                ClubStoreListActivity_.intent(context).extraTitle(brand.getName()).showFilter(false).start();

                            }
                        }

                    }
                }

                return true;
            }
        });

        int size = Math.min(8, brands.size());

        for (int i = 0 ; i < size ; i ++){
            adapter.add(new ClubTopBrandItem(brands.get(i)));
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        RecyclerView rvBrands;

        public ViewHolder(View itemView) {
            super(itemView);

            rvBrands = itemView.findViewById(R.id.rvBrands);
        }
    }
}
