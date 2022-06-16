package sg.com.fuzzie.android.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.ui.auth
        .TutorialFragment_;

import static sg.com.fuzzie.android.ui.auth.TutorialFragment.KEY_CONTENT;
import static sg.com.fuzzie.android.ui.auth.TutorialFragment.KEY_IMAGE_RES;
import static sg.com.fuzzie.android.ui.auth.TutorialFragment.KEY_TITLE;

/**
 * Created by ily on 04/23/2017.
 */

public class TutorialFragmentPagerAdapter extends FragmentStatePagerAdapter implements IconPagerAdapter {

    private List<Fragment> fragments;


    protected static final int[] IMAGES = new int[]{
            R.drawable.tutor_01, R.drawable.tutor_02, R.drawable.tutor_03, R.drawable.tutor_04
    };

    public TutorialFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        fragments = new ArrayList<>();
        String[] RES_TEXT_TITLES = context.getResources().getStringArray(R.array.arr_tutor_titles);
        String[] RES_TEXT_CONTENTS = context.getResources().getStringArray(R.array.arr_tutor_contents);
        for (int i = 0; i < IMAGES.length; i++) {
            fragments.add(TutorialFragment_.builder()
                    .arg(KEY_IMAGE_RES, IMAGES[i])
                    .arg(KEY_TITLE, RES_TEXT_TITLES[i])
                    .arg(KEY_CONTENT, RES_TEXT_CONTENTS[i])
                    .build());
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getIconResId(int index) {
        return R.drawable.selector_icon_pager_indicator;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}
