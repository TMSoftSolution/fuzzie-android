package sg.com.fuzzie.android.ui.auth;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.viewpagerindicator.IconPageIndicator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;


import sg.com.fuzzie.android.MainActivity_;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.adapter.TutorialFragmentPagerAdapter;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.ViewUtils;

/**
 * Created by ily on 4/12/2017.
 */

@EActivity(R.layout.activity_tutorial)
public class TutorialActivity extends BaseActivity {

    @ViewById(R.id.ll_start)
    LinearLayout llStart;
    @ViewById(R.id.btn_start)
    Button btnStart;
    @ViewById(R.id.btn_next)
    Button btnNext;
    @ViewById(R.id.tutorial_pager)
    ViewPager tutorialPager;
    @ViewById(R.id.tutorial_page_indicator)
    IconPageIndicator indicator;

    private TutorialFragmentPagerAdapter viewPagerAdapter;
    private int currentPage = 0;

    @AfterViews
    public void calledAfterViewInjection() {
        viewPagerAdapter = new TutorialFragmentPagerAdapter(getSupportFragmentManager(), this);
        tutorialPager.setAdapter(viewPagerAdapter);
        indicator.setViewPager(tutorialPager);
        tutorialPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                if(position==3)

                    btnNext.setText(R.string.btn_done);

                else
                    btnNext.setText(R.string.next_step);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Click(R.id.btn_start)
    public void startClicked() {
        ViewUtils.gone(btnStart);
        ViewUtils.gone(llStart);

        ViewUtils.visible(btnNext);
        ViewUtils.visible(tutorialPager);
        ViewUtils.visible(indicator);
    }

    @Click(R.id.btn_next)
    public void nextClicked() {
        currentPage++;
        if(currentPage<4) {
            tutorialPager.setCurrentItem(currentPage);
        } else {
            skipClicked();
        }
    }

    @Click(R.id.btn_skip)
    public void skipClicked() {

        Intent intent = new Intent(context, MainActivity_.class);
        startActivity(intent);
        setResult(RESULT_OK, null);

        finish();
    }

}

