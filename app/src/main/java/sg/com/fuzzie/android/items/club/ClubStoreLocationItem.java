package sg.com.fuzzie.android.items.club;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.ClubPlace;
import sg.com.fuzzie.android.api.models.ClubStore;
import sg.com.fuzzie.android.api.models.Store;

public class ClubStoreLocationItem extends AbstractItem<ClubStoreLocationItem, ClubStoreLocationItem.ViewHolder>{

    private ClubStore clubStore;
    private Store store;
    private ClubPlace clubPlace;

    public ClubStoreLocationItem(ClubStore clubStore, Store store){

        this.clubStore = clubStore;
        this.store = store;

    }

    public ClubStoreLocationItem(ClubPlace clubPlace){

        this.clubPlace = clubPlace;
    }

    public ClubPlace getClubPlace() {
        return clubPlace;
    }

    public ClubStore getClubStore() {
        return clubStore;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_club_store_location_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_club_store_location;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        if (clubPlace != null){

            holder.tvLocation.setText(clubPlace.getName());
            holder.tvDistance.setText(clubPlace.getDistance() != null && clubPlace.getDistance() != 0 ? String.format("%.2f km", clubPlace.getDistance()) : "");


        } else {

            holder.tvLocation.setText(store.getName());
            holder.tvDistance.setText(clubStore.getDistance() != null && clubStore.getDistance() != 0 ? String.format("%.2f km", clubStore.getDistance()) : "");
        }

    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvLocation;
        TextView tvDistance;

        public ViewHolder(View itemView) {
            super(itemView);

            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDistance = itemView.findViewById(R.id.tvDistance);
        }
    }
}
