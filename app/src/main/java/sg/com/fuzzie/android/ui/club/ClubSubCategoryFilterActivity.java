package sg.com.fuzzie.android.ui.club;

import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.BrandType;
import sg.com.fuzzie.android.api.models.Category;
import sg.com.fuzzie.android.api.models.Component;
import sg.com.fuzzie.android.api.models.Filter;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.club.ClubFilterCategoryItem;
import sg.com.fuzzie.android.items.club.ClubFilterHeaderItem;
import sg.com.fuzzie.android.items.club.ClubFilterTypeItem;
import sg.com.fuzzie.android.utils.FuzzieData;

@EActivity(R.layout.activity_club_sub_category_filter)
public class ClubSubCategoryFilterActivity extends BaseActivity {

    private BrandType brandType;
    FastItemAdapter<ClubFilterCategoryItem> categoryAdapter;
    FastItemAdapter typeAdapter;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.rvCategories)
    RecyclerView rvCategories;

    @ViewById(R.id.rvType)
    RecyclerView rvType;

    @AfterViews
    void callAfterViewInjection(){

        brandType = dataManager.getBrandType();
        if (brandType == null) return;

        categoryAdapter = new FastItemAdapter<>();
        rvCategories.setLayoutManager(new GridLayoutManager(context, 2));
        rvCategories.setAdapter(categoryAdapter);

        categoryAdapter.withOnClickListener(new OnClickListener<ClubFilterCategoryItem>() {
            @Override
            public boolean onClick(@Nullable View v, IAdapter<ClubFilterCategoryItem> adapter, ClubFilterCategoryItem item, int position) {

                item.setShowEffect(!item.isShowEffect());
                categoryAdapter.notifyAdapterItemChanged(position);

                if (dataManager.categoryFilters.contains(item.getCategory().getName())){

                    dataManager.categoryFilters.remove(item.getCategory().getName());

                } else {

                    dataManager.categoryFilters.add(item.getCategory().getName());
                }

                return true;
            }
        });
        showCategories();

        typeAdapter = new FastItemAdapter<>();
        rvType.setLayoutManager(new LinearLayoutManager(context));
        rvType.setAdapter(typeAdapter);

        typeAdapter.withOnClickListener(new OnClickListener() {
            @Override
            public boolean onClick(@Nullable View v, IAdapter adapter, IItem item, int position) {

                if (item.getType() == R.id.item_club_filter_type_id){

                    ClubFilterTypeItem typeItem = (ClubFilterTypeItem) item;
                    typeItem.setChecked(!typeItem.isChecked());

                    typeAdapter.notifyAdapterItemChanged(position);

                    if (dataManager.componentFilters.contains(typeItem.getComponent().getName())){

                        dataManager.componentFilters.remove(typeItem.getComponent().getName());

                    } else {

                        dataManager.componentFilters.add(typeItem.getComponent().getName());
                    }
                }

                return true;
            }
        });
        showTypes();

    }

    private void showCategories(){

        if (categoryAdapter == null){

            categoryAdapter = new FastItemAdapter<>();

        } else {

            categoryAdapter.clear();

        }

        if (brandType.getCategories() != null)
        for (Category category : brandType.getCategories()){
            categoryAdapter.add(new ClubFilterCategoryItem(category, dataManager.categoryFilters.contains(category.getName())));
        }
    }

    private void showTypes(){

        if (typeAdapter == null){

            typeAdapter = new FastItemAdapter<>();

        } else {

            typeAdapter.clear();

        }

        for (Filter filter : brandType.getFilters()){

            typeAdapter.add(new ClubFilterHeaderItem(filter.getName()));

            for (Component component : filter.getComponents()){

                typeAdapter.add(new ClubFilterTypeItem(component, dataManager.componentFilters.contains(component.getName())));
            }
        }

    }

    @Click(R.id.btnReset)
    void resetButtonClicked(){

        dataManager.categoryFilters = new ArrayList<>();
        dataManager.componentFilters = new ArrayList<>();
        showCategories();
        showTypes();
    }

    @Click(R.id.btnFilter)
    void filterButttonClicked(){
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slid_out_up);
    }
}
