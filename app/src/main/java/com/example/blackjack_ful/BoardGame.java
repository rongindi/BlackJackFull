package com.example.blackjack_ful;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;

public class BoardGame extends View {

    Bitmap bitmap;
    private Context context;
    StandButton standButton;

    private Bitmap background;
    public BoardGame(Context context) {
        super(context);
        this.context = context;

        bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.background);
        Bitmap bitmapStand = BitmapFactory.decodeResource(getResources(),R.drawable.standbutton);
        standButton = new StandButton(300,400,bitmapStand);
    }


    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bitmap,0,0,null);
        standButton.drawStand(canvas);


    }
}
