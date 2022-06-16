package sg.com.fuzzie.android.items.shop;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Home;
import sg.com.fuzzie.android.api.models.User;
import sg.com.fuzzie.android.ui.likes.LikerProfileActivity_;
import sg.com.fuzzie.android.ui.me.MyFriendsListActivity_;
import sg.com.fuzzie.android.utils.CurrentUser_;

/**
 * Created by mac on 6/26/17.
 */

public class ShopUpcomingBirthdatesItem extends AbstractItem<ShopUpcomingBirthdatesItem, ShopUpcomingBirthdatesItem.ViewHolder>{

    private final int LIMIT_MAX = 8;
    private int limit;

    private Context context;
    private Home home;
    private ShopUpcomingBirthdatesItemListener listener;

    FastItemAdapter<ShopUpcomingBirthdateItem> adapter;

    public ShopUpcomingBirthdatesItem(Home home, ShopUpcomingBirthdatesItemListener listener){
        this.home = home;
        this.listener = listener;
    }

    @Override
    public int getType() {
        return R.id.item_upcoming_birthdates_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_upcoming_birthdates;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        context = holder.itemView.getContext();
        CurrentUser_ currentUser = CurrentUser_.getInstance_(context);

        if (currentUser.getCurrentUser().getFacebookId() == null){
            holder.rvFriends.setVisibility(View.GONE);
            holder.llContent.setVisibility(View.VISIBLE);
            holder.llContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.facebookConnectButtonClicked();
                }
            });
        } else {
            holder.rvFriends.setVisibility(View.VISIBLE);
            holder.llContent.setVisibility(View.GONE);

            if (adapter == null) {
                adapter = new FastItemAdapter<>();
            } else {
                adapter.clear();
            }

            adapter.withOnClickListener(new OnClickListener<ShopUpcomingBirthdateItem>() {
                @Override
                public boolean onClick(View v, IAdapter<ShopUpcomingBirthdateItem> adapter, ShopUpcomingBirthdateItem item, int position) {

                    if (position != limit) {
                        User friend = item.getFriend();
                        String userJson = User.toJSON(friend);
                        LikerProfileActivity_.intent(context).userExtra(userJson).start();
                    } else {
                        MyFriendsListActivity_.intent(context).start();
                    }

                    return true;
                }
            });

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }

                @Override
                public boolean canScrollHorizontally() {
                    return true;
                }
            };
            holder.rvFriends.setLayoutManager(linearLayoutManager);
            holder.rvFriends.setAdapter(adapter);

            limit = Math.min(home.getFuzzieFriends().size(), LIMIT_MAX);

            List<User> limitedFriends = sortFriends().subList(0, limit);

            User dummyfriend = new User();
            dummyfriend.setAvatarBear(R.drawable.icon_more);
            limitedFriends.add(dummyfriend);

            for (User friend : limitedFriends) {
                adapter.add(new ShopUpcomingBirthdateItem(friend));
            }
        }
    }

    private List<User> sortFriends(){

        List<User> friends = new ArrayList<>();
        List<User> friendsToday = new ArrayList<>();
        List<User> friendsBeforeToday = new ArrayList<>();
        List<User> friendsAfterToday = new ArrayList<>();

        SimpleDateFormat curFormatter = new SimpleDateFormat("yyyy-MM-dd");
        DateTime today = new DateTime(new Date());

        for (User friend : home.getFuzzieFriends()){

            if (friend.getBirthdate() != null && !friend.getBirthdate().equals("")) {

                try {

                    DateTime dateObj = new DateTime(curFormatter.parse(friend.getBirthdate()));

                    if (dateObj.monthOfYear() == today.monthOfYear() && dateObj.dayOfMonth() == today.dayOfMonth()) {

                        friendsToday.add(friend);

                    } else {

                        if (dateObj.monthOfYear() == today.monthOfYear()) {

                            if (dateObj.getDayOfMonth() > today.getDayOfMonth()) {

                                friendsAfterToday.add(friend);

                            } else {

                                friendsBeforeToday.add(friend);

                            }

                        } else if (dateObj.getMonthOfYear() > today.getMonthOfYear()) {

                            friendsAfterToday.add(friend);

                        } else {

                            friendsBeforeToday.add(friend);

                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        friends.addAll(friendsToday);
        friends.addAll(friendsAfterToday);
        friends.addAll(friendsBeforeToday);

        return friends;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        RecyclerView rvFriends;
        LinearLayout llContent;

        public ViewHolder(View itemView) {
            super(itemView);

            this.rvFriends = (RecyclerView) itemView.findViewById(R.id.rvFriends);
            this.llContent = (LinearLayout) itemView.findViewById(R.id.content);
        }
    }

    public interface ShopUpcomingBirthdatesItemListener{
        void facebookConnectButtonClicked();
    }

}


