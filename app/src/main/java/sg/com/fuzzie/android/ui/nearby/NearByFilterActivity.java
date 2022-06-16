package sg.com.fuzzie.android.ui.nearby;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
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

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Category;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.brand.MapFilterItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_FILTER_DONE;

/**
 * Created by mac on 7/12/17.
 */

@EActivity(R.layout.activity_near_by_filter)
public class NearByFilterActivity extends BaseActivity {

    FastItemAdapter<MapFilterItem> adapter;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.rvSubCategory)
    RecyclerView rvSubCategory;

    @ViewById(R.id.tvClear)
    TextView tvClear;

    @AfterViews
    void calledAfterViewInjection() {

        adapter = new FastItemAdapter<>();
        rvSubCategory.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rvSubCategory.setAdapter(adapter);

        if (dataManager.getUsedSubCategoris() != null){
            adapter.clear();

            for (Category category : dataManager.getUsedSubCategoris()){
                MapFilterItem item = new MapFilterItem(category);
                adapter.add(item);
            }

            adapter.notifyAdapterDataSetChanged();
        }

        adapter.withOnClickListener(new OnClickListener<MapFilterItem>() {
            @Override
            public boolean onClick(View v, IAdapter<MapFilterItem> adapter, MapFilterItem item, int position) {

                Category category = item.getCategory();
                if (dataManager.selectedSubCategoryIds.contains(category.getId())){
                    dataManager.selectedSubCategoryIds.remove(category.getId());
                } else {
                    dataManager.selectedSubCategoryIds.add(category.getId());
                }

                item.getViewHolder().updateCheck(dataManager.selectedSubCategoryIds.contains(category.getId()));

                updateClear();

                return true;
            }
        });

        updateClear();
    }

    private void updateClear(){
        if (dataManager.selectedSubCategoryIds.size() == 0){
            tvClear.setText("Select all");
        } else {
            tvClear.setText("Clear");
        }
    }

    @Click(R.id.cvDone)
    void doneButtonClicked(){
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_FILTER_DONE));
        finish();
    }

    @Click(R.id.tvClear)
    void clearButtonClicked(){

        if (dataManager.selectedSubCategoryIds.size() == 0){
            dataManager.selectedSubCategoryIds.addAll(dataManager.getSubCategoryIds());
        } else {
            dataManager.selectedSubCategoryIds.clear();
        }

        adapter.notifyAdapterDataSetChanged();

        updateClear();

    }
}
