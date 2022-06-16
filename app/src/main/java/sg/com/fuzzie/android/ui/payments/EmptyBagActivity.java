package sg.com.fuzzie.android.ui.payments;

import android.content.Intent;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import sg.com.fuzzie.android.MainActivity_;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

/**
 * Created by hiennh on 4/24/17.
 */

@EActivity(R.layout.activity_empty_bag)
public class EmptyBagActivity extends BaseActivity {

    @Click(R.id.cvStartShopping)
    void onStartShoppingClicked() {
        Intent intent = new Intent(context, MainActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
    @Click(R.id.ivBack)
    void closeButtonClicked() {
        finish();
    }
}
