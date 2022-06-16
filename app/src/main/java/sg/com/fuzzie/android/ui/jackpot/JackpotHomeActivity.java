package sg.com.fuzzie.android.ui.jackpot;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import timber.log.Timber;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Coupon;
import sg.com.fuzzie.android.api.models.CouponTemplate;
import sg.com.fuzzie.android.api.models.Home;
import sg.com.fuzzie.android.api.models.Jackpot;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.jackpot.CouponItem;
import sg.com.fuzzie.android.ui.shop.filter.BrandListRefineActivity_;
import sg.com.fuzzie.android.ui.shop.filter.BrandListSortActivity_;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.CustomTypefaceSpan;
import sg.com.fuzzie.android.utils.FZTimer;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_JACKPOT_IS_END;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_JACKPOT_IS_START;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESHED_HOME;
import static sg.com.fuzzie.android.utils.Constants.JACKPOT_DRAW_TIME_INTERVAL;

/**
 * Created by joma on 10/13/17.
 */

@SuppressWarnings("ALL")
@EActivity(R.layout.activity_jackpot_home)
public class JackpotHomeActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener, FZTimer.OnTaskRunListener{

    public static int REQUEST_SORT =            0;
    public static int REQUEST_REFINE =          1;

    BroadcastReceiver broadcastReceiver;

