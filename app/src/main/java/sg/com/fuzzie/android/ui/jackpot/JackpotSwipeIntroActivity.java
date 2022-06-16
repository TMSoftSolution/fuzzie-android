package sg.com.fuzzie.android.ui.jackpot;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.FZUser;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;

/**
 * Created by mac on 12/12/17.
 */

@EActivity(R.layout.activity_jackpot_swipe_intro)
public class JackpotSwipeIntroActivity extends BaseActivity {

    @Bean
    CurrentUser currentUser;

    @AfterViews
    public void calledAfterViewInjection() {

        FZUser user = currentUser.getCurrentUser();
        user.getSettings().setShowJackpotInstructions(false);
        currentUser.setCurrentUser(user);

        FuzzieAPI.APIService().setShowJackpotInstructions(currentUser.getAccesstoken(), false).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }

    @Click(R.id.cvGotIt)
    void gotItButtonClicked(){
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slid_out_up);
    }
}
