package sg.com.fuzzie.android.ui.jackpot;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableLayoutListener;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import sg.com.fuzzie.android.FuzzieApplication_;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.WebActivity_;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Category;
import sg.com.fuzzie.android.api.models.Coupon;
import sg.com.fuzzie.android.api.models.Photos;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.brand.ValidOptionNewItem;
import sg.com.fuzzie.android.ui.club.ClubSubscribeActivity_;
import sg.com.fuzzie.android.ui.shop.BrandRedeemDetailsActivity_;
import sg.com.fuzzie.android.ui.shop.BrandStoreLocationsActivity_;
import sg.com.fuzzie.android.ui.shop.BrandTermsActivity_;
import sg.com.fuzzie.android.ui.shop.CashbackInfoActivity_;
import sg.com.fuzzie.android.utils.BlurBuilder;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.ViewUtils;

/**
 * Created by joma on 10/14/17.
 */

@EActivity(R.layout.activity_jackpot_coupon)
public class JackpotCouponActivity extends BaseActivity {

    Coupon coupon;
    Brand brand;

    FastItemAdapter<ValidOptionNewItem> validOptionAdapter;


    @Extra
    String couponId;

    @Bean
    FuzzieData dataManager;

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.tlPhotosIndicator)
    TabLayout tlPhotosIndicator;

    @ViewById(R.id.vpPhotos)
    ViewPager vpPhotos;

    @ViewById(R.id.tvSoldOut)
    TextView tvSoldOut;

    @ViewById(R.id.ivCategory)
    ImageView ivCategory;

    @ViewById(R.id.tvCategory)
    TextView tvCategory;

    @ViewById(R.id.tvBrandName)
    TextView tvBrandName;

    @ViewById(R.id.tvCouponName)
    TextView tvCouponName;

    @ViewById(R.id.tvPrice)
    TextView tvPrice;

    @ViewById(R.id.tvCashback)
    TextView tvCashback;

    @ViewById(R.id.tvPowerUp)
    TextView tvPowerUp;

    @ViewById(R.id.tvTicketCount)
    TextView tvTicketCount;

    @ViewById(R.id.tvEarn)
    TextView tvEarn;

    @ViewById(R.id.rvValidOption)
    RecyclerView rvValidOption;

    @ViewById(R.id.llValidOptions)
    View llValidOptions;

    @ViewById(R.id.llDesc)
    View llDesc;

    @ViewById(R.id.tvDesc)
    TextView tvDesc;

    @ViewById(R.id.llAbout)
    View llAbout;

    @ViewById(R.id.ellAbout)
    ExpandableRelativeLayout ellAbout;

    @ViewById(R.id.tvBrandAboutShort)
    TextView tvBrandAboutShort;

    @ViewById(R.id.tvBrandAbout)
    TextView tvBrandAbout;

    @ViewById(R.id.llSocialMedia)
    LinearLayout llSocialMedia;

    @ViewById(R.id.ivFacebook)
    ImageView ivFacebook;

    @ViewById(R.id.ivTwitter)
    ImageView ivTwitter;

    @ViewById(R.id.ivInstagram)
    ImageView ivInstagram;

    @ViewById(R.id.btnWebsite)
    Button btnWebsite;

    @ViewById(R.id.cvChoose)
    CardView cvChoose;

    @ViewById(R.id.tvRedeemValid)
    TextView tvRedeemValid;

    @ViewById(R.id.cashbackInfo)
    View cashbackInfo;

    @ViewById(R.id.tvCashbackPercentage)
    TextView tvCashbackPercentage;

    @ViewById(R.id.vClubMember)
    View vClubMember;

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @AfterViews
    public void calledAfterViewInjection() {

        coupon = dataManager.getCouponById(couponId);
        if (coupon == null) return;

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        vpPhotos.postDelayed(new Runnable() {
            @Override
            public void run() {
                vpPhotos.setBackground(null);
                vpPhotos.setAdapter(new ImageSlideAdapter(context, coupon.getPhotos()));
                if (coupon.getPhotos().size() == 1) {
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

        if (coupon.isSoldOut()) {
            tvSoldOut.setRotation(-20);
            tvSoldOut.setVisibility(View.VISIBLE);
            cvChoose.setCardBackgroundColor(getResources().getColor(R.color.loblolly));
            cvChoose.setEnabled(false);
        }

        tvCouponName.setText(coupon.getName());
        double price = Double.parseDouble(coupon.getPrice().getValue());
        tvPrice.setText(FuzzieUtils.getFormattedValue(price, 0.7f));


        tvPowerUp.setVisibility(View.GONE);
        if (coupon.getCashBack().getPercentage() > 0) {

            tvCashback.setText(FuzzieUtils.getFormattedCashbackPercentage(coupon.getCashBack().getPercentage(), 1));
            tvCashbackPercentage.setText(String.format("%s%% Instant Cashback", FuzzieUtils.getFormattedPercentage(coupon.getCashBack().getPercentage())));
            tvEarn.setText(FuzzieUtils.getFormattedValue(coupon.getCashBack().getValue(), 0.75f));


        } else {

            tvCashback.setVisibility(View.GONE);
            cashbackInfo.setVisibility(View.GONE);

        }

        tvTicketCount.setText(String.valueOf(coupon.getTicketCount()));

        if (coupon.getOptions() != null && coupon.getOptions().size() > 0){

            llValidOptions.setVisibility(View.VISIBLE);

            validOptionAdapter = new FastItemAdapter<>();
            rvValidOption.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            rvValidOption.setAdapter(validOptionAdapter);

            validOptionAdapter.clear();

            for (String option : coupon.getOptions()){

                validOptionAdapter.add(new ValidOptionNewItem(option));

            }

        } else {

            llValidOptions.setVisibility(View.GONE);

        }


        updateStoreLocationsState();

        showRedeemValidInfo();

        if (coupon.getDescription().length() == 0){
            llDesc.setVisibility(View.GONE);

        } else {
            llDesc.setVisibility(View.VISIBLE);
            tvDesc.setText(coupon.getDescription());
        }


        brand = dataManager.getBrandById(coupon.getBrandId());
        if (brand == null) return;

        tvTitle.setText(brand.getName());

        Drawable subCategoryIcon = ContextCompat.getDrawable(context, FuzzieUtils.getSubCategoryWhiteIcon(brand.getSubCategoryId()));
        ivCategory.setImageDrawable(subCategoryIcon);

        if (dataManager.getHome() != null) {
            Category subCategory = FuzzieUtils.getSubCategory(dataManager.getHome().getSubCategories(),brand.getSubCategoryId());
            tvCategory.setText(subCategory.getName());
        } else {
            tvCategory.setText("");
        }

        tvBrandName.setText(brand.getName());

        if (brand.getFacebookUrl() == null) {
            ivFacebook.setVisibility(View.GONE);
        }

        if (brand.getTwitterUrl() == null) {
            ivTwitter.setVisibility(View.GONE);
        }

        if (brand.getInstagramUrl() == null) {
            ivInstagram.setVisibility(View.GONE);
        }

        if (brand.getWebsite() == null) {
            btnWebsite.setVisibility(View.GONE);
        }

        if (brand.getDescription().length() == 0){
            llAbout.setVisibility(View.GONE);
        } else {
            llAbout.setVisibility(View.VISIBLE);

            tvBrandAbout.setText(brand.getDescription());

            if (brand.getDescription().length() > 180) {
                tvBrandAboutShort.setText(FuzzieUtils.fromHtml(brand.getDescription().substring(0, 180) + "&#133; <b><font color='#fa3e3f'>More</font></u>"));
                ellAbout.setClosePosition(300);

                ellAbout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ellAbout.toggle();
                    }
                });
                ellAbout.setListener(new ExpandableLayoutListener() {
                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationEnd() {

                    }

                    @Override
                    public void onPreOpen() {
                        tvBrandAboutShort.setVisibility(View.GONE);
                        tvBrandAbout.setVisibility(View.VISIBLE);
                        llSocialMedia.setVisibility(View.VISIBLE);
                        ellAbout.setEnabled(false);

                    }

                    @Override
                    public void onPreClose() {
                    }

                    @Override
                    public void onOpened() {
                    }

                    @Override
                    public void onClosed() {
                        tvBrandAboutShort.setVisibility(View.VISIBLE);
                        tvBrandAbout.setVisibility(View.GONE);
                        llSocialMedia.setVisibility(View.GONE);
                    }
                });
                ellAbout.collapse();
            } else {
                tvBrandAboutShort.setVisibility(View.GONE);
                tvBrandAbout.setVisibility(View.VISIBLE);
                llSocialMedia.setVisibility(View.VISIBLE);
            }
        }

        if (brand.isClubOnly()){

            ViewUtils.visible(vClubMember);

        } else {

            ViewUtils.gone(vClubMember);

        }

    }

    void showRedeemValidInfo(){

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {

            Date endDate = dateFormat.parse(coupon.getRedeemEndDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(endDate.getTime());

            Calendar now = Calendar.getInstance();
            long days = (calendar.getTimeInMillis() - now.getTimeInMillis()) / (24 * 60 * 60 * 1000);

            if (calendar.get(Calendar.HOUR_OF_DAY) > now.get(Calendar.HOUR_OF_DAY)){
                tvRedeemValid.setText("Valid for " + days + " days from purchase");
            } else {
                tvRedeemValid.setText("Valid for " + (days + 1) + " days from purchase");

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    void updateStoreLocationsState() {
        if (coupon.getStores().size() > 0) {
            findViewById(R.id.vStoreLocationsPreDivider).setVisibility(View.VISIBLE);
            findViewById(R.id.btnStoreLocations).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.vStoreLocationsPreDivider).setVisibility(View.GONE);
            findViewById(R.id.btnStoreLocations).setVisibility(View.GONE);
        }
    }

    @Click(R.id.ivFacebook)
    void facebookButtonClicked() {
        if (brand != null && brand.getFacebookUrl() != null)
            WebActivity_.intent(context).urlExtra(brand.getFacebookUrl()).titleExtra("FACEBOOK").start();
    }

    @Click(R.id.ivTwitter)
    void twitterButtonClicked() {
        if (brand != null && brand.getTwitterUrl() != null)
            WebActivity_.intent(context).urlExtra(brand.getTwitterUrl()).titleExtra("TWITTER").start();
    }

    @Click(R.id.ivInstagram)
    void instagramButtonClicked() {
        if (brand != null && brand.getInstagramUrl() != null)
            WebActivity_.intent(context).urlExtra(brand.getInstagramUrl()).titleExtra("INSTAGRAM").start();
    }

    @Click(R.id.btnWebsite)
    void websiteButtonClicked() {
        if (brand != null && brand.getWebsite() != null)
            WebActivity_.intent(context).urlExtra(brand.getWebsite()).titleExtra("WEBSITE").start();
    }

    @Click(R.id.btnHowToRedeem)
    void howToRedeemButtonClicked() {
        if (brand != null)
            BrandRedeemDetailsActivity_.intent(context).brandId(brand.getId()).start();
    }

    @Click(R.id.btnTermsAndConditions)
    void termsButtonClicked() {

        if (coupon.getTerms() != null){

            StringBuilder sb = new StringBuilder();
            for (String terms: coupon.getTerms()) {
                sb.append(terms);
                sb.append("\n");
            }

            BrandTermsActivity_.intent(context).termsExtra(sb.toString()).start();
        }
    }

    @Click(R.id.btnStoreLocations)
    void storeLocationClicked() {
        BrandStoreLocationsActivity_.intent(context).couponId(coupon.getId()).start();
    }

    @Click(R.id.tvLearnMore)
    void howToPlayButtonClicked(){
        ((FuzzieApplication_) getApplication()).lastActivityBitmap = BlurBuilder.blur(findViewById(android.R.id.content));
        CashbackInfoActivity_.intent(context).start();
        overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
    }

    @Click(R.id.llEarn)
    void cashBackInfoButtonClicked(){
        ((FuzzieApplication_) getApplication()).lastActivityBitmap = BlurBuilder.blur(findViewById(android.R.id.content));
        CashbackInfoActivity_.intent(context).start();
        overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
    }

    @Click(R.id.cvChoose)
    void chooseButtonClicked(){

        if (brand.isClubOnly()){

            if (currentUser.getCurrentUser().getFuzzieClub() != null && currentUser.getCurrentUser().getFuzzieClub().isMembership()){

                JackpotChooseActivity_.intent(context).couponId(couponId).start();

            } else {

                showClubExclusiveAlert();
            }

        } else {

            JackpotChooseActivity_.intent(context).couponId(couponId).start();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private static class ImageSlideAdapter extends PagerAdapter {
        Context context;

        List<Photos> photos;

        public ImageSlideAdapter(Context context, List<Photos> photos) {
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

    @Override
    public void FZClubExclusiveDialogExploreButtonClicked() {
        super.FZClubExclusiveDialogExploreButtonClicked();

        ClubSubscribeActivity_.intent(context).start();
    }
}
