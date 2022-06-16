package sg.com.fuzzie.android.ui.shop.filter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Category;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.filter.BrandListRefineItem;
import sg.com.fuzzie.android.utils.FuzzieData;

/**
 * Created by mac on 9/3/17.
 */

@EActivity(R.layout.activity_brand_list_refine)
public class BrandListRefineActivity extends BaseActivity {

    public static final int REFINE_BRAND = 0;
    public static final int REFINE_JACKPOT = 1;

    FastItemAdapter<BrandListRefineItem> adapter;
    List<Category> categories = new ArrayList<>();

    @Extra
    String titleExtra;

    @Extra
    int refineType;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.rvSubCategory)
    RecyclerView rvSubCategory;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.tvClear)
    TextView tvClear;

    @AfterViews
    void calledAfterViewInjection(){

        tvTitle.setText(titleExtra);

        adapter = new FastItemAdapter<>();
        rvSubCategory.setLayoutManager(new LinearLayoutManager(context));
        rvSubCategory.setAdapter(adapter);

        if (refineType == REFINE_BRAND){
            categories = dataManager.getSubCategoriesFromBrands(dataManager.selectedBrands);
        } else if (refineType == REFINE_JACKPOT){
            categories = dataManager.getSubCategoriesFromCoupons(dataManager.getCoupons());
        }

        for (Category category : categories){
            adapter.add(new BrandListRefineItem(category));
        }

        adapter.notifyAdapterDataSetChanged();
        adapter.withOnClickListener(new OnClickListener<BrandListRefineItem>() {
            @Override
            public boolean onClick(View v, IAdapter<BrandListRefineItem> adapter, BrandListRefineItem item, int position) {
                Category category = item.getCategory();
                if (dataManager.selectedBrandsSubCategoryIds.contains(category.getId())){
                    dataManager.selectedBrandsSubCategoryIds.remove(category.getId());
                } else {
                    dataManager.selectedBrandsSubCategoryIds.add(category.getId());
                }

                item.getViewHolder().updateCheck(dataManager.selectedBrandsSubCategoryIds.contains(category.getId()));
                updateClearButton();

                return true;
            }
        });

        updateClearButton();
    }

    @Click(R.id.tvClear)
    void clearButtonClicked(){
        if (dataManager.selectedBrandsSubCategoryIds.size() == 0){
            for (Category category : categories){
                dataManager.selectedBrandsSubCategoryIds.add(category.getId());
            }
        } else {
            dataManager.selectedBrandsSubCategoryIds.clear();
        }

        adapter.notifyAdapterDataSetChanged();

        updateClearButton();
    }

    private void updateClearButton(){
        if (dataManager.selectedBrandsSubCategoryIds.size() == 0){
            tvClear.setText("Select all");
        } else {
            tvClear.setText("Clear");
        }

    }

    @Click(R.id.cvDone)
    void doneButtonClicked(){
        setResult(RESULT_OK, null);
        finish();
    }
}
