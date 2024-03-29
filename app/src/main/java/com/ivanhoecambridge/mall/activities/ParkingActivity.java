package com.ivanhoecambridge.mall.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.analytics.Analytics;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.fragments.MapFragment;
import com.ivanhoecambridge.mall.parking.ChildParking;
import com.ivanhoecambridge.mall.parking.Parking;
import com.ivanhoecambridge.mall.parking.ParkingManager;
import com.ivanhoecambridge.mall.utility.Utility;
import com.ivanhoecambridge.mall.views.ActivityAnimation;
import com.ivanhoecambridge.mall.views.AlertDialogForInterest;
import com.ivanhoecambridge.mall.views.RecyclerViewFooter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kay on 2016-08-15.
 */
public class ParkingActivity extends AppCompatActivity {

    public static int PARKING_RESULT_CODE_SPOT_SAVED = 0;
    public static int PARKING_RESULT_CODE_SPOT_PENDING = 1; //when blue dot's chosen as parking spot, ask for confirmation on map

    public final int ITEM_TYPE_PARKING = 0;
    public final int ITEM_TYPE_ENTRANCE = 1;
    public final int ITEM_TYPE_FOOTER = 2;
    public final int ITEM_TYPE_USE_MY_LOCATION = 3;

    private int mTempParkingLotPreviouslySaved = -1;
    private int mTempEntrancePreviouslySaved = -1;
    private int mParkingLotSelectedPosition = -1;
    private int mEntranceSelectedPosition = -1;
    private boolean mDoneBtnShown = false;

    private String mParkingNote = "";
    private TextView tvFooter;
    private TextView tvNote;
    private RecyclerView rvParking;
    private RecyclerView rvParkingChild;
    private RelativeLayout rlParkingIcon;
    private RelativeLayout rlNote;
    private TextView tvParkingLotName;
    private TextView tvEntrancename;
    private TextView tvQuestionOne;
    private TextView tvQuestionTwo;
    private TextView tvEditSelection;
    private TextView tvSaveParkingLot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        mTempParkingLotPreviouslySaved = ParkingManager.getSavedParkingLotPosition(this);
        mTempEntrancePreviouslySaved = ParkingManager.getSavedEntrancePosition(this);

        ImageView ivBlur = (ImageView) findViewById(R.id.ivBlur);
        Bitmap blurred = Utility.getBitmapFromMemCache(Constants.KEY_PARKING_BLURRED, true);
        if(blurred != null) ivBlur.setBackground(new BitmapDrawable(getResources(), blurred));

