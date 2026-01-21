package com.example.blackjack_ful;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;

public class BoardGame extends View {

    Bitmap bitmap, bitmapStand,bitmapHit,bitmapDouble,bitmapSplit;
    private Context context;
    StandButton standButton;
    HitButton hitButton;
    DoubleButton doubleButton;
    SplitButton splitButton;

    private Bitmap background;
    public BoardGame(Context context) {
        super(context);
        this.context = context;

        bitmap =      BitmapFactory.decodeResource(context.getResources(),R.drawable.background);
        bitmapStand = BitmapFactory.decodeResource(context.getResources(),R.drawable.standbutton);
        bitmapStand = Bitmap.createScaledBitmap(bitmapStand,600,400,true);
        standButton = new StandButton(425,1800,bitmapStand);
        bitmapHit = BitmapFactory.decodeResource(getResources(),R.drawable.hitbutton);
        bitmapHit = Bitmap.createScaledBitmap(bitmapHit,600,400,true);
        hitButton = new HitButton(25,1800,bitmapHit);
        bitmapDouble = BitmapFactory.decodeResource(getResources(),R.drawable.doublebutton);
        bitmapDouble = Bitmap.createScaledBitmap(bitmapDouble,600,400,true);
        doubleButton = new DoubleButton(25,1900,bitmapDouble);
        bitmapSplit = BitmapFactory.decodeResource(getResources(),R.drawable.splitbutton);
        bitmapSplit = Bitmap.createScaledBitmap(bitmapSplit,600,400,true);
        splitButton = new SplitButton(425,1900,bitmapSplit);
    }


    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bitmap,0,0,null);

        standButton.drawStand(canvas);
        hitButton.drawHit(canvas);
        doubleButton.drawDouble(canvas);
        splitButton.drawSplit(canvas);



    }
}
