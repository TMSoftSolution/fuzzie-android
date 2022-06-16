package sg.com.fuzzie.android.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;

import sg.com.fuzzie.android.R;

public class FZClubExclusiveDialog extends Dialog {

    private Context context;
    private FZClubExclusiveDialogListener listener;

    public void setListener(FZClubExclusiveDialogListener listener){
        this.listener = listener;
    }

    public FZClubExclusiveDialog(@NonNull Context context) {
        super(context);

        this.context = context;
    }

    public FZClubExclusiveDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);

        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(true);
        setContentView(R.layout.dialog_fuzzie_club_exclusive);

        if (getWindow() != null){
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        CardView cvExplore = findViewById(R.id.cvExplore);
        cvExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (listener != null){

                    listener.FZClubExclusiveDialogExploreButtonClicked();
                }
            }
        });

        CardView cvClose = findViewById(R.id.cvClose);
        cvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (listener != null){

                    listener.FZClubExclusiveDialogCloseButtonClicked();
                }
            }
        });

    }

    public interface FZClubExclusiveDialogListener{

        void FZClubExclusiveDialogExploreButtonClicked();
        void FZClubExclusiveDialogCloseButtonClicked();
    }
}
