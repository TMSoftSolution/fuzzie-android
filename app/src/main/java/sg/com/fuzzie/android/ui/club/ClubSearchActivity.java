package sg.com.fuzzie.android.ui.club;

import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.ClubPlace;
import sg.com.fuzzie.android.api.models.ClubStore;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.club.ClubSearchItem;
import sg.com.fuzzie.android.items.club.ClubStoreListItem;
import sg.com.fuzzie.android.items.club.ClubStoreLocationItem;
import sg.com.fuzzie.android.utils.ClubSearchType;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.ViewUtils;
import sg.com.fuzzie.android.views.EditTextBackEvent;
import sg.com.fuzzie.android.views.EmptyRecyclerView;

@EActivity(R.layout.activity_club_search)
public class ClubSearchActivity extends BaseActivity {

    private int selectedTab;

    public static final int TAB_PLACES = 0;
    public static final int TAB_STORES = 1;

    private List<ClubStore> clubStores;
    private List<ClubPlace> clubPlaces;
    private FastItemAdapter adapter;
    private SectionedRecyclerViewAdapter searchAdapter;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.rvPlaces)
    RecyclerView rvPlaces;

    @ViewById(R.id.rvSearch)
    EmptyRecyclerView rvSearch;

    @ViewById(R.id.tab_layout)
    TabLayout tabLayout;

    @ViewById(R.id.etSearch)
    EditTextBackEvent etSearch;

    @ViewById(R.id.llEmpty)
    View emptyView;

    @ViewById(R.id.main)
    View main;

    @AfterViews
    void callAfterViewInjection(){

        clubStores = dataManager.getSearchClubStores();
        clubPlaces = dataManager.getSearchClubPlaces();

        adapter = new FastItemAdapter<>();
        rvPlaces.setLayoutManager(new LinearLayoutManager(context));
        rvPlaces.setAdapter(adapter);

        adapter.withOnClickListener(new OnClickListener() {
            @Override
            public boolean onClick(@Nullable View v, IAdapter adapter, IItem item, int position) {

                if (item.getType() == R.id.item_club_store_location_id){

                    dataManager.setTempStores(clubStores);
                    ClubPlaceStoreListActivity_.intent(context).clubPlaceExtra(ClubPlace.toJSON(((ClubStoreLocationItem)item).getClubPlace())).start();
                }

                return false;
            }
        });

        setTabFont();
        setupTabHandler();
        selectedTab = TAB_PLACES;
        tabChanged(selectedTab);
        tabLayout.getTabAt(selectedTab).select();

        rvSearch.setLayoutManager(new LinearLayoutManager(context));
        searchAdapter = new SectionedRecyclerViewAdapter();
        rvSearch.setAdapter(searchAdapter);
        rvSearch.setEmptyView(findViewById(R.id.llEmpty));
        showSearch();

        etSearch.setOnEditTextImeBackListener(new EditTextBackEvent.EditTextImeBackListener() {
            @Override
            public void onImeBack(EditTextBackEvent ctrl, String text) {
                hideKeyboard();
                etSearch.clearFocus();
            }
        });

        etSearch.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard();
                    etSearch.clearFocus();
                    return true;
                }
                return false;
            }
        });
    }

    private void setTabFont(){

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();

        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);

            int tabChildsCount = vgTab.getChildCount();

            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Brandon_blk.otf"));
                }
            }
        }
    }

    private void setupTabHandler(){

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabChanged(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void loadPlaces(){

        if (clubPlaces == null) return;

        if (adapter == null){

            adapter = new FastItemAdapter<>();

        } else {

            adapter.clear();

        }

        for (ClubPlace clubPlace : clubPlaces){
            adapter.add(new ClubStoreLocationItem(clubPlace));
        }
    }

    private void loadStores(){

        if (clubStores == null) return;

        if (adapter == null){

            adapter = new FastItemAdapter<>();

        } else {

            adapter.clear();

        }

        for (ClubStore clubStore : clubStores){
            adapter.add(new ClubStoreListItem(clubStore));
        }
    }

    private void showSearch(){

        if (searchAdapter == null){

            searchAdapter = new SectionedRecyclerViewAdapter();

        } else {

            searchAdapter.removeAllSections();

        }

        searchAdapter.addSection(new ClubSearchItem(clubPlaces, clubStores, ClubSearchType.PLACE));
        searchAdapter.addSection(new ClubSearchItem(clubPlaces, clubStores, ClubSearchType.STORE));

        searchAdapter.notifyDataSetChanged();
    }


    @UiThread
    protected void tabChanged(int newPosition){

        selectedTab = newPosition;

        switch (selectedTab){
            case TAB_PLACES:
                loadPlaces();
                break;
            case TAB_STORES:
                loadStores();
                break;

            default:
                break;
        }
    }

    @FocusChange(R.id.etSearch)
    void focusChanged(boolean hasFocus){

        if (hasFocus){

            ViewUtils.visible(rvSearch);
            ViewUtils.invisible(main);

        } else {

            if (TextUtils.isEmpty(etSearch.getText().toString())){

                ViewUtils.visible(main);
                ViewUtils.invisible(rvSearch);
            }

        }
    }

    @AfterTextChange(R.id.etSearch)
    void searchEditTextChanged() {

        for (Section section : searchAdapter.getCopyOfSectionsMap().values()) {
            if (section instanceof FilterableSection) {
                ((FilterableSection) section).filter(etSearch.getText().toString());
            }
        }
        searchAdapter.notifyDataSetChanged();
    }

    @Click(R.id.tvCancel)
    void cancelClicked(){
        finish();
    }


    public interface FilterableSection {
        void filter(String query);
    }
}
