package sg.com.fuzzie.android.ui.giftbox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import sg.com.fuzzie.android.api.models.PayResponse;
import sg.com.fuzzie.android.items.gift.GiftBoxPowerUpCardItem;
import timber.log.Timber;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.JsonObject;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.Gift;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.gift.GiftBoxItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_ONLINE_ACTIVE_GIFT_REDEEMED;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_ONLINE_ACTIVE_GIFT_REDEEM_STATE_CHANGED;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESHED_GIFTBOX_ACTIVE;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESHED_GIFTBOX_SENT;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESHED_GIFTBOX_USED;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_UPDATED_SENT_GIFT;
import static sg.com.fuzzie.android.utils.Constants.GIFTBOX_OFFSET;
import static sg.com.fuzzie.android.utils.Constants.REQUEST_GIFT_MARK_AS_UNREDEEMED;

/**
 * Created by nurimanizam on 15/12/16.
 */

@EActivity(R.layout.activity_giftbox)
public class GiftBoxActivity extends BaseActivity {

    public static final int TAB_ACTIVE = 0;
    public static final int TAB_USED = 1;
    public static final int TAB_SENT = 2;

    FastItemAdapter giftAdapter;
    LinearLayoutManager layoutManager;
    List<Gift> selectedGifts;

    private boolean isLastActive;
    private boolean isLastUsed;
    private boolean isLastSent;

    private boolean loading;

    private int selectedGiftIndex;

    @Extra
    int selectedTab;

    BroadcastReceiver broadcastReceiver;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @ViewById(R.id.rvGiftBox)
    RecyclerView rvGiftBox;

    @ViewById(R.id.tab_layout)
    TabLayout tabLayout;

    @ViewById(R.id.rlgiftempty)
    View emptyView;

    @ViewById(R.id.llGiftBox)
    View giftBoxView;

    @ViewById(R.id.loadMore)
    ProgressBar loadMore;

