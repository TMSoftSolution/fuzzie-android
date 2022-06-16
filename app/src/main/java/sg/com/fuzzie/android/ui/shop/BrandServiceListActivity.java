package sg.com.fuzzie.android.ui.shop;

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
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Service;
import sg.com.fuzzie.android.api.models.ServiceCategory;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.brand.CashbackItem;
import sg.com.fuzzie.android.items.brand.ServiceListGiftCardItem;
import sg.com.fuzzie.android.items.brand.ServiceListHeaderItem;
import sg.com.fuzzie.android.items.brand.ServiceListItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

/**
 * Created by nurimanizam on 17/12/16.
 */

@EActivity(R.layout.activity_brand_service_list)
public class BrandServiceListActivity extends BaseActivity {

    Brand brand;
    FastItemAdapter serviceAdapter;

    @Extra
    String brandId;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.rvServices)
    RecyclerView rvServices;

    @AfterViews
    public void calledAfterViewInjection() {

        if (brandId == null) return;

        brand = dataManager.getBrandById(brandId);
        if (brand == null) return;

        serviceAdapter = new FastItemAdapter();

        tvTitle.setText(brand.getName());

        rvServices.setLayoutManager(new LinearLayoutManager(context));
        rvServices.setAdapter(serviceAdapter);

        serviceAdapter.clear();
        if (brand.getCashBack().getPercentage() > 0){
            serviceAdapter.add(new CashbackItem(brand));
        }

        if (brand.getGiftCards().size() > 0) {
            serviceAdapter.add(new ServiceListGiftCardItem(brand));
        }

        // No Service Sub Category
        for (Service service : brand.getServices()){

            if (service.getServiceCategoryIds() != null && service.getServiceCategoryIds().size() == 0){
                serviceAdapter.add(new ServiceListItem(brand, service));
            }
        }

        // Service Sub Category
        for (ServiceCategory serviceCategory : brand.getServiceCategory()){

            boolean serviceSubCategoryHeaderAdded = false;

            for (Service service : brand.getServices()){

                if (service.getServiceCategoryIds() != null && service.getServiceCategoryIds().size() != 0){

                    for (int serviceSubCategoryId : service.getServiceCategoryIds()){

                        if (serviceSubCategoryId == serviceCategory.getId()){

                            if (!serviceSubCategoryHeaderAdded){

                                serviceSubCategoryHeaderAdded = true;
                                if (!serviceCategory.getName().equals("") && serviceCategory.getName() != null){
                                    serviceAdapter.add(new ServiceListHeaderItem(serviceCategory.getName()));
                                }
                            }

                            serviceAdapter.add(new ServiceListItem(brand, service));
                            break;

                        }
                    }
                }
            }

        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

}
