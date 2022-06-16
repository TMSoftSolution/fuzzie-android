package sg.com.fuzzie.android.ui.jackpot;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Coupon;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.jackpot.PowerUpItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

/**
 * Created by mac on 2/12/18.
 */

@EActivity(R.layout.activity_power_up_pack)
public class PowerUpPackActivity extends BaseActivity {

    private List<Coupon> coupons;
    FastItemAdapter<PowerUpItem> adapter;

    @Extra
    int bannerId = -1;

    @Bean
    FuzzieData dataManager;

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.rvCoupon)
    RecyclerView rvCoupon;

    @AfterViews
    public void calledAfterViewInjection() {

        adapter = new FastItemAdapter<>();
        rvCoupon.setLayoutManager(new LinearLayoutManager(context));
        rvCoupon.setAdapter(adapter);

        fetchPowerUpPacks();

    }

    private void fetchPowerUpPacks(){

        coupons = dataManager.getPowerUpPacks();
        Collections.sort(coupons, new Comparator<Coupon>() {
            @Override
            public int compare(Coupon o1, Coupon o2) {
                return ((Integer)(o1.getPowerUpPack().getSequence())).compareTo((Integer)(o2.getPowerUpPack().getSequence()));
            }
        });

        showJackpotOffers();
    }

    private void showJackpotOffers(){

        if (adapter == null){
            adapter = new FastItemAdapter<>();
        } else {
            adapter.clear();
        }

        for (Coupon coupon : coupons){
            adapter.add(new PowerUpItem(coupon));
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

}
