package sg.com.fuzzie.android.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import sg.com.fuzzie.android.R;

public class FZLocationNoMatchDialog extends Dialog {

    private FZLocationNoMatchDialogListener listener;

    public FZLocationNoMatchDialog(@NonNull Context context) {
        super(context, R.style.FullScreenDialogStyle);
    }

    public void setListener(FZLocationNoMatchDialogListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_location_no_match);
        setCancelable(true);
        if (getWindow() != null){
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        CardView cvChange = findViewById(R.id.cvChange);
        cvChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (listener != null){
                    listener.FZLocationNoMatchDialogChangeButtonClicked();
                }
            }
        });

        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (listener != null){
                    listener.FZLocationNoMatchDialogCancelButtonClicked();
                }
            }
        });
    }

    public interface FZLocationNoMatchDialogListener{

        void FZLocationNoMatchDialogChangeButtonClicked();
        void FZLocationNoMatchDialogCancelButtonClicked();
    }
}
