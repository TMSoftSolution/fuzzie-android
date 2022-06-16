package sg.com.fuzzie.android.ui.shop.filter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Category;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.filter.CategoryRefineItem;
import sg.com.fuzzie.android.utils.FuzzieData;

/**
 * Created by mac on 9/3/17.
 */

@EActivity(R.layout.activity_brand_list_category_refine)
public class BrandListCategoryRefineActivity extends BaseActivity {

    private FastItemAdapter<CategoryRefineItem> adapter;
    List<Category> categories = new ArrayList<>();

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.rvCategories)
    RecyclerView rvCategories;

    @ViewById(R.id.tvClear)
    TextView tvClear;


    @AfterViews
    void calledAfterViewInjection(){

        adapter = new FastItemAdapter<>();
        rvCategories.setLayoutManager(new LinearLayoutManager(context));
        rvCategories.setAdapter(adapter);

        categories = dataManager.getCategoriesFromBrands(dataManager.selectedBrands);
        for (Category category : categories){
            adapter.add(new CategoryRefineItem(category));
        }
        adapter.notifyAdapterDataSetChanged();
        adapter.withOnClickListener(new OnClickListener<CategoryRefineItem>() {
            @Override
            public boolean onClick(View v, IAdapter<CategoryRefineItem> adapter, CategoryRefineItem item, int position) {
                Category category = item.getCategory();
                if (dataManager.selectedBrandsCategoryIds.contains(category.getId())){
                    dataManager.selectedBrandsCategoryIds.remove(category.getId());
                } else {
                    dataManager.selectedBrandsCategoryIds.add(category.getId());
                }

                item.getViewHolder().updateCheck(dataManager.selectedBrandsCategoryIds.contains(category.getId()));

                updateClearButton();

                return true;
            }
        });

        updateClearButton();
    }

    @Click(R.id.tvClear)
    void clearButtonClicked(){
        if (dataManager.selectedBrandsCategoryIds.size() == 0){
            for (Category category : categories){
                dataManager.selectedBrandsCategoryIds.add(category.getId());
            }
        } else {
            dataManager.selectedBrandsCategoryIds.clear();
        }

        adapter.notifyAdapterDataSetChanged();

        updateClearButton();
    }

    private void updateClearButton(){
        if (dataManager.selectedBrandsCategoryIds.size() == 0){
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
