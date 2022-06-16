package sg.com.fuzzie.android.ui.redpacket;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
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

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.RedPacket;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.redpacket.RedPacketOthersItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

/**
 * Created by mac on 2/28/18.
 */

@EActivity(R.layout.activity_red_packet_receive_detail)
public class RedPacketReceiveDetailActivity extends BaseActivity {

    private RedPacket redPacket;
    private FastItemAdapter adapter;

    @Extra
    String redPacketId;

    @Bean
    FuzzieData dataManager;

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.ivAvatar)
    ImageView ivAvatar;

    @ViewById(R.id.tvName)
    TextView tvName;

    @ViewById(R.id.tvMessage)
    TextView tvMessage;

    @ViewById(R.id.others)
    View others;

    @ViewById(R.id.rvOthers)
    RecyclerView rvOthers;

    @AfterViews
    public void calledAfterViewInjection() {

        if (redPacketId == null) return;

        redPacket = dataManager.getRedPacketWithId(redPacketId);
        if (redPacket == null) return;

        if (redPacket.getSender().getAvatar() != null && !redPacket.getSender().getAvatar().equals("")){
            Picasso.get()
                    .load(redPacket.getSender().getAvatar())
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .transform(new CropCircleTransformation())
                    .fit()
                    .into(ivAvatar);
        }

        tvName.setText(redPacket.getSender().getName());

        String message = redPacket.getMessage();
        if (message != null && message.length() > 0){
            tvMessage.setText(message);
        } else {
            tvMessage.setText("");
            tvMessage.setVisibility(View.GONE);
        }

        adapter = new FastItemAdapter();
        rvOthers.setLayoutManager(new LinearLayoutManager(context));
        rvOthers.setAdapter(adapter);

        loadOthers();
    }

    private void loadOthers(){

        FuzzieAPI.APIService().getRedPackets(currentUser.getAccesstoken(), redPacket.getBundleId()).enqueue(new Callback<List<RedPacket>>() {
            @Override
            public void onResponse(Call<List<RedPacket>> call, Response<List<RedPacket>> response) {

                if (response.code() == 200 && response.body() != null){
                    showOthers(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<RedPacket>> call, Throwable t) {

            }
        });

    }

    private void showOthers(List<RedPacket> redPackets){

        redPackets.add(0, redPacket);

        if (adapter == null){
            adapter = new FastItemAdapter();
        } else {
            adapter.clear();
        }

        others.setVisibility(View.VISIBLE);

        boolean isMe = true;

        for (RedPacket redPacket : redPackets){
            if (redPacket.getUser() != null && redPacket.isUsed()){
                adapter.add(new RedPacketOthersItem(redPacket, isMe));
                isMe = false;
            }
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }
}
