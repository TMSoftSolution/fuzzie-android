package sg.com.fuzzie.android.ui.likes;

import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.User;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.ViewUtils;

/**
 * Created by mac on 6/13/17.
 */

@EActivity(R.layout.activity_user_profile_photo)
public class UserProfilePhotoActivity extends BaseActivity {

    @Extra
    String userExtra;

    User user;

    @ViewById(R.id.tvName)
    TextView tvName;

    @ViewById(R.id.ivProfilePic)
    ImageView ivProfilePic;

    @AfterViews
    public void calledAfterViewInjection() {
        if(userExtra == null) {
            return;
        }
        user = User.fromJSON(userExtra);

        if (user == null) return;

        if (user.getName() != null){
            tvName.setText(user.getName());
        }

        ivProfilePic.getLayoutParams().height = ViewUtils.getScreenWidth(context);
        ivProfilePic.getLayoutParams().width = ViewUtils.getScreenWidth(context);
        ivProfilePic.requestLayout();
        if (user.getAvatar() != null && !user.getAvatar().equals("")){
            Picasso.get()
                    .load(user.getAvatar())
                    .placeholder(R.drawable.profile_image_placeholder)
                    .fit()
                    .into(ivProfilePic);
        } else {
            Picasso.get()
                    .load(R.drawable.profile_image_placeholder)
                    .placeholder(R.drawable.profile_image_placeholder)
                    .fit()
                    .into(ivProfilePic);
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }
}
