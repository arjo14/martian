package com.example.root.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.root.activity.MainActivity;
import com.example.root.myapplication.R;

public class BackgroundFragment extends Fragment implements View.OnClickListener {
    private AppCompatImageView imageButton;
    private RelativeLayout frameLayout;
    private TextView textView;
    private SharedPreferences.Editor editor;
    private int coinCount;

    public BackgroundFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_background, container, false);

        SharedPreferences sharedPref = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        if (sharedPref.getInt("money", -1) == -1) {
            editor.putInt("money", coinCount);
            editor.apply();
        } else {
            coinCount = sharedPref.getInt("money", coinCount);
        }
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


}
