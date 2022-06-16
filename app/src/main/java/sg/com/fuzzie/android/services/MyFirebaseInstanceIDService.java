package sg.com.fuzzie.android.services;

import timber.log.Timber;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.JsonObject;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.utils.CurrentUser;

/**
 * Created by nurimanizam on 25/1/17.
 */


@EService
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Bean
    CurrentUser currentUser;

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String FCMToken = FirebaseInstanceId.getInstance().getToken();
        Timber.d("Refreshed token: " + FCMToken);

        sendFCMTokenToServer(FCMToken);
    }

    void sendFCMTokenToServer(String FCMToken) {
        Call<JsonObject> call = FuzzieAPI.APIService().registerFcm(currentUser.getAccesstoken(), FCMToken);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Timber.d("FCM Registration Loaded: " + response.code());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Timber.d("FCM Registration Error: " + t.getLocalizedMessage());
            }
        });
    }

}
