package com.ivanhoecambridge.mall.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ivanhoecambridge.kcpandroidsdk.constant.KcpConstants;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpContentPage;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.analytics.Analytics;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.activities.DetailActivity;
import com.ivanhoecambridge.mall.activities.InterestedCategoryActivity;
import com.ivanhoecambridge.mall.factory.GlideFactory;
import com.ivanhoecambridge.mall.factory.KcpContentTypeFactory;
import com.ivanhoecambridge.mall.fragments.HomeFragment;
import com.ivanhoecambridge.mall.interfaces.FavouriteInterface;
import com.ivanhoecambridge.mall.views.KCPSetRatioImageView;
import com.ivanhoecambridge.mall.views.RecyclerViewFooter;
import com.ivanhoecambridge.mall.managers.FavouriteManager;
import com.ivanhoecambridge.mall.utility.Utility;
import com.ivanhoecambridge.mall.views.ActivityAnimation;

import java.util.ArrayList;

public class DealsRecyclerViewAdapter extends RecyclerView.Adapter {

    //TODO: should implement the filtering method so other deals don't have duplicates from recommended deals
    private Context mContext;

    private ArrayList<KcpContentPage> mKcpContentPagesOtherDeals;
    private ArrayList<KcpContentPage> mKcpContentPagesRecommendedDeals;
    private boolean mhasSectionHeaders = true;
    private int mDealLayoutResource;
    private FavouriteInterface mFavouriteInterface;

    private ArrayList<Object> mItems;


    public DealsRecyclerViewAdapter(Context context, boolean hasSectionHeaders, ArrayList<KcpContentPage> recommendedDeals, ArrayList<KcpContentPage> otherDeals) {
        mContext = context;
        mhasSectionHeaders = hasSectionHeaders;

        mKcpContentPagesRecommendedDeals = recommendedDeals == null ? new ArrayList<KcpContentPage>() : new ArrayList<KcpContentPage>(recommendedDeals);
        mKcpContentPagesOtherDeals = otherDeals == null ? new ArrayList<KcpContentPage>() : new ArrayList<KcpContentPage>(otherDeals);

        createItems();
    }

    public DealsRecyclerViewAdapter(Context context, boolean hasSectionHeaders, int dealLayoutResource, ArrayList<KcpContentPage> recommendedDeals, ArrayList<KcpContentPage> otherDeals) {
        mContext = context;
        mhasSectionHeaders = hasSectionHeaders;
        mDealLayoutResource = dealLayoutResource;

        mKcpContentPagesRecommendedDeals = recommendedDeals == null ? new ArrayList<KcpContentPage>() : new ArrayList<KcpContentPage>(recommendedDeals);
        mKcpContentPagesOtherDeals = otherDeals == null ? new ArrayList<KcpContentPage>() : new ArrayList<KcpContentPage>(otherDeals);

        createItems();
    }

    public void setFavouriteListener(FavouriteInterface favouriteInterface){
        mFavouriteInterface = favouriteInterface;
    }

    public void createItems(){
        if(mItems == null) mItems = new ArrayList<>();
        else mItems.clear();

        removeDuplicateFromOtherDeals();

        int sizeOfRecommendedDeals = mKcpContentPagesRecommendedDeals == null ? 0 : mKcpContentPagesRecommendedDeals.size();
        int sizeOfOtherDeals = mKcpContentPagesOtherDeals == null ? 0 : mKcpContentPagesOtherDeals.size();

        boolean recommendedDealsExist =  sizeOfRecommendedDeals > 0 ? true : false;
        boolean otherDealsExist = sizeOfOtherDeals > 0 ? true : false;

        if(recommendedDealsExist){
            if(mhasSectionHeaders) mItems.add(KcpContentTypeFactory.ITEM_TYPE_SECTION_HEADER_RECOMMENDED_DEALS);
            mItems.addAll(mKcpContentPagesRecommendedDeals);
            if(mhasSectionHeaders) mItems.add(KcpContentTypeFactory.ITEM_TYPE_ADJUST_MY_INTEREST);
        } else {
            if(otherDealsExist) mItems.add(KcpContentTypeFactory.ITEM_TYPE_SET_MY_INTEREST); //meaning the entire lists are empty
        }

        if(otherDealsExist){
            if(mhasSectionHeaders) mItems.add(KcpContentTypeFactory.ITEM_TYPE_SECTION_HEADER_OTHER_DEALS);
            mItems.addAll(mKcpContentPagesOtherDeals);
        }

        notifyDataSetChanged();
    }

