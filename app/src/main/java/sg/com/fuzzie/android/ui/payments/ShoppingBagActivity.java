package sg.com.fuzzie.android.ui.payments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import sg.com.fuzzie.android.api.models.ShoppingBag;
import sg.com.fuzzie.android.api.models.ShoppingBagItem;
import sg.com.fuzzie.android.api.models.ShoppingBagItemDetail;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.shop.BagItem;
import sg.com.fuzzie.android.utils.Constants;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_CHANGED_SELETED_SHOPPING_BAG;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESHED_SHOPPING_BAG;

/**
 * Created by nurimanizam on 29/12/16.
 */

@EActivity(R.layout.activity_shopping_bag)
public class ShoppingBagActivity extends BaseActivity {

    ShoppingBag shoppingBag;
    FastItemAdapter<BagItem> bagAdapter;

    BroadcastReceiver broadcastReceiver;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvEditRight)
    TextView tvEditRight;

    @ViewById(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @ViewById(R.id.tvCheckout)
    TextView tvCheckout;

    @ViewById(R.id.rvShoppingBag)
    RecyclerView rvShoppingBag;

    @ViewById(R.id.cvCheckout)
    CardView cvCheckout;

    @ViewById(R.id.llDelete)
    LinearLayout llDelete;

    @ViewById(R.id.tvDelete)
    TextView tvDelete;

    private boolean isEditting = false;

    @AfterViews
    public void calledAfterViewInjection() {

        bagAdapter = new FastItemAdapter();
        dataManager.setEnableEditingBagItems(false);
        rvShoppingBag.setLayoutManager(new LinearLayoutManager(context));
        rvShoppingBag.setAdapter(bagAdapter);

        if (dataManager.getShoppingBag() != null) {
            shoppingBag = dataManager.getShoppingBag();
        }

        if (shoppingBag == null) {
            loadShoppingBag();
        } else {
            updateShoppingBag();
        }

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadShoppingBag();
            }
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(BROADCAST_REFRESHED_SHOPPING_BAG)) {
                updateShoppingBag();
            }

            if (intent.getAction().equals(BROADCAST_CHANGED_SELETED_SHOPPING_BAG)) {
                updateActionLayout();
            }

            }
        };

        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_REFRESHED_SHOPPING_BAG));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_CHANGED_SELETED_SHOPPING_BAG));

    }

    void loadShoppingBag() {

        if (swipeContainer != null) {
            swipeContainer.setRefreshing(true);
        }

        dataManager.emptyEdittingBags();

        Call<JsonObject> call = FuzzieAPI.APIService().getShoppingBag(currentUser.getAccesstoken());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dismissProgressDialog();

                if (swipeContainer != null) {
                    swipeContainer.setRefreshing(false);
                }

                if (response.code() == 200
                        && response.body() != null
                        && response.body().getAsJsonObject("shopping_bag") != null) {

                    ShoppingBag bag = ShoppingBag.fromJSON(response.body().getAsJsonObject("shopping_bag").toString());
                    shoppingBag = bag;
                    dataManager.setShoppingBag(shoppingBag);
                    updateShoppingBag();
                    updateActionLayout();

                    Intent intent = new Intent(Constants.BROADCAST_REFRESHED_SHOPPING_BAG);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  ShoppingBagActivity.this);
                } else {
                    dismissProgressDialog();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dismissProgressDialog();
                if (swipeContainer != null) {
                    swipeContainer.setRefreshing(false);
                }
            }
        });
    }


    private void removeMultipleItems(List<ShoppingBagItem>items) {
        if (items.size() == 0) {
            return;
        }

        JsonArray idArrays = new JsonArray();
        for(ShoppingBagItem item:items) {
            ShoppingBagItemDetail itemDetail = item.getItem();
            String type;
            if (itemDetail.getGiftCard() != null) {
                type = itemDetail.getGiftCard().getType();
            } else if (itemDetail.getService() != null) {
                type = itemDetail.getService().getType();
            } else {
                type = "";
            }
            idArrays.add(type);
        }
        JsonObject requestObject = new JsonObject();
        requestObject.add("ids", idArrays);

        displayProgressDialog("Removing...");
        Call<JsonObject> call = FuzzieAPI.APIService().removeItemsFromShoppingBag(currentUser.getAccesstoken(), requestObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                dataManager.emptyEdittingBags();

                if (response.code() == 200 && response.body() != null) {

                    Call<JsonObject> call1 = FuzzieAPI.APIService().getShoppingBag(currentUser.getAccesstoken());
                    call1.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            dismissProgressDialog();

                            if (response.code() == 200
                                    && response.body() != null
                                    && response.body().getAsJsonObject("shopping_bag") != null) {

                                ShoppingBag bag = ShoppingBag.fromJSON(response.body().getAsJsonObject("shopping_bag").toString());
                                shoppingBag = bag;
                                dataManager.setShoppingBag(shoppingBag);
                                updateShoppingBag();
                                updateActionLayout();

                                Intent intent = new Intent(Constants.BROADCAST_REFRESHED_SHOPPING_BAG);
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                            } else {
                                showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);

                            }

                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            dismissProgressDialog();
                        }
                    });
                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  ShoppingBagActivity.this);
                } else {
                    dismissProgressDialog();
                    showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dataManager.emptyEdittingBags();
                updateActionLayout();
                dismissProgressDialog();
                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);

            }
        });
    }


    @Click(R.id.ivClose)
    void closeButtonClicked() {
        isEditting = false;
        dataManager.setEnableEditingBagItems(false);
        finish();
    }

    @Click(R.id.cvCheckout)
    void checkoutButtonClicked() {

        if (shoppingBag != null && shoppingBag.getItems().size() > 0) {
            PaymentActivity_.intent(context).start();
        } else {
            showFZAlert("Oops!", "Your shopping bag is empty. Add items to your shopping bag by viewing your favourite brands.", "OK", "", R.drawable.ic_bear_dead_white);

        }
    }

    @Click(R.id.tvEditRight)
    void editRighMenuClicked() {
        isEditting  = !isEditting;
        dataManager.setEnableEditingBagItems(isEditting);
        if (isEditting) {
            tvEditRight.setText("Done");

        } else {
            tvEditRight.setText("Edit");
        }
        Intent intent = new Intent(Constants.BROADCAST_RELAYOUT_SHOPPING_BAG);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        if(isEditting) {
            cvCheckout.setVisibility(View.GONE);
            llDelete.setVisibility(View.VISIBLE);
        }else{
            cvCheckout.setVisibility(View.VISIBLE);
            llDelete.setVisibility(View.GONE);
        }
        updateActionLayout();
    }

    @Click(R.id.tvDelete)
    void deleteActionClicked() {
        if(dataManager.getSelectedBagItems().size() > 0) {
            removeMultipleItems(dataManager.getSelectedBagItems());
        }
    }

    void updateShoppingBag() {
        bagAdapter.clear();
        if (shoppingBag != null && shoppingBag.getItems().size() > 0) {
            for (ShoppingBagItem item : shoppingBag.getItems()) {
                BagItem temp = new BagItem(item);
                bagAdapter.add(temp);
            }

            tvCheckout.setText("S$"+ String.format("%.0f",shoppingBag.getTotalPrice()) );
        } else {

            dataManager.setEnableEditingBagItems(false);
            Intent emptyBagIntent = new Intent(context, EmptyBagActivity_.class);
            emptyBagIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(emptyBagIntent);
            finish();
        }

    }

    void updateActionLayout() {
        if(dataManager.getSelectedBagItems().size() == 0 ) {
            tvDelete.setTextColor(getResources().getColor(R.color.loblolly));
        }else{
            tvDelete.setTextColor(getResources().getColor(R.color.primary));
        }
    }

}
