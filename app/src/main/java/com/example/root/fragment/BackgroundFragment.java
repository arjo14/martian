package com.example.root.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.root.CardFragmentPagerAdapter;
import com.example.root.CardItem;
import com.example.root.CardPagerAdapter;
import com.example.root.ShadowTransformer;
import com.example.root.activity.MainActivity;
import com.example.root.myapplication.R;

public class BackgroundFragment extends Fragment implements View.OnClickListener {
    private AppCompatImageView imageButton;
    private RelativeLayout frameLayout;
    private TextView textView;
    private SharedPreferences.Editor editor;
    private int coinCount;
    private TextView coinCountView;
    private ViewPager mViewPager;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private CardFragmentPagerAdapter mFragmentCardAdapter;
    private ShadowTransformer mFragmentCardShadowTransformer;

    public BackgroundFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_background, container, false);

        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("ball", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        if (sharedPref.getInt("money", -1) == -1) {
            editor.putInt("money", coinCount);
            editor.apply();
        } else {
            coinCount = sharedPref.getInt("money", coinCount);
        }
        coinCountView = view.findViewById(R.id.coinCount);
        coinCountView.setText(String.valueOf(coinCount));
        mViewPager = view.findViewById(R.id.viewPager);
        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem(R.string.title_1, R.string.text_1, R.drawable.ball_happy));
        mCardAdapter.addCardItem(new CardItem(R.string.title_2, R.string.text_1, R.drawable.ball_sad));
        mFragmentCardAdapter = new CardFragmentPagerAdapter(getFragmentManager(),
                dpToPixels(2, getContext()));

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        mFragmentCardShadowTransformer = new ShadowTransformer(mViewPager, mFragmentCardAdapter);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);

        textView = view.findViewById(R.id.coinIcon);
        frameLayout = view.findViewById(R.id.frame_layout_id);
        frameLayout.setBackgroundResource(R.drawable.background);
        imageButton = view.findViewById(R.id.back_button);
        imageButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                Intent intent = new Intent(getContext(), MainActivity.class);
                getActivity().finish();
                startActivity(intent);
                break;

        }
    }

    /**
     * Change value in dp to pixels
     *
     * @param dp
     * @param context
     */
    private float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

}
