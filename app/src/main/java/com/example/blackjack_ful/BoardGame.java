package com.example.blackjack_ful;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class BoardGame extends View {
    private Hand playerHand;
    private Hand dealerHand;

    Bitmap bitmap, bitmapStand, bitmapHit, bitmapDouble, bitmapSplit, bitmapReturn, bitmapStart;
    private Context context;
    StandButton standButton;
    HitButton hitButton;
    DoubleButton doubleButton;
    ReturnButton returnButton;
    SplitButton splitButton;
    StartButton startButton;
    private Deck deck;
    private boolean gameActive = false;
    private boolean dealerTurn = false;

    private Bitmap background;

    public BoardGame(Context context) {
        super(context);
        this.context = context;
        deck = new Deck(context);

        bitmapStart = BitmapFactory.decodeResource(context.getResources(), R.drawable.startbutton);
        bitmapStart = Bitmap.createScaledBitmap(bitmapStart, 600, 400, true);

        startButton = new StartButton(200, 1400, bitmapStart);
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        bitmapStand = BitmapFactory.decodeResource(context.getResources(), R.drawable.standbutton);
        bitmapStand = Bitmap.createScaledBitmap(bitmapStand, 600, 400, true);
        standButton = new StandButton(425, 1800, bitmapStand);
        bitmapHit = BitmapFactory.decodeResource(getResources(), R.drawable.hitbutton);
        bitmapHit = Bitmap.createScaledBitmap(bitmapHit, 600, 400, true);
        hitButton = new HitButton(25, 1800, bitmapHit);
        bitmapDouble = BitmapFactory.decodeResource(getResources(), R.drawable.doublebutton);
        bitmapDouble = Bitmap.createScaledBitmap(bitmapDouble, 600, 400, true);
        doubleButton = new DoubleButton(25, 1900, bitmapDouble);
        bitmapSplit = BitmapFactory.decodeResource(getResources(), R.drawable.splitbutton);
        bitmapSplit = Bitmap.createScaledBitmap(bitmapSplit, 600, 400, true);
        splitButton = new SplitButton(425, 1900, bitmapSplit);
        bitmapReturn = BitmapFactory.decodeResource(getResources(), R.drawable.returnbutton);
        bitmapReturn = Bitmap.createScaledBitmap(bitmapReturn, 100, 100, true);
        returnButton = new ReturnButton(900, 50, bitmapReturn);

        // יצירת ידיים – מיקומים זמניים (יעודכנו ב-onSizeChanged)
        playerHand = new Hand(60, 0);
        dealerHand = new Hand(60, 0);

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

        dealerTurn = false;
        gameActive = true;

        // חלוקה ראשונית – 2 קלפים לכל אחד
        playerHand.addCard(deck.drawCard());
        playerHand.addCard(deck.drawCard());

        dealerHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());

        invalidate();
    }

    private void endRound() {
        gameActive = false;
        dealerTurn = true;        // חושף את כל הקלפים של הדילר
        invalidate();
        // השהיה של 1.5 שניות כדי שהשחקן יראה את יד הדילר
        postDelayed(this::showResultDialog, 1500);
    }

    private void showResultDialog() {
        int playerVal = playerHand.getValue();
        int dealerVal = dealerHand.getValue();

        String title;
        String message;

        boolean playerBlackjack = (playerVal == 21 && playerHand.getCards().size() == 2);
        boolean dealerBlackjack = (dealerVal == 21 && dealerHand.getCards().size() == 2);

        if (playerBlackjack && dealerBlackjack) {
            title = "תיקו";
            message = "שני הצדדים יצאו עם Blackjack!";
        } else if (playerBlackjack) {
            title = "ניצחת! 🎉";
            message = "Blackjack! כל הכבוד!";
        } else if (dealerBlackjack) {
            title = "הפסדת";
            message = "הדילר יצא עם Blackjack";}

            if (playerVal > 21) {
                title = "הפסדת";
                message = "היד שלך שרופה (Bust)";
            } else if (dealerVal > 21) {
                title = "ניצחת!";
                message = "הדילר שרף את היד";
            } else if (playerVal > dealerVal) {
                title = "ניצחת!";
                message = "היד שלך טובה יותר";
            } else if (dealerVal > playerVal) {
                title = "הפסדת";
                message = "הדילר ניצח";
            } else {
                title = "תיקו";
                message = "שתי הידיים שוות";
            }
            new AlertDialog.Builder(getContext())
                    .setTitle(title)
                    .setMessage(message + "\n\nרוצה לשחק סיבוב נוסף?")
                    .setPositiveButton("כן", (dialog, which) -> startNewGame())
                    .setNegativeButton("לא", (dialog, which) -> {
                        // חוזר למסך הבית
                        if (getContext() instanceof Activity) {
                            ((Activity) getContext()).finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }


        @Override
        protected void onDraw (@NonNull Canvas canvas){
            super.onDraw(canvas);

            // רקע + כפתורים
            canvas.drawBitmap(bitmap, 0, 0, null);
            standButton.drawStand(canvas);
            hitButton.drawHit(canvas);
            doubleButton.drawDouble(canvas);
            splitButton.drawSplit(canvas);
            returnButton.drawReturn(canvas);   // ← צריך ליישם drawReturn ב-ReturnButton

            if (!gameActive) {
                startButton.drawStart(canvas);
                return;
            }

            // ציור יד השחקן
            playerHand.drawAll(canvas);

            // ציור יד הדילר
            if (dealerTurn) {
                dealerHand.drawAll(canvas);                    // חושף את כל הקלפים
            } else {
                // מראה רק את הקלף הראשון של הדילר
                ArrayList<Card> dealerCards = dealerHand.getCards();
                if (!dealerCards.isEmpty()) {
                    dealerCards.get(0).drawCard(canvas);       // רק הקלף הראשון
                }
            }

            // טקסט ערכים
            Paint p = new Paint();
            p.setColor(Color.WHITE);
            p.setTextSize(50);
            p.setAntiAlias(true);
            p.setFakeBoldText(true);

            canvas.drawText("שחקן: " + playerHand.getValue(),
                    playerHand.startX, playerHand.startY - 30, p);

            if (dealerTurn) {
                canvas.drawText("דילר: " + dealerHand.getValue(),
                        dealerHand.startX, dealerHand.startY - 30, p);
            } else {
                canvas.drawText("דילר: ?", dealerHand.startX, dealerHand.startY - 30, p);
            }
        }

        @Override
        public boolean onTouchEvent (MotionEvent event){
            if (event.getAction() != MotionEvent.ACTION_DOWN) {
                return super.onTouchEvent(event);
            }

            float x = event.getX();
            float y = event.getY();

            if (!gameActive) {
                // מצב התחלה – רק Start מגיב
                if (startButton.contains(x, y)) {
                    startNewGame();
                }
                return true;

            }

            // מצב משחק – כפתורים רגילים
            if (hitButton.contains(x, y)) {
                playerHand.addCard(deck.drawCard());
                if (playerHand.isBust()) {
                    endRound();
                }
                invalidate();
                return true;
            }

            if (standButton.contains(x, y)) {
                dealerTurn = true;
                // הדילר משחק
                while (dealerHand.getValue() < 17) {
                    dealerHand.addCard(deck.drawCard());
                }
                endRound();
                return true;
            }

            if (returnButton.contains(x, y)) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
                return true;
            }

            // ... כפתורים אחרים (double, split וכו')

            return super.onTouchEvent(event);
        }
    }


