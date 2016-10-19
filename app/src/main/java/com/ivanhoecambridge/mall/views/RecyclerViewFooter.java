package com.ivanhoecambridge.mall.views;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.ivanhoecambridge.mall.R;

/**
 * Created by Kay on 2016-07-12.
 */
public class RecyclerViewFooter {

    public interface FooterInterface {
        public void onFooterClicked();

    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView tvFooter;

        public FooterViewHolder(View v) {
            super(v);
            mView = v;
            tvFooter = (TextView)  v.findViewById(R.id.tvFooter);

            if(v.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams){
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) v.getLayoutParams();
                p.setFullSpan(true);
            }
        }
    }
}
