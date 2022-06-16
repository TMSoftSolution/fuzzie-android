package sg.com.fuzzie.android.utils.network;

import java.io.IOException;

/**
 * Created by mac on 11/8/17.
 */

public class NoConnectivityException extends IOException {

    @Override
    public String getMessage() {
        return "The Internet connection appears to be offline.";
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }
}
