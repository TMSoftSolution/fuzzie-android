package sg.com.fuzzie.android.ui.redpacket;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.ui.jackpot.JackpotLearnMoreActvity_;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import timber.log.Timber;

/**
 * Created by movdev on 4/2/18.
 */

@EActivity(R.layout.activity_red_packet_quantity)
public class RedPacketQuantityActivity extends BaseActivity {

    @ViewById(R.id.tvNote)
    TextView tvNote;

    @ViewById(R.id.cvNext)
    CardView cvNext;

    @ViewById(R.id.etQuantity)
    EditText etQuantity;

    private int quantity;


    @AfterViews
    public void calledAfterViewInjection() {

        setupUI(findViewById(R.id.id_red_packet_quantity_activity));

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.showSoftInput(etQuantity, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);


        tvNote.setText(FuzzieUtils.fromHtml("HOW MANY <font color='#FA3E3F'>LUCKY PACKETS</font> DO YOU WISH TO SEND?"));

        updateNextButton();

        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (etQuantity.getText().toString().matches("^0")){
                    etQuantity.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                quantityValueChanged();
            }
        });

        etQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus){

                    if (etQuantity.getText().toString().equals("0")){
                        etQuantity.setText("");
                    }

                    etQuantity.post(new Runnable() {
                        @Override
                        public void run() {
                            etQuantity.setSelection(etQuantity.getText().length());
                        }
                    });

                }
            }
        });

        etQuantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || actionId == EditorInfo.IME_ACTION_DONE){

                    hideKeyboard();
                    etQuantity.clearFocus();
                }
                return false;
            }
        });
    }

    private void updateNextButton(){

        if (quantity != 0){

            cvNext.setEnabled(true);
            cvNext.setCardBackgroundColor(getResources().getColor(R.color.primary));

        } else {

            cvNext.setEnabled(false);
            cvNext.setCardBackgroundColor(getResources().getColor(R.color.loblolly));

        }


    }

    void quantityValueChanged(){

        if (etQuantity.getText().toString().length() > 0){

            try{

                quantity = Integer.parseInt(etQuantity.getText().toString());

            } catch (NumberFormatException e){
                Timber.e(e.getLocalizedMessage());
            }

        } else {

            quantity = 0;

        }

        updateNextButton();

    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.cvNext)
    void nextButtonClicked(){

        if (quantity == 1){

            showFZAlert("OOPS!", "You need to set 2 or more Lucky Packets with the group option.", "GOT IT", "", R.drawable.ic_bear_dead_white);

        } else {

            RedPacketItemsAddActivity_.intent(context).quantity(quantity).start();

        }
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
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Fuzzie Support");
        startActivity(Intent.createChooser(emailIntent, "Email support@fuzzie.com.sg"));
    }

    void facebookSupport() {
        String url = "http://m.me/fuzzieapp";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

}
