package sg.com.fuzzie.android.ui.jackpot;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import sg.com.fuzzie.android.api.models.JackpotResult;
import sg.com.fuzzie.android.items.jackpot.JackpotTicketsUseItem;
import sg.com.fuzzie.android.items.jackpot.JackpotTicketsViewPastItem;
import timber.log.Timber;

import com.google.gson.JsonObject;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.JackpotResults;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.jackpot.JackpotTicketsItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_JACKPOT_RESULT_REFRESHED;

/**
 * Created by joma on 10/19/17.
 */

@EActivity(R.layout.activity_jackpot_tickets)
public class JackpotTicketsActivity extends BaseActivity {

    BroadcastReceiver broadcastReceiver;

    FastItemAdapter adapter;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.rvJackpotTickets)
    RecyclerView rvJackpotTickets;

    @AfterViews
    public void calledAfterViewInjection() {

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BROADCAST_JACKPOT_RESULT_REFRESHED)){
                    showJackpotTickets();
                }
            }
        };
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_JACKPOT_RESULT_REFRESHED));

        adapter = new FastItemAdapter<>();
        rvJackpotTickets.setLayoutManager(new LinearLayoutManager(context));
        rvJackpotTickets.setAdapter(adapter);

        if (dataManager.getJackpotResults() == null){
            loadJackpotResults(true);
        } else {
            showJackpotTickets();
        }

        if (currentUser.getCurrentUser().getUnOpenedTicketCount() > 0){
            currentUser.resetUnopenedTicketCount();
            resetUnopenedTicketsCount();
        }
    }

    private void showJackpotTickets(){

        if (adapter == null){
            adapter = new FastItemAdapter<>();
        } else {
            adapter.clear();
        }

        adapter.add(new JackpotTicketsUseItem());
        for (JackpotResult result : dataManager.getJackpotResults()){

            if (dataManager.getHome().getJackpot().getDrawId() == null) break;

            if (Objects.equals(result.getId(), dataManager.getHome().getJackpot().getDrawId())){

                adapter.add(new JackpotTicketsItem(result));
                break;
            }
        }
        adapter.add(new JackpotTicketsViewPastItem());

    }

    private void loadJackpotResults(boolean refresh){

        if (refresh){
            showLoader();
        }

        FuzzieAPI.APIService().getJackpotResult(currentUser.getAccesstoken()).enqueue(new Callback<JackpotResults>() {
            @Override
            public void onResponse(Call<JackpotResults> call, Response<JackpotResults> response) {
                if (response.code() == 200 && response.body() != null){

                    hideLoader();
                    dataManager.setJackpotResults(response.body().getResults());
                    showJackpotTickets();

                }
            }

            @Override
            public void onFailure(Call<JackpotResults> call, Throwable t) {
                hideLoader();
                Timber.e(t.getLocalizedMessage());
            }
        });
    }

    private void resetUnopenedTicketsCount(){

        FuzzieAPI.APIService().resetUnopenedTicketsCount(currentUser.getAccesstoken()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
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

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

}
