package sg.com.fuzzie.android.ui.jackpot;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import timber.log.Timber;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.adapter.JackpotDrawSpinnerAdapter;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.JackpotResult;
import sg.com.fuzzie.android.api.models.Prize;
import sg.com.fuzzie.android.api.models.JackpotResults;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.jackpot.PrizeItem;
import sg.com.fuzzie.android.items.jackpot.TicketItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.TimeUtils;
import sg.com.fuzzie.android.utils.ViewUtils;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_JACKPOT_RESULT_REFRESHED;

/**
 * Created by joma on 10/17/17.
 */

@EActivity(R.layout.activity_jackpot_draw_history)
public class JackpotDrawHistoryActivity extends BaseActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener, PopupWindow.OnDismissListener {

    BroadcastReceiver broadcastReceiver;
    List<JackpotResult> jackpotResults;
    JackpotResult jackpotResult;
    private PopupWindow pw;
    private ListView lv;
    private boolean opened;
    private int selectedPosition;
    private JackpotDrawSpinnerAdapter adapter;
    private int popupHeight, popupWidth;

    FastItemAdapter<TicketItem> combinationAdapter;
    FastItemAdapter<PrizeItem> prizeAdapter;

    @ViewById(R.id.rvCombination)
    RecyclerView rvCombination;

    @ViewById(R.id.rvPrizes)
    RecyclerView rvPrizes;

    @ViewById(R.id.llNoCombination)
    View llNoCombination;

    @ViewById(R.id.tvNoCombination)
    TextView tvNoCombination;

    @ViewById(R.id.llParticipate)
    View llParticipate;

    @ViewById(R.id.spinner)
    Button spinner;

    @ViewById(R.id.tvSpinner)
    TextView tvSpinner;

    @ViewById(R.id.ivArrow)
    ImageView ivArrow;

    @ViewById(R.id.llSpinner)
    View llSpinner;

    @Bean
    FuzzieData dataManager;

    @Bean
    CurrentUser currentUser;

