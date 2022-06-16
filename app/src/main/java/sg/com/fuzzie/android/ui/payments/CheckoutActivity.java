package sg.com.fuzzie.android.ui.payments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.LocalBroadcastManager;
import timber.log.Timber;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.FuzzieApplication_;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.GiftCard;
import sg.com.fuzzie.android.api.models.Service;
import sg.com.fuzzie.android.api.models.ShoppingBag;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.Constants;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.ViewUtils;

/**
 * Created by nurimanizam on 28/12/16.
 */

@EActivity(R.layout.activity_checkout)
public class CheckoutActivity extends BaseActivity {

    Brand brand;
    GiftCard giftCard;
    Service service;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.ivBrandImage)
    ImageView ivBrandImage;

    @ViewById(R.id.tvGiftCardName)
    TextView tvGiftCardName;

    @ViewById(R.id.tvGiftCardValue)
    TextView tvGiftCardValue;

    @AfterViews
    public void calledAfterViewInjection() {

        if (dataManager.selectedBrand == null) return;
        brand = dataManager.selectedBrand;

        Bitmap background = ((FuzzieApplication_) getApplication()).lastActivityBitmap;
        if (background != null) {
            findViewById(R.id.content).setBackground(new BitmapDrawable(getResources(), background));
        } else {
            findViewById(R.id.content).setBackgroundColor(Color.parseColor("#D0000000"));
        }

        int cornerRadius = (int) ViewUtils.convertDpToPixel(4, this);

        Picasso.get()
                .load(brand.getBackgroundImageUrl())
                .placeholder(R.drawable.brands_placeholder)
                .transform(new RoundedCornersTransformation(cornerRadius, 0))
                .into(ivBrandImage);

        if (dataManager.selectedGiftCard != null) {

            giftCard = dataManager.selectedGiftCard;
            tvGiftCardName.setText(giftCard.getDisplayName());
            tvGiftCardValue.setText(giftCard.getPrice().getCurrencySymbol() + (int)giftCard.getDiscountedPrice());
        }

        if (dataManager.selectedService != null) {
            service = dataManager.selectedService;
            tvGiftCardName.setText(brand.getName() + " - " + service.getName());
            tvGiftCardValue.setText(service.getPrice().getCurrencySymbol() + (int)service.getDiscountedPrice());
        }
    }

    @Click(R.id.ivClose)
    void closeButtonClicked() {
        dataManager.selectedBrand = null;
        dataManager.selectedGiftCard = null;
        dataManager.selectedService = null;
        finish();
    }

    @Click(R.id.tvCheckout)
    void checkoutButtonClicked() {
        PaymentActivity_.intent(context).start();
    }

    @Click(R.id.tvAddToBag)
    void addToBagButtonClicked() {

        Call<JsonObject> addCall = null;

        if (giftCard != null) {
            addCall = FuzzieAPI.APIService().addToShoppingBag(currentUser.getAccesstoken(),giftCard.getType());
        } else if (service != null) {
            addCall = FuzzieAPI.APIService().addToShoppingBag(currentUser.getAccesstoken(),service.getType());
        }

        if (addCall == null) return;
        displayProgressDialog("Adding...");
        addCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                dismissProgressDialog();

                if (response.code() == 200) {

                    Intent intent = new Intent(Constants.BROADCAST_REFRESH_SHOPPING_BAG);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    Toast.makeText(context, "Added to Shopping Bag", Toast.LENGTH_SHORT).show();

                    dataManager.selectedBrand = null;
                    dataManager.selectedGiftCard = null;
                    dataManager.selectedService = null;
                    finish();


                } else if (response.code() == 406) {
                    if (response.errorBody() != null) {
                        try {
                            String jsonString = response.errorBody().string();
                            JsonParser parser = new JsonParser();
                            JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
                            if (jsonObject.get("error") != null) {
                                showFZAlert("Oops!", jsonObject.get("error").getAsString(), "OK", "", R.drawable.ic_bear_dead_white);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dismissProgressDialog();
                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);

            }
        });
    }


}
