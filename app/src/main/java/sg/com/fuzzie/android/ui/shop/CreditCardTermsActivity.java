package sg.com.fuzzie.android.ui.shop;

import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.WebActivity_;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.CreditCard;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

/**
 * Created by nurimanizam on 10/10/17.
 */

@EActivity(R.layout.activity_creditcard_terms)
public class CreditCardTermsActivity extends BaseActivity {

    CreditCard creditCard;

    @Extra
    String cardExtra;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.tvTerms)
    TextView tvTerms;

    @AfterViews
    public void calledAfterViewInjection() {

        if (cardExtra == null) return;

        creditCard = CreditCard.fromJSON(cardExtra);

        tvTerms.setText(creditCard.getTerms());
    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

    @Click({R.id.cvSignUp})
    void signUpButtonClicked() {
        WebActivity_.intent(context).titleExtra(creditCard.getTitle()).urlExtra(creditCard.getSignUpUrl()).start();
    }

}