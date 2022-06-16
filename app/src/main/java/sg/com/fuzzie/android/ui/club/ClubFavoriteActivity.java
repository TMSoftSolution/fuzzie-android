package sg.com.fuzzie.android.ui.club;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.ClubStore;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.club.ClubStoreListItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_CLUB_STORE_BOOKMARK_STATE_CHANGED;

@EActivity(R.layout.activity_club_favorite)
public class ClubFavoriteActivity extends BaseActivity {

    private final int REQUEST_FILTER = 1000;

    private FastItemAdapter<ClubStoreListItem> adapter;
    private List<ClubStore> clubStores;
    private List<ClubStore> filteredStores;
    private List<Integer> categoryIds;
    private boolean isSortByDistance = true;

    BroadcastReceiver broadcastReceiver;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.rvStores)
    RecyclerView rvStores;

    @AfterViews
    void callAfterViewInjection(){

        categoryIds = new ArrayList<>();

        adapter = new FastItemAdapter<>();
        rvStores.setLayoutManager(new LinearLayoutManager(context));
        rvStores.setAdapter(adapter);

        fetchClubStores();
        filterStores();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(BROADCAST_CLUB_STORE_BOOKMARK_STATE_CHANGED)){

                    fetchClubStores();
                    filterStores();
                }
            }
        };

        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_CLUB_STORE_BOOKMARK_STATE_CHANGED));

    }

    private void fetchClubStores(){

        clubStores = new ArrayList<>();

        for (String storeid : currentUser.getCurrentUser().getBookmakredStoreIds()){

            ClubStore clubStore = dataManager.getClubStore(storeid, dataManager.getClubHome().getStores());

            if (clubStore != null){

                clubStores.add(clubStore);

            }
        }
    }

    private void filterStores(){

        filteredStores = new ArrayList<>();

        if (categoryIds == null || categoryIds.size() == 0 || categoryIds.size() == dataManager.getClubHome().getBrandTypes().size()){


            filteredStores = clubStores;

        } else {

            for (ClubStore clubStore : clubStores){

                if (categoryIds.contains(clubStore.getBrandTypeId())){

                    filteredStores.add(clubStore);
                }
            }
        }

        sortStores();
    }

    private void sortStores(){

        List<ClubStore> sortedStores = new ArrayList<>(filteredStores);

        if (isSortByDistance){

            Collections.sort(sortedStores, new Comparator<ClubStore>() {
                @Override
                public int compare(ClubStore o1, ClubStore o2) {

                    return o1.getDistance().compareTo(o2.getDistance());
                }
            });
        }

        showStores(sortedStores);
    }

    private void showStores(List<ClubStore> clubStores){

        if (adapter == null){

            adapter = new FastItemAdapter<>();

        } else {

            adapter.clear();

        }

        for (ClubStore clubStore : clubStores){

            adapter.add(new ClubStoreListItem(clubStore));

        }

    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.ivFilter)
    void filterButtonClicked(){

        Gson gson = new Gson();
        Type type = new TypeToken<List<Integer>>(){}.getType();
        ClubFavoriteFilterActivity_.intent(context).categoryIdsExtra(gson.toJson(categoryIds, type)).isSortByDistance(isSortByDistance).startForResult(REQUEST_FILTER);
        overridePendingTransition(R.anim.slide_in_up, R.anim.stay);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            if (requestCode == REQUEST_FILTER){

                if (data != null){

                    isSortByDistance = data.getBooleanExtra("distanceSort", true);

                    if (data.getStringExtra("ids") != null){

                        Gson gson = new Gson();
                        Type type = new TypeToken<List<Integer>>(){}.getType();
                        categoryIds = gson.fromJson(data.getStringExtra("ids"),type);

                        filterStores();
                    }

                }

            }
        }
    }
}
