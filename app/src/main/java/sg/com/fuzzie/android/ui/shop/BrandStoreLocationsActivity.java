package sg.com.fuzzie.android.ui.shop;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Coupon;
import sg.com.fuzzie.android.api.models.Store;
import sg.com.fuzzie.android.api.models.Stores;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.brand.StoreListItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

/**
 * Created by faruq on 06/02/17.
 */

@EActivity(R.layout.activity_brand_store_locations)
public class BrandStoreLocationsActivity extends BaseActivity implements StoreListItem.OnPhoneClickListener {

    FastItemAdapter<StoreListItem> storeAdapter;

    @Extra
    String brandId;

    @Extra
    String couponId;

    @Extra
    boolean clickable = false;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.rvStoreLocations)
    RecyclerView rvStoreLocations;

    @AfterViews
    public void calledAfterViewInjection() {

        storeAdapter = new FastItemAdapter<StoreListItem>();

        rvStoreLocations.setLayoutManager(new LinearLayoutManager(context));
        rvStoreLocations.setAdapter(storeAdapter);
        storeAdapter.clear();

        if (brandId != null){
            Brand brand = dataManager.getBrandById(brandId);
            if (brand == null) return;

            for (Stores stores : brand.getStores()) {
                storeAdapter.add(new StoreListItem(stores.getStore(), this));
            }
        } else if (couponId != null){
            Coupon coupon = dataManager.getCouponById(couponId);
            if (coupon == null) return;

            for (Stores stores : coupon.getStores()){
                storeAdapter.add(new StoreListItem(stores.getStore(), this));
            }
        }


    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

    @Override
    public void onClick(Store store) {
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + store.getPhone()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onClickStoreName(Store store) {

        if (clickable){

            Intent intent = new Intent();
            intent.putExtra("extra_store", Store.toJSON(store));
            setResult(RESULT_OK, intent);
            finish();

        }
    }
}
