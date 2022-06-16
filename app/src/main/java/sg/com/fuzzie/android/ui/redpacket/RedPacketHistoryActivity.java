package sg.com.fuzzie.android.ui.redpacket;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.RedPacket;
import sg.com.fuzzie.android.api.models.RedPacketBundle;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.redpacket.RedPacketReceiveItem;
import sg.com.fuzzie.android.items.redpacket.RedPacketSentItem;
import sg.com.fuzzie.android.items.redpacket.RedPacketTotalLuckItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import timber.log.Timber;

import static sg.com.fuzzie.android.utils.Constants.REQUEST_RED_PACKET_OPEN;

/**
 * Created by mac on 2/27/18.
 */

@EActivity(R.layout.activity_red_packet_history)
public class RedPacketHistoryActivity extends BaseActivity {

    public static final int TAB_RECEIVED = 0;
    public static final int TAB_SENT = 1;

    private boolean isShowingLoader;
    private boolean isRefreshing;

    private SectionedRecyclerViewAdapter sectionAdapter;
    LinearLayoutManager layoutManager;

    private List<RedPacket> openedRedPackets;
    private List<RedPacket> unOpenedRedPackets;
    private List<RedPacketBundle> openedRedPacketBundles;
    private List<RedPacketBundle> unOpenedRedPacketBundles;
    private double totalLuck = 0.0;
    private int totalTicketsCount = 0;

    @Extra
    int selectedTab;

    // For Lucky Packet Notification
    @Extra
    String bundleId;

