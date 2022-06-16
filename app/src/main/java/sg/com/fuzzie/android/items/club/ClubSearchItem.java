package sg.com.fuzzie.android.items.club;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.BrandType;
import sg.com.fuzzie.android.api.models.ClubPlace;
import sg.com.fuzzie.android.api.models.ClubStore;
import sg.com.fuzzie.android.ui.club.ClubPlaceStoreListActivity_;
import sg.com.fuzzie.android.ui.club.ClubSearchActivity;
import sg.com.fuzzie.android.ui.club.ClubStoreActivity_;
import sg.com.fuzzie.android.utils.ClubSearchType;
import sg.com.fuzzie.android.utils.FuzzieData_;

public class ClubSearchItem extends StatelessSection implements ClubSearchActivity.FilterableSection {

    private List<ClubStore> clubStores;
    private List<ClubStore> filteredClubStores;
    private List<ClubPlace> clubPlaces;
    private List<ClubPlace> filteredClubPlaces;
    private ClubSearchType type;

    public ClubSearchItem(List<ClubPlace> clubPlaces, List<ClubStore> clubStores, ClubSearchType type){

        super(SectionParameters.builder()
                .itemResourceId(type == ClubSearchType.PLACE ? R.layout.item_club_store_location : R.layout.item_club_store_list)
                .headerResourceId(R.layout.item_red_packet_history_section)
                .build());

        this.type = type;

        if (clubPlaces == null) {

            this.clubPlaces = new ArrayList<>();
            this.filteredClubPlaces = new ArrayList<>();

        } else {

            this.clubPlaces = clubPlaces;
            this.filteredClubPlaces = new ArrayList<>(clubPlaces);
        }

        if (clubStores == null){

            this.clubStores = new ArrayList<>();
            this.filteredClubStores = new ArrayList<>();

        } else {

            this.clubStores = clubStores;
            this.filteredClubStores = new ArrayList<>(clubStores);

        }

    }


