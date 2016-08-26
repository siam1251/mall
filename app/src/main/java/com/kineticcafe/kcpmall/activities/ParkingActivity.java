package com.kineticcafe.kcpmall.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.mappedin.Amenities;
import com.kineticcafe.kcpmall.parking.ChildParking;
import com.kineticcafe.kcpmall.parking.Parking;
import com.kineticcafe.kcpmall.parking.ParkingManager;
import com.kineticcafe.kcpmall.views.ActivityAnimation;
import com.kineticcafe.kcpmall.views.AlertDialogForInterest;

import java.io.FileInputStream;
import java.util.List;

/**
 * Created by Kay on 2016-08-15.
 */
public class ParkingActivity extends AppCompatActivity {

    public final int ITEM_TYPE_PARKING = 0;
    public final int ITEM_TYPE_ENTRANCE = 1;
    public final int ITEM_TYPE_FOOTER = 2;
    private int mParkingLotSelectedPosition = -1;
    private int mEntranceSelectedPosition = -1;
    private String mParkingNote = "";
    private TextView tvFooter;
    private TextView tvNote;
    private RecyclerView rvParking;
    private RecyclerView rvParkingChild;
    private RelativeLayout rlParkingIcon;
    private RelativeLayout rlNote;
    private TextView tvParkingLotName;
    private TextView tvEntrancename;
    private TextView tvParkingLotQuestion;
    private TextView tvEntranceQuestion;
    private TextView tvEditSelection;
    private TextView tvSaveParkingLot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        RelativeLayout rlParking = (RelativeLayout) findViewById(R.id.rlParking);
        Bitmap bitmap = null;
        String filename = getIntent().getStringExtra("image");
        /*if(filename != null) {
            try {
                FileInputStream is = this.openFileInput(filename);
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
                if(bitmap != null){
                    rlParking.setBackground(new BitmapDrawable(bitmap));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        rvParking = (RecyclerView) findViewById(R.id.rvParking);
        rvParkingChild = (RecyclerView) findViewById(R.id.rvParkingChild);
        rlParkingIcon = (RelativeLayout) findViewById(R.id.rlParkingIcon);
        rlNote = (RelativeLayout) findViewById(R.id.rlNote);
        tvParkingLotName = (TextView) findViewById(R.id.tvParkingLotName);
        tvNote = (TextView) findViewById(R.id.tvNote);
        tvEntrancename = (TextView) findViewById(R.id.tvEntrancename);
        tvParkingLotQuestion = (TextView) findViewById(R.id.tvParkingLotQuestion);
        tvEntranceQuestion = (TextView) findViewById(R.id.tvEntranceQuestion);
        tvEditSelection = (TextView) findViewById(R.id.tvEditSelection);
        tvEditSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveParkingSpotScreen(false);
                setupChildRecyclerView();
            }
        });
        tvSaveParkingLot = (TextView) findViewById(R.id.tvSaveParkingLot);
        tvSaveParkingLot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParkingManager.saveParkingSpotAndEntrance(ParkingActivity.this, mParkingNote, mParkingLotSelectedPosition, mEntranceSelectedPosition);
                Toast.makeText(ParkingActivity.this, "Parking Lot Saved", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK, new Intent());
                onFinish();
            }
        });

        setParkingNoteBtn(ParkingManager.getParkingNotes(this));
        rlNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialogForInterest alertDialogForInterest = new AlertDialogForInterest();
                alertDialogForInterest.getEditTextAlertDialog(
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
                        }).show();
            }
        });

        ImageView ivDismiss = (ImageView) findViewById(R.id.ivDismiss);
        ivDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mParkingLotSelectedPosition != -1) {
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
                            }).show();
                } else onFinish();

            }
        });

        tvFooter = (TextView) findViewById(R.id.tvFooter);
        tvFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvFooter.getText().toString().equals("NEXT")){
                    setupChildRecyclerView();
                } else if(tvFooter.getText().toString().equals("BACK")){
                    setupChildRecyclerView();
                }  else if (tvFooter.getText().toString().equals("DONE")){
                    showSaveParkingSpotScreen(true);
                }
                /*if(rvParkingChild.getVisibility() == View.VISIBLE) {
                    setupChildRecyclerView();
                } else if(rvParkingChild.getVisibility() != View.VISIBLE) {
                    if(mParkingLotSelectedPosition != -1) setupChildRecyclerView();
                } else {
                    showSaveParkingSpotScreen(true);
                }*/
            }
        });

        mParkingNote = ParkingManager.getParkingNotes(ParkingActivity.this);
        mParkingLotSelectedPosition = ParkingManager.getSavedParkingLotPosition(this);
        mEntranceSelectedPosition = ParkingManager.getSavedEntrancePosition(this);

        setupRecyclerView();
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
        ParkingRecyclerViewAdapter parkingRecyclerViewAdapter = new ParkingRecyclerViewAdapter(this, ParkingManager.sParkings.getParkings(), null);
        rvParking.setAdapter(parkingRecyclerViewAdapter);


        try {
            //select previously saved parking lot
            if(ParkingManager.isParkingLotSaved(this) && mParkingLotSelectedPosition != -1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rvParking.findViewHolderForAdapterPosition(mParkingLotSelectedPosition).itemView.performClick();
                        showDoneBtn(true, rvParking, "NEXT");
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
            ;
            if(rvParkingChild.getVisibility() == View.VISIBLE) {
                rvParkingChild.setVisibility(View.GONE);

                tvParkingLotName.setVisibility(View.GONE);
                tvParkingLotName.startAnimation(fadeOutAnim);

                rlParkingIcon.startAnimation(slideDownAnimation);

                rvParkingChild.startAnimation(slideOutToRightAnimation);
//                mEntranceSelectedPosition = -1;
//                showDoneBtn(false, rvParking);
                if(mParkingLotSelectedPosition != - 1) showDoneBtn(true, rvParking, "NEXT");
                else showDoneBtn(false, rvParking, "");


                tvParkingLotQuestion.startAnimation(fadeInAnim);
                tvParkingLotQuestion.setVisibility(View.VISIBLE);
                tvEntranceQuestion.startAnimation(fadeOutAnim);
                tvEntranceQuestion.setVisibility(View.GONE);
            } else {

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

                try {
                    //select previously saved parking lot
                    /*if(mEntranceSelectedPosition != -1) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                rvParkingChild.findViewHolderForAdapterPosition(mEntranceSelectedPosition).itemView.performClick();
                            }
                        },1);
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }


//                showDoneBtn(true, rvParkingChild);
                if(mEntranceSelectedPosition != -1) showDoneBtn(true, rvParkingChild, "DONE");
                else showDoneBtn(true, rvParkingChild, "BACK");


                tvParkingLotQuestion.startAnimation(fadeOutAnim);
                tvParkingLotQuestion.setVisibility(View.GONE);
                tvEntranceQuestion.startAnimation(fadeInAnim);
                tvEntranceQuestion.setVisibility(View.VISIBLE);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }


    private void showSaveParkingSpotScreen(boolean enable){
        if(enable) {
            tvParkingLotQuestion.setVisibility(View.GONE);
            tvEntranceQuestion.setVisibility(View.GONE);

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
//            showDoneBtn(true, rvParking);
            showDoneBtn(false, rvParking, "");

            tvEditSelection.setVisibility(View.VISIBLE);
            tvSaveParkingLot.setVisibility(View.VISIBLE);
            rlNote.setVisibility(View.VISIBLE);

        } else {

            tvParkingLotQuestion.setVisibility(View.GONE);
            tvEntranceQuestion.setVisibility(View.VISIBLE);

            Animation slideUpAnimation = AnimationUtils.loadAnimation(ParkingActivity.this,
                    R.anim.anim_slide_up_from_out_of_screen);
            slideUpAnimation.reset();
            slideUpAnimation.setFillAfter(true);

            tvEntrancename.setVisibility(View.GONE);

            rvParking.startAnimation(slideUpAnimation);
            rvParkingChild.startAnimation(slideUpAnimation);
//            showDoneBtn(false, rvParkingChild);
            showDoneBtn(true, rvParkingChild, "NEXT");

            tvEditSelection.setVisibility(View.GONE);
            tvSaveParkingLot.setVisibility(View.GONE);
            rlNote.setVisibility(View.GONE);
        }
    }


    /*private void showDoneBtn(boolean forceHide, RecyclerView rv) {
        if(forceHide) {
            tvFooter.setVisibility(View.GONE);
            Animation slideUpAnimation = AnimationUtils.loadAnimation(this,
                    R.anim.anim_slide_down_out_of_screen);
            slideUpAnimation.reset();
            tvFooter.startAnimation(slideUpAnimation);
            rv.setPadding(0, 0, 0, 0);
        } else {
            if(tvFooter.getVisibility() != View.VISIBLE) {
                tvFooter.setVisibility(View.VISIBLE);
                Animation slideUpAnimation = AnimationUtils.loadAnimation(this,
                        R.anim.anim_slide_up_from_out_of_screen);
                slideUpAnimation.reset();
                tvFooter.startAnimation(slideUpAnimation);
                rv.setPadding(0, 0, 0, (int) getResources().getDimension(R.dimen.parking_footer_height));
            }
        }
    }*/

    private void showDoneBtn(boolean show, RecyclerView rv, String text) {
        if(!show) {
            tvFooter.setVisibility(View.GONE);
            Animation slideUpAnimation = AnimationUtils.loadAnimation(this,
                    R.anim.anim_slide_down_out_of_screen);
            slideUpAnimation.reset();
            tvFooter.startAnimation(slideUpAnimation);
            rv.setPadding(0, 0, 0, 0);
        } else {
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

        public ParkingRecyclerViewAdapter(Context context, List<Parking> parkingList, List<ChildParking> childParkingList) {
            mContext = context;
            mParkingList = parkingList;
            mChildParkingList = childParkingList;
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
                case ITEM_TYPE_FOOTER:
                    return new PlaceHolder(
                            LayoutInflater.from(mContext).inflate(R.layout.list_item_parking_footer, parent, false));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if(holder.getItemViewType() == ITEM_TYPE_PARKING){

                final Parking parking = mParkingList.get(position);
                final PlaceHolder placeHolder = (PlaceHolder) holder;
                setSelected(mParkingLotSelectedPosition == position, placeHolder.tvParkingLotName, placeHolder.ivCheck);
                placeHolder.tvParkingLotName.setText(parking.getName());
                placeHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParkingLotSelectedPosition = position;
                        notifyDataSetChanged();
//                        showDoneBtn(false, rvParking);
                        showDoneBtn(true, rvParking, "NEXT");
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
                        mEntranceSelectedPosition = position;
                        notifyDataSetChanged();
//                        showDoneBtn(false, rvParkingChild);
                        showDoneBtn(true, rvParkingChild, "DONE");
                    }
                });
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
            if(mParkingList != null) return ITEM_TYPE_PARKING;
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
                textView.setTextColor(getResources().getColor(R.color.intrstd_txt));
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
        } else if(rvParkingChild.getVisibility() == View.VISIBLE) {
            setupChildRecyclerView();
        } else {
            onFinish();
        }
    }

    public void onFinish(){
        finish();
        ActivityAnimation.exitActivityAnimation(this);
    }
}
