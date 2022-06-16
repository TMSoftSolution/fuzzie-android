package sg.com.fuzzie.android.ui.redpacket;

import android.content.Intent;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.CustomTypefaceSpan;

import static sg.com.fuzzie.android.utils.Constants.REQUEST_RED_PACKET_OPEN;

/**
 * Created by movdev on 3/2/18.
 */

@EActivity(R.layout.activity_red_packet_champion)
public class RedPacketChampionActivity extends BaseActivity {

    @Extra
    String redPacketId;

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.tvBody)
    TextView tvBody;

    @AfterViews
    public void calledAfterViewInjection() {

        String first = String.format("%s, you have obtained the ", currentUser.getCurrentUser().getFirstName());
        String second = "highest amount";
        String third = " for this round. Good job!";

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Lato_Black.ttf");
        TypefaceSpan typefaceSpan = new CustomTypefaceSpan("", typeface);
        SpannableString spannableString  = new SpannableString(first + second + third);
        spannableString.setSpan(typefaceSpan, first.length(), first.length() + second.length() , 0);

        tvBody.setText(spannableString);

    }

    @Click(R.id.cvGotIt)
    void gotItButtonClicked(){

        setResult(RESULT_OK, null);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_RED_PACKET_OPEN){
            if (resultCode == RESULT_OK){
                setResult(RESULT_OK, null);
                finish();
            }
        }
    }
}
