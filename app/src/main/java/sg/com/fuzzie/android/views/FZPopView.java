package sg.com.fuzzie.android.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.Space;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import pl.droidsonroids.gif.GifImageView;
import sg.com.fuzzie.android.R;

/**
 * Created by mac on 4/30/17.
 */

public class FZPopView extends Dialog {

    private Context context;

    private FZPopViewListener listener;

    private String title;
    private String body;
    private String buttonTitle;
    private int imageRes;
    private boolean showGif = false;

    TextView tvTitle;
    TextView tvBody;


    public FZPopView(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public FZPopView(@NonNull Context context, String title, String body, int imageRes){
        super(context);
        this.context = context;
        this.title = title;
        this.body = body;
        this.imageRes = imageRes;
    }

    public FZPopView(@NonNull Context context, String title, String body, int imageRes, boolean showGif){
        super(context);
        this.context = context;
        this.title = title;
        this.body = body;
        this.imageRes = imageRes;
        this.showGif = showGif;
    }

    public FZPopView(@NonNull Context context, String title, String body, int imageRes, boolean showGif , String buttonTitle){
        super(context);
        this.context = context;
        this.title = title;
        this.body = body;
        this.imageRes = imageRes;
        this.showGif = showGif;
        this.buttonTitle = buttonTitle;
    }

    public void setFZPopViewListener(FZPopViewListener listener){
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_fuzzie);
        if (getWindow() != null){
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        setCanceledOnTouchOutside(false);

        View titleView = findViewById(R.id.llTitle);
        if (title.equals("")){
            titleView.setVisibility(View.GONE);
        } else {
            titleView.setVisibility(View.VISIBLE);
        }

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Brandon_blk.otf"));
        tvTitle.setText(title);

        tvBody = (TextView) findViewById(R.id.tvBody);
        tvBody.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Lato_Regular.ttf"));
        if (body != null && !body.equals("")){
            tvBody.setVisibility(View.VISIBLE);
            tvBody.setText(body);
        } else {
            tvBody.setVisibility(View.GONE);
        }

        ImageView ivBear = (ImageView) findViewById(R.id.ivBear);
        Picasso.get()
                .load(imageRes)
                .placeholder(imageRes)
                .into(ivBear);

        GifImageView gfProcess = (GifImageView) findViewById(R.id.gfProcess);

        if (showGif){
            gfProcess.setVisibility(View.VISIBLE);
        } else {
            gfProcess.setVisibility(View.GONE);
        }

        AppCompatButton tvOK = (AppCompatButton) findViewById(R.id.tvOK);
        tvOK.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Brandon_blk.otf"));
        tvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.okButtonClicked();
            }
        });

        if (buttonTitle != null && !buttonTitle.equals("")){
            tvOK.setVisibility(View.VISIBLE);
            tvOK.setText(buttonTitle);
        } else {
            tvOK.setVisibility(View.GONE);
        }

        Space space = (Space) findViewById(R.id.space);
        if (imageRes == R.drawable.bear_craft){
            space.setVisibility(View.VISIBLE);
        } else {
            space.setVisibility(View.GONE);
        }
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public TextView getTvBody() {
        return tvBody;
    }
}
