package sg.com.fuzzie.android.ui.shop;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.User;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.me.LikersItem;
import sg.com.fuzzie.android.ui.likes.LikerProfileActivity_;
import sg.com.fuzzie.android.utils.FuzzieData;

/**
 * Created by mac on 4/14/17.
 */

@EActivity(R.layout.activity_likers)
public class BrandLikersActivity extends BaseActivity {

    Brand brand;

    FastItemAdapter<LikersItem> likesAdapter;

    @Bean
    FuzzieData dataManager;

    @Extra
    String brandIdExtra;

    @ViewById(R.id.rvLikes)
    RecyclerView rvLikes;

    @AfterViews
    public void calledAfterViewInjection() {

        if (brandIdExtra == null) return;
        brand = dataManager.getBrandById(brandIdExtra);
        if (brand == null) return;

        likesAdapter = new FastItemAdapter<>();
        rvLikes.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rvLikes.setAdapter(likesAdapter);

        likesAdapter.clear();
        for (User liker: brand.getLikers()){
            LikersItem likersItem = new LikersItem(liker);
            likesAdapter.add(likersItem);
        }

        likesAdapter.withOnClickListener(new OnClickListener<LikersItem>() {
            @Override
            public boolean onClick(View v, IAdapter<LikersItem> adapter, LikersItem item, int position) {

                User liker = item.getLiker();
                String userJson = User.toJSON(liker);
                LikerProfileActivity_.intent(context).userExtra(userJson).start();
                return true;
            }
        });

    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }
}
