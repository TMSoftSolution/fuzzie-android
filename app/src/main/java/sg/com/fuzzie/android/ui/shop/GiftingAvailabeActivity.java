package sg.com.fuzzie.android.ui.shop;

import android.text.SpannableString;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.FZUser;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.ui.gift.GiftItHowItWorksActivity_;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

/**
 * Created by mac on 7/7/17.
 */

@EActivity(R.layout.activity_gifting_available)
public class GiftingAvailabeActivity extends BaseActivity {

    @ViewById(R.id.tvDesc)
    TextView tvDesc;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @AfterViews
    public void calledAfterViewInjection() {

        SpannableString spannablecontent=new SpannableString(getString(R.string.gifting_available));
//        spannablecontent.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
//                62,73, 0);
//        spannablecontent.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
//                102,105, 0);
//        spannablecontent.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
//                116,123, 0);
        tvDesc.setText(spannablecontent);

        setDisplayGiftingPage();

    }

    private void setDisplayGiftingPage(){

        Call<Void> call = FuzzieAPI.APIService().setDisplayGiftingPage(currentUser.getAccesstoken(), false);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200){
                    FZUser user = currentUser.getCurrentUser();
                    user.getSettings().setDisplayGiftingPage(false);
                    currentUser.setCurrentUser(user);
                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  GiftingAvailabeActivity.this);
                } else {
                    FZUser user = currentUser.getCurrentUser();
                    user.getSettings().setDisplayGiftingPage(true);
                    currentUser.setCurrentUser(user);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }

    @Click(R.id.cvGotIt)
    void gotItButtonClicked(){
        finish();
    }

    @Click(R.id.howItWorks)
    void hotItWorksButtonClicked(){
        GiftItHowItWorksActivity_.intent(context).start();
    }
}
