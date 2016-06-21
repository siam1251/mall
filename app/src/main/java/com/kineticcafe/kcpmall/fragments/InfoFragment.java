package com.kineticcafe.kcpmall.fragments;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kineticcafe.kcpandroidsdk.utils.KcpUtility;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.adapters.InfoRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * Created by Kay on 2016-06-20.
 */
public class InfoFragment extends BaseFragment {
    private boolean shouldScroll = false;
    private OnListFragmentInteractionListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        final RecyclerView rvInfo = (RecyclerView) view.findViewById(R.id.rvInfo);
        setupRecyclerView(rvInfo);

        final AppBarLayout abInfo = (AppBarLayout) view.findViewById(R.id.abInfo);
        abInfo.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout abInfo, int abInfoverticalOffset) {
                shouldScroll = abInfo.getTotalScrollRange() == Math.abs(abInfoverticalOffset);
            }
        });

        rvInfo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //to prevent the recyclerview from scrolling before the imageview's completely collapsed
                if (!shouldScroll) {
                    rvInfo.scrollToPosition(0);
                } else super.onScrolled(recyclerView, dx, dy);
            }
        });

        RelativeLayout rlDirection = (RelativeLayout) view.findViewById(R.id.rlDirection);
        rlDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "clicked", Toast.LENGTH_SHORT).show();
            }
        });

        RelativeLayout rlSaveMyParkingSpot = (RelativeLayout) view.findViewById(R.id.rlSaveMyParkingSpot);
        rlSaveMyParkingSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "clicked", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView ivWalk = (ImageView) view.findViewById(R.id.ivWalk);
        ivWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "clicked", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView ivCar = (ImageView) view.findViewById(R.id.ivCar);
        ivCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "clicked", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView ivSubway = (ImageView) view.findViewById(R.id.ivSubway);
        ivSubway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "clicked", Toast.LENGTH_SHORT).show();
            }
        });


        //when recyclerview's placed under collapsed, it sometimes fails to set its height properly - last items cannot be scrolled to so manually setting the height
        final ViewTreeObserver viewTreeObserver = rvInfo.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    rvInfo.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) rvInfo.getLayoutParams();
                    lp.height = (int) (KcpUtility.getScreenHeight(getActivity())
                            - KcpUtility.getStatusBarHeight(getActivity())
                            - getActivity().getResources().getDimension(R.dimen.abc_action_bar_default_height_material)
                            - getResources().getDimension(R.dimen.info_collapsed_height)
                            - getResources().getDimension(R.dimen.info_hours_height)
                            - getResources().getDimension(R.dimen.vpMain_padding_bottom));
                    rvInfo.setLayoutParams(lp);
                }
            });
        }

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        mListener =  new OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(int position) {
                Toast.makeText(getActivity(), "clicked", Toast.LENGTH_SHORT).show();
            }
        };

        ArrayList<InfoRecyclerViewAdapter.Info> infoList = new ArrayList<InfoRecyclerViewAdapter.Info>();
        String[] infoTitle = getResources().getStringArray(R.array.infoTitles);
        String[] infoSubTitle = getResources().getStringArray(R.array.infoSubTitles);
        for (int i = 0; i < infoTitle.length; i++) {
            infoList.add(new InfoRecyclerViewAdapter.Info(infoTitle[i], infoSubTitle[i]));
        }

        InfoRecyclerViewAdapter infoRecyclerViewAdapter = new InfoRecyclerViewAdapter(
                getActivity(),
                infoList,
                mListener);
        recyclerView.setAdapter(infoRecyclerViewAdapter);
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(int position);
    }
}
