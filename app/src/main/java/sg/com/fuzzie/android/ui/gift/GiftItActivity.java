package sg.com.fuzzie.android.ui.gift;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.WebActivity_;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Friend;
import sg.com.fuzzie.android.api.models.GiftCard;
import sg.com.fuzzie.android.api.models.Service;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.ui.payments.PaymentActivity_;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;


/**
 * Created by mac on 6/17/17.
 */

@EActivity(R.layout.activity_gift_it)
public class GiftItActivity extends BaseActivity implements TextView.OnEditorActionListener{

    Brand brand;
    GiftCard giftCard;
    Service service;
    Friend receiver;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.cvCheckout)
    CardView cvCheckout;

    @ViewById(R.id.etSearch)
    EditText etSearch;

    @ViewById(R.id.etMessage)
    EditText etMessage;

    private boolean showBackAlert;

    @AfterViews
    public void calledAfterViewInjection() {

        receiver = new Friend();
        receiver.setName("");

        etSearch.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        etSearch.setOnEditorActionListener(this);

        if (dataManager.selectedBrand == null) return;

        brand = dataManager.selectedBrand;

        if (dataManager.selectedGiftCard != null) {
            giftCard = dataManager.selectedGiftCard;
        }

        if (dataManager.selectedService != null) {
            service = dataManager.selectedService;
        }

        cvCheckout.setEnabled(false);
        cvCheckout.setCardBackgroundColor(getResources().getColor(R.color.loblolly));

    }

    @Click(R.id.ivBack)
    void backButtonClicked(){

        if (receiver.getName().length() > 0 || etMessage.getText().length() > 0){

            showBackAlert = true;
            showFZAlert("DISCARD GIFT?", "Sure you would like to discard this gift?", "DISCARD GIFT", "CANCEL", R.drawable.ic_bear_musach_white);

        } else {

            dataManager.selectedBrand = null;
            dataManager.selectedGiftCard = null;
            dataManager.selectedService = null;
            finish();

        }
    }

    @Click(R.id.howItWorks)
    void howItWorkButtonClicked(){
        GiftItHowItWorksActivity_.intent(context).start();
    }

    @Click(R.id.ivInfo)
    void infoButtonClicked(){

        showFZAlert("PERSONALISE IT!", "We will display the receiver's name with what you enter here, for example, \"Rebecca Tan\". Don't add the email address or phone number here.", "GOT IT", "", R.drawable.ic_bear_baby_white);
    }

    @Click(R.id.tvHelp)
    void helpButtonClicked(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.gifting_help_menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:
                        WebActivity_.intent(context).titleExtra("FAQ").urlExtra("http://fuzzie.com.sg/faq.html#gifting").start();
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

    @AfterTextChange(R.id.etSearch)
    void searchEditChanged(){
        receiver.setName(etSearch.getText().toString());
        updateCheckOut();
    }

    @AfterTextChange(R.id.etMessage)
    void messageEditChanged(){

        updateCheckOut();
    }

    @Click(R.id.cvCheckout)
    void checkOutButtonClicked(){

        dataManager.setReceiver(receiver);

        PaymentActivity_.intent(context).isSent(true).messageExtra(etMessage.getText().toString()).start();
    }

    void updateCheckOut(){
        if (receiver.getName().length() > 0 && etMessage.getText().length() > 0){
            cvCheckout.setCardBackgroundColor(getResources().getColor(R.color.primary));
            cvCheckout.setEnabled(true);
        } else {
            cvCheckout.setCardBackgroundColor(getResources().getColor(R.color.loblolly));
            cvCheckout.setEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

     /*
     * FZAlertListner
     * */

    @Override
    public void FZAlertOkButtonClicked() {
        super.FZAlertOkButtonClicked();

        if (showBackAlert){

            dataManager.setReceiver(null);
            dataManager.selectedBrand = null;
            dataManager.selectedGiftCard = null;
            dataManager.selectedService = null;
            finish();

        }

    }

    @Override
    public void FZAlertCancelButtonClicked() {
        super.FZAlertCancelButtonClicked();

        if (showBackAlert){

            showBackAlert = false;

        }

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE){
            if (v == etSearch){
                etSearch.clearFocus();
                hideKeyboard();
            }
        }
        return false;
    }
}


