package sg.com.fuzzie.android.utils;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by nurimanizam on 10/12/16.
 */

@SharedPref(value=SharedPref.Scope.UNIQUE)
public interface MyPrefs {

    String accessToken();
    String userJSON();
    String jackpotLiveDrawDateTime();
    String jackpotDrawId();
}
