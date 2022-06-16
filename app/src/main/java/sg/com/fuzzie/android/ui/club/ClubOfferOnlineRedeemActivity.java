package sg.com.fuzzie.android.ui.club;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.WebActivity_;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Offer;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.ViewUtils;

import static sg.com.fuzzie.android.utils.Constants.COUNTDOWN_TIME;

@EActivity(R.layout.activity_club_offer_redeem_online)
public class ClubOfferOnlineRedeemActivity extends BaseActivity {

    private Offer offer;

    CountDownTimer timer;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvOfferName)
    TextView tvOfferName;

    @ViewById(R.id.tvBrandName)
    TextView tvBrandName;

    @ViewById(R.id.tvCode)
    TextView tvCode;

    @ViewById(R.id.tvTimer)
    TextView tvTimer;

    @AfterViews
    void callAfterViewInjection(){

        offer = dataManager.offer;

        if (offer == null) return;

        tvOfferName.setText(offer.getName());

        Brand brand = dataManager.getBrandById(offer.getBrandId());
        tvBrandName.setText(brand != null ? brand.getName() : "");

        tvCode.setText(offer.getRedeemDetail().getCode());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (offer != null && offer.getRedeemDetail() != null && offer.getRedeemDetail().getRedeemTimerStartedAt() != null){

            startTimer();

        } else {

            endTimer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        endTimer();
    }

    private void startTimer(){

        ViewUtils.visible(tvTimer);

        DateTimeFormatter parser = ISODateTimeFormat.dateTime();
        DateTime now = new DateTime();
        DateTime redeemStartTime = parser.parseDateTime(offer.getRedeemDetail().getRedeemTimerStartedAt());

        long milliseconds = COUNTDOWN_TIME - (now.getMillis() - redeemStartTime.getMillis());

        if (timer != null){
            timer.cancel();
            timer = null;
        }

        timer = new CountDownTimer(milliseconds, 1000) {

            public void onTick(long millisUntilFinished) {
                int hours = (int) (millisUntilFinished/3600000);
                int mins = (int) (millisUntilFinished/60000);
                mins %= 60;
                int secs = (int) (millisUntilFinished/1000);
                secs %= 60;

                tvTimer.setText(String.format("%02dh %02dm %02ds",hours,mins,secs));
            }

            public void onFinish() {
                endTimer();
            }
        };
        timer.start();
    }

    private void endTimer(){

        tvTimer.setText("");
        ViewUtils.gone(tvTimer);

        if (timer != null){
            timer.cancel();
            timer = null;
        }

    }

    @Click(R.id.tvHelp)
    void helpButtonClicked() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.activate_help_menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:
                        WebActivity_.intent(context).titleExtra("FAQ").urlExtra("http://fuzzie.com.sg/faq.html").start();
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
        startActivity(Intent.createChooser(emailIntent, "Email support@fuzzie.com.sg"));
    }

    void facebookSupport() {
        String url = "http://m.me/fuzzieapp";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Click(R.id.cvStore)
    void storeButtonClicked(){

        if (offer != null && offer.getExternalRedeemPage() != null && !offer.getExternalRedeemPage().equals("")){
            String url = offer.getExternalRedeemPage();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    }

    @Click(R.id.vCopy)
    void clipboardCopy(){

        ClipboardManager cm = (ClipboardManager)context.getSystemService(CLIPBOARD_SERVICE);
        ClipData cData = ClipData.newPlainText("text", currentUser.getCurrentUser().getFuzzieClub().getReferralCode());
        assert cm != null;
        cm.setPrimaryClip(cData);

        showClipboardPopup();
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }
}