    @Override
    public int getContentItemsTotal() {

        switch (type){

                case PLACE:
                    return this.filteredClubPlaces.size();
                case STORE:
                    return this.filteredClubStores.size();
                default:
                    return 0;
        }
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {

        switch (type){

                case PLACE:

                    return new PlaceViewHolder(view);

                case STORE:

                    return new StoreViewHolder(view);

                default:

                    return null;
        }

    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {

        final Context context = holder.itemView.getContext();
        final FuzzieData_ dataManager = FuzzieData_.getInstance_(context);

        switch (type){

            case PLACE:{

                PlaceViewHolder viewHolder = (PlaceViewHolder) holder;

                final ClubPlace clubPlace = filteredClubPlaces.get(position);

                viewHolder.tvLocation.setText(clubPlace.getName());
                viewHolder.tvDistance.setText(clubPlace.getDistance() != null && clubPlace.getDistance() != 0 ? String.format("%.2f km", clubPlace.getDistance()) : "");

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dataManager.setTempStores(clubStores);
                        ClubPlaceStoreListActivity_.intent(context).clubPlaceExtra(ClubPlace.toJSON(clubPlace)).start();
                    }
                });

                break;
            }

            case STORE:{


                StoreViewHolder viewHolder = (StoreViewHolder) holder;

                final ClubStore clubStore = filteredClubStores.get(position);

                Brand brand = dataManager.getBrandById(clubStore.getBrandId());

                if (brand != null && brand.getBackgroundImageUrl() != null && !brand.getBackgroundImageUrl().equals("")){

                    Picasso.get()
                            .load(brand.getBackgroundImageUrl())
                            .placeholder(R.drawable.brands_placeholder)
                            .into(viewHolder.ivStore);
                }

                viewHolder.tvBrandName.setText(clubStore.getBrandName());
                viewHolder.tvStoreName.setText(clubStore.getStoreName());
                viewHolder.tvDistance.setText(clubStore.getDistance() != null && clubStore.getDistance() != 0 ? String.format("%.2f km", clubStore.getDistance()) : "");

                BrandType brandType = dataManager.getBrandType(clubStore.getBrandTypeId());

                if (brandType != null && brandType.getBlackIcon() != null){

                    Picasso.get()
                            .load(brandType.getBlackIcon())
                            .into(viewHolder.ivCategory);
                }

                if (clubStore.getFilterComponents().size() == 0){

                    viewHolder.tvCategory.setText("");

                } else {

                    int count = Math.min(2, clubStore.getFilterComponents().size());
                    StringBuilder str = new StringBuilder("");
                    for (int i = 0 ; i < count ; i ++){

                        str.append(clubStore.getFilterComponents().get(i).getName()).append(" ");
                    }

                    viewHolder.tvCategory.setText(str.toString());
                }

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dataManager.clubStore = clubStore;
                        ClubStoreActivity_.intent(context).start();
                    }
                });

                break;
            }

            default:
                break;
        }
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {

        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        switch (type){

            case PLACE:
                headerHolder.tvTitle.setText("PLACES");
                break;
            case STORE:
                headerHolder.tvTitle.setText("STORES");
                break;
            default:
                headerHolder.tvTitle.setText("");
                break;
        }

    }

    @Override
    public void filter(String query) {

        if (TextUtils.isEmpty(query)) {

            filteredClubPlaces = new ArrayList<>(clubPlaces);
            filteredClubStores = new ArrayList<>(clubStores);

            this.setVisible(true);

        } else {

            if (type == ClubSearchType.PLACE){

                if (filteredClubPlaces == null){

                    filteredClubPlaces = new ArrayList<>();

                } else {

                    filteredClubPlaces.clear();

                }

                for (ClubPlace clubPlace : clubPlaces){

                    if (clubPlace.getName().toLowerCase().contains(query.toLowerCase())){

                        filteredClubPlaces.add(clubPlace);

                    }
                }

                this.setVisible(!filteredClubPlaces.isEmpty());

            } else if (type == ClubSearchType.STORE){


                if (filteredClubStores == null) {

                    filteredClubStores = new ArrayList<>();

                } else {

                    filteredClubStores.clear();

                }

                for (ClubStore clubStore : clubStores){

                    boolean matchType = true;
                    if (clubStore.getFilterComponents().size() != 0){

                        int count = Math.min(2, clubStore.getFilterComponents().size());
                        StringBuilder str = new StringBuilder("");
                        for (int i = 0 ; i < count ; i ++){

                            str.append(clubStore.getFilterComponents().get(i).getName()).append(" ");
                        }

                        matchType = str.toString().toLowerCase().contains(query.toLowerCase());
                    }

                    boolean matchBrandName = clubStore.getBrandName().toLowerCase().contains(query.toLowerCase());
                    boolean matchStoreName = clubStore.getStoreName().toLowerCase().contains(query.toLowerCase());

                    if (matchBrandName || matchStoreName || matchType){

                        filteredClubStores.add(clubStore);
                    }

                }

                this.setVisible(!filteredClubStores.isEmpty());
            }

        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;

        HeaderViewHolder(View view) {

            super(view);

            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        }
    }

    protected static class PlaceViewHolder extends RecyclerView.ViewHolder{

        TextView tvLocation;
        TextView tvDistance;

        public PlaceViewHolder(View itemView) {
            super(itemView);

            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDistance = itemView.findViewById(R.id.tvDistance);
        }
    }

    protected static class StoreViewHolder extends RecyclerView.ViewHolder{

        ImageView ivStore;
        TextView tvBrandName;
        TextView tvStoreName;
        TextView tvCategory;
        TextView tvDistance;
        ImageView ivCategory;

        public StoreViewHolder(View itemView) {

            super(itemView);

            ivStore = itemView.findViewById(R.id.ivStore);
            tvBrandName = itemView.findViewById(R.id.tvBrandName);
            tvStoreName = itemView.findViewById(R.id.tvStoreName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            ivCategory = itemView.findViewById(R.id.ivCategory);
        }
    }

}
