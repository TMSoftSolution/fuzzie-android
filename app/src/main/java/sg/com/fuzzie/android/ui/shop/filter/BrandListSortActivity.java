package sg.com.fuzzie.android.ui.shop.filter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.filter.SortByItem;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;

/**
 * Created by mac on 9/3/17.
 */

@EActivity(R.layout.activity_brand_list_sort)
public class BrandListSortActivity extends BaseActivity {

    public static final int SORT_BRAND = 0;
    public static final int SORT_JACKPOT = 1;

    FastItemAdapter<SortByItem> adapter;
    String [] sortArray;

    @Extra
    int sortType;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.rvSort)
    RecyclerView rvSort;

    @AfterViews
    public void calledAfterViewInjection() {

        adapter = new FastItemAdapter<>();
        rvSort.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rvSort.setAdapter(adapter);

        setAdapter();

        adapter.withOnClickListener(new OnClickListener<SortByItem>() {
            @Override
            public boolean onClick(View v, IAdapter<SortByItem> adapter, SortByItem item, int position) {
                dataManager.selectedSortByIndex = position;
                setResult(RESULT_OK, null);
                finish();
                return true;
            }
        });
    }

    private void setAdapter(){
        adapter.clear();

        if (sortType == SORT_BRAND){
            sortArray = FuzzieUtils.SORT_BY;
        } else if (sortType == SORT_JACKPOT){
            sortArray = FuzzieUtils.SORT_BY_JACKPOT;
        }

        for (int i = 0; i < sortArray.length ; i ++){
            boolean checked = dataManager.selectedSortByIndex == i;
            adapter.add(new SortByItem(sortArray[i], checked));
        }
    }


    @Override
    public void onBackPressed() {

    }

}
