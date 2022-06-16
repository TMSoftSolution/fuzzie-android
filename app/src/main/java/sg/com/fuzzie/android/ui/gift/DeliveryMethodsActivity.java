package sg.com.fuzzie.android.ui.gift;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.MainActivity_;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.Friend;
import sg.com.fuzzie.android.api.models.Gift;
import sg.com.fuzzie.android.api.models.PayResponse;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

/**
 * Created by mac on 6/17/17.
 */

@EActivity(R.layout.activity_delivery_mothods)
public class DeliveryMethodsActivity extends BaseActivity {

    static final int REQUEST_FINISH = 1;
    Gift gift;
    Friend friend;
    boolean deliveryMethodSelected;

    CallbackManager callbackManager;

    @Extra
    boolean showBack;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.cvDone)
    CardView cvDone;

    @ViewById(R.id.ivBackground)
    RoundedImageView ivBackground;

    @ViewById(R.id.tvBrandName)
    TextView tvBrandName;

    @ViewById(R.id.tvPrice)
    TextView tvPrice;

    @ViewById(R.id.tvName)
    TextView tvReceiverName;

    @ViewById(R.id.ivBack)
    ImageView ivBack;

    PackageManager pm;

    @AfterViews
    public void calledAfterViewInjection() {

        callbackManager = CallbackManager.Factory.create();

        pm = getPackageManager();

        cvDone.setEnabled(false);
        cvDone.setCardBackgroundColor(getResources().getColor(R.color.loblolly));

        if (dataManager.getSentGift() != null) {
            gift = dataManager.getSentGift();
        }

        if (gift != null) {
            if (gift.getImageUrl() != null && !gift.getImageUrl().equals("")) {
                Picasso.get()
                        .load(gift.getImageUrl())
                        .placeholder(R.drawable.brands_placeholder)
                        .fit()
                        .into(ivBackground);
            }

            if (gift.getGiftCard() != null) {
                tvBrandName.setText(gift.getGiftCard().getDisplayName());
                tvPrice.setText(gift.getGiftCard().getPrice().getCurrencySymbol() + (int) gift.getGiftCard().getDiscountedPrice());

            } else if (gift.getService() != null) {
                tvBrandName.setText(gift.getService().getDisplayName());
                tvPrice.setText(gift.getService().getPrice().getCurrencySymbol() + (int) gift.getService().getDiscountedPrice());

            }

        }

        if (dataManager.getReceiver() != null) {
            friend = dataManager.getReceiver();
        }

        if (friend != null) {
            tvReceiverName.setText(friend.getName());
        }

        if (showBack){
            ivBack.setVisibility(View.VISIBLE);
        } else {
            ivBack.setVisibility(View.GONE);
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        dataManager.setReceiver(null);
        dataManager.setSentGift(null);
        finish();;
    }

    @Click(R.id.llWhatsapp)
    void whatsappButtonClicked(){

        if (gift != null){

            try {
                if (!deliveryMethodSelected){
                    deliveryMethodSelected = true;
                }

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String text = "I've just sent you a gift on Fuzzie! Click this link to open your gift: " + gift.getGiftUrl();
                PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                intent.setPackage("com.whatsapp");
                intent.putExtra(Intent.EXTRA_TEXT, text);
                startActivityForResult(Intent.createChooser(intent, "Share with"), REQUEST_FINISH);

            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(DeliveryMethodsActivity.this, "Whatsapp not Installed", Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }

    @Click(R.id.llMessenger)
    void messengerButtonClicked(){

        if (gift != null){

            try {
                if (!deliveryMethodSelected){
                    deliveryMethodSelected = true;
                }

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String text = "I've just sent you a gift on Fuzzie! Click this link to open your gift: " + gift.getGiftUrl();
                PackageInfo info=pm.getPackageInfo("com.facebook.orca", PackageManager.GET_META_DATA);
                intent.setPackage("com.facebook.orca");
                intent.putExtra(Intent.EXTRA_TEXT, text);
                startActivityForResult(Intent.createChooser(intent, "Share with"), REQUEST_FINISH);


            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(DeliveryMethodsActivity.this, "Messenger not Installed", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Click(R.id.llSMS)
    void smsButtonClicked(){

        if (gift != null){

            if (!deliveryMethodSelected){
                deliveryMethodSelected = true;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String text = "I've just sent you a gift on Fuzzie! Click this link to open your gift: " + gift.getGiftUrl();
            intent.putExtra("sms_body", text);
            intent.setType("vnd.android-dir/mms-sms");
            startActivityForResult(intent, REQUEST_FINISH);
        }
    }

    @Click(R.id.llWeChat)
    void weChatButtonClicked(){

        if (gift != null){

            try {
                if (!deliveryMethodSelected){
                    deliveryMethodSelected = true;
                }

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String text = "I've just sent you a gift on Fuzzie! Click this link to open your gift:\n" + gift.getGiftUrl();
                PackageInfo info=pm.getPackageInfo("com.tencent.mm", PackageManager.GET_META_DATA);
                intent.setPackage("com.tencent.mm");
                intent.putExtra(Intent.EXTRA_TEXT, text);
                startActivityForResult(Intent.createChooser(intent, "Share with"), REQUEST_FINISH);

            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(DeliveryMethodsActivity.this, "Wechat not Installed", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Click(R.id.llEmail)
    void emailButtonClicked(){

        if (gift != null){
            if (!deliveryMethodSelected){
                deliveryMethodSelected = true;
            }

            EmailSendActivity_.intent(context).extraGiftId(gift.getId()).start();
        }
    }

    @Click(R.id.llCopy)
    void copyButtonClicked(){

        if (gift != null){

            ClipboardManager cm = (ClipboardManager)context.getSystemService(getApplicationContext().CLIPBOARD_SERVICE);
            ClipData cData = ClipData.newPlainText("text", gift.getGiftUrl());
            cm.setPrimaryClip(cData);
            Toast.makeText(context, "Link Copied to clipboard", Toast.LENGTH_SHORT).show();

            if (!deliveryMethodSelected){
                deliveryMethodSelected = true;
            }

            updateDone();
        }

    }

    @Click(R.id.cvDone)
    void doneButtonClicked(){

        if (gift != null){

            displayProgressDialog("Processing...");
            Call<PayResponse> call = FuzzieAPI.APIService().markAsDelivered(currentUser.getAccesstoken(), gift.getId());
            call.enqueue(new Callback<PayResponse>() {
                @Override
                public void onResponse(Call<PayResponse> call, Response<PayResponse> response) {

                    dismissProgressDialog();

                    if (response.code() == 200 && response.body() != null){

                        if (response.body().getGift() != null){

                            for (int i = 0 ; i < dataManager.getSentGifts().size() ; i ++){

                                Gift gift = dataManager.getSentGifts().get(i);
                                if (response.body().getGift().getId().equals(gift.getId())){

                                    dataManager.getSentGifts().set(i, response.body().getGift());
                                    break;
                                }
                            }

                        }

                        dataManager.setReceiver(null);
                        dataManager.setSentGift(null);

                        Intent intent = new Intent(context, MainActivity_.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("go_gift_box", true);
                        intent.putExtra("selected_tab", 2);
                        startActivity(intent);

                    } else if (response.code() == 417){
                        logoutUser(currentUser,dataManager, DeliveryMethodsActivity.this);
                    } else {
                        showFZAlert("Oops", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);
                    }
                }

                @Override
                public void onFailure(Call<PayResponse> call, Throwable t) {
                    dismissProgressDialog();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FINISH){
            if (resultCode == RESULT_OK){
                updateDone();
            }
        }
    }

    private void updateDone(){
        cvDone.setEnabled(true);
        cvDone.setCardBackgroundColor(getResources().getColor(R.color.primary));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (deliveryMethodSelected){
            updateDone();
        }
    }

    @Override
    public void onBackPressed() {

    }
}
