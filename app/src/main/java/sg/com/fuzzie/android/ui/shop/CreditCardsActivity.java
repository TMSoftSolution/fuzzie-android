package sg.com.fuzzie.android.ui.shop;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Bank;
import sg.com.fuzzie.android.api.models.CreditCard;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.payment.CreditCardItem;

/**
 * Created by nurimanizam on 6/10/17.
 */

@EActivity(R.layout.activity_creditcards)
public class CreditCardsActivity extends BaseActivity {

    @Extra
    String bankExtra;

    Bank bank;

    FastItemAdapter creditCardAdapter;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.rvCreditCards)
    RecyclerView rvCreditCards;

    @AfterViews
    public void calledAfterViewInjection() {

        if (bankExtra == null) return;

        bank = Bank.fromJSON(bankExtra);

        tvTitle.setText(bank.getName());

        creditCardAdapter = new FastItemAdapter();
        rvCreditCards.setLayoutManager(new LinearLayoutManager(context));
        rvCreditCards.setAdapter(creditCardAdapter);

        for (CreditCard creditCard : bank.getCreditCards()) {
            creditCardAdapter.add(new CreditCardItem(creditCard));
        }

    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

}
