package sg.com.fuzzie.android.ui.me.history;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.Order;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.history.OrderHistoryEmptyItem;
import sg.com.fuzzie.android.items.history.TransactionHistoryItem;
import sg.com.fuzzie.android.items.history.OrderHistoryHeaderItem;
import sg.com.fuzzie.android.items.history.OrderHistoryStickyHeaderAdapter;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

/**
 * Created by joma on 10/30/17.
 */

@EActivity(R.layout.activity_history)
public class OrderHistoryActivity extends BaseActivity {

    private static int LIMIT_PER_PAGE = 10;

    FastAdapter fastAdapter;
    ItemAdapter<OrderHistoryHeaderItem> headerAdapter;
    ItemAdapter transactionAdapter;
    OrderHistoryStickyHeaderAdapter stickyHeaderAdapter;
    List<Order> orders;
    private boolean endFirstLoading = false;
    private boolean loading = false;
    private boolean isLast = false;
    private int page = 1;


    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.rvTransaction)
    RecyclerView rvTransaction;

    @ViewById(R.id.loadMore)
    ProgressBar loadMore;

    @ViewById(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    LinearLayoutManager layoutManager;


    @AfterViews
    public void calledAfterViewInjection() {

        setUI();
        loadFirstOrderPage();

    }

    private void setUI(){

        headerAdapter = new ItemAdapter<OrderHistoryHeaderItem>();

        transactionAdapter = new ItemAdapter();

        stickyHeaderAdapter = new OrderHistoryStickyHeaderAdapter();
        final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);

        fastAdapter = FastAdapter.with(Arrays.asList(headerAdapter, transactionAdapter));

        layoutManager = new LinearLayoutManager(context);
        rvTransaction.setLayoutManager(layoutManager);
        rvTransaction.setHasFixedSize(false);
        rvTransaction.setAdapter(stickyHeaderAdapter.wrap(fastAdapter));
        rvTransaction.addItemDecoration(decoration);

        headerAdapter.add(new OrderHistoryHeaderItem());

        stickyHeaderAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                decoration.invalidateHeaders();
            }
        });


        swipeContainer.setRefreshing(false);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(false);
                loadFirstOrderPage();
            }
        });

        rvTransaction.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!loading && !isLast && endFirstLoading){
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 1
                            && firstVisibleItemPosition >= 0) {
                        loadMore.setVisibility(View.VISIBLE);
                        loading = true;
                        loadNextOrderPage();
                    }
                }

            }
        });
    }

    private void loadFirstOrderPage(){

        endFirstLoading = false;
        loading = false;
        isLast = false;
        page = 1;

        orders = new ArrayList<>();
        dataManager.setOrders(new ArrayList<Order>());

        showLoader();
        Call<List<Order>> call = FuzzieAPI.APIService().getOrders(currentUser.getAccesstoken(), page, LIMIT_PER_PAGE);
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                hideLoader();
                endFirstLoading = true;
                if (response.code() == 200 && response.body() != null){
                    isLast = response.body().size() % LIMIT_PER_PAGE != 0 || response.body().size() == 0;
                    orders.addAll(response.body());
                    dataManager.setOrders(orders);
                    showTransactions();

                } else if(response.code() == 417){
                    logoutUser(currentUser, dataManager, OrderHistoryActivity.this);
                } else {
                    showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                endFirstLoading = true;
                hideLoader();
                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
            }
        });
    }

    private void loadNextOrderPage(){
        page ++ ;
        Call<List<Order>> call = FuzzieAPI.APIService().getOrders(currentUser.getAccesstoken(), page, LIMIT_PER_PAGE);
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                loading = false;
                loadMore.setVisibility(View.GONE);
                if (response.code() == 200 && response.body() != null){
                    isLast = response.body().size() % LIMIT_PER_PAGE != 0 || response.body().size() == 0;
                    addTransactions(response.body());
                    orders.addAll(response.body());
                    dataManager.setOrders(orders);

                } else if(response.code() == 417){
                    logoutUser(currentUser, dataManager, OrderHistoryActivity.this);
                } else {
                    showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                loading = false;
                loadMore.setVisibility(View.GONE);
                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
            }
        });

    }

    private void showTransactions(){
        if (transactionAdapter == null){
            transactionAdapter = new ItemAdapter();
        } else {
            transactionAdapter.clear();
        }

        if (orders != null && orders.size() != 0){
            for (Order order : orders){
                transactionAdapter.add(new TransactionHistoryItem(order));
            }
        } else {
            transactionAdapter.add(new OrderHistoryEmptyItem());
        }

    }

    private void addTransactions(List<Order> orders){
        for (Order order : orders){
            transactionAdapter.add(new TransactionHistoryItem(order));
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }
}
