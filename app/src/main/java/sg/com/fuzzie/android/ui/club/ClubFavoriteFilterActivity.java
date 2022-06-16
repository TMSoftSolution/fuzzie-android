package sg.com.fuzzie.android.ui.club;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

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
import sg.com.fuzzie.android.api.models.BrandType;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.club.ClubFilterItem;
import sg.com.fuzzie.android.utils.FuzzieData;

@EActivity(R.layout.activity_club_favorite_filter)
public class ClubFavoriteFilterActivity extends BaseActivity {

    FastItemAdapter<ClubFilterItem> filterAdapter;
    private List<Integer> categoryIds;

    @Extra
    String categoryIdsExtra;

    @Extra
    boolean isSortByDistance;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.rvCategories)
    RecyclerView rvCategories;

    @ViewById(R.id.btnAll)
    Button btnAll;

    @ViewById(R.id.vDistance)
    View vDistance;

    @ViewById(R.id.ivDistance)
    ImageView ivDistance;

    @ViewById(R.id.tvDistance)
    TextView tvDistance;

    @ViewById(R.id.vRecent)
    View vRecent;

    @ViewById(R.id.ivRecent)
    ImageView ivRecent;

    @ViewById(R.id.tvRecent)
    TextView tvRecent;

    @AfterViews
    void callAfterViewInjection(){

        if (categoryIdsExtra == null || categoryIdsExtra.equals("")){

            categoryIds = new ArrayList<>();

        } else {

            Gson gson = new Gson();
            Type type = new TypeToken<List<Integer>>(){}.getType();
            categoryIds = gson.fromJson(categoryIdsExtra,type);
        }

        filterAdapter = new FastItemAdapter<>();
        rvCategories.setLayoutManager(new LinearLayoutManager(context));
        rvCategories.setAdapter(filterAdapter);

        filterAdapter.withOnClickListener(new OnClickListener<ClubFilterItem>() {
            @Override
            public boolean onClick(@Nullable View v, IAdapter<ClubFilterItem> adapter, ClubFilterItem item, int position) {

                item.setChecked(!item.isChecked());

                if (categoryIds.contains(item.getBrandType().getId())){

                    categoryIds.remove(Integer.valueOf(item.getBrandType().getId()));

                } else {

                    categoryIds.add(item.getBrandType().getId());

                }

                filterAdapter.notifyAdapterItemChanged(position);

                return true;
            }
        });

        updateSelectButton();
        updateSort();
        showCategories();
    }

    private void showCategories(){

        if (filterAdapter == null){

            filterAdapter = new FastItemAdapter<>();

        } else {

            filterAdapter.clear();
        }

        for (BrandType brandType : dataManager.getClubHome().getBrandTypes()){
            filterAdapter.add(new ClubFilterItem(brandType, categoryIds.contains(brandType.getId())));
        }
    }

    private void updateSelectButton(){

        if (categoryIds.size() == dataManager.getClubHome().getBrandTypes().size()){

            btnAll.setText("Clear");

        } else {

            btnAll.setText("Select all");
        }
    }

    private void updateSort(){

        if (isSortByDistance){

            vDistance.setBackgroundResource(R.drawable.bg_club_favorite_filter_selected);
            ivDistance.setImageResource(R.drawable.ic_store_location_white);
            tvDistance.setTextColor(getResources().getColor(R.color.white));

            vRecent.setBackgroundResource(R.drawable.bg_club_favorite_filter_unselected);
            ivRecent.setImageResource(R.drawable.ic_recent_grey);
            tvRecent.setTextColor(getResources().getColor(R.color.colorPowerUpInactive));

        } else {

            vDistance.setBackgroundResource(R.drawable.bg_club_favorite_filter_unselected);
            ivDistance.setImageResource(R.drawable.ic_store_location_grey);
            tvDistance.setTextColor(getResources().getColor(R.color.colorPowerUpInactive));

            vRecent.setBackgroundResource(R.drawable.bg_club_favorite_filter_selected);
            ivRecent.setImageResource(R.drawable.ic_recent_white);
            tvRecent.setTextColor(getResources().getColor(R.color.white));
        }
    }

    @Click(R.id.vDistance)
    void distanceButtonClicked(){

        isSortByDistance = true;
        updateSort();
    }

    @Click(R.id.vRecent)
    void recentButtonClicked(){

        isSortByDistance = false;
        updateSort();
    }

    @Click(R.id.btnAll)
    void allButtonClicked(){

        if (btnAll.getText().toString().equals("Clear")){

            categoryIds = new ArrayList<>();
            btnAll.setText("Select all");

        } else {

            categoryIds = new ArrayList<>();
            for (BrandType brandType : dataManager.getClubHome().getBrandTypes()){
                categoryIds.add(brandType.getId());
            }
            btnAll.setText("Clear");

        }

        showCategories();
    }


    @Click(R.id.btnFilter)
    void filterButtonClicked(){

        Intent intent = new Intent();
        Gson gson = new Gson();
        Type type = new TypeToken<List<Integer>>(){}.getType();
        intent.putExtra("ids", gson.toJson(categoryIds, type));
        intent.putExtra("distanceSort", isSortByDistance);
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slid_out_up);

    }

    @Override
    public void onBackPressed() {

    }
}
