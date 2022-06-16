package sg.com.fuzzie.android.ui.shop.topup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.WebActivity_;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.topup.TopUpItem;

import static sg.com.fuzzie.android.utils.Constants.REQUEST_TOP_UP_PAYMENT;

/**
 * Created by mac on 10/9/17.
 */

@EActivity(R.layout.activity_top_up)
public class TopUpActivity extends BaseActivity {

    public static int[] DEFAULT_VALUES = {30, 50, 100};

    private FastItemAdapter adapter;

    @ViewById(R.id.rvTopUp)
    RecyclerView rvTopUp;

    @ViewById(R.id.tvLearnMore)
    TextView tvLearnMore;

    @AfterViews
    public void calledAfterViewInjection() {

        tvLearnMore.setPaintFlags(tvLearnMore.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        adapter = new FastItemAdapter<>();
        rvTopUp.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rvTopUp.setAdapter(adapter);

        for (int i = 0 ; i < DEFAULT_VALUES.length ; i ++){
            adapter.add(new TopUpItem(i, DEFAULT_VALUES[i]));
        }

    }


    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.cvEnter)
    void enterButtonClicked(){
        TopUpEnterActivity_.intent(context).startForResult(REQUEST_TOP_UP_PAYMENT);

    }

    @Click(R.id.tvLearnMore)
    void learnMoreButtonClicked(){
        WebActivity_.intent(context).titleExtra("FAQ").urlExtra("http://fuzzie.com.sg/faq.html#credits").showLoading(true).start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TOP_UP_PAYMENT){
            if (resultCode == Activity.RESULT_OK){
                setResult(RESULT_OK, null);
                finish();
            }
        }
    }
}
