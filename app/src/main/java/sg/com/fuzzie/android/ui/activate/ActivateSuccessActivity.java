package sg.com.fuzzie.android.ui.activate;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.MainActivity_;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.Constants;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_CHANGE_TAB;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_USER;

/**
 * Created by nurimanizam on 16/12/16.
 */

@EActivity(R.layout.activity_activate_success)
public class ActivateSuccessActivity extends BaseActivity {

    @Extra
    String jsonExtra;

    @Extra
    boolean fromPowerUpGift;

    JsonObject jsonObject;

    String type;

    @ViewById(R.id.ivBackground)
    ImageView ivBackground;

    @ViewById(R.id.ivBackground1)
    View ivBackground1;

    @ViewById(R.id.ivCircle)
    ImageView ivCircle;

    @ViewById(R.id.ivLightning)
    ImageView ivLightning;

    @ViewById(R.id.ivCheck)
    ImageView ivCheck;

    @ViewById(R.id.tvCredits)
    TextView tvCredits;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.tvDescription)
    TextView tvDescription;

    @ViewById(R.id.tvShop)
    TextView tvShop;

    @ViewById(R.id.tvAnotherCode)
    TextView tvAnotherCode;

    @AfterViews
    void calledAfterViewInjection() {

        if (jsonExtra == null) return;

        JsonParser parser = new JsonParser();
        jsonObject = parser.parse(jsonExtra).getAsJsonObject();

        if (jsonObject.get("type") != null){
            type = jsonObject.get("type").getAsString();
        }

        if (jsonObject.get("credits") != null && jsonObject.get("credits").getAsDouble() > 0){

            ivBackground.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(R.drawable.code_success_red)
                    .placeholder(R.drawable.code_success_red)
                    .fit()
                    .into(ivBackground);
            ivBackground1.setVisibility(View.GONE);
            ivLightning.setVisibility(View.GONE);
            ivCheck.setVisibility(View.GONE);
            tvTitle.setText("HAS BEEN CREDITED TO YOUR FUZZIE ACCOUNT");
            tvDescription.setText("Enjoy your shopping experience!");
            tvCredits.setText("S$" + String.format("%.0f",jsonObject.get("credits").getAsDouble()));

        } else {
            if (type.equals("PowerUpCode") && jsonObject.get("time_to_expire").getAsInt() > 0){

                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.BROADCAST_REFRESH_HOME));

                tvCredits.setText("");
                ivBackground.setVisibility(View.GONE);
                ivBackground1.setVisibility(View.VISIBLE);
                ivCheck.setVisibility(View.GONE);
                ivCircle.setVisibility(View.INVISIBLE);
                tvTitle.setText("YOU'VE POWERED UP FOR " + jsonObject.get("time_to_expire").getAsString() + "H!");
                tvDescription.setText("Enjoy boosted cashback on all brands. Happy shopping!");
                tvShop.setTextColor(Color.parseColor("#388DD1"));

            } else if (type.equals("ActivationCode")){

                tvCredits.setText("");
                ivLightning.setVisibility(View.GONE);
                ivBackground.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(R.drawable.code_success_red)
                        .placeholder(R.drawable.code_success_red)
                        .fit()
                        .into(ivBackground);
                ivBackground1.setVisibility(View.GONE);
                tvTitle.setText(jsonObject.get("brand_name").getAsString() + "-" + jsonObject.get("gift_title").getAsString() + " ACTIVATED");
                tvDescription.setText("Your card has been added to your Gift Box");
                tvShop.setText("GO TO GIFT BOX");
            }
        }

        Picasso.get()
                .load(R.drawable.success_circle)
                .placeholder(R.drawable.success_circle)
                .fit()
                .into(ivCircle);

        if (fromPowerUpGift){
            tvAnotherCode.setVisibility(View.GONE);
        }

        Intent refreshIntent = new Intent(BROADCAST_REFRESH_USER);
        LocalBroadcastManager.getInstance(context).sendBroadcast(refreshIntent);

    }

    @Click(R.id.tvShop)
    void shopButtonClicked() {

        Intent intent = new Intent(context, MainActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
        if (type.equals("ActivationCode")){
            intent.putExtra("go_gift_box", true);
        }
        startActivity(intent);

        if (type.equals("ActivationCode")){
            Intent intent1 = new Intent(BROADCAST_CHANGE_TAB);
            intent1.putExtra("tabId",R.id.tab_wallet);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
        } else {
            Intent intent1 = new Intent(BROADCAST_CHANGE_TAB);
            intent1.putExtra("tabId",R.id.tab_shop);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
        }
    }

    @Click(R.id.tvAnotherCode)
    void anotherButtonClicked(){
        finish();
    }

}
