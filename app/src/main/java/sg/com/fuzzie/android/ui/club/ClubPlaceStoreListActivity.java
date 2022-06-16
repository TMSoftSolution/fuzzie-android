package sg.com.fuzzie.android.ui.club;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.ClubPlace;
import sg.com.fuzzie.android.api.models.ClubStore;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.club.ClubStoreListItem;
import sg.com.fuzzie.android.utils.FuzzieData;

@EActivity(R.layout.activity_club_place_store_list)
public class ClubPlaceStoreListActivity extends BaseActivity {

    private ClubPlace clubPlace;
    private FastItemAdapter<ClubStoreListItem> adapter;

    @Extra
    String clubPlaceExtra;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.rvStores)
    RecyclerView rvStores;

    @AfterViews
    void callAfterViewInjection(){

        if (clubPlaceExtra == null) return;

        clubPlace = ClubPlace.fromJSON(clubPlaceExtra);
        if (clubPlace == null) return;

        tvTitle.setText(clubPlace.getName());

        adapter = new FastItemAdapter<ClubStoreListItem>();
        rvStores.setLayoutManager(new LinearLayoutManager(context));
        rvStores.setAdapter(adapter);

        showStores();

    }

    private void showStores(){

        for (String storeId : clubPlace.getStoreIds()){

            ClubStore clubStore = dataManager.getClubStore(storeId, dataManager.getTempStores());

            if (clubStore != null){

                adapter.add(new ClubStoreListItem(clubStore));
            }
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }
}
