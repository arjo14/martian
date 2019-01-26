package com.example.root.dto;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Image {
    private Bitmap image;
    private int width;
    private int height;
    private int x;
    private int y;
    private int centreX;
    private int centreY;

    public Image(Resources resources, int imageId, int imageWidth, int imageHeight, int x, int y) {
        image = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, imageId), imageWidth, imageHeight, true);
        this.height = imageHeight;
        this.width = imageWidth;
        this.x = x;
        this.y = y;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getCentreX() {
        return centreX;
    }

    public void setCentreX(int centreX) {
        this.centreX = centreX;
    }

    public int getCentreY() {
        return centreY;
    }

    public void setCentreY(int centreY) {
        this.centreY = centreY;
    }

    public Image(Bitmap image, int imageWidth, int imageHeight, int imageX, int imageY) {
        this.image = image;
        this.width = imageWidth;
        this.height = imageHeight;
        this.x = imageX;
        this.y = imageY;
    }

}
