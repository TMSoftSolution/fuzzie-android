package sg.com.fuzzie.android.ui.club;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.ClubStore;
import sg.com.fuzzie.android.api.models.Offer;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.club.ClubFlashListItem;
import sg.com.fuzzie.android.items.club.ClubStoreListItem;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.ViewUtils;

@EActivity(R.layout.activity_club_store_list)
public class ClubStoreListActivity extends BaseActivity {

    private final int REQUEST_FILTER = 1000;

    private List<Integer> categoryIds;
    FastItemAdapter adapter;
    private List<ClubStore> filteredStores;
    private List<Offer> filteredOffers;

    @Bean
    FuzzieData dataManager;

    @Extra
    String extraTitle = "";

    @Extra
    boolean showFilter = true;

    @Extra
    boolean flashMode = false;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.rvStores)
    RecyclerView rvStores;

    @ViewById(R.id.ivFilter)
    ImageView ivFilter;

    @AfterViews
    void callAfterViewInjection(){

        categoryIds = new ArrayList<>();

        if (flashMode){

            if (dataManager.getOffers() == null){

                return;
            }

            filteredOffers = dataManager.getOffers();

        } else {

            if (dataManager.getTempStores() == null){

                return;
            }

            filteredStores = dataManager.getTempStores();
        }

        tvTitle.setText(extraTitle);

        adapter = new FastItemAdapter();
        rvStores.setLayoutManager(new LinearLayoutManager(context));
        rvStores.setAdapter(adapter);

        if (flashMode){

            showOffers();

        } else {

            showStores();
        }

        if (showFilter){

            ViewUtils.visible(ivFilter);

        } else {

            ViewUtils.gone(ivFilter);

        }

    }

    private void filterStores(){

        filteredStores = new ArrayList<>();

        if (categoryIds == null || categoryIds.size() == 0){

            filteredStores = dataManager.getTempStores();

        } else {

            for (ClubStore clubStore : dataManager.getTempStores()){

                if (categoryIds.contains(clubStore.getBrandTypeId())){

                    filteredStores.add(clubStore);
                }
            }
        }
    }

    private void showStores(){

        if (adapter == null){

            adapter = new FastItemAdapter();

        } else {

            adapter.clear();
        }

        for (int i = 0 ; i < filteredStores.size() ; i ++){
            adapter.add(new ClubStoreListItem(filteredStores.get(i)));
        }
    }

    private void filterOffers(){

        filteredOffers = new ArrayList<>();

        if (categoryIds == null || categoryIds.size() == 0){

            filteredOffers = dataManager.getOffers();

        } else {

            for (Offer offer : dataManager.getOffers()){

                if (categoryIds.contains(offer.getBrandTypeId())){

                    filteredOffers.add(offer);
                }
            }
        }
    }

    private void showOffers(){

        if (adapter == null){

            adapter = new FastItemAdapter();

        } else {

            adapter.clear();
        }

        for (int i = 0 ; i < filteredOffers.size() ; i ++){
            adapter.add(new ClubFlashListItem(filteredOffers.get(i)));
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
        ClubFilterActivity_.intent(context).categoryIdsExtra(gson.toJson(categoryIds, type)).startForResult(REQUEST_FILTER);
        overridePendingTransition(R.anim.slide_in_up, R.anim.stay);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            if (requestCode == REQUEST_FILTER){

                if (data != null && data.getStringExtra("ids") != null){

                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Integer>>(){}.getType();
                    categoryIds = gson.fromJson(data.getStringExtra("ids"),type);

                    if (flashMode){

                        filterOffers();
                        showOffers();

                    } else {

                        filterStores();
                        showStores();
                    }
                }

            }
        }
    }
}
