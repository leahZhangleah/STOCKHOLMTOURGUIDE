package com.example.android.stockholmtourguide;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.example.android.stockholmtourguide.data.StockholmContract.StockholmEntry;

public class ScreenSlidePagerActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,ViewPager.OnPageChangeListener {
    private  ViewPager slidePager;
    private static final int LOADER_VERSION = 2;
    private static Uri uriToQuery;
    private String tabTitle;
    private ScreenSlidePagerAdapter slidePagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide_pager);

        if (savedInstanceState==null){
            Bundle extra = getIntent().getExtras();
            if (extra!=null){
                tabTitle = extra.getString(getString(R.string.tab_title_key));
            }
        }else{
            tabTitle = savedInstanceState.getString(getString(R.string.tab_title_key));
        }

        if (tabTitle!=null){
            if (tabTitle.equals(getString(R.string.fragment_two))){
                uriToQuery = StockholmEntry.ATTRACTION_CONTENT_URI;
            }else if (tabTitle.equals(getString(R.string.fragment_three))){
                uriToQuery = StockholmEntry.HOTEL_CONTENT_URI;
            }else if (tabTitle.equals(getString(R.string.fragment_four))){
                uriToQuery = StockholmEntry.RESTAURANT_CONTENT_URI;
            }
        }

        slidePager = findViewById(R.id.screen_slide_pager);
        slidePagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(),null,this);
        slidePager.setAdapter(slidePagerAdapter);
        slidePager.addOnPageChangeListener(this);
        slidePager.setPageTransformer(true, new ZoomOutPagerTransformer());
    }

    @Override
    protected void onStart() {
        super.onStart();
        getLoaderManager().initLoader(LOADER_VERSION,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{StockholmEntry.TABLE_COLUMN_ID,
                StockholmEntry.TABLE_COLUMN_NAME,StockholmEntry.TABLE_COLUMN_PHOTO,
                StockholmEntry.TABLE_COLUMN_INTRODUCTION,StockholmEntry.TABLE_COLUMN_OPEN_TIME,
                StockholmEntry.TABLE_COLUMN_WEBSITE, StockholmEntry.TABLE_COLUMN_ADDRESS,
                StockholmEntry.TABLE_COLUMN_PHONE,StockholmEntry.TABLE_COLUMN_EMAIL,
                StockholmEntry.TABLE_COLUMN_LAT,StockholmEntry.TABLE_COLUMN_LNG};
        switch (id){
            case LOADER_VERSION:
                return new CursorLoader(this,uriToQuery,projection,null,null,null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data!=null){
            slidePagerAdapter.swapCursor(data);
            slidePager.setCurrentItem(MainActivity.currentPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        slidePagerAdapter.swapCursor(null);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        /*int currentPos = slidePager.getCurrentItem();
        if (currentPos== position){

        }*/
        MainActivity.currentPosition = position;
        Log.i("ScreenActivity", "the current position is: "+ position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class ZoomOutPagerTransformer implements ViewPager.PageTransformer{
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        @Override
        public void transformPage(View page, float position) {
            int pageWidth = page.getWidth();
            int pageHeight = page.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    page.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    page.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                page.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                page.setAlpha(0);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (slidePager.getCurrentItem()==0){
            super.onBackPressed();
        }else{
            slidePager.setCurrentItem(slidePager.getCurrentItem()-1);
        }
    }
}
