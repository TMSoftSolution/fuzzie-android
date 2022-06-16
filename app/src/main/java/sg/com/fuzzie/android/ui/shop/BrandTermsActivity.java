package sg.com.fuzzie.android.ui.shop;

import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

/**
 * Created by faruq on 03/01/17.
 */

@EActivity(R.layout.activity_brand_terms)
public class BrandTermsActivity extends BaseActivity {

    Brand brand;

    @Extra
    String brandId;

    @Extra
    String termsExtra;

    @Extra
    String titleExtra;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.tvTerms)
    TextView tvTerms;

    @AfterViews
    public void calledAfterViewInjection() {

        if (titleExtra != null && titleExtra.length() > 0){

            tvTitle.setText(titleExtra);
        }

        if (termsExtra != null && termsExtra.length() > 0){

            tvTerms.setText(termsExtra);

        } else {

            if (brandId == null) return;

            brand = dataManager.getBrandById(brandId);
            if (brand == null) return;

            StringBuilder sb = new StringBuilder();
            for (String terms: brand.getTermsAndConditionsList()) {
                sb.append(terms);
                sb.append("\n");
            }

            tvTerms.setText(sb);

        }

    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

}
