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
    private boolean isDoubled = false; // האם השחקן ביצע Double ליד הראשונה
    private boolean playingSecondHand = false; // האם אנחנו משחקים כרגע את היד השנייה
    private Hand dealerHand;

    Bitmap bitmap, bitmapStand, bitmapHit, bitmapDouble, bitmapSplit, bitmapReturn;
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

    public BoardGame(Context context) {
        super(context);
        this.context = context;
        deck = new Deck(context);

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        
        int btnWidth = 500;
        int btnHeight = 250;

        bitmapStand = BitmapFactory.decodeResource(context.getResources(), R.drawable.standbutton);
        bitmapStand = Bitmap.createScaledBitmap(bitmapStand, btnWidth, btnHeight, true);
        standButton = new StandButton(550, 1750, bitmapStand);

        bitmapHit = BitmapFactory.decodeResource(getResources(), R.drawable.hitbutton);
        bitmapHit = Bitmap.createScaledBitmap(bitmapHit, btnWidth, btnHeight, true);
        hitButton = new HitButton(20, 1750, bitmapHit);

        bitmapDouble = BitmapFactory.decodeResource(getResources(), R.drawable.doublebutton);
        bitmapDouble = Bitmap.createScaledBitmap(bitmapDouble, btnWidth, btnHeight, true);
        doubleButton = new DoubleButton(20, 2050, bitmapDouble);

        bitmapSplit = BitmapFactory.decodeResource(getResources(), R.drawable.splitbutton);
        bitmapSplit = Bitmap.createScaledBitmap(bitmapSplit, btnWidth, btnHeight, true);
        splitButton = new SplitButton(550, 2050, bitmapSplit);

        bitmapReturn = BitmapFactory.decodeResource(getResources(), R.drawable.returnbutton);
        bitmapReturn = Bitmap.createScaledBitmap(bitmapReturn, 100, 100, true);
        returnButton = new ReturnButton(950, 50, bitmapReturn);

        playerHand = new Hand(0, 0);
        dealerHand = new Hand(0, 0);
        initFirebaseChips();
        deck.createDeck();
        deck.shuffle();

        startNewGame();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateHandPositions();
    }

    private void updateHandPositions() {
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        if (isSplit && playerHand2 != null) {
            float splitY = h - 950;
            playerHand.setPosition(50, splitY);
            playerHand.setMaxWidth(w / 2f - 100);
            
            playerHand2.setPosition(w / 2f + 50, splitY);
            playerHand2.setMaxWidth(w / 2f - 100);
        } else {
            playerHand.setPosition(w / 2f - 300, h - 950);
            playerHand.setMaxWidth(w - 200);
        }
        dealerHand.setPosition(w / 2f - 300, 150);
        dealerHand.setMaxWidth(w - 200);
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
        playerHand2 = null;
        isSplit = false;
        playingSecondHand = false;
        dealerTurn = false;
        gameActive = true;
        isDoubled = false;

        playerHand.addCard(deck.drawCard());
        playerHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());

        updateHandPositions();
    }

    private void endRound() {
        gameActive = false;
        dealerTurn = true;
        invalidate();
        postDelayed(this::showResultDialog, 1500);
    }

    private void showResultDialog() {
        StringBuilder sb = new StringBuilder();
        if (isSplit) {
            sb.append("תוצאת יד 1:\n").append(getHandResult(playerHand, isDoubled)).append("\n\n");
            sb.append("תוצאת יד 2:\n").append(getHandResult(playerHand2, false));
        } else {
            sb.append(getHandResult(playerHand, isDoubled));
        }

        saveChipsToFirebase();

        new AlertDialog.Builder(getContext())
                .setTitle("סיום סיבוב")
                .setMessage(sb.toString() + "\n\nרוצה לשחק סיבוב נוסף?")
                .setPositiveButton("כן", (dialog, which) -> startNewGame())
                .setNegativeButton("לא", (dialog, which) -> {
                    if (getContext() instanceof Activity) {
                        ((Activity) getContext()).finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private String getHandResult(Hand hand, boolean wasDoubled) {
        int pVal = hand.getValue();
        int dVal = dealerHand.getValue();
        boolean pBJ = (pVal == 21 && hand.getCards().size() == 2);
        boolean dBJ = (dVal == 21 && dealerHand.getCards().size() == 2);

        if (pVal > 21) return "נשרפת!";
        if (dBJ && !pBJ) return "לדילר יש Blackjack!";
        if (pBJ && !dBJ) { currentChips += 25; return "Blackjack! זכית ב-25"; }
        if (dVal > 21 || pVal > dVal) {
            int win = wasDoubled ? 40 : 20;
            currentChips += win;
            return "ניצחת! זכית ב-" + win;
        }
        if (pVal == dVal) {
            int ret = wasDoubled ? 20 : 10;
            currentChips += ret;
            return "תיקו! קיבלת חזרה " + ret;
        }
        return "הפסדת בסיבוב הזה";
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
        if (isSplit && playerHand2 != null) playerHand2.drawAll(canvas);
        if (dealerTurn) dealerHand.drawAll(canvas);
        else if (!dealerHand.getCards().isEmpty()) dealerHand.getCards().get(0).drawCard(canvas);

        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setTextSize(50);
        p.setAntiAlias(true);
        p.setFakeBoldText(true);

        String p1Label = "שחקן 1: " + playerHand.getValue();
        if (isSplit && !playingSecondHand && !dealerTurn) p1Label += " <---";
        canvas.drawText(p1Label, playerHand.getStartX(), playerHand.getStartY() - 30, p);

        if (isSplit && playerHand2 != null) {
            String p2Label = "שחקן 2: " + playerHand2.getValue();
            if (playingSecondHand && !dealerTurn) p2Label += " <---";
            canvas.drawText(p2Label, playerHand2.getStartX(), playerHand2.getStartY() - 30, p);
        }

        if (dealerTurn) canvas.drawText("דילר: " + dealerHand.getValue(), dealerHand.getStartX(), dealerHand.getStartY() - 30, p);
        else canvas.drawText("דילר: ?", dealerHand.getStartX(), dealerHand.getStartY() - 30, p);

        Paint chipsPaint = new Paint();
        chipsPaint.setColor(Color.YELLOW);
        chipsPaint.setTextSize(55);
        chipsPaint.setAntiAlias(true);
        chipsPaint.setFakeBoldText(true);
        canvas.drawText("גטונים: " + currentChips, getWidth() - 380, 990, chipsPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) return super.onTouchEvent(event);
        float x = event.getX(), y = event.getY();

        if (doubleButton.contains(x, y)) {
            if (canDouble()) performDouble();
            else Toast.makeText(context, "Double מותר רק עם 2 קלפים ובסכום 9-11", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (splitButton.contains(x, y)) {
            if (canSplit()) performSplit();
            else Toast.makeText(context, "Split אפשרי רק עם 2 קלפים זהים", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (hitButton.contains(x, y)) {
            handleHit();
            return true;
        }
        if (standButton.contains(x, y)) {
            handleStand();
            return true;
        }
        if (returnButton.contains(x, y)) {
            saveChipsToFirebase();
            context.startActivity(new Intent(context, MainActivity.class));
            if (context instanceof Activity) ((Activity) context).finish();
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void handleHit() {
        if (!gameActive || dealerTurn) return;
        Hand activeHand = playingSecondHand ? playerHand2 : playerHand;
        activeHand.addCard(deck.drawCard());
        if (activeHand.isBust()) {
            if (isSplit && !playingSecondHand) {
                playingSecondHand = true;
                Toast.makeText(context, "יד 1 נשרפה! עוברים ליד 2", Toast.LENGTH_SHORT).show();
            } else startDealerTurn();
        }
        invalidate();
    }

    private void handleStand() {
        if (!gameActive || dealerTurn) return;
        if (isSplit && !playingSecondHand) {
            playingSecondHand = true;
            Toast.makeText(context, "עוברים ליד 2", Toast.LENGTH_SHORT).show();
        } else startDealerTurn();
        invalidate();
    }

    private void startDealerTurn() {
        dealerTurn = true;
        while (dealerHand.getValue() < 17) dealerHand.addCard(deck.drawCard());
        endRound();
    }

    private void initFirebaseChips() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        userRef = FirebaseDatabase.getInstance().getReference("details").child(user.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    myDetails = snapshot.getValue(MyDetailsInFb.class);
                    if (myDetails != null) currentChips = myDetails.getChips();
                }
                invalidate();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void saveChipsToFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = (myDetails != null) ? myDetails.getName() : "שחקן";
            FBsingelton.getInstance().setDetails(name, currentChips);
        }
    }

    private boolean canDouble() {
        return gameActive && !dealerTurn && !isSplit && playerHand.getCards().size() == 2 &&
                playerHand.getValue() >= 9 && playerHand.getValue() <= 11;
    }

    private void performDouble() {
        if (currentChips < 10) return;
        currentChips -= 10; isDoubled = true; saveChipsToFirebase();
        playerHand.addCard(deck.drawCard());
        if (playerHand.isBust()) endRound(); else startDealerTurn();
        invalidate();
    }

    private boolean canSplit() {
        if (!gameActive || dealerTurn || isSplit || playerHand.getCards().size() != 2) return false;
        int v1 = playerHand.getCards().get(0).getVal(), v2 = playerHand.getCards().get(1).getVal();
        return (v1 > 10 ? 10 : v1) == (v2 > 10 ? 10 : v2);
    }

    private void performSplit() {
        if (currentChips < 10) return;
        currentChips -= 10; saveChipsToFirebase();
        isSplit = true; playingSecondHand = false;
        playerHand2 = new Hand(0, 0);
        playerHand2.addCard(playerHand.getCards().remove(1));
        playerHand.addCard(deck.drawCard());
        playerHand2.addCard(deck.drawCard());
        updateHandPositions();
    }
}
