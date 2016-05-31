package com.kineticcafe.kcpmall.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.views.zoomable.TouchImageView;

public class ZoomableImage extends AppCompatActivity {

	private Toolbar mToolbar;
    private boolean mIsToolbarHidden;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zoomable_image);

		String imageUrl = (String) getIntent().getSerializableExtra(Constants.ARG_IMAGE_URL);
		TouchImageView ivDetailImage = (TouchImageView) findViewById(R.id.ivDetailImage);
        ivDetailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleHideyBar();
                animateToolbar(!mIsToolbarHidden);
            }
        });

        Glide.with(this)
                .load(imageUrl)
                .crossFade()
                .error(R.drawable.placeholder)
                .into(ivDetailImage);

		initActionBar();
		hideSystemUi();
	}
	
	private void hideSystemUi() {
		if (Build.VERSION.SDK_INT < 16) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_IMMERSIVE);
		}
        animateToolbar(true);
	}

	public void initActionBar(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
	}

    public void animateToolbar(boolean hide){
        if(hide){
            getSupportActionBar().hide();
//            mToolbar.animate().translationY(-mToolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
        } else {
            getSupportActionBar().show();
//            mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
        }
        mIsToolbarHidden = hide;
    }
	public void toggleHideyBar() {

		int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
		int newUiOptions = uiOptions;
		boolean isImmersiveModeEnabled =
				((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
		if (isImmersiveModeEnabled) {
		} else {
		}

		if (Build.VERSION.SDK_INT >= 14) {
			newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		}

		if (Build.VERSION.SDK_INT >= 16) {
			newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
		}

		if (Build.VERSION.SDK_INT >= 18) {
			newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		}

		getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                /*NavUtils.navigateUpFromSameTask(this);*/
                this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
