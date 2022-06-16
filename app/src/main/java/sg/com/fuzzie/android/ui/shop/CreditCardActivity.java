package sg.com.fuzzie.android.ui.shop;

import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.WebActivity_;
import sg.com.fuzzie.android.api.models.CreditCard;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

/**
 * Created by nurimanizam on 16/6/17.
 */

@EActivity(R.layout.activity_creditcard)
public class CreditCardActivity extends BaseActivity {

    @Extra
    String creditCardExtra;

    CreditCard creditCard;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.ivCreditCard)
    ImageView ivCreditCard;

    @ViewById(R.id.tvCreditCard)
    TextView tvCreditCard;

    @ViewById(R.id.tvDetails)
    TextView tvDetails;

    @ViewById(R.id.rlBonus)
    RelativeLayout rlBonus;

    @ViewById(R.id.tvBonusTitle)
    TextView tvBonusTitle;

    @ViewById(R.id.tvBonusBody)
    TextView tvBonusBody;

    @AfterViews
    public void calledAfterViewInjection() {

        if (creditCardExtra == null) return;

        creditCard = CreditCard.fromJSON(creditCardExtra);

        tvTitle.setText(creditCard.getBankName());

        Picasso.get()
                .load(creditCard.getDetailsBanner())
                .into(ivCreditCard);

        tvCreditCard.setText(creditCard.getTitle());

        if (creditCard.getDetails() != null && creditCard.getDetails().size() > 0) {
            int i = 0;

            for (String detail : creditCard.getDetails()) {
                if (i>0) tvDetails.append("<br><br>");
                tvDetails.append("<font color='#FA3E3F'>●</font> " + detail);
                i++;
            }

            String details = tvDetails.getText().toString();
            tvDetails.setText(fromHtml(details));
        }

        if (creditCard.isEnableBonus()) {
            if (creditCard.getBonusTitle() != null) tvBonusTitle.setText(creditCard.getBonusTitle());
            if (creditCard.getBonusBody() != null) tvBonusBody.setText(creditCard.getBonusBody());
        } else {
            rlBonus.setVisibility(View.GONE);
        }

    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

    @Click({R.id.cvSignUp})
    void signUpButtonClicked() {
        WebActivity_.intent(context).showLoading(true).titleExtra(creditCard.getTitle()).urlExtra(creditCard.getSignUpUrl()).start();
    }

    @Click(R.id.tvLearnMore)
    void termsButtonClicked() {
        CreditCardTermsActivity_.intent(context).cardExtra(CreditCard.toJSON(creditCard)).start();
    }

}
