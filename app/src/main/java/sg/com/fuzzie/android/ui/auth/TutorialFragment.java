package sg.com.fuzzie.android.ui.auth;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseFragment;
import sg.com.fuzzie.android.utils.ViewUtils;

/**
 * Created by ily on 4/12/2017.
 */

@EFragment(R.layout.fragment_tutorial)
public class TutorialFragment extends BaseFragment {

    public static final String KEY_IMAGE_RES = "image_res";
    public static final String KEY_TITLE = "title";
    public static final String KEY_CONTENT = "content";

    @ViewById(R.id.iv_bear)
    ImageView ivBear;
    @ViewById(R.id.iv_tutorial)
    ImageView ivTutorial;
    @ViewById(R.id.frame_tutor_pic)
    FrameLayout frameTutorPic;
    @ViewById(R.id.tv_tutorial_title)
    TextView tvTitle;
    @ViewById(R.id.tv_tutorial_content)
    TextView tvContent;

    @AfterViews
    void calledAfterViewInjection() {
        ivTutorial.setImageResource(getArguments().getInt(KEY_IMAGE_RES));
        tvTitle.setText(getArguments().getString(KEY_TITLE));
        tvContent.setText(getArguments().getString(KEY_CONTENT));
        ViewUtils.gone(ivBear);
        ViewUtils.visible(frameTutorPic);
    }

}