    @AfterViews
    public void calledAfterViewInjection() {

        layoutManager = new LinearLayoutManager(context);

        giftAdapter = new FastItemAdapter<>();
        giftAdapter.withOnClickListener(new OnClickListener() {
            @Override
            public boolean onClick(View v, IAdapter adapter, IItem item, int position) {

                Gift gift = null;

                if (item instanceof GiftBoxItem){
                    gift = ((GiftBoxItem)item).getGift();
                } else if (item instanceof GiftBoxPowerUpCardItem){
                    gift = ((GiftBoxPowerUpCardItem)item).getGift();
                }

                if (gift != null){

                    if (gift.isOpened() != null && !gift.isOpened()){
                        if (gift.isGifted() != null && gift.isGifted()){
                            if (gift.isSent() != null && !gift.isSent()){
                                setMarkAsOpened(gift, position);
                            }
                        } else {
                            setMarkAsOpened(gift, position);
                        }

                    }

                    boolean used = gift.isUsed() || gift.isExpired();

                    if (gift.getType() != null && gift.getType().equals("PowerUpCodeGift")){

                        PowerUpGiftCardActivity_.intent(context).giftId(gift.getId()).used(used).start();

                    } else {

                        if (gift.isGifted() != null && gift.isGifted() && gift.isSent() != null && gift.isSent()){
                            if (gift.getGiftCard() != null){
                                SentGiftCardActivity_.intent(context).giftId(gift.getId()).start();
                            } else if (gift.getService() != null){
                                SentGiftPackageActivity_.intent(context).giftId(gift.getId()).start();
                            }
                        } else {

                            selectedGiftIndex = position;

                            if (gift.getGiftCard() != null){
                                GiftCardActivity_.intent(context).giftId(gift.getId()).used(used).startForResult(REQUEST_GIFT_MARK_AS_UNREDEEMED);
                            } else if (gift.getService() != null){
                                GiftPackageActivity_.intent(context).giftId(gift.getId()).used(used).startForResult(REQUEST_GIFT_MARK_AS_UNREDEEMED);
                            }

                        }
                    }
                }

                return true;
            }
        });

        rvGiftBox.setLayoutManager(layoutManager);
        rvGiftBox.setAdapter(giftAdapter);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loadFirstGifts();
            }
        });

        if (dataManager.getActiveGifts() == null) {
            loadFirstActiveGifts();
        } else {
            showGifts();
        }

        if (dataManager.getUsedGifts() == null){
            loadFirstUsedGifts();
        }

        if (dataManager.getSentGifts() == null){
            loadFirstSentGifts();
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Timber.d("Selected Tab: " + tab.getText());
                selectedTab = tab.getPosition();
                showGifts();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        if (tabLayout.getTabAt(selectedTab) != null){
            tabLayout.getTabAt(selectedTab).select();
        }


        rvGiftBox.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 1
                        && firstVisibleItemPosition >= 0) {

                    loadNextGifts();
                }
            }
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(BROADCAST_REFRESHED_GIFTBOX_ACTIVE)
                        || intent.getAction().equals(BROADCAST_REFRESHED_GIFTBOX_USED)
                        || intent.getAction().equals(BROADCAST_REFRESHED_GIFTBOX_SENT)
                        || intent.getAction().equals(BROADCAST_ONLINE_ACTIVE_GIFT_REDEEMED)
                        || intent.getAction().equals(BROADCAST_UPDATED_SENT_GIFT)){

                    showGifts();
                }

                if (intent.getAction().equals(BROADCAST_ONLINE_ACTIVE_GIFT_REDEEM_STATE_CHANGED)){
                    updateGiftRedeemState(selectedGiftIndex);
                }

            }
        };

        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_REFRESHED_GIFTBOX_ACTIVE));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_REFRESHED_GIFTBOX_USED));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_REFRESHED_GIFTBOX_SENT));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_ONLINE_ACTIVE_GIFT_REDEEM_STATE_CHANGED));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_ONLINE_ACTIVE_GIFT_REDEEMED));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_UPDATED_SENT_GIFT));

        if (currentUser.getCurrentUser().getUnOpenedGiftCount() > 0){
            currentUser.resetUnopenedGiftsCount();
            resetUnopenedGiftsCount();
        }

    }

    private void resetUnopenedGiftsCount(){

        FuzzieAPI.APIService().resetUnopenedGiftsCount(currentUser.getAccesstoken()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }


    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.cvRefresh)
    void onRefresh(){

        loadFirstGifts();
    }

    void showEmptyView(){

        if (emptyView != null)
            emptyView.setVisibility(View.VISIBLE);

        if (giftBoxView != null)
            giftBoxView.setVisibility(View.GONE);
    }

    void hideEmptyView(){

        if (emptyView != null)
            emptyView.setVisibility(View.GONE);

        if (giftBoxView != null)
            giftBoxView.setVisibility(View.VISIBLE);
    }

    private void setMarkAsOpened(Gift gift, int position){

        dataManager.setGiftAsOpened(gift.getId());
        giftAdapter.notifyAdapterItemChanged(position);

        Call<PayResponse> call = FuzzieAPI.APIService().markAsOpened(currentUser.getAccesstoken(), gift.getId());
        call.enqueue(new Callback<PayResponse>() {
            @Override
            public void onResponse(Call<PayResponse> call, Response<PayResponse> response) {

                if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  GiftBoxActivity.this);
                }
            }

            @Override
            public void onFailure(Call<PayResponse> call, Throwable t) {

            }
        });
    }

    private void showGifts(){

        switch (selectedTab){

            case TAB_ACTIVE:
                selectedGifts = dataManager.getActiveGifts();
                break;

            case TAB_USED:
                selectedGifts = dataManager.getUsedGifts();
                break;

            case TAB_SENT:
                selectedGifts = dataManager.getSentGifts();
                break;

            default:
                selectedGifts = dataManager.getActiveGifts();
                break;
        }

        if (giftAdapter == null){
            giftAdapter = new FastItemAdapter<GiftBoxItem>();
        } else {
            giftAdapter.clear();
        }

        if (selectedGifts != null && selectedGifts.size() != 0){
            hideEmptyView();
            for (Gift gift : selectedGifts) {
                if (gift.getType() != null && gift.getType().equals("PowerUpCodeGift")){
                    giftAdapter.add(new GiftBoxPowerUpCardItem(gift));
                } else {
                    giftAdapter.add(new GiftBoxItem(gift));
                }

            }
        } else {
            showEmptyView();
        }
    }

    private void addGifts(List<Gift> gifts){

        for (Gift gift : gifts){
            if (gift.getType() != null && gift.getType().equals("PowerUpCodeGift")){
                giftAdapter.add(new GiftBoxPowerUpCardItem(gift));
            } else {
                giftAdapter.add(new GiftBoxItem(gift));
            }
        }
    }

    private void loadFirstGifts(){

        switch (selectedTab){

            case TAB_ACTIVE:
                loadFirstActiveGifts();
                break;

            case TAB_USED:
                loadFirstUsedGifts();
                break;

            case TAB_SENT:
                loadFirstSentGifts();
                break;
        }
    }

    private void loadFirstActiveGifts(){

        if (swipeContainer != null) {
            swipeContainer.setRefreshing(true);
        }

        loading = true;
        isLastActive = false;

        Call<List<Gift>> call = FuzzieAPI.APIService().getActiveGifts(currentUser.getAccesstoken(), 1, GIFTBOX_OFFSET);
        call.enqueue(new Callback<List<Gift>>() {
            @Override
            public void onResponse(Call<List<Gift>> call, Response<List<Gift>> response) {

                if (swipeContainer != null) {
                    swipeContainer.setRefreshing(false);
                }

                loading = false;

                if (response.code() == 200 && response.body() != null){

                    isLastActive = response.body().size() % GIFTBOX_OFFSET != 0 || response.body().size() == 0;
                    dataManager.setActiveGifts(response.body());

                    Intent intent = new Intent(BROADCAST_REFRESHED_GIFTBOX_ACTIVE);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  GiftBoxActivity.this);
                }
            }

            @Override
            public void onFailure(Call<List<Gift>> call, Throwable t) {

                if (swipeContainer != null) {
                    swipeContainer.setRefreshing(false);
                }

                loading = false;

                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
            }
        });
    }

    private void loadFirstUsedGifts(){

        if (swipeContainer != null) {
            swipeContainer.setRefreshing(true);
        }

        loading = true;
        isLastSent = false;

        Call<List<Gift>> call = FuzzieAPI.APIService().getUsedGifts(currentUser.getAccesstoken(), 1, GIFTBOX_OFFSET);
        call.enqueue(new Callback<List<Gift>>() {
            @Override
            public void onResponse(Call<List<Gift>> call, Response<List<Gift>> response) {

                if (swipeContainer != null) {
                    swipeContainer.setRefreshing(false);
                }

                loading = false;

                if (response.code() == 200 && response.body() != null){

                    isLastUsed = response.body().size() % GIFTBOX_OFFSET != 0 || response.body().size() == 0;
                    dataManager.setUsedGifts(response.body());

                    Intent intent = new Intent(BROADCAST_REFRESHED_GIFTBOX_USED);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  GiftBoxActivity.this);
                }
            }

            @Override
            public void onFailure(Call<List<Gift>> call, Throwable t) {

                if (swipeContainer != null) {
                    swipeContainer.setRefreshing(false);
                }

                loading = false;

                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
            }
        });
    }

    private void loadFirstSentGifts(){

        if (swipeContainer != null) {
            swipeContainer.setRefreshing(true);
        }

        loading = true;
        isLastSent = false;

        Call<List<Gift>> call = FuzzieAPI.APIService().getSentGifts(currentUser.getAccesstoken(), 1, GIFTBOX_OFFSET);
        call.enqueue(new Callback<List<Gift>>() {
            @Override
            public void onResponse(Call<List<Gift>> call, Response<List<Gift>> response) {

                if (swipeContainer != null) {
                    swipeContainer.setRefreshing(false);
                }

                loading = false;

                if (response.code() == 200 && response.body() != null){

                    isLastSent = response.body().size() % GIFTBOX_OFFSET != 0 || response.body().size() == 0;
                    dataManager.setSentGifts(response.body());

                    Intent intent = new Intent(BROADCAST_REFRESHED_GIFTBOX_SENT);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  GiftBoxActivity.this);
                }
            }

            @Override
            public void onFailure(Call<List<Gift>> call, Throwable t) {

                if (swipeContainer != null) {
                    swipeContainer.setRefreshing(false);
                }

                loading = false;

                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
            }
        });
    }

    private void loadNextGifts(){

        switch (selectedTab){

            case TAB_ACTIVE:

                if (!loading && !isLastActive){

                    loadMore.setVisibility(View.VISIBLE);
                    loading = true;
                    loadNextActiveGifts();
                }

                break;

            case TAB_USED:

                if (!loading && !isLastUsed){

                    loadMore.setVisibility(View.VISIBLE);
                    loading = true;
                    loadNextUsedGifts();
                }

                break;

            case TAB_SENT:

                if (!loading && !isLastSent){

                    loadMore.setVisibility(View.VISIBLE);
                    loading = true;
                    loadNextSentGifts();
                }

                break;
        }

    }

    private void loadNextActiveGifts(){

        loading = true;
        int start = dataManager.getActiveGifts().size() + 1;

        FuzzieAPI.APIService().getActiveGifts(currentUser.getAccesstoken(), start, GIFTBOX_OFFSET).enqueue(new Callback<List<Gift>>() {
            @Override
            public void onResponse(Call<List<Gift>> call, Response<List<Gift>> response) {

                loading = false;
                loadMore.setVisibility(View.GONE);

                if (response.code() == 200 && response.body() != null){

                    isLastActive = response.body().size() % GIFTBOX_OFFSET != 0 || response.body().size() == 0;
                    dataManager.getActiveGifts().addAll(response.body());
                    addGifts(response.body());

                }
            }

            @Override
            public void onFailure(Call<List<Gift>> call, Throwable t) {

                loading = false;
                loadMore.setVisibility(View.GONE);
            }
        });
    }

    private void loadNextUsedGifts(){

        loading = true;
        int start = dataManager.getUsedGifts().size() + 1;

        FuzzieAPI.APIService().getUsedGifts(currentUser.getAccesstoken(), start, GIFTBOX_OFFSET).enqueue(new Callback<List<Gift>>() {
            @Override
            public void onResponse(Call<List<Gift>> call, Response<List<Gift>> response) {

                loading = false;
                loadMore.setVisibility(View.GONE);

                if (response.code() == 200 && response.body() != null){

                    isLastUsed = response.body().size() % GIFTBOX_OFFSET != 0 || response.body().size() == 0;
                    dataManager.getUsedGifts().addAll(response.body());

                    addGifts(response.body());

                }
            }

            @Override
            public void onFailure(Call<List<Gift>> call, Throwable t) {

                loading = false;
                loadMore.setVisibility(View.GONE);
            }
        });
    }

    private void loadNextSentGifts(){

        loading = true;
        int start = dataManager.getSentGifts().size() + 1;

        FuzzieAPI.APIService().getSentGifts(currentUser.getAccesstoken(), start, GIFTBOX_OFFSET).enqueue(new Callback<List<Gift>>() {
            @Override
            public void onResponse(Call<List<Gift>> call, Response<List<Gift>> response) {

                loading = false;
                loadMore.setVisibility(View.GONE);

                if (response.code() == 200 && response.body() != null){

                    isLastSent = response.body().size() % GIFTBOX_OFFSET != 0 || response.body().size() == 0;
                    dataManager.getSentGifts().addAll(response.body());

                    addGifts(response.body());

                }
            }

            @Override
            public void onFailure(Call<List<Gift>> call, Throwable t) {

                loading = false;
                loadMore.setVisibility(View.GONE);
            }
        });
    }

    private void updateGiftRedeemState(int position){

        if (selectedTab == TAB_ACTIVE){

            if (giftAdapter != null && position < giftAdapter.getAdapterItemCount() && giftAdapter.getAdapterItem(position) != null){

                if (giftAdapter.getAdapterItem(position) instanceof GiftBoxItem){

                    GiftBoxItem item = (GiftBoxItem)giftAdapter.getAdapterItem(position);
                    Gift gift = item.getGift();

                    Gift updatedGift = dataManager.getGiftById(gift.getId(), false);
                    if (updatedGift != null){

                        item.setGift(updatedGift);
                        giftAdapter.notifyAdapterItemChanged(position);
                    }
                }

                if (giftAdapter.getAdapterItem(position) instanceof GiftBoxPowerUpCardItem){

                    GiftBoxPowerUpCardItem item = (GiftBoxPowerUpCardItem)giftAdapter.getAdapterItem(position);
                    Gift gift = item.getGift();

                    Gift updatedGift = dataManager.getGiftById(gift.getId(), false);
                    if (updatedGift != null){

                        item.setGift(updatedGift);
                        giftAdapter.notifyAdapterItemChanged(position);
                    }
                }

            }


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            if (requestCode == REQUEST_GIFT_MARK_AS_UNREDEEMED){

                if (tabLayout.getSelectedTabPosition() == TAB_ACTIVE){

                    showGifts();

                } else {

                    if (tabLayout.getTabAt(TAB_ACTIVE) != null){
                        tabLayout.getTabAt(TAB_ACTIVE).select();
                    }
                }

            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
