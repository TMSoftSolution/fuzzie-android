package sg.com.fuzzie.android.ui.me;

/**
 * Created by nurimanizam on 14/12/16.
 */

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import sg.com.fuzzie.android.ui.redpacket.RedPacketActivity_;
import sg.com.fuzzie.android.utils.FuzzieUtils;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import sg.com.fuzzie.android.MainActivity;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.WebActivity_;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Service;
import sg.com.fuzzie.android.core.BaseFragment;
import sg.com.fuzzie.android.items.brand.BrandListItem;
import sg.com.fuzzie.android.ui.activate.CodeActivity_;
import sg.com.fuzzie.android.ui.me.history.OrderHistoryActivity_;
import sg.com.fuzzie.android.ui.me.profile.EditProfileActivity_;
import sg.com.fuzzie.android.ui.me.profile.EditProfilePicActivity_;
import sg.com.fuzzie.android.ui.payments.PaymentMethodsActivity_;
import sg.com.fuzzie.android.ui.shop.BrandGiftActivity_;
import sg.com.fuzzie.android.ui.shop.BrandServiceActivity_;
import sg.com.fuzzie.android.ui.shop.BrandServiceListActivity_;
import sg.com.fuzzie.android.ui.shop.topup.TopUpActivity_;
import sg.com.fuzzie.android.utils.Constants;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESHED_HOME;
import static sg.com.fuzzie.android.utils.Constants.REQUEST_TOP_UP_PAYMENT;

@EFragment(R.layout.fragment_me)
public class MeFragment extends BaseFragment {

    BroadcastReceiver broadcastReceiver;

    @FragmentArg
    boolean fromLike = false;

