package com.kineticcafe.kcpmall.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kineticcafe.kcpandroidsdk.models.KcpPlaces;
import com.kineticcafe.kcpandroidsdk.models.KcpPlacesRoot;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.activities.MainActivity;
import com.kineticcafe.kcpmall.adapters.CategoryStoreRecyclerViewAdapter;
import com.kineticcafe.kcpmall.adapters.adapterHelper.IndexableRecylerView;
import com.kineticcafe.kcpmall.adapters.adapterHelper.SectionedLinearRecyclerViewAdapter;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;

import java.util.ArrayList;
import java.util.List;


public class PlacesFragment extends BaseFragment {
    public CategoryStoreRecyclerViewAdapter mPlaceRecyclerViewAdapter;
    private TextView tvEmptyState;
    private IndexableRecylerView rvNews;

    public static PlacesFragment newInstance() {
        PlacesFragment fragment = new PlacesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview_indexablle, container, false);
        final SwipeRefreshLayout srl = (SwipeRefreshLayout) view.findViewById(R.id.srl);
        rvNews = (IndexableRecylerView) view.findViewById(R.id.rv);
        rvNews.setNestedScrollingEnabled(true);
        setupRecyclerView();

        tvEmptyState = (TextView) view.findViewById(R.id.tvEmptyState);

        srl.setColorSchemeResources(R.color.themeColor);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mMainActivity.setOnRefreshListener(new MainActivity.RefreshListener() {
                    @Override
                    public void onRefresh(int msg) {
                        srl.setRefreshing(false);
                        mMainActivity.showSnackBar(msg, 0, null);
                    }
                });

                DirectoryFragment.getInstance().downloadPlaces();
            }
        });
        return view;
    }

    public void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvNews.setLayoutManager(linearLayoutManager);
        rvNews.setLayoutManager(linearLayoutManager);

        ArrayList<KcpPlaces> kcpPlaces = KcpPlacesRoot.getInstance().getPlacesList();
        mPlaceRecyclerViewAdapter = new CategoryStoreRecyclerViewAdapter(
                getActivity(),
                kcpPlaces, KcpContentTypeFactory.PREF_ITEM_TYPE_ALL_PLACE);

        List<SectionedLinearRecyclerViewAdapter.Section> sections =
                new ArrayList<SectionedLinearRecyclerViewAdapter.Section>();
        List<String> sectionName = new ArrayList<String>();
        List<Integer> sectionPosition = new ArrayList<Integer>();
        String startLetter = null;
        for(int i = 0; i < kcpPlaces.size(); i++){
            String currentStoreNameStartLetter = String.valueOf(kcpPlaces.get(i).getPlaceName().toUpperCase().charAt(0));
            if(startLetter == null || !startLetter.equals(currentStoreNameStartLetter)) {
                startLetter = currentStoreNameStartLetter;
                sections.add(new SectionedLinearRecyclerViewAdapter.Section(i, startLetter));
                sectionName.add(startLetter);
                sectionPosition.add(i + sections.size());
            }
        }

        rvNews.setFastScrollEnabled(true);
        rvNews.setIndexAdapter(sectionName, sectionPosition);

        SectionedLinearRecyclerViewAdapter.Section[] dummy = new SectionedLinearRecyclerViewAdapter.Section[sections.size()];
        SectionedLinearRecyclerViewAdapter mSectionedAdapter = new SectionedLinearRecyclerViewAdapter(
                getActivity(),
                R.layout.list_section_place,
                R.id.section_text,
                rvNews,
                mPlaceRecyclerViewAdapter);
        mSectionedAdapter.setSections(sections.toArray(dummy));
        rvNews.setAdapter(mSectionedAdapter);
    }
}