    @ViewById(R.id.app_bar_layout)
    AppBarLayout appBarLayout;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.llHeader)
    View llHeader;

    @ViewById(R.id.tvHeader)
    TextView tvHeader;

    @ViewById(R.id.tvBody)
    TextView tvBody;

    @ViewById(R.id.tvDays)
    TextView tvDays;

    @ViewById(R.id.tvHours)
    TextView tvHours;

    @ViewById(R.id.tvMins)
    TextView tvMins;

    @ViewById(R.id.tvSec)
    TextView tvSec;

    @ViewById(R.id.tvSortBy)
    TextView tvSortBy;

    @ViewById(R.id.tvRefine)
    TextView tvRefine;

    @ViewById(R.id.tvCount)
    TextView tvCount;

    @ViewById(R.id.rvCoupon)
    RecyclerView rvCoupon;

    @ViewById(R.id.llDrawTime)
    View llDrawTime;

    @ViewById(R.id.cvLive)
    CardView cvLive;

    @ViewById(R.id.scrollView)
    NestedScrollView scrollView;

    @ViewById(R.id.tvPurchasedCount)
    TextView tvPurchasedCount;

    @ViewById(R.id.tvWeeklyLimit)
    TextView tvWeeklyLimit;

    @ViewById(R.id.llPowerUpPack)
    View llPowerUpPack;

    FZTimer timer;

    @Bean
    FuzzieData dataManager;

    @Bean
    CurrentUser currentUser;

    Home home;

    FastItemAdapter adapter;
    List<Coupon> coupons;
    List<Coupon> sortedCoupons;
    List<Coupon> filteredCoupons;

    LinearLayoutManager layoutManager;

    @AfterViews
    public void calledAfterViewInjection() {

        home = dataManager.getHome();

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appBarLayout.addOnOffsetChangedListener(this);

        String htmlText = "<font color='#FA3E3F'>WIN BIG. </font><font color='#FFFFFF'>NEVER LOSE MONEY, EVER</font>";
        tvHeader.setText(FuzzieUtils.fromHtml(htmlText));

        String first = "Win S$150,000 cash prizes every Wed & Sat, 6.35pm.";
        String second = " Jackpot tickets are given FREE with every purchase below.";
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Lato_Black.ttf");
        TypefaceSpan typefaceSpan = new CustomTypefaceSpan("", typeface);
        SpannableString spannableString  = new SpannableString(first + second);
        spannableString.setSpan(typefaceSpan, 0, first.length() , 0);
        tvBody.setText(spannableString);

        showPowerUpBanner();

        layoutManager = new LinearLayoutManager(this);
        adapter = new FastItemAdapter<CouponItem>();
        rvCoupon.setLayoutManager(layoutManager);
        rvCoupon.setAdapter(adapter);

        timer = new FZTimer();
        timer.setInterval(JACKPOT_DRAW_TIME_INTERVAL);
        timer.setOnTaskRunListener(this);

        dataManager.selectedBrandsSubCategoryIds = new TreeSet<>();
        dataManager.selectedSortByIndex = 0;

        if (dataManager.getCoupons() == null){
            loadCouponTemplates();
        } else {
            coupons = dataManager.getCoupons();
            showCoupons(coupons);
        }

        int purchasedTicketsCount  = 0;
        if (FuzzieUtils.isCuttingOffLiveDraw(context)){
            purchasedTicketsCount = currentUser.getCurrentUser().getNextJackpotTicketsCount();
        } else {
            purchasedTicketsCount = currentUser.getCurrentUser().getCurrentJackpotTicketsCount();
        }
        int numberOfTicketsPerWeek = 10;
        if (dataManager.getHome() != null){
            numberOfTicketsPerWeek = dataManager.getHome().getJackpot().getNumberOfTicketsPerWeek();
        }

        String htmlString = "";
        if (purchasedTicketsCount > 1){
            htmlString = "<font color='#FFFFFF'>YOU'VE GOT </font><font color='#F8C736'>" + purchasedTicketsCount +
                    "/" + numberOfTicketsPerWeek + " JACKPOT TICKETS </font><font color='#FFFFFF'>FOR THE NEXT DRAW</font>";
        } else {
            htmlString = "<font color='#FFFFFF'>YOU'VE GOT </font><font color='#F8C736'>" + purchasedTicketsCount +
                    "/" + numberOfTicketsPerWeek + " JACKPOT TICKET </font><font color='#FFFFFF'>FOR THE NEXT DRAW</font>";
        }
        tvPurchasedCount.setText(FuzzieUtils.fromHtml(htmlString));
        tvWeeklyLimit.setText("You are entitled to " + numberOfTicketsPerWeek + " Jackpot tickets per week");


        updateLiveDrawState();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(BROADCAST_REFRESHED_HOME)){
//                    Timber.e(TAG, "Home Refreshed");
                    home = dataManager.getHome();
                    updateLiveDrawState();
                } else if(intent.getAction().equals(BROADCAST_JACKPOT_IS_END)){
                    if (home != null){
                        updateLiveDrawState();
                    }

                } else if(intent.getAction().equals(BROADCAST_JACKPOT_IS_START)){
                    if (home != null){
                        home.getJackpot().setLive(true);
                        updateLiveDrawState();
                    }

                }
            }
        };

        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_REFRESHED_HOME));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_JACKPOT_IS_END));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_JACKPOT_IS_START));
    }

    private void showPowerUpBanner(){

        if (dataManager.getPowerUpPacks() != null && dataManager.getPowerUpPacks().size() > 0){
            llPowerUpPack.setVisibility(View.VISIBLE);
        } else {
            llPowerUpPack.setVisibility(View.GONE);
        }
    }

    private void updateLiveDrawState(){
        if (home != null && home.getJackpot() != null && home.getJackpot().isLive()){
            llDrawTime.setVisibility(View.GONE);
            cvLive.setVisibility(View.VISIBLE);
        } else {
            llDrawTime.setVisibility(View.VISIBLE);
            cvLive.setVisibility(View.GONE);
        }
    }

    private void updateSortBy(){
        tvSortBy.setText(FuzzieUtils.SORT_BY_JACKPOT[dataManager.selectedSortByIndex]);
        tvSortBy.requestLayout();

        sort();
    }

    private void sort(){

        switch (dataManager.selectedSortByIndex){
            case 0:
                sortedCoupons = new ArrayList<>(coupons);
                break;
            case 1:
                Collections.sort(sortedCoupons, new Comparator<Coupon>() {
                    @Override
                    public int compare(Coupon o1, Coupon o2) {
                        return Float.compare(Float.valueOf(o2.getPrice().getValue()), Float.valueOf(o1.getPrice().getValue()));
                    }
                });
                break;
            case 2:
                Collections.sort(sortedCoupons, new Comparator<Coupon>() {
                    @Override
                    public int compare(Coupon o1, Coupon o2) {
                        return Float.compare(Float.valueOf(o1.getPrice().getValue()), Float.valueOf(o2.getPrice().getValue()));
                    }
                });
                break;
            case 3:
                Collections.sort(sortedCoupons, new Comparator<Coupon>() {
                    @Override
                    public int compare(Coupon o1, Coupon o2) {
                        return Double.compare(o1.getPricePerTicket(), o2.getPricePerTicket());
                    }
                });
                break;
            case 4:
                Collections.sort(sortedCoupons, new Comparator<Coupon>() {
                    @Override
                    public int compare(Coupon o1, Coupon o2) {
                        return Integer.compare(o2.getTicketCount(), o1.getTicketCount());
                    }
                });
                break;
            case 5:
                Collections.sort(sortedCoupons, new Comparator<Coupon>() {
                    @Override
                    public int compare(Coupon o1, Coupon o2) {
                        return Double.compare(o2.getCashBack().getPercentage(), o1.getCashBack().getPercentage());
                    }
                });
                break;
            case 6:
                Collections.sort(sortedCoupons, new Comparator<Coupon>() {
                    @Override
                    public int compare(Coupon o1, Coupon o2) {
                        return o1.getBrandName().compareToIgnoreCase(o2.getBrandName());
                    }
                });
                break;
        }

        updateRefine();
    }

    private void updateRefine(){
        if (dataManager.selectedBrandsSubCategoryIds.size() == 0){
            tvCount.setVisibility(View.GONE);
            filteredCoupons = new ArrayList<>(sortedCoupons);
            tvRefine.setText("All");
        } else {
            tvCount.setText(String.valueOf(dataManager.selectedBrandsSubCategoryIds.size()));
            tvCount.setVisibility(View.VISIBLE);
            tvRefine.setText("Subcategories");

            if (filteredCoupons == null){
                filteredCoupons = new ArrayList<>();
            } else {
                filteredCoupons.clear();
            }

            for (int categoryId : dataManager.selectedBrandsSubCategoryIds){
                for (Coupon coupon : sortedCoupons){
                    if (coupon.getSubCategoryId() == categoryId){
                        filteredCoupons.add(coupon);
                    }
                }
            }
        }

        switch (dataManager.selectedSortByIndex){
            case 0:
                break;
            case 1:
                Collections.sort(filteredCoupons, new Comparator<Coupon>() {
                    @Override
                    public int compare(Coupon o1, Coupon o2) {
                        return Float.compare(Float.valueOf(o2.getPrice().getValue()), Float.valueOf(o1.getPrice().getValue()));
                    }
                });
                break;
            case 2:
                Collections.sort(filteredCoupons, new Comparator<Coupon>() {
                    @Override
                    public int compare(Coupon o1, Coupon o2) {
                        return Float.compare(Float.valueOf(o1.getPrice().getValue()), Float.valueOf(o2.getPrice().getValue()));
                    }
                });
                break;
            case 3:
                Collections.sort(filteredCoupons, new Comparator<Coupon>() {
                    @Override
                    public int compare(Coupon o1, Coupon o2) {
                        return Double.compare(o1.getPricePerTicket(), o2.getPricePerTicket());
                    }
                });
                break;
            case 4:
                Collections.sort(filteredCoupons, new Comparator<Coupon>() {
                    @Override
                    public int compare(Coupon o1, Coupon o2) {
                        return Integer.compare(o2.getTicketCount(), o1.getTicketCount());
                    }
                });
                break;
            case 5:
                Collections.sort(filteredCoupons, new Comparator<Coupon>() {
                    @Override
                    public int compare(Coupon o1, Coupon o2) {
                        return Double.compare(o2.getCashBack().getCashBackPercentage(), o1.getCashBack().getCashBackPercentage());
                    }
                });
                break;
            case 6:
                Collections.sort(filteredCoupons, new Comparator<Coupon>() {
                    @Override
                    public int compare(Coupon o1, Coupon o2) {
                        return o1.getBrandName().compareToIgnoreCase(o2.getBrandName());
                    }
                });
                break;
        }

        if (adapter == null){
            adapter = new FastItemAdapter<CouponItem>();
        } else {
            adapter.clear();
        }

        for (Coupon coupon : filteredCoupons){
            adapter.add(new CouponItem(coupon));
        }
    }

    private void loadCouponTemplates(){
        showLoader();
        Call<CouponTemplate> call = FuzzieAPI.APIService().getCouponTemplates(currentUser.getAccesstoken());
        call.enqueue(new Callback<CouponTemplate>() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onResponse(@NonNull Call<CouponTemplate> call, @NonNull Response<CouponTemplate> response) {
                hideLoader();
                if (response.code() == 200 && response.body() != null){
                    fetchCoupons(response.body());

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager, JackpotHomeActivity.this);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CouponTemplate> call, @NonNull Throwable t) {
                hideLoader();
                Timber.e(t.getLocalizedMessage());
            }
        });
    }

    private void fetchCoupons(CouponTemplate couponTemplate){

        List<Coupon> templates = new ArrayList<Coupon>();
        for (Coupon coupon : couponTemplate.getCoupons()){
            if (coupon.getPowerUpPack() != null){

                coupon.setBrandName("Fuzzie");
                coupon.setSubCategoryId(32);

            } else {

                Brand brand = dataManager.getBrandById(coupon.getBrandId());
                if (brand != null){
                    coupon.setBrandName(brand.getName());
                    coupon.setSubCategoryId(brand.getSubCategoryId());
                }

            }

            templates.add(coupon);
        }

        dataManager.setCoupons(templates);
        coupons = templates;

        showPowerUpBanner();
        showCoupons(coupons);
    }

    @SuppressLint("SetTextI18n")
    private void showCoupons(List<Coupon> coupons){

        updateSortBy();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (home != null && home.getJackpot() != null){
            Jackpot jackpot = home.getJackpot();
            if (jackpot.getDrawTime() != null && !jackpot.getDrawTime().equals("")){
                timer.startTimer();
                Timber.e("Timer is running...");
            }

            DateTimeFormatter parser = ISODateTimeFormat.dateTime();
            DateTime now = new DateTime();
            DateTime drawTime = parser.parseDateTime(jackpot.getDrawTime());

            long leftTime = drawTime.getMillis() - now.getMillis();
            if (leftTime <= 0){
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_JACKPOT_IS_START));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null){
            timer.stopTimer();
            Timber.e("Timer is stopped...");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.ivShare)
    void shareButtonClicked(){
        ShareCompat.IntentBuilder.from(this) .setType("text/plain") .setText("Check out The Fuzzie Jackpot! You can win S$150,000 cash prizes EVERY WEEK with your own 4D number. Get free Jackpot tickets simply by buying their e-vouchers from popular brands. https://fuzzie.com.sg/jackpot").setSubject("The Fuzzie Jackpot").startChooser();

    }

    @Click(R.id.llLearnMore)
    void learnMoreButtonClicked(){
        JackpotLearnMoreActvity_.intent(context).start();
    }

    @Click(R.id.llSort)
    void sortButtonClicked(){
        BrandListSortActivity_.intent(context).sortType(1).startForResult(REQUEST_SORT);
    }

    @Click(R.id.llRefine)
    void refineButtonClicked(){
        BrandListRefineActivity_.intent(context).titleExtra("REFINE").refineType(1).startForResult(REQUEST_REFINE);
    }

    @Click(R.id.cvLive)
    void liveButtonClicked(){
        JackpotLiveActivity_.intent(context).start();
    }

    @Click(R.id.llPowerUpPack)
    void powerUpPackButtonClicked(){

        if (dataManager.getPowerUpPacks() != null && dataManager.getPowerUpPacks().size() > 0){

            if (dataManager.getPowerUpPacks().size() == 1){

                Coupon coupon = dataManager.getPowerUpPacks().get(0);
                PowerUpCouponActivity_.intent(context).couponId(coupon.getId()).start();

            } else {

                PowerUpPackActivity_.intent(context).start();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            if (requestCode == REQUEST_SORT){
                updateSortBy();
            } else if (requestCode == REQUEST_REFINE){
                updateRefine();
            }
        }

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//        Timber.e(String.valueOf(verticalOffset));
        int height = - llHeader.getHeight() + 240;
        if (verticalOffset <= height){
            tvTitle.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTaskRun(long past_time, String rendered_time) {

        if (home != null){
            DateTimeFormatter parser = ISODateTimeFormat.dateTime();
            DateTime now = new DateTime();
            DateTime drawTime = parser.parseDateTime(home.getJackpot().getDrawTime());

            long leftTime = drawTime.getMillis() - now.getMillis();
            if (leftTime > 0){
                int seconds = (int)(leftTime / 1000) % 60;
                int mins = (int) (leftTime / 1000 / 60) % 60;
                int hours = (int) (leftTime / 1000 / 60 / 60) % 24;
                int days = (int) (leftTime / 1000 / 60 / 60 / 24);

                if (seconds < 10){
                    tvSec.setText("0" + seconds);
                } else {
                    tvSec.setText(String.valueOf(seconds));
                }

                if (mins < 10){
                    tvMins.setText("0" + mins);
                } else {
                    tvMins.setText(String.valueOf(mins));
                }

                if (hours < 10){
                    tvHours.setText("0" + hours);
                } else {
                    tvHours.setText(String.valueOf(hours));
                }

                if (days < 10){
                    tvDays.setText("0" + days);
                } else {
                    tvDays.setText(String.valueOf(days));
                }
            }
        }

    }
}
