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
import sg.com.fuzzie.android.ui.jackpot.JackpotHomeActivity_;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.ViewUtils;
import timber.log.Timber;


/**
 * Created by movdev on 4/2/18.
 */

@EActivity(R.layout.activity_red_packet_ticket_add)
public class RedPacketTicketAddActivity extends BaseActivity {

    private boolean isGetMore;
    private boolean isDiscard;
    private boolean isEditDiscard;

    private int tempCount;

    @Bean
    CurrentUser currentUser;

    @Extra
    int quantity;

    @Extra
    int count;

    @ViewById(R.id.tvTicketNote)
    TextView tvTicketNote;

    @ViewById(R.id.etTicket)
    EditText etTicket;

    @ViewById(R.id.tvTotalTicket)
    TextView tvTotalTicket;

    @ViewById(R.id.tvGetMore)
    TextView tvGetMore;

    @ViewById(R.id.tvQuantityNote)
    TextView tvQuantityNote;

    @ViewById(R.id.tvTicketGive)
    TextView tvTicketGive;

    @AfterViews
    public void calledAfterViewInjection() {

        setupUI(findViewById(R.id.id_red_packet_ticket_add_activity));

        tempCount = count;
        tvGetMore.setText(FuzzieUtils.fromHtml("Get more Jackpot tickets <font color='#FA3E3F'><b>here</b></font>"));

        etTicket.setText(String.valueOf(count));

        if (currentUser.getCurrentUser().getAvailableJackpotTicketsCount() > 1){

            tvTicketNote.setText(String.format("%d Jackpot tickets available", currentUser.getCurrentUser().getAvailableJackpotTicketsCount()));

        } else {

            tvTicketNote.setText(String.format("%d Jackpot ticket available", currentUser.getCurrentUser().getAvailableJackpotTicketsCount()));

        }

        if (quantity == 1){

            ViewUtils.gone(findViewById(R.id.llTotalTickets));
            ViewUtils.gone(findViewById(R.id.middleSeparator));

            tvTicketGive.setText("JACKPOT TICKETS TO GIVE");

        } else {

            tvQuantityNote.setText(String.format("For %d Packets", quantity));
            tvTotalTicket.setText(String.valueOf(quantity * count));

        }

        etTicket.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus){

                    if (etTicket.getText().toString().equals("0")){
                        etTicket.setText("");
                    }

                    etTicket.post(new Runnable() {
                        @Override
                        public void run() {
                            etTicket.setSelection(etTicket.getText().length());
                        }
                    });

                } else {
                    if (etTicket.getText().toString().equals("")){
                        etTicket.setText("0");
                    }
                }
            }
        });

        etTicket.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() >= 2 && (s.charAt(0) == '0')){
                    etTicket.setText(s.subSequence(1, s.length()));
                    etTicket.post(new Runnable() {
                        @Override
                        public void run() {
                            etTicket.setSelection(etTicket.getText().length());
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateTickets();
            }
        });

        etTicket.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || actionId == EditorInfo.IME_ACTION_DONE){

                    hideKeyboard();
                    etTicket.clearFocus();
                }
                return false;
            }
        });

    }

    private void updateTickets(){

        if (etTicket.getText().toString().length() > 0){

            try{

                tempCount = Integer.parseInt(etTicket.getText().toString());

            } catch (NumberFormatException e){
                Timber.e(e.getLocalizedMessage());
            }

        } else {

            tempCount = 0;
        }

        if (quantity > 1){
            tvTotalTicket.setText(String.valueOf(quantity * tempCount));
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){

        if (count != tempCount){

            showFZAlert("DISCARD LUCKY PACKET?", "Do you wish to discard your Lucky Packet?", "YES, DISCARD", "No, cancel", R.drawable.ic_normal_bear);
            isEditDiscard = true;

        } else {

            finish();
        }
    }

    @Click(R.id.cvSave)
    void saveButtonClicked(){

        if (currentUser.getCurrentUser().getAvailableJackpotTicketsCount() < quantity * tempCount){

            showFZAlert("OOPS!", "You don't have enough Jackpot tickets available.", "GET MORE TICKETS", "Close", R.drawable.ic_normal_bear);
            isGetMore = true;

        } else {

            Intent intent = new Intent();
            intent.putExtra("count", tempCount);
            setResult(RESULT_OK, intent);
            finish();
        }

    }

    @Click(R.id.tvGetMore)
    void getMoreButtonClicked(){

        showGetMoreAlert();
    }

    private void showGetMoreAlert(){

        showFZAlert("DISCARD LUCKY PACKET?", "To get more Jackpot tickets now, you will need to discard your Lucky Packet. Do you wish to continue?", "YES, CONTINUE", "No, cancel", R.drawable.ic_normal_bear);
        isDiscard = true;
    }

    private void goJackpotHomePage(){

        JackpotHomeActivity_.intent(context).start();
    }

    @Override
    public void FZAlertOkButtonClicked() {
        super.FZAlertOkButtonClicked();

        if (isGetMore){

            isGetMore = false;
            showGetMoreAlert();

        } else if (isDiscard){

            isDiscard = false;
            goJackpotHomePage();

        } else if (isEditDiscard){

            finish();
        }
    }

    @Override
    public void FZAlertCancelButtonClicked() {
        super.FZAlertCancelButtonClicked();

        isGetMore = false;
        isDiscard = false;
        isEditDiscard = false;
    }

    @Override
    public void onBackPressed() {
        backButtonClicked();
    }
}
