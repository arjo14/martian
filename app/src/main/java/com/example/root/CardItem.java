package com.example.root;

public class CardItem {

    private int mTextResource;
    private int mTitleResource;
    private int mImageResource;

    public CardItem(int title, int text, int image) {
        mTitleResource = title;
        mTextResource = text;
        mImageResource = image;
    }

    public int getText() {
        return mTextResource;
    }

    public int getTitle() {
        return mTitleResource;
    }

    public int getImageResource() {
        return mImageResource;
    }
}
