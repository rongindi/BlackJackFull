package com.example.blackjack_ful;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Card {


        Bitmap bitmap;
        float x, y;
        private float canvasWidth;
        private float canvasHeight;
        private int val;
        private String shap;


        public Card(float x, float y, Bitmap bitmap,int val) {
            this.bitmap = bitmap;
            this.x = x;
            this.y = y;
            this.val = val;
        }

        public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        }
        public void drawCard(Canvas canvas)
        {
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, x, y, null);
            }
        }
        public int getVal(){
            return this.val;
        }

    }


