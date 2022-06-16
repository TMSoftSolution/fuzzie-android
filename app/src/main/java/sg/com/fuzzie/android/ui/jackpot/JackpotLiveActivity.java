package sg.com.fuzzie.android.ui.jackpot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;

import sg.com.fuzzie.android.api.models.JackpotResults;
import sg.com.fuzzie.android.views.ticker.TickerView;
import timber.log.Timber;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.MainActivity_;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.JackpotResult;
import sg.com.fuzzie.android.api.models.Prize;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.jackpot.PrizeItem;
import sg.com.fuzzie.android.items.jackpot.TicketItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.CustomTypefaceSpan;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_JACKPOT_IS_END;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_JACKPOT_RESULT_REFRESH;

/**
 * Created by joma on 10/20/17.
 */

@EActivity(R.layout.activity_jackpot_live)
public class JackpotLiveActivity extends BaseActivity {

    FastItemAdapter<TicketItem> combinationAdapter;
    FastItemAdapter<PrizeItem> prizeAdapter;

    private String drawId;
    private JackpotResult result;
    private JackpotResult liveResult;
    private boolean firstLoaded = true;
    private boolean showResultFirst = true;

    private Handler handler = new Handler();
    private long interval = 5000;
    private Prize currentWinning;
    private Prize previousWinning;
    private int winningAmount;
    private boolean isFirst;
    private boolean isNext;
    private Prize nextPrize;
    private Prize prevPrize;

