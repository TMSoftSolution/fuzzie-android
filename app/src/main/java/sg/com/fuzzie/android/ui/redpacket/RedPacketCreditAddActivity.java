package sg.com.fuzzie.android.ui.redpacket;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.ui.shop.topup.TopUpActivity_;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.ViewUtils;
import timber.log.Timber;

/**
 * Created by movdev on 4/2/18.
 */

@EActivity(R.layout.activity_red_packet_credit_add)
public class RedPacketCreditAddActivity extends BaseActivity {

    private double tempCredits;
    private boolean tempRandom;
    private boolean isTopUp;
    private boolean isDiscard;

    @Bean
    CurrentUser currentUser;

    @Extra
    int quantity;

    @Extra
    double credits;

    @Extra
    boolean isRandom;

    @ViewById(R.id.tvCreditsNote)
    TextView tvCreditsNote;

    @ViewById(R.id.etCredits)
    EditText etCredits;

    @ViewById(R.id.tvRandom)
    TextView tvRandom;

    @ViewById(R.id.tvEqual)
    TextView tvEqual;

    @ViewById(R.id.tvSplitNote)
    TextView tvSplitNote;

    @AfterViews
    public void calledAfterViewInjection() {

        setupUI(findViewById(R.id.id_red_packet_credit_add_activity));

        tempCredits = credits;
        tempRandom = isRandom;

        if (quantity == 1){
            ViewUtils.invisible(findViewById(R.id.splitView));
        }

        etCredits.setText("S$" + (int)tempCredits);
        tvCreditsNote.setText(String.format("S$%.2f available", currentUser.getCurrentUser().getWallet().getCashableBalance()));

        etCredits.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus){

                    if (etCredits.getText().length() < 2 || etCredits.getText().toString().equals("S$0")){
                        etCredits.setText("S$");
                    }

                    etCredits.post(new Runnable() {
                        @Override
                        public void run() {
                            etCredits.setSelection(etCredits.getText().length());
                        }
                    });

                } else {

                }
            }
        });

        etCredits.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || actionId == EditorInfo.IME_ACTION_DONE){
                    if (etCredits.getText().length() < 3 || etCredits.getText().toString().equals("S$0")){
                        etCredits.setText("");
                    }
                    hideKeyboard();
                    etCredits.clearFocus();
                }
                return false;
            }
        });

        etCredits.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() < 3){

                    etCredits.setText("S$0");
                    etCredits.post(new Runnable() {
                        @Override
                        public void run() {
                            etCredits.setSelection(etCredits.getText().length());
                        }
                    });

                } else if (s.length() >= 4 && s.charAt(2) == '0'){

                    etCredits.setText("S$"  + s.charAt(3));
                    etCredits.post(new Runnable() {
                        @Override
                        public void run() {
                            etCredits.setSelection(etCredits.getText().length());
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateCredits();
            }
        });

        updateSplitMode();
        updateSplitNote();

    }

    private void updateSplitMode(){

        if (quantity > 1) {

            if (tempRandom){

                tvRandom.setText(FuzzieUtils.fromHtml("<font color='#424242'>SPLIT<br></br></font><font color='#FA3E3F'>RANDOMLY</font>"));
                tvRandom.setAlpha(1.0f);
                tvEqual.setText(FuzzieUtils.fromHtml("<font color='#424242'>SPLIT<br></br></font><font color='#424242'>EQUALLY</font>"));
                tvEqual.setAlpha(0.3f);

            } else {

                tvRandom.setText(FuzzieUtils.fromHtml("<font color='#424242'>SPLIT<br></br></font><font color='#424242'>RANDOMLY</font>"));
                tvRandom.setAlpha(0.3f);
                tvEqual.setText(FuzzieUtils.fromHtml("<font color='#424242'>SPLIT<br></br></font><font color='#FA3E3F'>EQUALLY</font>"));
                tvEqual.setAlpha(1.0f);
            }
        }
    }

    private void updateSplitNote(){

        if (quantity > 1){

            if (tempRandom){

                tvSplitNote.setText("Your budget will be split randomly among all your Lucky Packets.");

            } else {

                double credit = tempCredits / quantity;
                tvSplitNote.setText(FuzzieUtils.fromHtml("Your budget will be split equally. Each Lucky Packet will contain <font color='#FA3E3F'><b>" + String.format("S$%.2f", credit) + "</b></font>"));

            }
        }
    }

    private void updateCredits(){

        if (etCredits.getText().toString().length() > 2){

            try{
                String enterValue = etCredits.getText().subSequence(2, etCredits.getText().length()).toString();
                tempCredits = Double.parseDouble(enterValue);

            } catch (NumberFormatException e){
                Timber.e(e.getLocalizedMessage());
            }


        } else {

            tempCredits = 0.0;

        }

        if (!tempRandom){
            updateSplitNote();
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){

        if (credits != tempCredits || isRandom != tempRandom){

            showFZAlert("DISCARD LUCKY PACKET?", "Do you wish to discard your Lucky Packet?", "YES, DISCARD", "No, cancel", R.drawable.ic_normal_bear);
            isDiscard = true;

        } else {

            finish();

        }
    }

    @Click(R.id.tvRandom)
    void randomButtonClicked(){

        tempRandom = true;
        updateSplitMode();
        updateSplitNote();
        etCredits.clearFocus();
    }

    @Click(R.id.tvEqual)
    void equalButtonClicked(){

        tempRandom = false;
        updateSplitMode();
        updateSplitNote();
        etCredits.clearFocus();
    }

    @Click(R.id.cvSave)
    void saveButtonClicked(){

        if (tempCredits > currentUser.getCurrentUser().getWallet().getCashableBalance()){

            showFZAlert("OOPS!", "You don't have enough credits.", "TOP UP CREDITS NOW", "Close", R.drawable.ic_normal_bear);
            isTopUp = true;

        } else {

            Intent intent = new Intent();
            intent.putExtra("credits", tempCredits);
            intent.putExtra("isRandom", tempRandom);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void FZAlertOkButtonClicked() {
        super.FZAlertOkButtonClicked();

        if (isTopUp){

            goTopUpPage();
            isTopUp = false;

        } else if (isDiscard){

            finish();
        }
    }

    @Override
    public void FZAlertCancelButtonClicked() {
        super.FZAlertCancelButtonClicked();

        isTopUp = false;
        isDiscard = false;
    }

    @Override
    public void onBackPressed() {
        backButtonClicked();
    }

    private void goTopUpPage(){

        TopUpActivity_.intent(context).start();
    }
}
