package sg.com.fuzzie.android.ui.shop;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.Bank;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.payment.BankItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

/**
 * Created by nurimanizam on 22/6/17.
 */

@EActivity(R.layout.activity_banks)
public class BanksActivity extends BaseActivity {

    FastItemAdapter bankAdapter;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @ViewById(R.id.rvBanks)
    RecyclerView rvBanks;

    @AfterViews
    public void calledAfterViewInjection() {

        bankAdapter = new FastItemAdapter();
        rvBanks.setLayoutManager(new LinearLayoutManager(context));
        rvBanks.setAdapter(bankAdapter);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            loadBanks();
            }
        });

        loadBanks();
    }

    void loadBanks() {

        if (swipeContainer != null) {
            swipeContainer.setRefreshing(true);
        }

        Call<List<Bank>> call =  FuzzieAPI.APIService().getBanks(currentUser.getAccesstoken());
        call.enqueue(new Callback<List<Bank>>() {
            @Override
            public void onResponse(Call<List<Bank>> call, Response<List<Bank>> response) {

                if (swipeContainer != null) {
                    swipeContainer.setRefreshing(false);
                }

                bankAdapter.clear();

                for (Bank bank : response.body()) {
                    bankAdapter.add(new BankItem(bank));
                }
            }

            @Override
            public void onFailure(Call<List<Bank>> call, Throwable t) {

                if (swipeContainer != null) {
                    swipeContainer.setRefreshing(false);
                }

                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
            }
        });
    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

}