    @Extra
    boolean goSentDetailsPage;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.rvRedPackets)
    RecyclerView rvRedPackets;

    @ViewById(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @ViewById(R.id.tab_layout)
    TabLayout tabLayout;

    @ViewById(R.id.rlEmpty)
    View emptyView;

    @ViewById(R.id.ivEmpty)
    ImageView ivEmpty;

    @ViewById(R.id.tvEmpty)
    TextView tvEmpty;

    @AfterViews
    public void calledAfterViewInjection() {

        if (goSentDetailsPage){

            RedPacketSentDetailActivity_.intent(context).bundleId(bundleId).loadBundle(true).start();

        }

        layoutManager = new LinearLayoutManager(context);
        rvRedPackets.setLayoutManager(layoutManager);
        sectionAdapter = new SectionedRecyclerViewAdapter();
        rvRedPackets.setAdapter(sectionAdapter);

        if (dataManager.getRedPackets() == null){
            loadRedPackets();
        } else {
            groupRedPackets();
            showRedPackets();
        }

        if (dataManager.getRedPacketBundles() == null){
            loadRedPacketBundles();
        } else {
            groupRedPacketBundles();
        }

        swipeContainer.setRefreshing(false);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(false);

                isRefreshing = true;

                if (selectedTab == TAB_RECEIVED){
                    loadRedPackets();
                } else if (selectedTab == TAB_SENT){
                    loadRedPacketBundles();
                }
            }
        });
        swipeContainer.setDistanceToTriggerSync(300);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Timber.d("Selected Tab: " + tab.getText());
                selectedTab = tab.getPosition();

                switch (selectedTab){
                    case TAB_RECEIVED:
                        groupRedPackets();
                        showRedPackets();
                        break;
                    case TAB_SENT:
                        groupRedPacketBundles();
                        showRedPacketBundles();
                        break;
                    default:
                        break;

                }
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

    }

    private void groupRedPackets(){

        openedRedPackets = new ArrayList<>();
        unOpenedRedPackets = new ArrayList<>();

        totalLuck = 0.0f;
        totalTicketsCount = 0;

        if (dataManager.getRedPackets() != null && dataManager.getRedPackets().size() > 0){
            for (RedPacket redPacket : dataManager.getRedPackets()){
                if (redPacket.isUsed()){
                    openedRedPackets.add(redPacket);
                    totalLuck += redPacket.getValue();
                    totalTicketsCount += redPacket.getTicketCount();
                } else {
                    unOpenedRedPackets.add(redPacket);
                }
            }
        }
    }

    private void groupRedPacketBundles(){

        openedRedPacketBundles = new ArrayList<>();
        unOpenedRedPacketBundles = new ArrayList<>();

        if (dataManager.getRedPacketBundles() != null && dataManager.getRedPacketBundles().size() > 0){
            for (RedPacketBundle redPacketBundle : dataManager.getRedPacketBundles()){
                if (redPacketBundle.isAllPacketsOpened()){
                    openedRedPacketBundles.add(redPacketBundle);
                } else {
                    unOpenedRedPacketBundles.add(redPacketBundle);
                }
            }
        }
    }

    private void showRedPackets(){

        if (dataManager.getRedPackets() !=null && dataManager.getRedPackets().size() > 0){

            rvRedPackets.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            if (sectionAdapter == null){
                sectionAdapter = new SectionedRecyclerViewAdapter();
            } else {
                sectionAdapter.removeAllSections();
            }

            if (totalLuck > 0 || totalTicketsCount > 0){
                sectionAdapter.addSection(new RedPacketTotalLuckItem(totalLuck, totalTicketsCount));
            }

            if (unOpenedRedPackets.size() > 0){
                sectionAdapter.addSection(new RedPacketReceiveItem(unOpenedRedPackets, "TO OPEN"));
            }

            if (openedRedPackets.size() > 0){
                sectionAdapter.addSection(new RedPacketReceiveItem(openedRedPackets, "OPENED"));
            }

            sectionAdapter.notifyDataSetChanged();

        } else {

            rvRedPackets.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);

            tvEmpty.setText("No Lucky Packets received yet");
            ivEmpty.setImageResource(R.drawable.bear_female);
        }

    }

    private void showRedPacketBundles(){

        if (dataManager.getRedPacketBundles() != null && dataManager.getRedPacketBundles().size() > 0){

            rvRedPackets.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            if (sectionAdapter == null){
                sectionAdapter = new SectionedRecyclerViewAdapter();
            } else {
                sectionAdapter.removeAllSections();
            }

            if (unOpenedRedPacketBundles.size() > 0){
                sectionAdapter.addSection(new RedPacketSentItem(unOpenedRedPacketBundles, "NOT OPENED YET"));
            }

            if (openedRedPacketBundles.size() > 0){
                sectionAdapter.addSection(new RedPacketSentItem(openedRedPacketBundles, "OPENED"));
            }

            sectionAdapter.notifyDataSetChanged();
        } else {

            rvRedPackets.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);

            tvEmpty.setText("No Lucky Packets sent yet");
            ivEmpty.setImageResource(R.drawable.bear_mustache);
        }

    }

    private void loadRedPackets(){

        if (!isShowingLoader && !isRefreshing){
            showLoader();
            isShowingLoader = true;
        }

        FuzzieAPI.APIService().getRedPackets(currentUser.getAccesstoken()).enqueue(new Callback<List<RedPacket>>() {
            @Override
            public void onResponse(Call<List<RedPacket>> call, Response<List<RedPacket>> response) {

                hideLoader();
                isShowingLoader = false;
                isRefreshing = false;

                if (response.code() == 200 && response.body() != null){
                    dataManager.setRedPackets(response.body());
                    groupRedPackets();

                    if (selectedTab == TAB_RECEIVED){
                        showRedPackets();
                    }

                }
            }

            @Override
            public void onFailure(Call<List<RedPacket>> call, Throwable t) {

                hideLoader();
                isShowingLoader = false;
                isRefreshing = false;
            }
        });
    }

    private void loadRedPacketBundles(){

        if (!isShowingLoader && !isRefreshing){
            showLoader();
            isShowingLoader = true;
        }

        FuzzieAPI.APIService().getRedPacketBundles(currentUser.getAccesstoken()).enqueue(new Callback<List<RedPacketBundle>>() {
            @Override
            public void onResponse(Call<List<RedPacketBundle>> call, Response<List<RedPacketBundle>> response) {

                hideLoader();
                isShowingLoader = false;
                isRefreshing = false;

                if (response.code() == 200 && response.body() !=null){
                    dataManager.setRedPacketBundles(response.body());
                    groupRedPacketBundles();

                    if (selectedTab == TAB_SENT){
                        showRedPacketBundles();
                    }
                }

            }

            @Override
            public void onFailure(Call<List<RedPacketBundle>> call, Throwable t) {

                hideLoader();
                isShowingLoader = false;
                isRefreshing = false;
            }
        });

    }

    private void showUpdatedRedPacket(String redPacketId){

        if (tabLayout.getTabAt(TAB_RECEIVED) != null){
            tabLayout.getTabAt(TAB_RECEIVED).select();
        }

        groupRedPackets();
        showRedPackets();

        RedPacketReceiveDetailActivity_.intent(context).redPacketId(redPacketId).start();
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_RED_PACKET_OPEN){
            if (resultCode == RESULT_OK){

                if (data.getStringExtra("red_packet_id") != null) {

                    String redPacketId = data.getStringExtra("red_packet_id");
                    showUpdatedRedPacket(redPacketId);
                }

            }
        }
    }
}