    @AfterViews
    public void calledAfterViewInjection() {

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BROADCAST_JACKPOT_RESULT_REFRESHED)){
                    setSpinner();
                }
            }
        };
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_JACKPOT_RESULT_REFRESHED));

        combinationAdapter = new FastItemAdapter<>();
        rvCombination.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rvCombination.setAdapter(combinationAdapter);

        prizeAdapter = new FastItemAdapter<>();
        rvPrizes.setLayoutManager(new LinearLayoutManager(context));
        rvPrizes.setAdapter(prizeAdapter);

        if (dataManager.getJackpotResults() == null){
            loadJackpotResults();
        } else {
            setSpinner();
        }
    }

    private void loadJackpotResults(){
        showLoader();
        FuzzieAPI.APIService().getJackpotResult(currentUser.getAccesstoken()).enqueue(new Callback<JackpotResults>() {
            @Override
            public void onResponse(Call<JackpotResults> call, Response<JackpotResults> response) {
                if (response.code() == 200 && response.body() != null){
                    hideLoader();
                    dataManager.setJackpotResults(response.body().getResults());
                    setSpinner();
                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  JackpotDrawHistoryActivity.this);
                }
            }

            @Override
            public void onFailure(Call<JackpotResults> call, Throwable t) {
                hideLoader();
                Timber.e(t.getLocalizedMessage());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showSelectedResult(){

        if (jackpotResult.getCombinations().size() == 0){
            rvCombination.setVisibility(View.GONE);
            llNoCombination.setVisibility(View.VISIBLE);

            DateTimeFormatter parser = ISODateTimeFormat.dateTime();
            DateTime now = new DateTime();
            DateTime drawTime = parser.parseDateTime(jackpotResult.getDrawDateTime());
            if (now.isAfter(drawTime)){
                llParticipate.setVisibility(View.INVISIBLE);
                tvNoCombination.setText("You didn’t participate in this draw.");
            } else {
                llParticipate.setVisibility(View.VISIBLE);
                tvNoCombination.setText("You have not chosen your 4D number.");
            }


        } else {
            rvCombination.setVisibility(View.VISIBLE);
            llNoCombination.setVisibility(View.GONE);

            if (combinationAdapter == null){
                combinationAdapter = new FastItemAdapter<>();
            } else {
                combinationAdapter.clear();
            }

            for (List<String> list : jackpotResult.getCombinations()){
                combinationAdapter.add(new TicketItem(list, checkWon(list)));
            }
        }

        if (prizeAdapter == null){
            prizeAdapter = new FastItemAdapter<>();
        } else {
            prizeAdapter.clear();
        }

        for (int i = 0 ; i < jackpotResult.getPrizes().size() ; i ++){
            Prize prize = jackpotResult.getPrizes().get(i);
            prizeAdapter.add(new PrizeItem(prize, i, checkWon(prize.getFourD()), i == jackpotResult.getPrizes().size() - 1));
        }

    }

    private void setSpinner(){

        jackpotResults = new ArrayList<>(dataManager.getJackpotResults());
        jackpotResults.remove(0);
        if (jackpotResults.get(0).getId().equals(dataManager.getJackpotDrawId())){
            jackpotResults.remove(0);
        }

        if (jackpotResults.size() > 3){
            popupHeight = (int) ViewUtils.convertDpToPixel(175, this);
        } else {
            popupHeight = (int) ViewUtils.convertDpToPixel(50 * jackpotResults.size(), this);
        }

        popupWidth = ViewUtils.getScreenWidth(this) - (int) ViewUtils.convertDpToPixel(32, this);

        JackpotDrawSpinnerAdapter adapter = new JackpotDrawSpinnerAdapter(this, jackpotResults);
        setAdapter(adapter);

        jackpotResult = jackpotResults.get(0);

        updateSpinner();
        showSelectedResult();

    }

    private void updateSpinner(){

        tvSpinner.setText(TimeUtils.jackpotDrawHistoryFormat(jackpotResult.getDrawDateTime()));

    }

    private boolean checkWon(String fourD){

        for (List<String> list : jackpotResult.getCombinations()){
            if (list.contains(fourD)){
                return true;
            }
        }

        return false;
    }

    private boolean checkWon(List<String> list){

        if (jackpotResult != null){
            for (Prize prize : jackpotResult.getPrizes()){
                if (prize.getFourD().equals(list.get(0))){
                    return true;
                }
            }
        }

        return false;
    }

    public void setAdapter(JackpotDrawSpinnerAdapter adapter) {
        if (adapter != null) {
            this.adapter = adapter;
            if (lv == null)
                lv = new ListView(this);
            // lv.setCacheColorHint(0);
            lv.setAdapter(adapter);
            lv.setDividerHeight(0);
            lv.setBackgroundResource(R.drawable.bg_jackpot_draw_history_pop);
            lv.setOnItemClickListener(this);
            lv.setOnItemSelectedListener(this);
            lv.setSelector(android.R.color.transparent);
            lv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            lv.setOverScrollMode(View.OVER_SCROLL_NEVER);
            selectedPosition = 0;
        } else {
            selectedPosition = -1;
            this.adapter = null;
        }
    }

    private void updateArrow(){
        if (opened){
            ivArrow.setImageResource(R.drawable.ic_arrow_top_red);
            llSpinner.setBackgroundResource(R.drawable.bg_jackpot_draw_history_spinner_selected);

        } else {
            ivArrow.setImageResource(R.drawable.ic_arrow_bottom_red);
            llSpinner.setBackgroundResource(R.drawable.bg_white);
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.llParticipate)
    void participateButtonClicked(){
        JackpotHomeActivity_.intent(context).start();
    }

    @Click(R.id.tvHelp)
    void helpButtonClicked(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.activate_help_menu, new DialogInterface.OnClickListener() {
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
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Help with Fuzzie Jackpot");
        startActivity(Intent.createChooser(emailIntent, "Help with Fuzzie Jackpot"));
    }

    void facebookSupport() {
        String url = "http://m.me/fuzzieapp";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Click(R.id.spinner)
    void spinnerClicked(){
        if (!opened){
            if (pw == null || !pw.isShowing()) {
                pw = new PopupWindow(spinner);
                pw.setContentView(lv);
                pw.setWidth(popupWidth);
                pw.setHeight(popupHeight);
                pw.setOutsideTouchable(true);
                pw.setFocusable(true);
                pw.setClippingEnabled(true);
                pw.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_jackpot_draw_history_pop));
                pw.showAsDropDown(spinner, spinner.getLeft(),0);
                pw.setOnDismissListener(this);
                opened = true;
                updateArrow();
            }

        } else {
            if (pw != null){
                pw.dismiss();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (pw != null)
            pw.dismiss();
        selectedPosition = position;
        jackpotResult = jackpotResults.get(selectedPosition);
        updateSpinner();
        showSelectedResult();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDismiss() {
        pw = null;
        if (opened){
            opened = false;
            updateArrow();
        }

    }
}
