package sg.com.fuzzie.android.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import sg.com.fuzzie.android.R;

/**
 * Created by mac on 6/23/17.
 */

public class FZAlertDialog extends Dialog {

    private Context context;
    private String title;
    private String body;
    private SpannableString spannableBody;
    private int imageRes;
    private String okButtonTitle;
    private String cancelButtonTitle;


    private FZAlertDialogListener listener;

    public void setListener(FZAlertDialogListener listener){
        this.listener = listener;
    }

    public FZAlertDialog(@NonNull Context context) {
        super(context, R.style.FullScreenDialogStyle);
        this.context = context;
    }

    public FZAlertDialog(@NonNull Context context, String title, String body, String okButtonTitle, String cancelButtonTitle, int imageRes) {
        super(context, R.style.FullScreenDialogStyle);
        this.context = context;
        this.title = title;
        this.body = body;
        this.okButtonTitle = okButtonTitle;
        this.cancelButtonTitle = cancelButtonTitle;
        this.imageRes = imageRes;
        this.spannableBody = new SpannableString("");
    }

    public FZAlertDialog(@NonNull Context context, String title, SpannableString spannableBody, String okButtonTitle, String cancelButtonTitle, int imageRes) {
        super(context, R.style.FullScreenDialogStyle);
        this.context = context;
        this.title = title;
        this.spannableBody = spannableBody;
        this.okButtonTitle = okButtonTitle;
        this.cancelButtonTitle = cancelButtonTitle;
        this.imageRes = imageRes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(false);
        setContentView(R.layout.dialog_fuzzie_alert);
        if (getWindow() != null){
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(title);

        TextView tvBody = (TextView) findViewById(R.id.tvBody);
        if (spannableBody != null && spannableBody.length() > 0){
            tvBody.setText(spannableBody);
        } else {
            tvBody.setText(body);
        }

        Button btnOK = (Button) findViewById(R.id.btnOK);
        btnOK.setText(okButtonTitle);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.FZAlertOkButtonClicked();
            }
        });

        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        if (!cancelButtonTitle.equals("")){
            btnCancel.setVisibility(View.VISIBLE);
            btnCancel.setText(cancelButtonTitle);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.FZAlertCancelButtonClicked();
                }
            });
        } else {
            btnCancel.setVisibility(View.GONE);
        }

        ImageView ivBear = (ImageView) findViewById(R.id.ivBear);
        ivBear.setImageResource(imageRes);


    }

    public interface FZAlertDialogListener{
        void FZAlertOkButtonClicked();
        void FZAlertCancelButtonClicked();
    }
}
