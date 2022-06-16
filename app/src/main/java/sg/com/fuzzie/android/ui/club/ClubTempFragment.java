package sg.com.fuzzie.android.ui.club;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseFragment;

/**
 * Created by joma on 11/10/17.
 */
@EFragment(R.layout.fragment_club_temp)
public class ClubTempFragment extends BaseFragment{

        @ViewById(R.id.ivBackground)
        ImageView ivBackground;

        @AfterViews
        void calledAfterViewInjection() {
            Picasso.get()
                    .load(R.drawable.club_background)
                    .into(ivBackground);
        }

}