    @FragmentArg
    boolean selectWishList = false;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.profileImageView)
    CircleImageView profileImageView;

    @ViewById(R.id.nameView)
    TextView nameView;

    @ViewById(R.id.tvBirth)
    TextView tvBirth;

    @ViewById(R.id.tvCredits)
    TextView tvCredits;

    @ViewById(R.id.tvCashback)
    TextView tvCashback;

    @ViewById(R.id.ns_general)
    NestedScrollView nsGeneral;

    @ViewById(R.id.fl_me)
    FrameLayout flMe;

    @ViewById(R.id.rvBrands)
    RecyclerView rvBrands;

    @ViewById(R.id.tab_layout)
    TabLayout tabLayout;

    @ViewById(R.id.fm_empty_likedlist)
    View fmEmptyLikedView;
    @ViewById(R.id.tv_like_empty_description)
    TextView tvEmptyLiked;
    @ViewById(R.id.tv_like_empty_title)
    TextView tvEmptyLikedTitle;

    @ViewById(R.id.fm_empty_wishlist)
    View fmEmptyWishedView;
    @ViewById(R.id.tv_empty_description)
    TextView tvEmptyWished;
    @ViewById(R.id.tv_empty_title)
    TextView tvEmptyWishedTitle;

    List<Brand> brands;
    FastItemAdapter<BrandListItem> brandsAdapter;
    ArrayList<BrandListItem> likedData;
    ArrayList<BrandListItem> wishedData;

    int selectedTab;

    public static final int TAB_GENERAL = 0;
    public static final int TAB_LIKED = 1;
    public static final int TAB_WISHED = 2;

    @AfterViews
    public void calledAfterViewInjection() {

        if (currentUser.getCurrentUser() == null) return;

        setupTabHandler();
        setupAdapter();

        if (fromLike){
            if (tabLayout != null){
                tabLayout.getTabAt(TAB_LIKED).select();
                tabChanged(TAB_LIKED);
            }
        }

        if (selectWishList){
            if (tabLayout != null){
                tabLayout.getTabAt(TAB_WISHED).select();
                tabChanged(TAB_WISHED);
            }
        }

        getLikedList();
        getWishList();
        setupBroadcastReceiver();

    }

    private void setupBroadcastReceiver(){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BROADCAST_LIKE_ADDED_IN_BRAND) || intent.getAction().equals(Constants.BROADCAST_LIKE_REMOVED_IN_BRAND)){
                    getLikedList();
                }
                if (intent.getAction().equals(Constants.BROADCAST_ADD_OR_REMOVE_WISHLIST_TO_BRAND)){
                    getWishList();
                }
                if (intent.getAction().equals(BROADCAST_REFRESHED_HOME)){
                    if (selectWishList){
                        getWishList();
                        getLikedList();
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_LIKE_ADDED_IN_BRAND));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_LIKE_REMOVED_IN_BRAND));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_ADD_OR_REMOVE_WISHLIST_TO_BRAND));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_REFRESHED_HOME));

    }

    private void setupPersonalInfo(){
        if (currentUser.getCurrentUser().getProfileImage() != null && !currentUser.getCurrentUser().getProfileImage().equals("")) {
            Picasso.get()
                    .load(currentUser.getCurrentUser().getProfileImage())
                    .placeholder(R.drawable.profile_image_placeholder)
                    .fit()
                    .into(profileImageView);
        } else {
            Picasso.get()
                    .load(R.drawable.profile_image_placeholder)
                    .fit()
                    .into(profileImageView);
        }

        nameView.setText(currentUser.getCurrentUser().getName());

        if (currentUser.getCurrentUser().getBirthdate_formatted() != null && !currentUser.getCurrentUser().getBirthdate_formatted().equals("")) {
            tvBirth.setVisibility(View.VISIBLE);
            tvBirth.setText("Born " + currentUser.getCurrentUser().getBirthdate_formatted());
        } else {
            tvBirth.setVisibility(View.GONE);
            tvBirth.setText("");
        }

        tvCredits.setText(FuzzieUtils.getFormattedValue(context, currentUser.getCurrentUser().getWallet().getBalance(), 0.56f));
        tvCashback.setText(FuzzieUtils.getFormattedValue(context, currentUser.getCurrentUser().getCashbackEarned(), 0.56f));

        if (tvEmptyLikedTitle != null){
            tvEmptyLikedTitle.setText(getString(R.string.txt_empty_likelist_title));
        }
        if(tvEmptyLiked != null) {
            tvEmptyLiked.setText(getString(R.string.txt_empty_likelst_instruction));
        }
        if (tvEmptyWishedTitle != null){
            tvEmptyWishedTitle.setText(getString(R.string.txt_empty_wishlist_title));
        }
        if(tvEmptyWished != null) {
            tvEmptyWished.setText(getString(R.string.txt_empty_wishlist_instruction));
        }
    }

    private void setupTabHandler(){
        selectedTab = TAB_GENERAL;
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
    }

    @UiThread
    protected void tabChanged(int newPosition){
        if (newPosition == selectedTab) return;

        selectedTab = newPosition;

        switch (selectedTab){
            case TAB_GENERAL:
                nsGeneral.setVisibility(View.VISIBLE);
                flMe.setVisibility(View.GONE);
                break;
            case TAB_LIKED:
                nsGeneral.setVisibility(View.GONE);
                flMe.setVisibility(View.VISIBLE);
                setDataEmpty(likedData.size() == 0);
                updateAdapter(likedData);
                break;
            case TAB_WISHED:
                nsGeneral.setVisibility(View.GONE);
                flMe.setVisibility(View.VISIBLE);
                setDataEmpty(wishedData.size() == 0);
                updateAdapter(wishedData);
                break;
            default:
                break;
        }

    }

    private void getLikedList(){

        if (dataManager.getHome() == null || dataManager.getHome().getBrands() == null || currentUser.getCurrentUser() == null) return;

        if(likedData == null) {

            likedData = new ArrayList<>();

        } else {

            likedData.clear();
        }

        brands = dataManager.getHome().getBrands();

        List<String> brandIds = currentUser.getCurrentUser().getLikedListIds();
        for (String brandId : brandIds){
            for (Brand brand : brands){
                if (brand.getId().equals(brandId)){
                    likedData.add(new BrandListItem(brand));
                    break;
                }
            }
        }

        if (selectedTab == TAB_LIKED){
            brandsAdapter.clear();
            setDataEmpty(likedData.size() == 0);
            updateAdapter(likedData);
        }
    }

    private void getWishList(){

        if (dataManager.getHome() == null || dataManager.getHome().getBrands() == null || currentUser.getCurrentUser() == null) return;

        if(wishedData == null) {

            wishedData = new ArrayList<>();

        } else {

            wishedData.clear();

        }

        brands = dataManager.getHome().getBrands();

        List<String> brandIds = currentUser.getCurrentUser().getWishListIds();
        for (String brandId : brandIds){
            for (Brand brand : brands){
                if (brand.getId().equals(brandId)){
                    wishedData.add(new BrandListItem(brand));
                    break;
                }
            }
        }

        if (selectedTab == TAB_WISHED){
            brandsAdapter.clear();
            setDataEmpty(wishedData.size() == 0);
            updateAdapter(wishedData);
        }
    }

    private void setupAdapter(){
        brandsAdapter = new FastItemAdapter<>();
        brandsAdapter.withOnClickListener(new OnClickListener<BrandListItem>() {
            @Override
            public boolean onClick(View v, IAdapter<BrandListItem> adapter, BrandListItem item, int position) {
                Brand brand = item.getBrand();

                if (brand.getServices() != null && brand.getServices().size() > 0) {
                    if (brand.getServices().size() == 1 && (brand.getGiftCards() == null || brand.getGiftCards().size() == 0) ) {
                        BrandServiceActivity_.intent(context).brandId(brand.getId()).serviceExtra(Service.toJSON(brand.getServices().get(0))).start();
                    } else {
                        BrandServiceListActivity_.intent(context).brandId(brand.getId()).start();
                    }
                } else if (brand.getGiftCards() != null && brand.getGiftCards().size() > 0) {
                    BrandGiftActivity_.intent(context).brandId(brand.getId()).start();
                }
                return true;
            }
        });

        rvBrands.setLayoutManager(new LinearLayoutManager(context));
        rvBrands.setAdapter(brandsAdapter);

    }

    private void updateAdapter(ArrayList<BrandListItem> data) {
        brandsAdapter.clear();
        for (BrandListItem item: data) {
            brandsAdapter.add(item);
        }
        brandsAdapter.notifyAdapterDataSetChanged();
    }

    protected void setDataEmpty(boolean isEmpty) {
        if (isEmpty) {
            if (rvBrands != null)
                rvBrands.setVisibility(View.INVISIBLE);
            if (selectedTab == TAB_LIKED) {
                if (fmEmptyLikedView != null)
                    fmEmptyLikedView.setVisibility(View.VISIBLE);
                if (fmEmptyWishedView != null)
                    fmEmptyWishedView.setVisibility(View.GONE);
            } else if (selectedTab == TAB_WISHED){
                if (fmEmptyLikedView != null)
                    fmEmptyLikedView.setVisibility(View.GONE);
                if (fmEmptyWishedView != null)
                    fmEmptyWishedView.setVisibility(View.VISIBLE);
            }
        } else {
            if (rvBrands != null)
                rvBrands.setVisibility(View.VISIBLE);
            if (fmEmptyLikedView != null)
                fmEmptyLikedView.setVisibility(View.GONE);
            if (fmEmptyWishedView != null)
                fmEmptyWishedView.setVisibility(View.GONE);
        }
    }

    @Click(R.id.profileImageView)
    void avatarButtonClicked(){
        EditProfilePicActivity_.intent(context).start();
    }

    @Click(R.id.llHelp)
    void helpButtonClicked() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.me_help_menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:
                        emailSupport();
                        break;
                    case 1:
                        facebookSupport();
                        break;

                    default:
                        break;
                }
            }
        }).show();

    }

    void emailSupport() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","support@fuzzie.com.sg", null));
        startActivity(Intent.createChooser(emailIntent, "Email support@fuzzie.com.sg"));
    }

    void facebookSupport() {
        String url = "http://m.me/fuzzieapp";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Click(R.id.llCash)
    void cashButtonClicked(){
//        RateExperienceActivity_.intent(context).start();
    }

    @Click(R.id.llTopUp)
    void topUpButtonClicked(){
        TopUpActivity_.intent(context).startForResult(REQUEST_TOP_UP_PAYMENT);
    }

    @Click(R.id.llRedPacket)
    void redPacketButtonClicked(){
        RedPacketActivity_.intent(context).start();
    }

    @Click(R.id.llFriends)
    void myFriendsButtonClicked(){
        MyFriendsListActivity_.intent(context).start();
    }

    @Click(R.id.llCode)
    void fuzzieCodeButtonClicked(){
        CodeActivity_.intent(context).start();
    }

    @Click(R.id.llFAQ)
    void FAQButtonClicked() {
        WebActivity_.intent(this).titleExtra("FAQ").urlExtra("http://fuzzie.com.sg/faq.html").start();
    }

    @Click(R.id.llSettings)
    void settingsButtonClicked() {
        SettingsActivity_.intent(context).start();
    }

    @Click(R.id.llInvite)
    void inviteFriendsButtonClicked() {
        ReferralActivity_.intent(context).start();
    }

    @Click(R.id.llManageCards)
    void manageCreditCards() {
        PaymentMethodsActivity_.intent(context).fromPaymentPage(false).start();
    }

    @Click(R.id.llHistory)
    void historyButtonClicked(){
        OrderHistoryActivity_.intent(context).start();
    }

    @Click(R.id.tvEdit)
    void editButtonClicked(){
        EditProfileActivity_.intent(context).start();
    }


    @Override
    public void onResume() {
        super.onResume();
        setupPersonalInfo();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onPause() {
        super.onPause();
        fromLike = false;
        selectWishList = false;
        ((MainActivity)mActivity).fromLike = false;
        ((MainActivity)mActivity).selectWishList = false;
    }

    // FZAlertDialogListner


    @Override
    public void FZAlertOkButtonClicked() {
        hideFZAlert();
    }

    @Override
    public void FZAlertCancelButtonClicked() {
        hideFZAlert();
    }
}