    public void updateRecommendedDealData(ArrayList<KcpContentPage> recommendedDeals) {
        mKcpContentPagesRecommendedDeals.clear();
        mKcpContentPagesRecommendedDeals.addAll(recommendedDeals); //TESTING
        createItems();
    }

    public void updateOtherDealData(ArrayList<KcpContentPage> otherDeals) {
        mKcpContentPagesOtherDeals.clear();
        mKcpContentPagesOtherDeals.addAll(otherDeals);
        createItems();
    }

    private void removeDuplicateFromOtherDeals(){
        if(mKcpContentPagesOtherDeals != null && mKcpContentPagesRecommendedDeals != null){
            for(KcpContentPage kcpContentPageRecommended : mKcpContentPagesRecommendedDeals){
                if(mKcpContentPagesOtherDeals.contains(kcpContentPageRecommended)){
                    mKcpContentPagesOtherDeals.remove(kcpContentPageRecommended);
                }
            }
        }
    }

    private int mFooterLayout;
    private boolean mFooterExist = false;
    private String mFooterText;
    private View.OnClickListener mOnClickListener;

    public void addFooter(String footerText, int footerLayout, View.OnClickListener onClickListener){
        mFooterExist = true;
        mFooterText = footerText;
        mFooterLayout = footerLayout;
        mOnClickListener = onClickListener;
        mItems.add("FOOTER");
        notifyDataSetChanged();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        public MainViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    public class LoadingViewHolder extends MainViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pbNewsAdapter);
        }
    }

    public class SectionHeaderViewHolder extends MainViewHolder {
        public TextView  tvSectionHeader;

        public SectionHeaderViewHolder(View v) {
            super(v);
            tvSectionHeader = (TextView)  v.findViewById(R.id.tvSectionHeader);
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) v.getLayoutParams();
            p.setFullSpan(true);
        }
    }

    public class DealsViewHolder extends MainViewHolder {
        public RelativeLayout rlDeal;
        public ImageView ivDealLogo;
        public TextView tvDealStoreName;
        public TextView tvDealTitle;
        public TextView tvExpiryDate;
        public ImageView  ivFav;

        public DealsViewHolder(View v) {
            super(v);
            rlDeal             = (RelativeLayout)  v.findViewById(R.id.rlDeal);
            ivDealLogo  = (ImageView) v.findViewById(R.id.ivDealLogo);
            tvDealStoreName = (TextView)  v.findViewById(R.id.tvDealStoreName);
            tvDealTitle = (TextView)  v.findViewById(R.id.tvDealTitle);
            tvExpiryDate = (TextView)  v.findViewById(R.id.tvExpiryDate);
            ivFav         = (ImageView)  v.findViewById(R.id.ivFav);
        }
    }

    public class SetMyInterestViewHolder extends MainViewHolder {
        public TextView tvIntrstTitle;
        public TextView tvIntrstDesc;
        public TextView tvIntrstBtn;


        public SetMyInterestViewHolder(View v){
            super(v);

            tvIntrstTitle = (TextView)  v.findViewById(R.id.tvIntrstTitle);
            tvIntrstDesc = (TextView)  v.findViewById(R.id.tvIntrstDesc);
            tvIntrstBtn = (TextView)  v.findViewById(R.id.tvIntrstBtn);

            StaggeredGridLayoutManager.LayoutParams param = (StaggeredGridLayoutManager.LayoutParams) mView.getLayoutParams();
            param.bottomMargin = (int) mContext.getResources().getDimension(R.dimen.intrst_card_bot_margin);
            mView.setLayoutParams(param);

            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) v.getLayoutParams();
            p.setFullSpan(true);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType){
            case KcpContentTypeFactory.ITEM_TYPE_LOADING:
                return new LoadingViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_loading_item, parent, false));
            case KcpContentTypeFactory.ITEM_TYPE_DEAL:
                if(mDealLayoutResource != 0) return new DealsViewHolder(LayoutInflater.from(mContext).inflate(mDealLayoutResource, parent, false)); //showing the horizontal deals list inside store details
                else return new DealsViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item_deal, parent, false)); //normal deals list
            case KcpContentTypeFactory.ITEM_TYPE_ADJUST_MY_INTEREST:
                return new SetMyInterestViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item_interest, parent, false));
            case KcpContentTypeFactory.ITEM_TYPE_SET_MY_INTEREST:
                return new SetMyInterestViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item_interest, parent, false));
            case KcpContentTypeFactory.ITEM_TYPE_SECTION_HEADER_RECOMMENDED_DEALS:
                return new SectionHeaderViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_section_header, parent, false));
            case KcpContentTypeFactory.ITEM_TYPE_SECTION_HEADER_OTHER_DEALS:
                return new SectionHeaderViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_section_header, parent, false));
            case KcpContentTypeFactory.ITEM_TYPE_FOOTER:
                return new RecyclerViewFooter.FooterViewHolder(
                        LayoutInflater.from(mContext).inflate(mFooterLayout, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        } else if(holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_SET_MY_INTEREST||holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_ADJUST_MY_INTEREST){
            SetMyInterestViewHolder setMyInterestViewHolder = (SetMyInterestViewHolder) holder;
            setMyInterestViewHolder.tvIntrstTitle.setText(mContext.getResources().getString(R.string.intrst_card_recommended_title));
            if (holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_ADJUST_MY_INTEREST) {
                setMyInterestViewHolder.tvIntrstDesc.setText(mContext.getResources().getString(R.string.intrst_card_recommended_desc));
                setMyInterestViewHolder.tvIntrstBtn.setText(mContext.getResources().getString(R.string.intrst_card_recommended_btn));
            }
            setMyInterestViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity)mContext).startActivityForResult(new Intent(mContext, InterestedCategoryActivity.class), Constants.REQUEST_CODE_CHANGE_INTEREST);
                    ActivityAnimation.startActivityAnimation(mContext);
                }
            });

            //this doubles the top margin of the set interests card
            StaggeredGridLayoutManager.LayoutParams param = (StaggeredGridLayoutManager.LayoutParams) setMyInterestViewHolder.mView.getLayoutParams();
            param.topMargin = (int) mContext.getResources().getDimension(R.dimen.card_vertical_margin);
            setMyInterestViewHolder.mView.setLayoutParams(param);
        } else if(holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_SECTION_HEADER_RECOMMENDED_DEALS){
            SectionHeaderViewHolder sectionHeaderViewHolder = (SectionHeaderViewHolder) holder;
            sectionHeaderViewHolder.tvSectionHeader.setText(mContext.getResources().getString(R.string.section_header_recommended_deals));
        } else if(holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_SECTION_HEADER_OTHER_DEALS){
            SectionHeaderViewHolder sectionHeaderViewHolder = (SectionHeaderViewHolder) holder;
            sectionHeaderViewHolder.tvSectionHeader.setText(mContext.getResources().getString(R.string.section_header_other_deals));
        } else if(holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_DEAL){
            final KcpContentPage kcpContentPage = (KcpContentPage) mItems.get(position);
            final DealsViewHolder dealHolder = (DealsViewHolder) holder;

            if(kcpContentPage.getStoreName().contains("Oakley")){
                String a = "ewfsef";
            }

            int placeHolderDrawable = R.drawable.placeholder;
            String imageUrl = kcpContentPage.getHighestResImageUrl();
            if(imageUrl.equals("")) {
                imageUrl = kcpContentPage.getHighestResFallbackImageUrl();
                placeHolderDrawable = R.drawable.placeholder_logo;

                Glide.with(mContext)
                        .load(imageUrl)
                        .crossFade()
                        .error(placeHolderDrawable)
                        .fitCenter()
                        .override(KcpUtility.getScreenWidth(mContext), (int) (KcpUtility.getScreenWidth(mContext) / KcpUtility.getFloat(mContext, R.dimen.ancmt_image_ratio)))
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .placeholder(placeHolderDrawable)
                        .into(dealHolder.ivDealLogo);


            } else {


                Glide.with(mContext)
                        .load(imageUrl)
                        .crossFade()
                        .error(placeHolderDrawable)
                        .centerCrop()
                        .override(KcpUtility.getScreenWidth(mContext), (int) (KcpUtility.getScreenWidth(mContext) / KcpUtility.getFloat(mContext, R.dimen.ancmt_image_ratio)))
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .placeholder(placeHolderDrawable)
                        .into(dealHolder.ivDealLogo);
            }

            final String storename = kcpContentPage.getStoreName();
            dealHolder.tvDealStoreName.setText(storename);

            final String title = kcpContentPage.getTitle();
            dealHolder.tvDealTitle.setText(title);

            final String likeLink = kcpContentPage.getLikeLink();
            dealHolder.ivFav.setSelected(FavouriteManager.getInstance(mContext).isLiked(likeLink, kcpContentPage));
            dealHolder.ivFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(HomeFragment.getInstance().isResumed()) {
                        if (dealHolder.ivFav.isSelected())
                            Analytics.getInstance(mContext).logEvent("HOME_Deal_Unlike", "HOME", "Unlike Deal", title, -1);
                        else
                            Analytics.getInstance(mContext).logEvent("HOME_Deal_Like", "HOME", "Like Deal", title, 1);
                    }
                    Utility.startSqueezeAnimationForFav(new Utility.SqueezeListener() {
                        @Override
                        public void OnSqueezeAnimationDone() {
                            dealHolder.ivFav.setSelected(!dealHolder.ivFav .isSelected());
                            FavouriteManager.getInstance(mContext).addOrRemoveFavContent(likeLink, kcpContentPage, mFavouriteInterface);
                        }
                    }, (Activity) mContext, dealHolder.ivFav);
                }
            });

            int daysLeftUntilEffectiveDate = KcpUtility.getDaysLeftUntil(kcpContentPage.effectiveEndTime, KcpConstants.EFFECTIVE_DATE_FORMAT);
            final String daysLeft = kcpContentPage.getDaysLeftText(daysLeftUntilEffectiveDate, Constants.DAYS_LEFT_TO_SHOW_IN_EXPIRY_DATE);

            if(daysLeft.equals("")){
                dealHolder.tvExpiryDate.setVisibility(View.GONE);
            } else {
                dealHolder.tvExpiryDate.setVisibility(View.VISIBLE);
                dealHolder.tvExpiryDate.setText(daysLeft); //setAnimation(new ExpiryDateAnimation().getAnimation());
            }

            dealHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(HomeFragment.getInstance().isResumed()) {
                        Analytics.getInstance(mContext).logEvent("HOME_Deal_Click", "HOME", "Click On Deal", title);
                    }

                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(Constants.ARG_CONTENT_PAGE, kcpContentPage);

                    String transitionNameImage = mContext.getResources().getString(R.string.transition_news_image);
                    String transitionNameExpiry = mContext.getResources().getString(R.string.transition_news_expiry_date);

                    ActivityOptionsCompat options;

                    if(daysLeft.equals("")) {
                        options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                (Activity)mContext,
                                Pair.create((View)dealHolder.ivDealLogo, transitionNameImage));
                    } else {
                        options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                (Activity)mContext,
                                Pair.create((View)dealHolder.ivDealLogo, transitionNameImage),
                                Pair.create((View)dealHolder.tvExpiryDate, transitionNameExpiry));
                    }

                    ActivityCompat.startActivityForResult((Activity) mContext, intent, Constants.REQUEST_CODE_VIEW_STORE_ON_MAP, options.toBundle());
                    ActivityAnimation.startActivityAnimation(mContext);
                }
            });
        } else if(holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_FOOTER){
            RecyclerViewFooter.FooterViewHolder footerViewHolder = (RecyclerViewFooter.FooterViewHolder) holder;
            //todo: manually setting the width instead of changing it dyanmically which causes cardview shift on layout change
            /*int screenWidth = KcpUtility.getScreenWidth(mContext);
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.width = screenWidth;
            int margin = KcpUtility.dpToPx((Activity) mContext, 8);
            params.setMargins(margin, margin, margin, margin);
            footerViewHolder.mView.setLayoutParams(params);*/
            footerViewHolder.mView.setOnClickListener(mOnClickListener);
            footerViewHolder.tvFooter.setText(mFooterText);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item =  mItems.get(position);
        if(mFooterExist && position == mItems.size() - 1) return KcpContentTypeFactory.ITEM_TYPE_FOOTER;
        if(item == null){
            return KcpContentTypeFactory.ITEM_TYPE_LOADING;
        } else if (item instanceof Integer) {
            return (Integer) item;
        } else {
            return KcpContentTypeFactory.ITEM_TYPE_DEAL;
        }
    }



}