    @Bean
    FuzzieData dataManager;

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.llLiveNow)
    View llLiveNow;

    @ViewById(R.id.llNextDraw)
    View llNextDraw;

    @ViewById(R.id.llSendBankDetails)
    View llSendBankDetails;

    @ViewById(R.id.tvBankDetails)
    TextView tvBankDetails;

    @ViewById(R.id.tvCongrate)
    TextView tvCongrate;

    @ViewById(R.id.rvCombination)
    RecyclerView rvCombination;

    @ViewById(R.id.tvNoParticipate)
    TextView tvNoParticipate;

    @ViewById(R.id.rvPrizes)
    RecyclerView rvPrizes;

    @ViewById(R.id.tvDrawAmount)
    TextView tvDrawAmount;

    @ViewById(R.id.tvDrawIdentifier)
    TextView tvDrawIdentifier;

    @ViewById(R.id.digit1)
    TickerView digit1;

    @ViewById(R.id.digit2)
    TickerView digit2;

    @ViewById(R.id.digit3)
    TickerView digit3;

    @ViewById(R.id.digit4)
    TickerView digit4;

    @ViewById(R.id.ivSlot)
    ImageView slot;

    @ViewById(R.id.llDigits)
    View llDigits;

    @ViewById(R.id.tvWatchingNumber)
    TextView tvWatchingNumber;

    @ViewById(R.id.givCommencing)
    GifImageView givCommencing;

    @AfterViews
    public void calledAfterViewInjection() {

        drawId = dataManager.getJackpotDrawId();

        initSlots();
        loadJackpotLiveDrawResult();

        combinationAdapter = new FastItemAdapter<>();
        rvCombination.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rvCombination.setAdapter(combinationAdapter);

        prizeAdapter = new FastItemAdapter<>();
        rvPrizes.setLayoutManager(new LinearLayoutManager(context));
        rvPrizes.setAdapter(prizeAdapter);

        Picasso.get()
                .load(R.drawable.bg_slot)
                .placeholder(R.drawable.bg_slot)
                .into(slot);

        digit1.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Brandon_bld.otf"));
        digit2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Brandon_bld.otf"));
        digit3.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Brandon_bld.otf"));
        digit4.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Brandon_bld.otf"));


        String first = "You did not participate in this draw. ";
        String second = "Get your tickets for the next draw!";
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Lato_Black.ttf");
        TypefaceSpan typefaceSpan = new CustomTypefaceSpan("", typeface);
        SpannableString spannableString  = new SpannableString(first + second);
        spannableString.setSpan(typefaceSpan, first.length(), first.length() + second.length() , 0);
        spannableString.setSpan(new ForegroundColorSpan(Color.rgb(250,62,63)), first.length(), first.length() + second.length(), 0);
        spannableString.setSpan(new UnderlineSpan(), first.length(), first.length() + second.length(), 0);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                goJackpotHomePage();
            }
        }, first.length(), first.length()+second.length(), 0);
        tvNoParticipate.setMovementMethod(LinkMovementMethod.getInstance());
        tvNoParticipate.setText(spannableString);

        if (dataManager.getJackpotResults() == null){
            loadJackpotResults();
        }

    }

    private void goJackpotHomePage(){
        Intent intent = new Intent(JackpotLiveActivity.this, MainActivity_.class);
        intent.putExtra("go_jackpot_coupon_list", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void initSlots(){
        digit1.setText("", false);
        digit2.setText("", false);
        digit3.setText("", false);
        digit4.setText("", false);
    }

    private void loadJackpotResults(){
        FuzzieAPI.APIService().getJackpotResult(currentUser.getAccesstoken()).enqueue(new Callback<JackpotResults>() {
            @Override
            public void onResponse(Call<JackpotResults> call, Response<JackpotResults> response) {
                if (response.code() == 200 && response.body() != null){
                    dataManager.setJackpotResults(response.body().getResults());
                    showMyCombinations();
                }
            }

            @Override
            public void onFailure(Call<JackpotResults> call, Throwable t) {
                Timber.e(t.getLocalizedMessage());
            }
        });
    }


    private void loadJackpotLiveDrawResult(){

        if (firstLoaded){
            showLoader();
        }

        FuzzieAPI.APIService().getJackpotLiveDrawResult(drawId).enqueue(new Callback<JackpotResult>() {
            @Override
            public void onResponse(Call<JackpotResult> call, Response<JackpotResult> response) {

                hideLoader();

                if (response.code() == 200 && response.body() != null){
                    liveResult = response.body();
                    if (firstLoaded){
                        startTimer();
                        showMyCombinations();
                    }
                    showResult();
                }
            }

            @Override
            public void onFailure(Call<JackpotResult> call, Throwable t) {
                hideLoader();
                Timber.e(t.getLocalizedMessage());
            }
        });
    }

    private void showResult(){
        if (firstLoaded){
            firstLoaded = false;
        }

        if (showResultFirst || prizeAdapter.getItemCount() == 0){
            showResultFirst = false;
            for (int i = 0 ; i < liveResult.getPrizes().size() ; i ++){
                Prize prize = liveResult.getPrizes().get(i);
                PrizeItem item = new PrizeItem(prize, i, checkWon(prize.getFourD()), i == liveResult.getPrizes().size() - 1);
                prizeAdapter.add(item);
            }
        } else {
            for (int i = 0 ; i < liveResult.getPrizes().size() ; i ++){
                Prize prize = liveResult.getPrizes().get(i);
                boolean won = checkWon(prize.getFourD());
                PrizeItem item = prizeAdapter.getAdapterItem(i);
                item.setPrize(prize);
                item.setWon(won);
                prizeAdapter.notifyDataSetChanged();
            }

        }


        currentWinning = liveResult.getCurrentWinning();
        nextPrize = liveResult.getNextPrize();
        if (nextPrize != null){
            if (prevPrize == null){

                if (liveResult.getCurrentWinning() != null){
                    prevPrize = new Prize(currentWinning.getFourD(), currentWinning.getIdentifier(), currentWinning.getName(), currentWinning.getAmount());
                } else {
                    prevPrize = new Prize(nextPrize.getFourD(), nextPrize.getIdentifier(), nextPrize.getName(), nextPrize.getAmount());
                }

                keepPrevPrize();
                new CountDownTimer(5000, 1000){

                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        prevPrize = new Prize(nextPrize.getFourD(), nextPrize.getIdentifier(), nextPrize.getName(), nextPrize.getAmount());
                        showNextPrize();
                    }
                }.start();

            } else {
                if (!nextPrize.getIdentifier().equals(prevPrize.getIdentifier())){

                    keepPrevPrize();
                    prevPrize = new Prize(nextPrize.getFourD(), nextPrize.getIdentifier(), nextPrize.getName(), nextPrize.getAmount());
                    new CountDownTimer(5000, 1000){

                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            showNextPrize();
                        }
                    }.start();

                }
            }


        } else {
            givCommencing.setVisibility(View.INVISIBLE);
            llDigits.setVisibility(View.VISIBLE);
        }


        if (currentWinning != null){

            if (previousWinning == null){

                previousWinning = currentWinning;

                showWonPage();
                showWinningNumber();

            } else {
                if (!previousWinning.getIdentifier().equals(currentWinning.getIdentifier())){

                    previousWinning = currentWinning;
                    showWonPage();
                    showWinningNumber();

                }
            }

            if (!isNext){
                isNext = true;
                givCommencing.setImageResource(R.drawable.commencing_next);
            }


        } else {

            if (!isFirst){
                isFirst = true;
                givCommencing.setImageResource(R.drawable.commencing_live);
            }
        }

        if (liveResult.getState().equals("ended")){
            endLiveDraw();
        }

        DecimalFormat format = new DecimalFormat("#,###");
        tvWatchingNumber.setText(format.format(liveResult.getWatchersCount()) + " watching now");

    }

    private void showWinningNumber(){

        String fourD = currentWinning.getFourD();
        if (fourD != null && fourD.length() == 4){

            initSlots();
            digit1.setText(fourD.substring(0, 1), true);
            digit2.setText(fourD.substring(1,2), true);
            digit3.setText(fourD.substring(2,3), true);
            digit4.setText(fourD.substring(3,4), true);

        }
    }

    private void showNextPrize(){
        llDigits.setVisibility(View.INVISIBLE);
        givCommencing.setVisibility(View.VISIBLE);
        if (nextPrize != null){
            tvDrawAmount.setText("S$" + String.valueOf(nextPrize.getAmount()));
            tvDrawIdentifier.setText(nextPrize.getName());
        }
    }

    private void keepPrevPrize(){
        llDigits.setVisibility(View.VISIBLE);
        if (currentWinning != null){
            givCommencing.setVisibility(View.INVISIBLE);
        } else {
            givCommencing.setVisibility(View.VISIBLE);
        }

        if (prevPrize != null){
            tvDrawAmount.setText("S$" + String.valueOf(prevPrize.getAmount()));
            tvDrawIdentifier.setText(prevPrize.getName());
        }
    }

    private void endLiveDraw(){

        stopTimer();

        if (dataManager.getHome() != null && dataManager.getHome().getJackpot() != null){

            dataManager.getHome().getJackpot().setLive(false);
            dataManager.getHome().getJackpot().setDrawTime(dataManager.getHome().getJackpot().getNextDrawTime());
            dataManager.getHome().getJackpot().setDrawId(dataManager.getHome().getJackpot().getNextDrawId());

            dataManager.setJackpotDrawId(dataManager.getHome().getJackpot().getDrawId());
            currentUser.setJackpotLiveDrawDateTime(dataManager.getHome().getJackpot().getNextDrawTime());

        }

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_JACKPOT_IS_END));

        new CountDownTimer(2000, 1000){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                updateLiveDrawEnd();

            }
        }.start();

    }

    private void updateLiveDrawEnd(){

        llLiveNow.setVisibility(View.GONE);
        if (checkWon()){

            int winningAmount = 0;
            if (result != null && liveResult != null){
                for (Prize prize : liveResult.getPrizes()){
                    for (List<String> tickets : result.getCombinations()){
                        if (tickets.contains(prize.getFourD())){
                            winningAmount = winningAmount + prize.getAmount() * tickets.size();
                            break;
                        }
                    }
                }
            }
            tvCongrate.setText("YOU'VE WON S$" + String.valueOf(winningAmount) + "!");

            llSendBankDetails.setVisibility(View.VISIBLE);
            String string = "Congrats " + currentUser.getCurrentUser().getFirstName() + "! Write to us at <b>jackpot@fuzzie.com.sg</b> with your bank details. We will transfer the prize money directly to your bank account within <b>30 days</b> from your email.";
            tvBankDetails.setText(FuzzieUtils.fromHtml(string));
            llNextDraw.setVisibility(View.GONE);
        } else {
            llSendBankDetails.setVisibility(View.GONE);
            llNextDraw.setVisibility(View.VISIBLE);
        }

    }

    private void showWonPage(){
        if (checkWon(liveResult.getCurrentWinning().getFourD())){

            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_JACKPOT_RESULT_REFRESH));

            new CountDownTimer(2000, 1000){

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    JackpotLiveWonActivity_.intent(context).prize(winningAmount).start();
                }
            }.start();

            showMyCombinations();
        }
    }

    private Runnable task = new Runnable() {
        @Override
        public void run() {

            handler.postDelayed(this,interval);
            loadJackpotLiveDrawResult();

        }
    };

    public void startTimer(){
        handler.removeCallbacks(task);
        handler.postDelayed(task, interval);
    }

    public void stopTimer(){
        handler.removeCallbacks(task);
    }

    private void showMyCombinations(){

        if (combinationAdapter == null){
            combinationAdapter = new FastItemAdapter<>();
        } else {
            combinationAdapter.clear();
        }

        if (result == null && dataManager.getJackpotResults() != null && liveResult != null){
            for (JackpotResult result : dataManager.getJackpotResults()){
                if (Objects.equals(result.getId(), liveResult.getId())){
                    this.result = result;
                    break;
                }
            }
        }

        if (result != null){

            if (result.getCombinations().size() > 0){

                tvNoParticipate.setVisibility(View.GONE);

                for (List<String> list : result.getCombinations()){
                    combinationAdapter.add(new TicketItem(list, checkWon(list)));
                }
            } else {

                tvNoParticipate.setVisibility(View.VISIBLE);
            }

        }
    }

    private boolean checkWon(){

        if (result != null && liveResult != null){
            if (result.getCombinations().size() > 1){
                for (List<String> tickets : result.getCombinations()){
                    if (checkWon(tickets)){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean checkWon(String fourD){

        if (result != null){
            for (List<String> list : result.getCombinations()){
                if (list.contains(fourD)){
                    winningAmount = liveResult.getCurrentWinning().getAmount() * list.size();
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkWon(List<String> list){

        if (liveResult != null){
            for (Prize prize : liveResult.getPrizes()){
                if (prize.getFourD().equals(list.get(0))){
                    return true;
                }
            }
        }

        return false;
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
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

    @Click(R.id.cvJoinNextDraw)
    void joinNextDrawButtonClicked(){
        goJackpotHomePage();
    }

    @Click(R.id.cvSendBankDetails)
    void sendBankDetails(){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","jackpot@fuzzie.com.sg", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Claiming The Fuzzie Jackpot prize");
        String body = "Name: " + currentUser.getCurrentUser().getFirstName() + " " + currentUser.getCurrentUser().getLastName() + "\nMobile: " + currentUser.getCurrentUser().getPhone()
                + "\nBank Name: \nAccount Number: ";
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        try {
            startActivity(Intent.createChooser(emailIntent, "Email jackpot@fuzzie.com.sg"));
        } catch (android.content.ActivityNotFoundException ex){
            Timber.e("No Email App Installed!");
            String first = "You need an email client to continue. Or write to us directly at ";
            String second = "jackpot@fuzzie.com.sg.";
            SpannableString spannableString = new SpannableString(first + second);
            spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),first.length(),first.length() + second.length(), 0);
            showFZAlert("OOPS!", spannableString, "GOT IT", "", R.drawable.ic_bear_dead_white);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!firstLoaded){
            startTimer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
    }
}
