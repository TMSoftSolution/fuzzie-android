package sg.com.fuzzie.android.ui.likes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Service;
import sg.com.fuzzie.android.api.models.User;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.brand.BrandListItem;
import sg.com.fuzzie.android.items.me.HeaderItem;
import sg.com.fuzzie.android.ui.shop.BrandGiftActivity_;
import sg.com.fuzzie.android.ui.shop.BrandServiceActivity_;
import sg.com.fuzzie.android.ui.shop.BrandServiceListActivity_;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESHED_HOME;

/**
 * Created by mac on 5/17/17.
 */

@EActivity(R.layout.activity_liker_profile)
public class LikerProfileActivity extends BaseActivity implements ObservableScrollViewCallbacks{

    BroadcastReceiver broadcastReceiver;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    User user;
    List<Brand> brands;

    @Extra
    String userExtra;

    @Extra
    String userIdExtra;

    @Extra
    boolean selectWishList;

    FastAdapter fastAdapter;
    ItemAdapter<HeaderItem> headerAdapter;
    ItemAdapter<BrandListItem> brandsAdapter;
    ArrayList<BrandListItem> likedData;
    ArrayList<BrandListItem> wishedData;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.tvBirth)
    TextView tvBirth;

    @ViewById(R.id.tvName)
    TextView tvName;

    @ViewById(R.id.avatar)
    CircleImageView avatar;

    @ViewById(R.id.tlTabMenu)
    TabLayout tabLayout;

    @ViewById(R.id.tiLiked)
    TabItem tiLiked;

    @ViewById(R.id.tiWhished)
    TabItem tiWhished;

    @ViewById(R.id.rvBrands)
    ObservableRecyclerView rvBrands;

    @ViewById(R.id.fm_empty_likedlist)
    View fmEmptyLikedView;

    @ViewById(R.id.fm_empty_wishlist)
    View fmEmptyWishedView;

    @ViewById(R.id.loader)
    View loader;

    @ViewById(R.id.header)
    View mHeader;

    @ViewById(R.id.rrLikes)
    View mLikes;

    private int mBaseTranslationY;

    int selectedTab;

    private static final int TAB_LIKED = 0;
    private static final int TAB_WISHED = 1;

    private boolean loadedLike          = false;
    private boolean loadedWish         = false;

    @AfterViews
    public void calledAfterViewInjection() {

        setupLayout();
        setupAdapter();
        setupTabHandler();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BROADCAST_REFRESHED_HOME)){
                    if (selectWishList){
                        getUserInfo();
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_REFRESHED_HOME));

        if (dataManager.getHome() == null || dataManager.getHome().getBrands() == null) return;

        if(userExtra != null) {
            user = User.fromJSON(userExtra);
            userIdExtra = user.getId();
        } else if (userIdExtra != null){
            getUserInfo();
        } else {
            return;
        }

        if(user != null) {
            initSetup();
        }
    }

    private void initSetup(){
        setupUserInfo();
        getLikedList();
        getWishList();
    }

    private void setupLayout(){
        rvBrands.setScrollViewCallbacks(this);
        rvBrands.setLayoutManager(new LinearLayoutManager(this));
        rvBrands.setHasFixedSize(false);
    }

    private void setupUserInfo() {

        tvTitle.setText(user.getName());

        TypedArray avatarBear = getResources().obtainTypedArray(R.array.avatar_bears);
        if (user.getAvatar() != null && !user.getAvatar().equals("")){
            Picasso.get()
                    .load(user.getAvatar())
                    .placeholder(R.drawable.avatar_bear_1)
                    .into(this.avatar);
        } else {
            if (user.getAvatarBear() != null && user.getAvatarBear() != -1){
                Picasso.get()
                        .load(avatarBear.getResourceId(user.getAvatarBear(), 0))
                        .into(this.avatar);
            } else {
                Picasso.get()
                        .load(R.drawable.avatar_bear_1)
                        .into(this.avatar);
            }
        }
        avatarBear.recycle();

        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        if (user.getBirthdate() != null && !user.getBirthdate().equals("")){
            tvBirth.setVisibility(View.VISIBLE);
            try {
                Date dateObj = dateFormater.parse(user.getBirthdate());
                String birth = "Born " + new SimpleDateFormat("dd MMM").format(dateObj);
                tvBirth.setText(birth);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else {
            tvBirth.setVisibility(View.GONE);
            tvBirth.setText("");
        }

        String formattedName = "";
        if (user.getFirstName() != null && user.getFirstName().length() > 0){
            if (user.getFirstName().length() == 1){
                formattedName += user.getFirstName().toUpperCase();
            } else {
                formattedName += user.getFirstName().substring(0,1).toUpperCase() + user.getFirstName().substring(1);
            }
        }

        if (formattedName.length() != 0){
            formattedName += " ";
        }
        if (user.getLastName() != null && user.getLastName().length() > 0){
            if (user.getLastName().length() == 1){
                formattedName += user.getLastName().toUpperCase();
            } else {
                formattedName += user.getLastName().substring(0,1).toUpperCase() + user.getLastName().substring(1);
            }
        }
        tvName.setText(formattedName);
    }

    private void setupAdapter() {

        headerAdapter = new ItemAdapter<>();
        brandsAdapter = new ItemAdapter<>();

        fastAdapter = FastAdapter.with(Arrays.asList(headerAdapter, brandsAdapter));
        fastAdapter.withSelectable(true);
        fastAdapter.withOnClickListener(new OnClickListener<BrandListItem>() {
            @Override
            public boolean onClick(View v, IAdapter<BrandListItem> adapter, BrandListItem item, int position) {

                Brand brand = item.getBrand();

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

                return true;
            }
        });

        rvBrands.setAdapter(fastAdapter);
        headerAdapter.add(new HeaderItem());
    }

    private void setupTabHandler() {
        if (selectWishList){
            selectedTab = TAB_WISHED;
        } else {
            selectedTab = TAB_LIKED;
        }
        tabLayout.getTabAt(TAB_LIKED).setText("LIKED");
        tabLayout.getTabAt(TAB_WISHED).setText("WISHLISTED");
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabChanged(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.getTabAt(selectedTab).select();
        tabChanged(selectedTab);

    }

    private void tabChanged(int newPosition) {

        if (newPosition == selectedTab) { return; }

        selectedTab = newPosition;

        if(newPosition == TAB_LIKED) {
            if(likedData == null) { getLikedList(); } else {
                if (loadedLike){
                    setDataEmpty(likedData.size() == 0);
                    updateAdapter(likedData);
                }
                if (likedData.size() > 0){
                    tabLayout.getTabAt(TAB_LIKED).setText(String.format("LIKED (%d)", likedData.size()));
                } else {
                    tabLayout.getTabAt(TAB_LIKED).setText("LIKED");
                }

            }
        } else {
            if(wishedData == null) { getWishList(); } else {
                if (loadedWish){
                    setDataEmpty(wishedData.size() == 0);
                    updateAdapter(wishedData);
                }
                if (wishedData.size() > 0){
                    tabLayout.getTabAt(TAB_WISHED).setText(String.format("WISHLISTED (%d)", wishedData.size()));
                } else {
                    tabLayout.getTabAt(TAB_WISHED).setText("WISHLISTED");
                }

            }
        }
    }

    private void getLikedList() {

        if(likedData == null) { likedData = new ArrayList<>(); }
        brands = dataManager.getHome().getBrands();

        loader.setVisibility(View.VISIBLE);

        Call<List<String>> call = FuzzieAPI.APIService().getUserLikedBrands(currentUser.getAccesstoken(), userIdExtra);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {

                loadedLike = true;
                if (loadedWish){
                    loader.setVisibility(View.GONE);
                }

                if (response.code() == 200 && response.body() != null) {

                    if(selectedTab == TAB_LIKED) {
                        brandsAdapter.clear();
                    }
                    likedData.clear();

                    List<String> brandIds = response.body();
                    for (String brandId : brandIds){
                        for (Brand brand : brands){
                            if (brand.getId().equals(brandId)){
                                BrandListItem brandListItem = new BrandListItem(brand);
                                if(selectedTab == TAB_LIKED) {
                                    brandsAdapter.add(brandListItem);
                                }
                                likedData.add(brandListItem);
                                break;
                            }
                        }
                    }
                    if (likedData.size() > 0){
                        tabLayout.getTabAt(TAB_LIKED).setText(String.format("LIKED (%d)", likedData.size()));
                    } else {
                        tabLayout.getTabAt(TAB_LIKED).setText("LIKED");
                    }

                    if (selectedTab == TAB_LIKED){
                        setDataEmpty(likedData.size() == 0);
                        updateAdapter(likedData);
                    }
                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  LikerProfileActivity.this);
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                loader.setVisibility(View.GONE);
            }
        });
    }

    private void getWishList() {

        if(wishedData == null) { wishedData = new ArrayList<>(); }
        brands = dataManager.getHome().getBrands();

        loader.setVisibility(View.VISIBLE);

        Call<List<String>> call = FuzzieAPI.APIService().getUserWishlistedBrands(currentUser.getAccesstoken(), userIdExtra);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {

                loadedWish = true;
                if (loadedLike){
                    loader.setVisibility(View.GONE);
                }

                if (response.code() == 200 && response.body() != null) {

                    if(selectedTab == TAB_WISHED) {
                        brandsAdapter.clear();
                    }
                    wishedData.clear();

                    List<String> brandIds = response.body();
                    for (String brandId : brandIds){
                        for (Brand brand : brands){
                            if (brand.getId().equals(brandId)){
                                BrandListItem brandListItem = new BrandListItem(brand);
                                if(selectedTab == TAB_WISHED) {
                                    brandsAdapter.add(brandListItem);
                                }
                                wishedData.add(brandListItem);
                                break;
                            }
                        }
                    }

                    if (wishedData.size() > 0){
                        tabLayout.getTabAt(TAB_WISHED).setText(String.format("WISHLISTED (%d)", wishedData.size()));
                    } else {
                        tabLayout.getTabAt(TAB_WISHED).setText("WISHLISTED");
                    }

                    if (selectedTab == TAB_WISHED){
                        setDataEmpty(wishedData.size() == 0);
                        updateAdapter(wishedData);
                    }
                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  LikerProfileActivity.this);
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                loader.setVisibility(View.GONE);
            }
        });
    }

    private void getUserInfo(){
        Call<User> call = FuzzieAPI.APIService().getUserInfoWithId(currentUser.getAccesstoken(), userIdExtra);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200 && response.body() != null){
                    user = response.body();
                    initSetup();

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager, LikerProfileActivity.this);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    private void updateAdapter(ArrayList<BrandListItem> data) {
        brandsAdapter.clear();
        for (BrandListItem item: data) {
            brandsAdapter.add(item);
        }
    }

    private void setDataEmpty(boolean isEmpty) {
        if (isEmpty) {
            rvBrands.setVisibility(View.INVISIBLE);
            if (selectedTab == TAB_LIKED) {
                fmEmptyLikedView.setVisibility(View.VISIBLE);
                fmEmptyWishedView.setVisibility(View.GONE);
            } else {
                fmEmptyLikedView.setVisibility(View.GONE);
                fmEmptyWishedView.setVisibility(View.VISIBLE);
            }
        } else {
            rvBrands.setVisibility(View.VISIBLE);
            fmEmptyLikedView.setVisibility(View.GONE);
            fmEmptyWishedView.setVisibility(View.GONE);
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

        int mlikesHeight = mLikes.getHeight();

        if (firstScroll){
            float currentHeaderTranslationY = ViewHelper.getTranslationY(mHeader);
            if (-mlikesHeight < currentHeaderTranslationY) {
                mBaseTranslationY = scrollY;
            }
        }

        float headerTranslationY = ScrollUtils.getFloat(-(scrollY - mBaseTranslationY), -mlikesHeight, 0);
        ViewPropertyAnimator.animate(mHeader).cancel();
        ViewHelper.setTranslationY(mHeader, headerTranslationY);

    }

    @Override
    public void onDownMotionEvent() {
        mBaseTranslationY = 0;
        int mlikesHeight = mLikes.getHeight();
        int scrollY = rvBrands.getCurrentScrollY();
        if (mlikesHeight < scrollY) {
            hideHeader();
        } else {
            showHeader();
        }
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        mBaseTranslationY = 0;
        int mlikesHeight = mLikes.getHeight();
        int scrollY = rvBrands.getCurrentScrollY();
        if (mlikesHeight < scrollY) {
            hideHeader();
        } else {
            showHeader();
        }
    }

    private void showHeader() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeader);
        if (headerTranslationY != 0) {
            ViewPropertyAnimator.animate(mHeader).cancel();
            ViewPropertyAnimator.animate(mHeader).translationY(0).setDuration(0).start();
        }
    }

    private void hideHeader() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeader);
        int mLikeHeight = mLikes.getHeight();
        if (headerTranslationY != -mLikeHeight) {
            ViewPropertyAnimator.animate(mHeader).cancel();
            ViewPropertyAnimator.animate(mHeader).translationY(-mLikeHeight).setDuration(0).start();
        }
    }

    @Click(R.id.avatar)
    void avatarButtonClicked(){
        UserProfilePhotoActivity_.intent(context).userExtra(userExtra).start();
    }
}

