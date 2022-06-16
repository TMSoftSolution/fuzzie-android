package sg.com.fuzzie.android.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Category;
import sg.com.fuzzie.android.api.models.ClubStore;
import sg.com.fuzzie.android.utils.FuzzieData_;
import sg.com.fuzzie.android.utils.FuzzieUtils;

public class ClubStoreInfoAdapter extends PagerAdapter {

    private Context context;
    private FuzzieData_ dataManager;
    private List<ClubStore> clubStores;
    private ClubStoreInfoAdapterListener listener;

    public ClubStoreInfoAdapter(Context context, List<ClubStore> clubStores){

        this.context = context;
        this.clubStores = clubStores;
        dataManager = FuzzieData_.getInstance_(context);

    }

    public void setListener(ClubStoreInfoAdapterListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_club_store_info, null);

        ImageView ivStore = view.findViewById(R.id.ivStore);
        TextView tvBrandName = view.findViewById(R.id.tvBrandName);
        TextView tvLocation = view.findViewById(R.id.tvLocation);
        TextView tvDistance = view.findViewById(R.id.tvDistance);
        ImageView ivCategory = view.findViewById(R.id.ivCategory);
        TextView tvCategory = view.findViewById(R.id.tvCategory);

        final ClubStore clubStore = clubStores.get(position);
        Brand brand = dataManager.getBrandById(clubStore.getBrandId());

        if (brand != null && brand.getBackgroundImageUrl() != null){
            Picasso.get()
                    .load(brand.getBackgroundImageUrl())
                    .placeholder(R.drawable.brands_placeholder)
                    .into(ivStore);
        }
        tvBrandName.setText(clubStore.getBrandName());
        tvLocation.setText(clubStore.getStoreName());
        tvDistance.setText(clubStore.getDistance() != null && clubStore.getDistance() != 0 ? String.format("%.2f km", clubStore.getDistance()) : "");

        Drawable subCategoryIcon = ContextCompat.getDrawable(context, FuzzieUtils.getSubCategoryBlackIcon(brand.getSubCategoryId()));
        ivCategory.setImageDrawable(subCategoryIcon);

        Category subCategory = FuzzieUtils.getSubCategory(dataManager.getHome().getSubCategories(),brand.getSubCategoryId());
        if (subCategory != null){
            tvCategory.setText(subCategory.getName());
        }


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null){
                    listener.itemClicked(clubStore);
                }
            }
        });

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {

        return clubStores.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public interface ClubStoreInfoAdapterListener{

        void itemClicked(ClubStore clubStore);

    }
}
