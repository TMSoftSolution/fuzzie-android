package sg.com.fuzzie.android.ui.shop;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.lang.reflect.Type;
import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.WebActivity_;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.GeneralPage;
import sg.com.fuzzie.android.api.models.Service;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.FuzzieData;

/**
 * Created by mac on 7/13/17.
 */

@EActivity(R.layout.activity_campaign_details)
public class CampaignDetailsActivity extends BaseActivity {

    GeneralPage generalPage;

    @Extra
    String generalPageExtra;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.ivBackground)
    ImageView ivBackground;

    @ViewById(R.id.tvCampaignTitle)
    TextView tvCampaignTitle;

    @ViewById(R.id.tvCampaignBody)
    TextView tvCampaignBody;

    @ViewById(R.id.cvLink)
    CardView cvLink;

    @ViewById(R.id.tvLink)
    TextView tvLink;

    @AfterViews
    void calledAfterViewInjection(){

        if (generalPageExtra != null && !generalPageExtra.equals("")){

            generalPage = GeneralPage.fromJSON(generalPageExtra);

        }
        if (generalPage == null) return;

        tvTitle.setText(generalPage.getTitle());

        if (generalPage.getImage() != null){
            Picasso.get()
                    .load(generalPage.getImage())
                    .placeholder(R.drawable.brands_placeholder)
                    .into(ivBackground);
        }

        tvCampaignTitle.setText(generalPage.getTitle());
        tvCampaignBody.setText(generalPage.getContent());

        if (generalPage.getButtonName() != null && !generalPage.getButtonName().equals("")){
            cvLink.setVisibility(View.VISIBLE);
            tvLink.setText(generalPage.getButtonName());
        } else {
            cvLink.setVisibility(View.GONE);
        }

    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.cvLink)
    void linkButtonClicked(){

        if (generalPage.getPageType() != null){

            if (generalPage.getPageType().equals("Campaign")){

                Gson gson = new Gson();
                Type type = new TypeToken<List<String>>(){}.getType();

                BrandsListActivity_.intent(context).titleExtra(generalPage.getTitle()).brandIdsExtra(gson.toJson(generalPage.getBrandIds(), type)).start();

            } else if (generalPage.getPageType().equals("Web-linked")){

                WebActivity_.intent(context).urlExtra(generalPage.getWebLink()).titleExtra(generalPage.getTitle()).showLoading(true).start();

            } else if (generalPage.getPageType().equals("Brand")){

                if (generalPage.getBrandId() != null){

                    Brand brand = dataManager.getBrandById(generalPage.getBrandId());

                    if (brand != null){

                        if (brand.getServices() != null && brand.getServices().size() > 0) {
                            if (brand.getServices().size() == 1 && (brand.getGiftCards() == null || brand.getGiftCards().size() == 0) ) {
                                BrandServiceActivity_.intent(context).brandId(brand.getId()).serviceExtra(Service.toJSON(brand.getServices().get(0))).start();
                            } else {
                                BrandServiceListActivity_.intent(context).brandId(brand.getId()).start();
                            }
                        } else if (brand.getGiftCards() != null && brand.getGiftCards().size() > 0) {
                            BrandGiftActivity_.intent(context).brandId(brand.getId()).start();
                        }
                    }
                }
            }
        }


    }
}
