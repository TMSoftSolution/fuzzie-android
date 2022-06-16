package sg.com.fuzzie.android.ui.jackpot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.utils.CurrentUser;

import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.jackpot.JackpotDigitInputItem;
import sg.com.fuzzie.android.utils.FuzzieData;

/**
 * Created by joma on 10/15/17.
 */

@EActivity(R.layout.activity_jackpot_digit_enter)
public class JackpotDigitEnterActivity extends BaseActivity implements JackpotDigitInputItem.DigitInputListener{

    FastItemAdapter<JackpotDigitInputItem> adapter;
    boolean fullFilled = false;
    Boolean[] filled;
    private boolean isRandomise;
    private boolean isBack;

    LinearLayoutManager layoutManager;

    @Extra
    int count;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataMaganer;

    @ViewById(R.id.rvDigit)
    RecyclerView rvDigit;

    @ViewById(R.id.cvLock)
    CardView cvLock;

    @ViewById(R.id.tvLock)
    TextView tvLock;

    @ViewById(R.id.llRandom)
    LinearLayout llRandom;

    @ViewById(R.id.tvRandom)
    TextView tvRandom;

    @AfterViews
    public void calledAfterViewInjection() {

        tvRandom.setPaintFlags(tvRandom.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        adapter = new FastItemAdapter<JackpotDigitInputItem>();
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvDigit.setLayoutManager(layoutManager);
        rvDigit.setAdapter(adapter);
        rvDigit.setItemViewCacheSize(20);

        filled = new Boolean[count];
        dataMaganer.digits = new String[count];
        for (int i = 0 ; i < count ; i ++){
            dataMaganer.digits[i] = "";
            filled[i] = false;
            adapter.add(new JackpotDigitInputItem(count, i, this));
        }

        updateLockButton();

        if (count > 1 && currentUser.getCurrentUser().getSettings().isShowJackpotInstructions()){
            JackpotSwipeIntroActivity_.intent(context).start();
            overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
        }

    }

    private void lockTickets(){

        showPopView("PROCESSING", "", R.drawable.bear_craft, true);

        final ArrayList<String> four_d = new ArrayList<String>(Arrays.asList(dataMaganer.digits));
        FuzzieAPI.APIService().lockTickets(currentUser.getAccesstoken(), four_d).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                hidePopView();

                if (response.code() == 200){

                    JackpotTicketSetSuccessActivity_.intent(context).start();

                } else {

                    if (response.code() == 413 || response.code() == 416 || response.code() == 419 ){
                        String errorMessage = "";
                        if (response.errorBody() != null) {
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
                        } else {
                            errorMessage = "Unknown error occurred: " + response.code();
                        }

                        showFZAlert("Oops!", errorMessage, "GOT IT", "", R.drawable.ic_bear_dead_white);

                    } else if (response.code() == 412){

                        if (response.errorBody() != null){
                            try {
                                String jsonString = response.errorBody().string();
                                JsonParser parser = new JsonParser();
                                JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
                                if (jsonObject != null && jsonObject.get("four_d") != null){
                                    String first = "Your 4D number ";
                                    String fourD = jsonObject.get("four_d").getAsString();
                                    String second = " has been chosen by too many users. Pick another one!";
                                    SpannableString spannableString = new SpannableString(first + fourD + second);
                                    spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),first.length(),first.length() + fourD.length(), 0);
                                    showFZAlert("OOPS!", spannableString, "OK, GOT IT", "", R.drawable.ic_bear_dead_white);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "GOT IT", "", R.drawable.ic_bear_dead_white);
                        }


                    } else {
                        showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                hidePopView();
                showFZAlert("Oops!", "Unknown error occurred: " + t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
            }
        });
    }

    private void updateLockButton(){

        boolean filled = true;
        for (int i = 0 ; i < count ; i ++){
            filled = filled && this.filled[i];
        }

        fullFilled = filled;

        if (fullFilled){
            cvLock.setEnabled(true);
            cvLock.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tvLock.setAlpha(1.0f);
        } else {
            cvLock.setEnabled(false);
            cvLock.setCardBackgroundColor(getResources().getColor(R.color.loblolly));
            tvLock.setAlpha(0.4f);
        }
    }

    private boolean check4DEntered(){

        if (dataMaganer.digits == null){
            return false;
        } else {
            for (int i = 0 ; i < count ; i++){
                String fourD = dataMaganer.digits[i];
                if (fourD.length() == 4){
                    return true;
                }
            }
        }

        return false;
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        if (check4DEntered()){
            isBack = true;
            showFZAlert("DISCARD YOUR NUMBERS?", "You will lose your numbers if you proceed. Do you wish to continue?", "YES, DISCARD", "No, cancel", R.drawable.ic_bear_baby_white);
        } else {
            finish();
        }

    }

    @Click(R.id.tvHelp)
    void helpButtonClicked(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.jackpot_help_menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:
                        JackpotLearnMoreActvity_.intent(context).start();
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

    @Click(R.id.cvLock)
    void lockButtonClicked(){
        
        lockTickets();
    }

    @Click(R.id.llRandom)
    void randomButtonClicked(){

        isRandomise = true;
        showFZAlert("FEELING LUCKY?", "Do you wish to let the Bear choose all the numbers for you?", "YES, CHOOSE FOR ME!", "No, cancel", R.drawable.ic_bear_baby_white);

    }

    private String generateRandom4D(){

        Random random = new Random();

        return String.valueOf(random.nextInt(8999) + 1000);
    }

    @Override
    public void FZAlertOkButtonClicked() {
        super.FZAlertOkButtonClicked();
        if (isRandomise){

            isRandomise = false;
            for (int i = 0 ; i < count ; i ++){
                String digit = generateRandom4D();
                dataMaganer.digits[i] = digit;
                filled[i] = true;
            }
            adapter.notifyAdapterDataSetChanged();

        } else if (isBack){

            isBack = false;
            finish();

        }

    }

    @Override
    public void FZAlertCancelButtonClicked() {
        super.FZAlertCancelButtonClicked();

        isRandomise = false;
        isBack = false;
    }

    @Override
    public void fullDigitEntered(boolean filled, String digit, int index) {
//        Timber.e(TAG, digit);
        dataMaganer.digits[index] = digit;
        this.filled[index] = filled;
        updateLockButton();
    }
}
