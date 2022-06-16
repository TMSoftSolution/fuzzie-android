package sg.com.fuzzie.android.ui.redpacket;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.RedPacket;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_USER;
import static sg.com.fuzzie.android.utils.Constants.REQUEST_RED_PACKET_OPEN;

/**
 * Created by mac on 2/28/18.
 */

@EActivity(R.layout.activity_red_packet_open)
public class RedPacketOpenActivity extends BaseActivity {

    RedPacket redPacket;

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
            tvMessage.setText(String.format("\"%s\"", message));
        } else {
            tvMessage.setText("");
        }

    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.cvOpen)
    void openButtonClicked(){

        if (redPacketId != null){

            openRedPacket();
        }

    }

    private void openRedPacket(){

        showPopView("PROCESSING", "", R.drawable.bear_craft, true);

        FuzzieAPI.APIService().openRedPacket(currentUser.getAccesstoken(), redPacketId).enqueue(new Callback<RedPacket>() {
            @Override
            public void onResponse(Call<RedPacket> call, Response<RedPacket> response) {

                hidePopView();

                if (response.code() == 200 && response.body() != null){

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_REFRESH_USER));
                    dataManager.replaceRedPacket(response.body());
                    RedPacketOpenSuccessActivity_.intent(context).redPacketId(redPacketId).startForResult(REQUEST_RED_PACKET_OPEN);

                } else {

                    String errorMessage = "Unknown error occurred: " + response.code();

                    try {
                        String jsonString = response.errorBody().string();
                        JsonParser parser = new JsonParser();
                        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
                        if (jsonObject != null && jsonObject.get("error") != null && !jsonObject.get("error").isJsonNull()){
                            errorMessage = jsonObject.get("error").getAsString();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    showFZAlert("OOPS!", errorMessage, "GOT IT", "", R.drawable.ic_bear_dead_white);
                }

            }

            @Override
            public void onFailure(Call<RedPacket> call, Throwable t) {
                hidePopView();
                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_RED_PACKET_OPEN){
            if (resultCode == RESULT_OK){
                Intent intent = new Intent();
                intent.putExtra("red_packet_id", redPacketId);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }


}
