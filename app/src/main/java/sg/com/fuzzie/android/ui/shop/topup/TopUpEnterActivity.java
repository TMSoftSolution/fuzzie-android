package sg.com.fuzzie.android.ui.shop.topup;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import timber.log.Timber;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.bachors.prefixinput.EditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;


import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.ui.payments.TopUpPaymentActivity_;

import static sg.com.fuzzie.android.utils.Constants.REQUEST_TOP_UP_PAYMENT;

/**
 * Created by mac on 10/9/17.
 */

@EActivity(R.layout.activity_top_up_enter)
public class TopUpEnterActivity extends BaseActivity {

    @ViewById(R.id.etValue)
    EditText etValue;

    @ViewById(R.id.cvTopUp)
    CardView cvTopUp;

    int value;

    @AfterViews
    public void calledAfterViewInjection() {

        setupUI(findViewById(R.id.id_top_up_enter_activity));

        etValue.setPrefix("S$");
        etValue.setText("S$0");
        etValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().length() > 2){

                    try{
                        String enterValue = s.subSequence(2, s.length()).toString();
                        value = Integer.parseInt(enterValue);

                    } catch (NumberFormatException e){
                        Timber.e(e.getLocalizedMessage());
                    }

                    Timber.e(String.valueOf(value));
                }

                updateTopUpButton();
            }
        });

        etValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && (etValue.getText().length() < 2 || etValue.getText().toString().equals("S$0"))){
                    etValue.setText("S$");
                }
                if (hasFocus){
                    etValue.post(new Runnable() {
                        @Override
                        public void run() {
                            etValue.setSelection(etValue.getText().length());
                        }
                    });
                }
            }
        });

        etValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || actionId == EditorInfo.IME_ACTION_DONE){
                    if (etValue.getText().length() < 3){
                        etValue.setText("S$0");
                    }
                    hideKeyboard();
                    etValue.clearFocus();
                }
                return false;
            }
        });

        updateTopUpButton();
    }

    private void updateTopUpButton(){
        if (etValue.getText().length() < 3 || etValue.getText().length() == 3 && etValue.getText().toString().equals("S$0")){
            cvTopUp.setEnabled(false);
            cvTopUp.setCardBackgroundColor(getResources().getColor(R.color.loblolly));
            etValue.setTextColor(getResources().getColor(R.color.textHintColor));

        } else {
            cvTopUp.setEnabled(true);
            cvTopUp.setCardBackgroundColor(getResources().getColor(R.color.primary));
            etValue.setTextColor(getResources().getColor(R.color.textColorTertiary));

        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.cvTopUp)
    void topUpButtonClicked(){
        if (value < 10){
            showFZAlert("Oops!", "You have to choose a minimum of S$10 Fuzzie Credits.", "GOT IT", "", R.drawable.ic_bear_dead_white);
        } else if (value > 999){
            showFZAlert("Oops!", "You have exceeded the maximum top up amount of S$999.", "GOT IT", "", R.drawable.ic_bear_dead_white);
        } else {
            TopUpPaymentActivity_.intent(context).topUpPrice(value).startForResult(REQUEST_TOP_UP_PAYMENT);

        }
    }

    protected void setupUI(View view){
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof android.widget.EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (etValue.getText().length() < 3){
                        etValue.setText("S$0");
                    }
                    hideKeyboard();
                    etValue.clearFocus();
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TOP_UP_PAYMENT){
            if (resultCode == Activity.RESULT_OK){
                setResult(RESULT_OK, null);
                finish();
            }
        }
    }
}
