package sg.com.fuzzie.android.ui.redpacket;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.RedPacket;
import sg.com.fuzzie.android.api.models.RedPacketBundle;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.redpacket.RedPacketOthersItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;

/**
 * Created by movdev on 3/2/18.
 */

@EActivity(R.layout.activity_red_packet_sent_detail)
public class RedPacketSentDetailActivity extends BaseActivity {

    private RedPacketBundle redPacketBundle;
    private FastItemAdapter adapter;

    @Extra
    String bundleId;

    @Extra
    boolean loadBundle;

    @Bean
    FuzzieData dataManager;

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.tvQuantity)
    TextView tvQuantity;

    @ViewById(R.id.tvTotal)
    TextView tvTotal;

    @ViewById(R.id.tvTicketCount)
    TextView tvTicketCount;

    @ViewById(R.id.tvOpened)
    TextView tvOpened;

    @ViewById(R.id.tvMessage)
    TextView tvMessage;

    @ViewById(R.id.llSend)
    View sendView;

    @ViewById(R.id.tvSend)
    TextView tvSend;

    @ViewById(R.id.others)
    View others;

    @ViewById(R.id.rvOthers)
    RecyclerView rvOthers;

    @ViewById(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @AfterViews
    public void calledAfterViewInjection() {

        if (bundleId == null) return;

        swipeContainer.setRefreshing(false);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(false);
                loadRedPacketBundle();
            }
        });

        if (loadBundle){

            loadRedPacketBundle();

        } else {

            redPacketBundle = dataManager.getRedPacketBundleWithId(bundleId);
            if (redPacketBundle == null) return;

            showInfo();
        }

    }

    private void loadRedPacketBundle(){

        showLoader();

        FuzzieAPI.APIService().getRedPacketBundle(currentUser.getAccesstoken(), bundleId).enqueue(new Callback<RedPacketBundle>() {
            @Override
            public void onResponse(Call<RedPacketBundle> call, Response<RedPacketBundle> response) {

                hideLoader();

                if (response.code() == 200 && response.body() != null){

                    redPacketBundle = response.body();
                    showInfo();

                } else {

                    showFZAlert("OOPS!", "Unknown Error Occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);
                }
            }

            @Override
            public void onFailure(Call<RedPacketBundle> call, Throwable t) {

                hideLoader();
                showFZAlert("OOPS!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);

            }
        });
    }

    private void showInfo(){

        tvQuantity.setText(String.valueOf(redPacketBundle.getRedPacketsCount()));
        tvTotal.setText(FuzzieUtils.getFormattedValue(redPacketBundle.getValue(), 0.8125f));
        tvTicketCount.setText(String.valueOf(redPacketBundle.getTicketCount() * redPacketBundle.getRedPacketsCount()));
        tvOpened.setText(String.valueOf(redPacketBundle.getOpenedRedPacketsCount()));

        String message = redPacketBundle.getMessage();
        if (message != null && message.length() > 0){
            tvMessage.setText(message);
        } else {
            tvMessage.setText("");
            tvMessage.setVisibility(View.GONE);
        }

        adapter = new FastItemAdapter();
        rvOthers.setLayoutManager(new LinearLayoutManager(context));
        rvOthers.setAdapter(adapter);

        if (redPacketBundle.isAllPacketsOpened()){
            sendView.setVisibility(View.GONE);
        }

        if (redPacketBundle.getRedPacketsCount() == 1){

            tvSend.setText(FuzzieUtils.fromHtml("Your Lucky Packet has not been opened yet. You can <font color='#FA3E3F'><b>send it again to your friend here.<b></font>"));

        } else {

            tvSend.setText(FuzzieUtils.fromHtml("Some Lucky Packets haven\'t been opened yet. You can <font color='#FA3E3F'><b>send them again to your friends here.<b></font>"));

        }

        showOthers();

    }

    private void showOthers(){

        if (redPacketBundle.getAssignedRedPacketsCount() > 0){

            others.setVisibility(View.VISIBLE);

            for (RedPacket redPacket : redPacketBundle.getRedPackets()){
                if (redPacket.getUser() != null && redPacket.isUsed()){
                    adapter.add(new RedPacketOthersItem(redPacket));
                }
            }

        } else {
            others.setVisibility(View.GONE);
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click({R.id.llSend, R.id.btnSend})
    void sendButtonClicked(){

        if (redPacketBundle != null){
            dataManager.redPacketBundle = redPacketBundle;
            RedPacketDeliverActivity_.intent(context).fromWallet(true).start();
        }
    }
}
