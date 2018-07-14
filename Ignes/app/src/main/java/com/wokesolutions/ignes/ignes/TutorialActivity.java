package com.wokesolutions.ignes.ignes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TutorialActivity extends AppCompatActivity {

    private ViewPager mViewPagerTutorial;
    private int[] mLayouts = {R.layout.slideone, R.layout.slidetwo};
    private Context mContext;
    private ViewPagerAdapterClass mViewPagerAdapterClass;
    private Button botao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        mContext = this;
        mViewPagerTutorial = findViewById(R.id.viewpager_tutorial);
        mViewPagerAdapterClass = new ViewPagerAdapterClass(mLayouts);
        mViewPagerTutorial.setAdapter(mViewPagerAdapterClass);
    }

    private class ViewPagerAdapterClass extends PagerAdapter {

        private int[] layouts;
        private LayoutInflater layoutInflater;

        public ViewPagerAdapterClass(int[] layouts) {
            this.layouts = layouts;
            layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            if (position == 1) {
                view.findViewById(R.id.olaaaaa).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        finish();
                    }
                });


            }
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position,
                                @NonNull Object object) {

            View view = (View) object;
            container.removeView(view);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return super.getItemPosition(object);
        }

    }

}
