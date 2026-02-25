package com.example.blackjack_ful;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class BoardGame extends View {
    private Hand playerHand;
    private Hand dealerHand;

    Bitmap bitmap, bitmapStand,bitmapHit,bitmapDouble,bitmapSplit, bitmapReturn, bitmapStart;
    private Context context;
    StandButton standButton;
    HitButton hitButton;
    DoubleButton doubleButton;
    ReturnButton returnButton;
    SplitButton splitButton;
    StartButton startButton;
    private Deck deck;
    private boolean gameActive = false;

    private Bitmap background;
    public BoardGame(Context context) {
        super(context);
        this.context = context;
        deck = new Deck(context);

        bitmapStart = BitmapFactory.decodeResource(context.getResources(), R.drawable.startbutton);
        bitmapStart = Bitmap.createScaledBitmap(bitmapStart, 600, 400, true);

        startButton = new StartButton(200, 1400, bitmapStart);
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
        bitmapReturn = BitmapFactory.decodeResource(getResources(),R.drawable.returnbutton);
        bitmapReturn = Bitmap.createScaledBitmap(bitmapReturn,400,400,true);
        returnButton = new ReturnButton(800,50,bitmapReturn);
        bitmapReturn = BitmapFactory.decodeResource(getResources(),R.drawable.returnbutton);
        bitmapReturn = Bitmap.createScaledBitmap(bitmapReturn,20,20,true);
        // יצירת ידיים – מיקומים זמניים (יעודכנו ב-onSizeChanged)
        playerHand = new Hand(60, 0);
        dealerHand  = new Hand(60, 0);

        deck.createDeck();
        deck.shuffle();

        // אפשר להתחיל משחק מיד – או להוסיף כפתור Start
        startNewGame();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // עדכון מיקומים דינמי לפי גובה המסך
        if (playerHand != null) {
            playerHand.setPosition(300, h - 1000);   // שחקן – 400 פיקסל מהתחתית
        }
        if (dealerHand != null) {
            dealerHand.setPosition(300, 120);       // דילר – קבוע למעלה
        }

        // אם רוצים להתאים את הרווח בין קלפים לגודל המסך
        // playerHand.cardSpacing = w / 10f;   // דוגמה – 10% מרוחב המסך

        invalidate();  // לצייר מחדש אחרי שינוי גודל
    }

    private void startNewGame() {
        playerHand.clear();
        dealerHand.clear();

        // חלוקה ראשונית – 2 קלפים לכל אחד
        playerHand.addCard(deck.drawCard());
        playerHand.addCard(deck.drawCard());

        dealerHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());

        gameActive = true;
        invalidate();
    }


    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // רקע + כפתורים
        canvas.drawBitmap(bitmap, 0, 0, null);
        standButton.drawStand(canvas);
        hitButton.drawHit(canvas);
        doubleButton.drawDouble(canvas);
        splitButton.drawSplit(canvas);
        returnButton.drawReturn(canvas);   // ← צריך ליישם drawReturn ב-ReturnButton

        if (!gameActive) {
            // מצב התחלה – מראים רק את כפתור Start
            startButton.drawStart(canvas);   // ← תצטרך ליישם מתודה כזו בקלאס StartButton
        } else {
            // מצב משחק – מראים כפתורי פעולה + קלפים
            playerHand.drawAll(canvas);
            dealerHand.drawAll(canvas);

            // טקסט ערך (אופציונלי)
            Paint p = new Paint();
            p.setColor(Color.WHITE);
            p.setTextSize(48);
            p.setAntiAlias(true);

            canvas.drawText("שחקן: " + playerHand.getValue(),
                    playerHand.startX, playerHand.startY - 20, p);
            canvas.drawText("דילר: " + dealerHand.getValue(),
                    dealerHand.startX, dealerHand.startY - 20, p);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return super.onTouchEvent(event);
        }

        float x = event.getX();
        float y = event.getY();

        if (!gameActive) {
            // מצב התחלה – רק Start מגיב
            if (startButton.contains(x, y)) {
                startNewGame();
                return true;
            }
            return super.onTouchEvent(event);
        }

        // מצב משחק – כפתורים רגילים
        if (hitButton.contains(x, y)) {
            playerHand.addCard(deck.drawCard());
            if (playerHand.isBust()) {
                gameActive = false;
            }
            invalidate();
            return true;
        }

        if (standButton.contains(x, y)) {
            // תור דילר
            while (dealerHand.getValue() < 17) {
                dealerHand.addCard(deck.drawCard());
            }
            gameActive = false;
            invalidate();
            return true;
        }

        // ... כפתורים אחרים (double, split וכו')

        return super.onTouchEvent(event);
    }
}

