package sg.com.fuzzie.android.items.brand;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Store;
import sg.com.fuzzie.android.utils.ViewUtils;

/**
 * Created by faruq on 04/02/17.
 */

public class StoreListItem extends AbstractItem<StoreListItem, StoreListItem.ViewHolder> {

    Store store;

    OnPhoneClickListener listener;

    public interface OnPhoneClickListener {
        void onClick(Store store);
        void onClickStoreName(Store store);
    }

    public StoreListItem(Store store, OnPhoneClickListener listener) {
        this.store = store;
        this.listener = listener;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.item_store_location_list_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_store_location_list;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(StoreListItem.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        final Context context = viewHolder.itemView.getContext();
        viewHolder.tvName.setText(store.getName());
        viewHolder.tvAddress.setText(store.getAddress());

        if (store.getPhone() == null || store.getPhone().equals("")){

            ViewUtils.gone(viewHolder.rlPhone);

        } else{

            ViewUtils.visible(viewHolder.rlPhone);
            viewHolder.tvTelephone.setText(store.getPhone());
        }

        if (store.getBusinessHours().equals("")){

            ViewUtils.gone(viewHolder.llOpenHour);

        } else {

            ViewUtils.visible(viewHolder.llOpenHour);
            viewHolder.tvOpeningHours.setText(store.getBusinessHours());
        }

        viewHolder.btnTelephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(store);
                }
            }
        });

        viewHolder.tvAddress.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager)context.getSystemService(context.CLIPBOARD_SERVICE);
                ClipData cData = ClipData.newPlainText("address", store.getAddress());
                cm.setPrimaryClip(cData);
                Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        viewHolder.tvTelephone.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager)context.getSystemService(context.CLIPBOARD_SERVICE);
                ClipData cData = ClipData.newPlainText("phone", store.getPhone());
                cm.setPrimaryClip(cData);
                Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        viewHolder.vName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onClickStoreName(store);
                }
            }
        });
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected View vName;
        protected TextView tvName;
        protected TextView tvAddress;
        protected TextView tvTelephone;
        protected ImageView btnTelephone;
        protected TextView tvOpeningHours;
        protected View rlPhone;
        protected View llOpenHour;

        public ViewHolder(View view) {
            super(view);

            this.vName = view.findViewById(R.id.llName);
            this.tvName = view.findViewById(R.id.tvName);
            this.tvAddress = view.findViewById(R.id.tvAddress);
            this.tvTelephone = view.findViewById(R.id.tvTelephone);
            this.btnTelephone = view.findViewById(R.id.btnTelephone);
            this.tvOpeningHours = view.findViewById(R.id.tvOpeningHours);
            this.rlPhone =  view.findViewById(R.id.rlPhone);
            this.llOpenHour = view.findViewById(R.id.llOpenHour);
        }
    }

}
