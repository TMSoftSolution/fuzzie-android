package sg.com.fuzzie.android.ui.giftbox;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.WebActivity_;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Category;
import sg.com.fuzzie.android.api.models.Gift;
import sg.com.fuzzie.android.api.models.GiftCard;
import sg.com.fuzzie.android.api.models.Photos;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.brand.ValidOptionNewItem;
import sg.com.fuzzie.android.ui.gift.DeliveryMethodsActivity_;
import sg.com.fuzzie.android.ui.shop.BrandRedeemDetailsActivity_;
import sg.com.fuzzie.android.ui.shop.BrandStoreLocationsActivity_;
import sg.com.fuzzie.android.ui.shop.BrandTermsActivity_;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.TimeUtils;
import sg.com.fuzzie.android.utils.ViewUtils;

/**
 * Created by mac on 7/24/17.
 */

@EActivity(R.layout.activity_sent_gift_card)
public class SentGiftCardActivity extends BaseActivity {

    @Extra
    String giftId;

    Gift gift;
    Brand brand;
    GiftCard giftCard;

    FastItemAdapter<ValidOptionNewItem> validOptionAdapter;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.vpPhotos)
    ViewPager vpPhotos;

    @ViewById(R.id.tlPhotosIndicator)
    TabLayout tlPhotosIndicator;

    @ViewById(R.id.ivCategory)
    ImageView ivCategory;

    @ViewById(R.id.tvCategory)
    TextView tvCategory;

    @ViewById(R.id.tvBrandName)
    TextView tvBrandName;

    @ViewById(R.id.tvPrice)
    TextView tvPrice;

    @ViewById(R.id.llValidOptions)
    View llValidOptions;

    @ViewById(R.id.rvValidOption)
    RecyclerView rvValidOption;

    @ViewById(R.id.cvSendGift)
    CardView cvSendGift;

    @ViewById(R.id.tvSendGift)
    TextView tvSendGift;

    @ViewById(R.id.tvValidValue)
    TextView tvValidValue;

    @ViewById(R.id.tvSentValue)
    TextView tvSentValue;

    @ViewById(R.id.tvOrderNumber)
    TextView tvOrderNumber;

    @ViewById(R.id.llSenderInfo)
    LinearLayout llSenderInfo;

    @ViewById(R.id.tvSenderName)
    TextView tvSenderName;

    @ViewById(R.id.tvMessage)
    TextView tvMessage;


    @AfterViews
    public void calledAfterViewInjection(){
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (giftId == null) return;

        gift = dataManager.getSentGiftById(giftId);
        if (gift == null) return;

        giftCard = gift.getGiftCard();

        brand = gift.getBrand();

        vpPhotos.postDelayed(new Runnable() {
            @Override
            public void run() {
                vpPhotos.setBackground(null);
                vpPhotos.setAdapter(new ImageSlideAdapter(context, brand.getPhotos()));
                if (brand.getPhotos().size() == 1) {
                    tlPhotosIndicator.setVisibility(View.GONE);
                } else {
                    tlPhotosIndicator.setupWithViewPager(vpPhotos, true);
                    for (int i = 0; i < tlPhotosIndicator.getTabCount(); i++) {
                        View view = getLayoutInflater().inflate(R.layout.item_photo_indicator, tlPhotosIndicator, false);
                        tlPhotosIndicator.getTabAt(i).setCustomView(view);
                    }
                }
            }
        }, 500);

        Drawable subCategoryIcon = ContextCompat.getDrawable(context, FuzzieUtils.getSubCategoryWhiteIcon(brand.getSubCategoryId()));
        ivCategory.setImageDrawable(subCategoryIcon);

        if (dataManager.getHome() != null) {
            Category subCategory = FuzzieUtils.getSubCategory(dataManager.getHome().getSubCategories(),brand.getSubCategoryId());
            tvCategory.setText(subCategory.getName());
        } else {
            tvCategory.setText("");
        }

        tvTitle.setText(brand.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvBrandName.setText(giftCard.getDisplayName());
        tvPrice.setText(giftCard.getPrice().getCurrencySymbol() + giftCard.getPrice().getValue());


        if (brand.getOptionsForGiftCards() != null && brand.getOptionsForGiftCards().size() > 0){

            llValidOptions.setVisibility(View.VISIBLE);

            validOptionAdapter = new FastItemAdapter<>();
            rvValidOption.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            rvValidOption.setAdapter(validOptionAdapter);
            validOptionAdapter.clear();

            for (String option : brand.getOptionsForGiftCards()){
                validOptionAdapter.add(new ValidOptionNewItem(option));
            }
        } else {

            llValidOptions.setVisibility(View.GONE);
        }

        tvValidValue.setText(TimeUtils.dateTimeDDMMYYYY(gift.getExpirationDate()));
        tvSentValue.setText(TimeUtils.dateTimeDDMMYYYY(gift.getSentTime()));
        tvOrderNumber.setText(String.valueOf(gift.getOrderNumber()));

        updateSenderInfo();

    }

    void updateSenderInfo(){

        if (gift.isGifted() != null && gift.isGifted()){
            if (gift.getReceiver() != null){
                llSenderInfo.setVisibility(View.VISIBLE);

                if (gift.getReceiver().getName() != null){
                    tvSenderName.setText(gift.getReceiver().getName());
                }

            }

            if (gift.getMessage() != null){
                tvMessage.setText(gift.getMessage());
            }
        } else {
            llSenderInfo.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Click(R.id.ivFacebook)
    void facebookButtonClicked() {
        WebActivity_.intent(context).urlExtra(brand.getFacebookUrl()).titleExtra("FACEBOOK").start();
    }

    @Click(R.id.ivTwitter)
    void twitterButtonClicked() {
        WebActivity_.intent(context).urlExtra(brand.getTwitterUrl()).titleExtra("TWITTER").start();
    }

    @Click(R.id.ivInstagram)
    void instagramButtonClicked() {
        WebActivity_.intent(context).urlExtra(brand.getInstagramUrl()).titleExtra("INSTAGRAM").start();
    }

    @Click(R.id.btnWebsite)
    void websiteButtonClicked() {
        if (brand.getWebsite() != null){
            WebActivity_.intent(context).urlExtra(brand.getWebsite()).titleExtra("WEBSITE").start();
        }
    }

    @Click(R.id.btnTermsAndConditions)
    void termsButtonClicked() {
        BrandTermsActivity_.intent(context).brandId(brand.getId()).start();
    }

    @Click(R.id.btnHowToRedeem)
    void howToRedeemButtonClicked() {
        BrandRedeemDetailsActivity_.intent(context).brandId(brand.getId()).start();
    }

    @Click(R.id.btnStoreLocations)
    void storeLocationClicked() {
        BrandStoreLocationsActivity_.intent(context).brandId(brand.getId()).start();
    }

    @Click(R.id.cvSendGift)
    void sendGiftButtonClicked(){
        if(gift != null && gift.getReceiver() != null){
            dataManager.setSentGift(gift);
            dataManager.setReceiver(gift.getReceiver());
            DeliveryMethodsActivity_.intent(context).showBack(true).start();
        }
    }

    @Click(R.id.tvEditGift)
    void editButtonClicked(){
        GiftEditActivity_.intent(context).giftId(giftId).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (giftId == null) return;
        gift = dataManager.getSentGiftById(giftId);
        if (gift == null) return;
        updateSenderInfo();
    }

    private static class ImageSlideAdapter extends PagerAdapter {
        Context context;

        List<Photos> photos;

        ImageSlideAdapter(Context context, List<Photos> photos) {
            this.context = context;
            this.photos = photos;
        }

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, final int position) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_photo, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            Picasso.get()
                    .load(photos.get(position).getUrl())
                    .placeholder(R.drawable.brands_placeholder)
                    .resize(context.getResources().getDisplayMetrics().widthPixels, (int) ViewUtils.convertDpToPixel(256, context))
                    .centerCrop()
                    .into(imageView);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
