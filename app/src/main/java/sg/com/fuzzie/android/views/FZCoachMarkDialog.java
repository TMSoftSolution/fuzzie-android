package sg.com.fuzzie.android.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import sg.com.fuzzie.android.R;

/**
 * Created by mac on 7/18/17.
 */

public class FZCoachMarkDialog extends Dialog {

    Context context;

    private FZCoachMarkDialogListener listener;

    public void setListener(FZCoachMarkDialogListener listener){
        this.listener = listener;
    }

    public FZCoachMarkDialog(@NonNull Context context) {
        super(context, R.style.FullScreenDialogStyle);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(true);
        setContentView(R.layout.dialog_map_highlight);
        if (getWindow() != null){
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tryItOut = (TextView) findViewById(R.id.tvTryItOutNow);
        tryItOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.coachMarkDialogTryButtonClicked();
            }
        });

        ImageView ivHighlighted = (ImageView) findViewById(R.id.ivHighlighted);
        ivHighlighted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.coachMarkDialogTryButtonClicked();
            }
        });

        RelativeLayout rlBody = (RelativeLayout) findViewById(R.id.id_dialog_map_highlight);
        rlBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.coachMarkDialogClicked();
            }
        });


    }

    public interface FZCoachMarkDialogListener{
        void coachMarkDialogTryButtonClicked();
        void coachMarkDialogClicked();
    }
}
