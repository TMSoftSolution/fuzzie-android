package sg.com.fuzzie.android.ui.redpacket;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.MainActivity_;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.RedPacketBundle;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.ui.gift.DeliveryMethodsActivity;
import sg.com.fuzzie.android.ui.gift.EmailSendActivity_;
import sg.com.fuzzie.android.ui.jackpot.JackpotLearnMoreActvity_;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

/**
 * Created by mac on 2/27/18.
 */

@EActivity(R.layout.activity_red_packet_delivery)
public class RedPacketDeliverActivity extends BaseActivity {

    private CallbackManager callbackManager;
    private PackageManager pm;
    private RedPacketBundle redPacketBundle;
    private String message;
    private String url;
    private String shareMessage;

    static final int REQUEST_FINISH = 1;

    @Extra
    boolean fromWallet;

    @Bean
    FuzzieData dataManager;


    @ViewById(R.id.tvQuantity)
    TextView tvQuantity;

    @ViewById(R.id.tvTotal)
    TextView tvTotal;

    @ViewById(R.id.tvTicketCount)
    TextView tvTicketCount;

    @ViewById(R.id.tvMessage)
    TextView tvMessage;

    @ViewById(R.id.llRedPacket)
    View vRedPacket;

    @ViewById(R.id.tvDelivery)
    TextView tvDelivery;


    @AfterViews
    public void calledAfterViewInjection() {

        redPacketBundle = dataManager.redPacketBundle;
        if (redPacketBundle == null) return;

        if (redPacketBundle.getMessage() != null && redPacketBundle.getMessage().length() > 0){
            message = redPacketBundle.getMessage();
        }

        url = redPacketBundle.getUrl();

        if (!fromWallet){

            vRedPacket.setVisibility(View.VISIBLE);
            tvDelivery.setVisibility(View.VISIBLE);

            tvQuantity.setText(String.valueOf(redPacketBundle.getRedPacketsCount()));

            tvTotal.setText(FuzzieUtils.getFormattedValue(redPacketBundle.getValue(), 0.8125f));

            tvTicketCount.setText(String.valueOf(redPacketBundle.getTicketCount() * redPacketBundle.getRedPacketsCount()));

            if (message != null && message.length() > 0){
                tvMessage.setText(message);
            } else {
                tvMessage.setText("");

            }

        } else {

            vRedPacket.setVisibility(View.GONE);
            tvDelivery.setVisibility(View.GONE);
        }

        callbackManager = CallbackManager.Factory.create();

        pm = getPackageManager();

        if (redPacketBundle.getRedPacketsCount() == 1){

            shareMessage = String.format("I\'ve just sent you a Lucky Packet. Click here to open it: %s", url);

        } else {

            shareMessage = String.format("I\'ve just created %s Lucky Packets on Fuzzie. Click here to grab yours: %s", String.valueOf(redPacketBundle.getRedPacketsCount()), url);

        }

    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.llWhatsapp)
    void whatsappButtonClicked(){

        if (redPacketBundle != null){

            try {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                intent.setPackage("com.whatsapp");
                intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivityForResult(Intent.createChooser(intent, "Share with"), REQUEST_FINISH);

            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(RedPacketDeliverActivity.this, "Whatsapp not Installed", Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }

    @Click(R.id.llMessenger)
    void messengerButtonClicked(){

        if (redPacketBundle != null){

            try {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                PackageInfo info=pm.getPackageInfo("com.facebook.orca", PackageManager.GET_META_DATA);
                intent.setPackage("com.facebook.orca");
                intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivityForResult(Intent.createChooser(intent, "Share with"), REQUEST_FINISH);

            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(RedPacketDeliverActivity.this, "Messenger not Installed", Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }

    @Click(R.id.llSMS)
    void smsButtonClicked(){

        if (redPacketBundle != null){

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.putExtra("sms_body", shareMessage);
            intent.setType("vnd.android-dir/mms-sms");
            startActivityForResult(intent, REQUEST_FINISH);
        }

    }

    @Click(R.id.llWeChat)
    void wechatButtonClicked(){

        if (redPacketBundle != null){

            try {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                PackageInfo info=pm.getPackageInfo("com.tencent.mm", PackageManager.GET_META_DATA);
                intent.setPackage("com.tencent.mm");
                intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivityForResult(Intent.createChooser(intent, "Share with"), REQUEST_FINISH);

            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(RedPacketDeliverActivity.this, "Wechat not Installed", Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }

    @Click(R.id.llEmail)
    void emailButtonClicked(){

        if (redPacketBundle != null){
            EmailSendActivity_.intent(context).fromRedPacket(true).start();
        }

    }

    @Click(R.id.llCopy)
    void copyButtonClicked(){

        if (redPacketBundle != null){
            ClipboardManager cm = (ClipboardManager)context.getSystemService(getApplicationContext().CLIPBOARD_SERVICE);
            ClipData cData = ClipData.newPlainText("text", url);
            cm.setPrimaryClip(cData);
            Toast.makeText(context, "Link Copied to clipboard", Toast.LENGTH_SHORT).show();
        }

    }

    @Click(R.id.cvDone)
    void doneButtonClicked(){
        if (fromWallet){
            finish();
        } else {
            goRedPacketHistoryPage();
        }
    }

    private void goRedPacketHistoryPage(){

        Intent intent = new Intent(context, MainActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("go_red_packet_history_page", true);
        intent.putExtra("selected_tab", 1);
        startActivity(intent);
    }

    @Click(R.id.tvHelp)
    void helpButtonClicked(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.activate_help_menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:
                        JackpotLearnMoreActvity_.intent(context).fromRedPacket(true).start();
                        break;
                    case 1:
                        emailSupport();
                        break;
                    case 2:
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
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Help with Fuzzie Jackpot");
        startActivity(Intent.createChooser(emailIntent, "Help with Fuzzie Jackpot"));
    }

    void facebookSupport() {
        String url = "http://m.me/fuzzieapp";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