        rvParking = (RecyclerView) findViewById(R.id.rvParking);
        rvParkingChild = (RecyclerView) findViewById(R.id.rvParkingChild);
        rlParkingIcon = (RelativeLayout) findViewById(R.id.rlParkingIcon);
        rlNote = (RelativeLayout) findViewById(R.id.rlNote);
        tvParkingLotName = (TextView) findViewById(R.id.tvParkingLotName);
        tvNote = (TextView) findViewById(R.id.tvNote);
        tvEntrancename = (TextView) findViewById(R.id.tvEntrancename);
        tvQuestionOne = (TextView) findViewById(R.id.tvQuestionOne);
        tvQuestionTwo = (TextView) findViewById(R.id.tvQuestionTwo);
        tvEditSelection = (TextView) findViewById(R.id.tvEditSelection);
        tvEditSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.getInstance(ParkingActivity.this).logEvent("Parking_Startover_Click", "Parking", "Click on the Start Over Button");
                showSaveParkingSpotScreen(false);
                setupChildRecyclerView();
            }
        });
        tvSaveParkingLot = (TextView) findViewById(R.id.tvSaveParkingLot);
        tvSaveParkingLot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.getInstance(ParkingActivity.this).logEvent("Parking_Saveparkingspot_Click", "Parking", "Click on the Save Parking Spot Button");
                ParkingManager.saveParkingSpotAndEntrance(ParkingActivity.this, mParkingNote, mParkingLotSelectedPosition, mEntranceSelectedPosition);
                Toast.makeText(ParkingActivity.this, getResources().getString(R.string.parking_saved_my_parking_spot), Toast.LENGTH_SHORT).show();
                setResult(PARKING_RESULT_CODE_SPOT_SAVED, new Intent());
                onFinish();
            }
        });

        setParkingNoteBtn(ParkingManager.getParkingNotes(this));
        rlNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogForInterest alertDialogForInterest = new AlertDialogForInterest();
                alertDialogForInterest.showEditTextAlertDialog(
                        ParkingActivity.this,
                        mParkingNote,
                        getResources().getString(R.string.title_add_notes),
                        getResources().getString(R.string.action_save),
                        getResources().getString(R.string.action_cancel),
                        new AlertDialogForInterest.DialogEditTextLinstener() {
                            @Override
                            public void saveClicked(String text) {
                                mParkingNote = text;
                                setParkingNoteBtn(mParkingNote);
                                return;
                            }
                        });
            }
        });

        ImageView ivDismiss = (ImageView) findViewById(R.id.ivDismiss);
        ivDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForUnsavedAndShowAlertBeforeExit();
            }
        });

        tvFooter = (TextView) findViewById(R.id.tvFooter);
        tvFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvFooter.getText().toString().equals(getString(R.string.action_next))){
                    setupChildRecyclerView();
                } else if(tvFooter.getText().toString().equals(getString(R.string.action_back))){
                    setupChildRecyclerView();
                }  else if (tvFooter.getText().toString().equals(getString(R.string.action_done))){
                    showSaveParkingSpotScreen(true);
                }
            }
        });

        mParkingNote = ParkingManager.getParkingNotes(ParkingActivity.this);
        mParkingLotSelectedPosition = ParkingManager.getSavedParkingLotPosition(this);
        mEntranceSelectedPosition = ParkingManager.getSavedEntrancePosition(this);

        setupRecyclerView();
    }

    private void checkForUnsavedAndShowAlertBeforeExit(){
        if(mParkingLotSelectedPosition != mTempParkingLotPreviouslySaved || mEntranceSelectedPosition != mTempEntrancePreviouslySaved) {
            AlertDialogForInterest alertDialogForInterest = new AlertDialogForInterest();
            alertDialogForInterest.getAlertDialog(
                    ParkingActivity.this,
                    R.string.title_do_not_save_parking,
                    R.string.warning_no_parking_saved,
                    R.string.action_do_not_save,
                    R.string.action_cancel,
                    new AlertDialogForInterest.DialogAnsweredListener() {
                        @Override
                        public void okClicked() {
                            onFinish();
                        }
                    });
        } else onFinish();
    }

    private void setParkingNoteBtn(String note){
        if(!note.equals("")) {
            tvNote.setText(note);
            rlNote.setBackground(null);
        } else {
            tvNote.setText(getResources().getString(R.string.parking_add_note));
            rlNote.setBackground(getResources().getDrawable(R.drawable.btn_style_corener_radius_white_edge));
        }
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setAutoMeasureEnabled(false);

        rvParking.setLayoutManager(linearLayoutManager);
        rvParking.setHasFixedSize(true);
        ParkingRecyclerViewAdapter parkingRecyclerViewAdapter = new ParkingRecyclerViewAdapter(this, new ArrayList<Parking>(ParkingManager.sParkings.getParkings()), null);

        if(MapFragment.isBlueDotShown()) {
            parkingRecyclerViewAdapter.addHeader(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(PARKING_RESULT_CODE_SPOT_PENDING, new Intent());
                    onFinish();
                }
            });
        }
        rvParking.setAdapter(parkingRecyclerViewAdapter);

        try {
            //select previously saved parking lot
            if(ParkingManager.isParkingLotSaved(this) && mParkingLotSelectedPosition != -1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(rvParking.findViewHolderForAdapterPosition(mParkingLotSelectedPosition) != null) {
                                rvParking.findViewHolderForAdapterPosition(mParkingLotSelectedPosition).itemView.performClick();
                                showDoneBtn(true, rvParking, getString(R.string.action_next));
                                if(ParkingManager.isParkingLotSaved(ParkingActivity.this)) showSaveParkingSpotScreen(true);
                                rvParkingChild.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupChildRecyclerView() {
        try {
            Animation fadeOutAnim = AnimationUtils.loadAnimation(this,
                    android.R.anim.fade_out);
            fadeOutAnim.reset();

            Animation fadeInAnim = AnimationUtils.loadAnimation(this,
                    android.R.anim.fade_in);
            fadeInAnim.setStartOffset(200);
            fadeInAnim.reset();

            Animation slideDownAnimation = AnimationUtils.loadAnimation(this,
                    R.anim.anim_slide_down_from_its_position);
            slideDownAnimation.reset();
            slideDownAnimation.setStartOffset(200);
            slideDownAnimation.setFillAfter(true);

            Animation slideOutToRightAnimation = AnimationUtils.loadAnimation(this,
                    R.anim.anim_slide_out_right);
            slideOutToRightAnimation.reset();

            Animation slideInFromRightAnimation = AnimationUtils.loadAnimation(this,
                    R.anim.anim_slide_in_left);
            slideInFromRightAnimation.reset();

            Animation slideUpAnimation = AnimationUtils.loadAnimation(this,
                    R.anim.anim_slide_up_from_its_position);
            slideUpAnimation.reset();
            slideUpAnimation.setFillAfter(true);

            if(rvParkingChild.getVisibility() == View.VISIBLE) { //show 1st page
                rvParkingChild.setVisibility(View.GONE);

                tvParkingLotName.setVisibility(View.GONE);
                tvParkingLotName.startAnimation(fadeOutAnim);

                rlParkingIcon.startAnimation(slideDownAnimation);

                rvParkingChild.startAnimation(slideOutToRightAnimation);
                if(mParkingLotSelectedPosition != - 1) showDoneBtn(true, rvParking, getString(R.string.action_next));
                else showDoneBtn(false, rvParking, "");


                tvQuestionOne.startAnimation(fadeInAnim);
                tvQuestionOne.setVisibility(View.VISIBLE);
                tvQuestionTwo.startAnimation(fadeOutAnim);
                tvQuestionTwo.setVisibility(View.GONE);
            } else {//now 2nd page
                rlParkingIcon.startAnimation(slideUpAnimation);

                tvParkingLotName.setVisibility(View.VISIBLE);
                tvParkingLotName.setText(ParkingManager.sParkings.getParkings().get(mParkingLotSelectedPosition).getName());
                tvParkingLotName.startAnimation(fadeInAnim);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setAutoMeasureEnabled(false);

                rvParkingChild.setLayoutManager(linearLayoutManager);
                rvParkingChild.setHasFixedSize(true);
                ParkingRecyclerViewAdapter parkingRecyclerViewAdapter = new ParkingRecyclerViewAdapter(this, null, ParkingManager.sParkings.getParkings().get(mParkingLotSelectedPosition).getChildParkings());
                rvParkingChild.setAdapter(parkingRecyclerViewAdapter);

                rvParkingChild.setVisibility(View.VISIBLE);
                rvParkingChild.startAnimation(slideInFromRightAnimation);

                if(mEntranceSelectedPosition != -1) showDoneBtn(true, rvParkingChild, getString(R.string.action_done));
                else showDoneBtn(true, rvParkingChild, getString(R.string.action_back));

                tvQuestionOne.startAnimation(fadeOutAnim);
                tvQuestionOne.setVisibility(View.GONE);
                tvQuestionTwo.startAnimation(fadeInAnim);
                tvQuestionTwo.setVisibility(View.VISIBLE);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showSaveParkingSpotScreen(boolean enable){
        mDoneBtnShown = false;
        Analytics.getInstance(this).logScreenView(this, "Save My Parking Spot Confirmation");

        if(enable) { //now show 3rd page
            tvQuestionOne.setVisibility(View.GONE);
            tvQuestionTwo.setVisibility(View.GONE);

            Animation slideDownAnimation = AnimationUtils.loadAnimation(ParkingActivity.this,
                    R.anim.anim_slide_down_out_of_screen);
            slideDownAnimation.reset();
            slideDownAnimation.setFillAfter(true);

            Animation fadeInAnim = AnimationUtils.loadAnimation(ParkingActivity.this,
                    android.R.anim.fade_in);
            fadeInAnim.reset();

            tvEntrancename.setVisibility(View.VISIBLE);
            tvEntrancename.setText(ParkingManager.sParkings.getParkings().get(mParkingLotSelectedPosition).getChildParkings().get(mEntranceSelectedPosition).getName());
            tvEntrancename.startAnimation(fadeInAnim);

            rvParking.startAnimation(slideDownAnimation);
            rvParkingChild.startAnimation(slideDownAnimation);
            showDoneBtn(false, rvParking, "");

            tvEditSelection.setVisibility(View.VISIBLE);
            tvSaveParkingLot.setVisibility(View.VISIBLE);
            rlNote.setVisibility(View.VISIBLE);

        } else {

            tvQuestionOne.setVisibility(View.GONE);
            tvQuestionTwo.setVisibility(View.VISIBLE);

            Animation slideUpAnimation = AnimationUtils.loadAnimation(ParkingActivity.this,
                    R.anim.anim_slide_up_from_out_of_screen);
            slideUpAnimation.reset();
            slideUpAnimation.setFillAfter(true);

            tvEntrancename.setVisibility(View.GONE);

            rvParking.startAnimation(slideUpAnimation);
            rvParkingChild.startAnimation(slideUpAnimation);
            showDoneBtn(true, rvParkingChild, getString(R.string.action_next));

            tvEditSelection.setVisibility(View.GONE);
            tvSaveParkingLot.setVisibility(View.GONE);
            rlNote.setVisibility(View.GONE);
        }
    }

    private void showDoneBtn(boolean show, RecyclerView rv, String text) {
        if(!show) {
            mDoneBtnShown = true;
            tvFooter.setVisibility(View.GONE);
            Animation slideUpAnimation = AnimationUtils.loadAnimation(this,
                    R.anim.anim_slide_down_out_of_screen);
            slideUpAnimation.reset();
            tvFooter.startAnimation(slideUpAnimation);
            rv.setPadding(0, 0, 0, 0);
        } else {
            if(mDoneBtnShown) return;
            tvFooter.setVisibility(View.VISIBLE);
            tvFooter.setText(text);
            if(tvFooter.getVisibility() != View.VISIBLE) {
                Animation slideUpAnimation = AnimationUtils.loadAnimation(this,
                        R.anim.anim_slide_up_from_out_of_screen);
                slideUpAnimation.reset();
                tvFooter.startAnimation(slideUpAnimation);
            }
            rv.setPadding(0, 0, 0, (int) getResources().getDimension(R.dimen.parking_footer_height));
        }
    }


    private boolean isViewOverlapping(View firstView, View secondView) {
        int[] firstPosition = new int[2];
        int[] secondPosition = new int[2];

        firstView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        firstView.getLocationOnScreen(firstPosition);
        secondView.getLocationOnScreen(secondPosition);

        int r = firstView.getMeasuredWidth() + firstPosition[0];
        int l = secondPosition[0];
        return r >= l && (r != 0 && l != 0);
    }

    public class ParkingRecyclerViewAdapter extends RecyclerView.Adapter {

        private Context mContext;
        private List<Parking> mParkingList;
        private List<ChildParking> mChildParkingList;
        private boolean mUseMyLocationExist = false;
        private View.OnClickListener mOnClickListener;

        public ParkingRecyclerViewAdapter(Context context, List<Parking> parkingList, List<ChildParking> childParkingList) {
            mContext = context;
            mParkingList = parkingList;
            mChildParkingList = childParkingList;
        }

        public void addHeader(View.OnClickListener onClickListener){
            mUseMyLocationExist = true;
            mOnClickListener = onClickListener;
            Parking fakeParking = new Parking();
            mParkingList.add(0, fakeParking);
            notifyDataSetChanged();
        }

        public class PlaceHolder extends RecyclerView.ViewHolder {
            public View mView;
            public TextView tvParkingLotName;
            public ImageView ivCheck;

            public PlaceHolder(View v) {
                super(v);
                mView = v;
                tvParkingLotName = (TextView)  v.findViewById(R.id.tvParkingLotName);
                ivCheck = (ImageView)  v.findViewById(R.id.ivCheck);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType){
                case ITEM_TYPE_PARKING:
                    return new PlaceHolder(
                            LayoutInflater.from(mContext).inflate(R.layout.list_item_parking, parent, false));
                case ITEM_TYPE_ENTRANCE:
                    return new PlaceHolder(
                            LayoutInflater.from(mContext).inflate(R.layout.list_item_parking, parent, false));
                case ITEM_TYPE_USE_MY_LOCATION:
                    return new RecyclerViewFooter.HeaderViewHolder(
                            LayoutInflater.from(mContext).inflate(R.layout.list_item_parking_use_my_location, parent, false));
                case ITEM_TYPE_FOOTER:
                    return new PlaceHolder(
                            LayoutInflater.from(mContext).inflate(R.layout.list_item_parking_footer, parent, false));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            try {
                if(getItemViewType(position) == ITEM_TYPE_USE_MY_LOCATION) {
                    RecyclerViewFooter.HeaderViewHolder headerViewHolder = (RecyclerViewFooter.HeaderViewHolder) holder;
                    headerViewHolder.mView.setOnClickListener(mOnClickListener);
                    return;
                }

                if(holder.getItemViewType() == ITEM_TYPE_PARKING){

                    final Parking parking = mParkingList.get(position);
                    final PlaceHolder placeHolder = (PlaceHolder) holder;
                    setSelected(mParkingLotSelectedPosition == position, placeHolder.tvParkingLotName, placeHolder.ivCheck);
                    placeHolder.tvParkingLotName.setText(parking.getName());
                    placeHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.clearAnimation();
                            Utility.startSqueezeAnimationForInterestedParking(new Utility.SqueezeListener() {
                                @Override
                                public void OnSqueezeAnimationDone() {
                                    mParkingLotSelectedPosition = position;
                                    notifyDataSetChanged();
                                    showDoneBtn(true, rvParking, getString(R.string.action_next));
                                }
                            }, (Activity) mContext, placeHolder.mView);
                        }
                    });
                } else if(holder.getItemViewType() == ITEM_TYPE_ENTRANCE) {
                    final ChildParking childParking = mChildParkingList.get(position);
                    final PlaceHolder placeHolder = (PlaceHolder) holder;
                    setSelected(mEntranceSelectedPosition == position, placeHolder.tvParkingLotName, placeHolder.ivCheck);
                    placeHolder.tvParkingLotName.setText(childParking.getName());
                    placeHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.clearAnimation();
                            Utility.startSqueezeAnimationForInterestedParking(new Utility.SqueezeListener() {
                                @Override
                                public void OnSqueezeAnimationDone() {
                                    mEntranceSelectedPosition = position;
                                    notifyDataSetChanged();
                                    showDoneBtn(true, rvParkingChild, getString(R.string.action_done));
                                }
                            }, (Activity) mContext, placeHolder.mView);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if(mParkingList != null) {
                return mParkingList.size();
            } else if (mChildParkingList != null) {
                return mChildParkingList.size();
            }
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            if(position == 0 && mUseMyLocationExist) return ITEM_TYPE_USE_MY_LOCATION;
            else if(mParkingList != null) return ITEM_TYPE_PARKING;
            else if(mChildParkingList != null) return ITEM_TYPE_ENTRANCE;
            return ITEM_TYPE_PARKING;
        }

        public void setSelected(boolean selected, TextView textView, ImageView imageView){
            if(selected) {
                textView.setTextColor(getResources().getColor(R.color.themeColor));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                Typeface tf = Typeface.createFromAsset(mContext.getAssets(), mContext.getResources().getString(R.string.fontFamily_roboto_medium));
                textView.setTypeface(tf);
                imageView.setVisibility(View.VISIBLE);
            } else {
                textView.setTextColor(getResources().getColor(R.color.intrstd_txt_off));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                Typeface tf = Typeface.createFromAsset(mContext.getAssets(), mContext.getResources().getString(R.string.fontFamily_roboto_regular));
                textView.setTypeface(tf);
                imageView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(tvEditSelection.getVisibility() == View.VISIBLE){
            showSaveParkingSpotScreen(false);
            setupChildRecyclerView();
        } else if(rvParkingChild.getVisibility() == View.VISIBLE) {
            setupChildRecyclerView();
        } else {
            checkForUnsavedAndShowAlertBeforeExit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Analytics.getInstance(this).logScreenView(this, "Save My Parking Spot");
    }

    public void onFinish(){
        finish();
        ActivityAnimation.exitActivityAnimation(this);
    }
}
