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
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class BoardGame extends View {
    private Hand playerHand;
    private Hand playerHand2 = null;   // יד שנייה אחרי Split

    private boolean isSplit = false;   // האם השחקן פיצל
    private boolean playingSecondHand = false;
    private Hand dealerHand;

    Bitmap bitmap, bitmapStand, bitmapHit, bitmapDouble, bitmapSplit, bitmapReturn, bitmapStart;
    private Context context;
    StandButton standButton;
    HitButton hitButton;
    DoubleButton doubleButton;
    ReturnButton returnButton;
    SplitButton splitButton;
    private Deck deck;
    private boolean gameActive = false;
    private boolean dealerTurn = false;
    private DatabaseReference userRef;
    private MyDetailsInFb myDetails;
    private int currentChips = 1000;

    private Bitmap background;

    public BoardGame(Context context) {
        super(context);
        this.context = context;
        deck = new Deck(context);

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        bitmapStand = BitmapFactory.decodeResource(context.getResources(), R.drawable.standbutton);
        bitmapStand = Bitmap.createScaledBitmap(bitmapStand, 600, 400, true);
        standButton = new StandButton(475, 1800, bitmapStand);

        bitmapHit = BitmapFactory.decodeResource(getResources(), R.drawable.hitbutton);
        bitmapHit = Bitmap.createScaledBitmap(bitmapHit, 600, 400, true);
        hitButton = new HitButton(0, 1800, bitmapHit);

        bitmapDouble = BitmapFactory.decodeResource(getResources(), R.drawable.doublebutton);
        bitmapDouble = Bitmap.createScaledBitmap(bitmapDouble, 600, 400, true);
        doubleButton = new DoubleButton(0, 1900, bitmapDouble);

        bitmapSplit = BitmapFactory.decodeResource(getResources(), R.drawable.splitbutton);
        bitmapSplit = Bitmap.createScaledBitmap(bitmapSplit, 600, 400, true);
        splitButton = new SplitButton(475, 1900, bitmapSplit);

        bitmapReturn = BitmapFactory.decodeResource(getResources(), R.drawable.returnbutton);
        bitmapReturn = Bitmap.createScaledBitmap(bitmapReturn, 100, 100, true);
        returnButton = new ReturnButton(900, 50, bitmapReturn);

        // יצירת ידיים
        playerHand = new Hand(60, 0);
        dealerHand = new Hand(60, 0);
        initFirebaseChips();
        deck.createDeck();
        deck.shuffle();

        startNewGame();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (playerHand != null) {
            playerHand.setPosition(300, h - 1000);
        }
        if (dealerHand != null) {
            dealerHand.setPosition(300, 120);
        }
        invalidate();
    }

    private void startNewGame() {
        if (currentChips < 10) {
            Toast.makeText(context, "אין לך מספיק גטונים! (צריך 10)", Toast.LENGTH_SHORT).show();
            gameActive = false;
            return;
        }

        currentChips -= 10;
        saveChipsToFirebase();

        playerHand.clear();
        dealerHand.clear();
        dealerTurn = false;
        gameActive = true;

        playerHand.addCard(deck.drawCard());
        playerHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());

        invalidate();
    }

    private void endRound() {
        gameActive = false;
        dealerTurn = true;
        invalidate();
        postDelayed(this::showResultDialog, 1500);
    }

    private void showResultDialog() {
        String title = "";
        String message = "";

        boolean playerBlackjack = (playerHand.getValue() == 21 && playerHand.getCards().size() == 2);
        boolean dealerBlackjack = (dealerHand.getValue() == 21 && dealerHand.getCards().size() == 2);

        if (playerBlackjack) {
            title = "ניצחת! 🎉";
            message = "Blackjack! ×2.5\nקיבלת 25 גטונים";
            currentChips += 25;
        } else if (dealerBlackjack || playerHand.getValue() > 21 || (dealerHand.getValue() > playerHand.getValue() && dealerHand.getValue() < 22)) {
            title = "הפסדת";
            message = "הפסדת בסיבוב הזה";
        } else if (playerHand.getValue() > dealerHand.getValue() || dealerHand.isBust()) {
            title = "ניצחת! 🎉";
            message = "היד שלך טובה יותר";
            currentChips += 20;
        } else if (playerHand.getValue() == dealerHand.getValue()) {
            title = "תיקו";
            message = "שתי הידיים שוות";
            currentChips += 10;
        }

        saveChipsToFirebase(); // שמירה בסוף סיבוב

        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message + "\n\nרוצה לשחק סיבוב נוסף?")
                .setPositiveButton("כן", (dialog, which) -> startNewGame())
                .setNegativeButton("לא", (dialog, which) -> {
                    if (getContext() instanceof Activity) {
                        ((Activity) getContext()).finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, null);
        standButton.drawStand(canvas);
        hitButton.drawHit(canvas);
        doubleButton.drawDouble(canvas);
        splitButton.drawSplit(canvas);
        returnButton.drawReturn(canvas);

        playerHand.drawAll(canvas);
        if (isSplit && playerHand2 != null) {
            playerHand2.drawAll(canvas);
        }

        if (dealerTurn) {
            dealerHand.drawAll(canvas);
        } else if (!dealerHand.getCards().isEmpty()) {
            dealerHand.getCards().get(0).drawCard(canvas);
        }

        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setTextSize(50);
        p.setAntiAlias(true);
        p.setFakeBoldText(true);

        canvas.drawText("שחקן: " + playerHand.getValue(), playerHand.getStartX(), playerHand.getStartY() - 30, p);
        if (dealerTurn) {
            canvas.drawText("דילר: " + dealerHand.getValue(), dealerHand.getStartX(), dealerHand.getStartY() - 30, p);
        } else {
            canvas.drawText("דילר: ?", dealerHand.getStartX(), dealerHand.getStartY() - 30, p);
        }

        Paint chipsPaint = new Paint();
        chipsPaint.setColor(Color.YELLOW);
        chipsPaint.setTextSize(55);
        chipsPaint.setAntiAlias(true);
        chipsPaint.setFakeBoldText(true);
        canvas.drawText("גטונים: " + currentChips, getWidth() - 380, 990, chipsPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return super.onTouchEvent(event);
        }

        float x = event.getX();
        float y = event.getY();

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
            while (dealerHand.getValue() < 17) {
                dealerHand.addCard(deck.drawCard());
            }
            endRound();
            return true;
        }

        if (doubleButton.contains(x, y)) {
            if (canDouble()) {
                performDouble();
            } else {
                Toast.makeText(context, "Double מותר רק עם 2 קלפים ובסכום 9-11", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        if (splitButton.contains(x, y)) {
            if (canSplit()) {
                performSplit();
            } else {
                Toast.makeText(context, "Split אפשרי רק עם 2 קלפים זהים", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        if (returnButton.contains(x, y)) {
            saveChipsToFirebase();
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
            return true;
        }

        return super.onTouchEvent(event);
    }

    private void initFirebaseChips() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(context, "יש להתחבר כדי לשחק עם גטונים", Toast.LENGTH_LONG).show();
            return;
        }

        String userId = user.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("details").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    myDetails = snapshot.getValue(MyDetailsInFb.class);
                } else {
                    // יצירת אובייקט חדש אם לא קיים
                    String name = user.getDisplayName();
                    if (name == null || name.isEmpty()) name = "שחקן חדש";
                    myDetails = new MyDetailsInFb(name, currentChips);
                    saveChipsToFirebase();
                }

                if (myDetails != null) {
                    currentChips = myDetails.getChips();
                }
                invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "שגיאה בטעינת גטונים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveChipsToFirebase() {
        if (myDetails != null) {
            FBsingelton.getInstance().setDetails(myDetails.getName(), currentChips);
        } else {
            // אם myDetails עדיין null (למשל לפני שהטעינה הסתיימה), ננסה לשמור לפי פרטי המשתמש
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String name = user.getDisplayName();
                if (name == null || name.isEmpty()) name = "שחקן";
                FBsingelton.getInstance().setDetails(name, currentChips);
            }
        }
    }

    private boolean canDouble() {
        return gameActive && !dealerTurn && playerHand.getCards().size() == 2 &&
                playerHand.getValue() >= 9 && playerHand.getValue() <= 11;
    }

    private void performDouble() {
        if (currentChips < 10) {
            Toast.makeText(context, "אין לך מספיק גטונים להכפלה", Toast.LENGTH_SHORT).show();
            return;
        }
        currentChips -= 10;
        saveChipsToFirebase();
        playerHand.addCard(deck.drawCard());
        dealerTurn = true;
        while (dealerHand.getValue() < 17) {
            dealerHand.addCard(deck.drawCard());
        }
        endRound();
        invalidate();
    }

    private boolean canSplit() {
        if (!gameActive || dealerTurn || isSplit || playerHand.getCards().size() != 2) return false;
        int val1 = playerHand.getCards().get(0).getVal();
        int val2 = playerHand.getCards().get(1).getVal();
        if (val1 > 10) val1 = 10;
        if (val2 > 10) val2 = 10;
        return val1 == val2;
    }

    private void performSplit() {
        if (currentChips < 10) {
            Toast.makeText(context, "אין לך מספיק גטונים לפיצול", Toast.LENGTH_SHORT).show();
            return;
        }
        currentChips -= 10;
        saveChipsToFirebase();
        isSplit = true;
        playerHand2 = new Hand(400, 0);
        Card secondCard = playerHand.getCards().remove(1);
        playerHand2.addCard(secondCard);
        playerHand.addCard(deck.drawCard());
        playerHand2.addCard(deck.drawCard());
        playerHand.setPosition(60, getHeight() - 420);
        playerHand2.setPosition(400, getHeight() - 420);
        invalidate();
    }
}
