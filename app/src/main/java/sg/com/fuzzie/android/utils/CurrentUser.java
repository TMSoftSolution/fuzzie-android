package sg.com.fuzzie.android.utils;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import sg.com.fuzzie.android.api.models.FZUser;

/**
 * Created by nurimanizam on 10/12/16.
 */

@EBean(scope = EBean.Scope.Singleton)
public class CurrentUser {

    @Pref
    MyPrefs_ prefs;

    public Boolean isLoggedIn() {
        return (getAccesstoken() != null
                && !getAccesstoken().trim().equals("")
                && getCurrentUser() != null
                && getCurrentUser().getPhone() != null
                && !getCurrentUser().getPhone().trim().equals(""));
    }

    public String getAccesstoken() {
        return prefs.accessToken().get();
    }

    public void setAccesstoken(String accesstoken) {
        if (accesstoken != null) {
            prefs.edit().accessToken().put(accesstoken).apply();
        } else {
            prefs.edit().accessToken().remove().apply();
        }
    }

    public FZUser getCurrentUser() {
        Gson gson = new Gson();
        try {

            return gson.fromJson(prefs.userJSON().get(), FZUser.class);

        } catch (OutOfMemoryError error){

            return null;
        }
    }

    public void setCurrentUser(FZUser user) {
        if (user != null) {

            Crashlytics.setUserIdentifier(user.getFuzzieId());
            Crashlytics.setUserEmail(user.getEmail());

            Gson gson = new Gson();
            String userJSON = gson.toJson(user);
            prefs.edit().userJSON().put(userJSON).apply();
        } else {
            prefs.edit().userJSON().remove().apply();
        }

    }

    public String getJackpotLiveDrawDateTime(){
        return prefs.jackpotLiveDrawDateTime().get();
    }

    public void setJackpotLiveDrawDateTime(String dateTime){
        if (dateTime != null){
            prefs.edit().jackpotLiveDrawDateTime().put(dateTime).apply();
        } else {
            prefs.edit().jackpotLiveDrawDateTime().remove().apply();
        }
    }


    public void changeUnopenedRedPacketCount(int offset){

        if (getCurrentUser() != null){

            FZUser user = getCurrentUser();
            user.setUnOpenedRedPacketCount(user.getUnOpenedRedPacketCount() + offset);
            setCurrentUser(user);

        }

    }

    public void changeActiveGiftCount(int offset){

        if (getCurrentUser() != null){

            FZUser user = getCurrentUser();
            user.setActiveGiftCount(user.getActiveGiftCount() + offset);
            setCurrentUser(user);

        }

    }

    public void changeUnopenedGiftCount(int offset){

        if (getCurrentUser() != null){

            FZUser user = getCurrentUser();
            user.setUnOpenedGiftCount(user.getUnOpenedGiftCount() + offset);
            setCurrentUser(user);

        }

    }

    public void resetUnopenedGiftsCount(){

        if (getCurrentUser() != null){

            FZUser user = getCurrentUser();
            user.setUnOpenedGiftCount(0);
            setCurrentUser(user);

        }
    }

    public void resetUnopenedTicketCount(){

        if (getCurrentUser() != null){

            FZUser user = getCurrentUser();
            user.setUnOpenedTicketCount(0);
            setCurrentUser(user);

        }
    }

    public void likeBrand(String brandId, boolean like){

        if (getCurrentUser() != null){

            FZUser user = getCurrentUser();

            if (like){

                if (!user.getLikedListIds().contains(brandId)){
                    user.getLikedListIds().add(brandId);
                }

            } else {

                if (user.getLikedListIds().contains(brandId)){
                    user.getLikedListIds().remove(brandId);
                }

            }

            setCurrentUser(user);
        }
    }

    public void wishListBrand(String brandId, boolean wish){

        if (getCurrentUser() != null){

            FZUser user = getCurrentUser();

            if (wish){

                if (!user.getWishListIds().contains(brandId)){
                    user.getWishListIds().add(brandId);
                }

            } else {

                if (user.getWishListIds().contains(brandId)){
                    user.getWishListIds().remove(brandId);
                }

            }

            setCurrentUser(user);
        }
    }

    public void bookmarkStore(String storeId, boolean bookmarked){

        if (getCurrentUser() != null){

            FZUser user = getCurrentUser();

            if (bookmarked){

                if (!user.getBookmakredStoreIds().contains(storeId)){
                    user.getBookmakredStoreIds().add(0, storeId);
                }

            } else {

                if (user.getBookmakredStoreIds().contains(storeId)){
                    user.getBookmakredStoreIds().remove(storeId);
                }

            }

            setCurrentUser(user);
        }

    }
}
