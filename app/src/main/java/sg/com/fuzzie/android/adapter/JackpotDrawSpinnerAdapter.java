package sg.com.fuzzie.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.JackpotResult;
import sg.com.fuzzie.android.utils.TimeUtils;
import sg.com.fuzzie.android.utils.ViewUtils;

/**
 * Created by joma on 10/17/17.
 */

public class JackpotDrawSpinnerAdapter extends BaseAdapter{

    private Context context;
    private List<JackpotResult> results;

    public JackpotDrawSpinnerAdapter(Context context, List<JackpotResult> results){
        this.context = context;
        this.results = results;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int position) {
        return results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        @SuppressLint("ViewHolder")
        View view = LayoutInflater.from(context).inflate(R.layout.item_jackpot_spinner, parent, false);

        JackpotResult result = results.get(position);

        TextView tvSpinner = (TextView) view.findViewById(R.id.tvSpinner);
        tvSpinner.setText(TimeUtils.jackpotDrawHistoryFormat(result.getDrawDateTime()));

        View divider = view.findViewById(R.id.divider);
        if (position == results.size() - 1){
            ViewUtils.gone(divider);
        } else {
            ViewUtils.visible(divider);
        }

        return view;
    }


}
