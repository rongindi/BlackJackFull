package com.example.blackjack_ful;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;

/**
 * המחלקה המרכזית של המשחק. יורשת מ-View ומנהלת את כל הציור והלוגיקה.
 * כאן מתבצע ניהול התורות, חישוב התוצאות והאינטראקציה עם המשתמש.
 */
public class BoardGame extends View {
    // --- משתני מצב המשחק (Game State) ---
    private Hand playerHand;           // היד הראשית של השחקן
    private Hand playerHand2 = null;   // יד שנייה (נוצרת רק במצב Split)

    private boolean isSplit = false;           // האם השחקן בחר לפצל את היד
    private boolean isDoubled = false;         // האם בוצעה הכפלה (Double) ביד הראשונה
    private boolean playingSecondHand = false; // בשלב ה-Split: האם כרגע משחקים את היד השנייה
    private Hand dealerHand;                   // היד של הדילר
    private boolean isAnimating = false;       // האם מתבצעת אנימציה כרגע (חוסם אינטראקציה)
    private boolean gameStarted = false;       // האם המשחק כבר התחיל (למניעת התחלה כפולה)

    // --- אובייקטים לציור (Graphics & UI) ---
    Bitmap bitmap, bitmapStand, bitmapHit, bitmapDouble, bitmapSplit, bitmapReturn, bitmapCardBack;
    private Context context;
    // שימוש במחלקה האחודה GameButton לכל הכפתורים
    GameButton standButton;
    GameButton hitButton;
    GameButton doubleButton;
    GameButton returnButton;
    GameButton splitButton;

    // מיקום ה"קופה" (חפיסת הקלפים)
    private final float DECK_X_OFFSET = 280;
    private final float DECK_Y = 650; // מיקום הקופה

    // Handler לניהול תזמונים ואנימציות
    private final Handler handler = new Handler(Looper.getMainLooper());

    // --- לוגיקת קלפים ונתונים ---
    private Deck deck;                // חפיסת הקלפים (52 קלפים)
    private boolean gameActive = false; // האם כרגע מתנהל סיבוב פעיל
    private boolean dealerTurn = false; // האם הגיע תורו של הדילר לחשוף קלפים ולמשוך
    private DatabaseReference userRef; // קישור למיקום המשתמש ב-Firebase
    private MyDetailsInFb myDetails;   // אובייקט הנתונים (שם וגטונים) מהענן
    private int currentChips = 1000;   // כמות הגטונים בזיכרון המקומי

    public BoardGame(Context context) {
        super(context);
        this.context = context;
        deck = new Deck(context);

        // טעינת תמונות הרקע והכפתורים מתיקיית ה-Resources
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        
        int btnWidth = 540;
        int btnHeight = 320;

        // יצירת הכפתורים באמצעות המחלקה החדשה GameButton
        bitmapStand = BitmapFactory.decodeResource(context.getResources(), R.drawable.standbutton);
        bitmapStand = Bitmap.createScaledBitmap(bitmapStand, btnWidth, btnHeight, true);
        standButton = new GameButton(540, 1680, bitmapStand);

        bitmapHit = BitmapFactory.decodeResource(getResources(), R.drawable.hitbutton);
        bitmapHit = Bitmap.createScaledBitmap(bitmapHit, btnWidth, btnHeight, true);
        hitButton = new GameButton(0, 1680, bitmapHit);

        bitmapDouble = BitmapFactory.decodeResource(getResources(), R.drawable.doublebutton);
        bitmapDouble = Bitmap.createScaledBitmap(bitmapDouble, btnWidth, btnHeight, true);
        doubleButton = new GameButton(0, 2020, bitmapDouble);

        bitmapSplit = BitmapFactory.decodeResource(getResources(), R.drawable.splitbutton);
        bitmapSplit = Bitmap.createScaledBitmap(bitmapSplit, btnWidth, btnHeight, true);
        splitButton = new GameButton(540, 2020, bitmapSplit);

        bitmapReturn = BitmapFactory.decodeResource(getResources(), R.drawable.returnbutton);
        bitmapReturn = Bitmap.createScaledBitmap(bitmapReturn, 100, 100, true);
        returnButton = new GameButton(950, 50, bitmapReturn);

        // טעינת תמונת גב הקלף והקטנה מיידית
        bitmapCardBack = BitmapFactory.decodeResource(getResources(), R.drawable.card);
        if (bitmapCardBack != null) {
            bitmapCardBack = Bitmap.createScaledBitmap(bitmapCardBack, 220, 330, true);
        }

        // אתחול אובייקטי הידיים
        playerHand = new Hand(0, 0);
        dealerHand = new Hand(0, 0);
        
        initFirebaseChips(); // משיכת כמות הגטונים העדכנית מה-Firebase
        deck.createDeck();
        deck.shuffle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateHandPositions();
        if (!gameStarted && w > 0) {
            gameStarted = true;
            startNewGame();
        }
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
            Toast.makeText(context, "אין לך מספיק גטונים!", Toast.LENGTH_SHORT).show();
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

        updateHandPositions();
        dealInitialCardsSequentially(0);
    }

    private void dealInitialCardsSequentially(final int step) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (step == 0) {
                    addCardWithAnimation(playerHand, deck.drawCard(), new Runnable() {
                        @Override public void run() { dealInitialCardsSequentially(1); }
                    });
                } else if (step == 1) {
                    addCardWithAnimation(dealerHand, deck.drawCard(), new Runnable() {
                        @Override public void run() { dealInitialCardsSequentially(2); }
                    });
                } else if (step == 2) {
                    addCardWithAnimation(playerHand, deck.drawCard(), new Runnable() {
                        @Override public void run() { dealInitialCardsSequentially(3); }
                    });
                } else if (step == 3) {
                    addCardWithAnimation(dealerHand, deck.drawCard(), null);
                }
            }
        }, 100);
    }

    private void addCardWithAnimation(Hand hand, Card newCard, final Runnable onFinish) {
        isAnimating = true;
        final ArrayList<Card> cards = hand.getCards();
        final int countBefore = cards.size();
        final float[] oldX = new float[countBefore];
        final float[] oldY = new float[countBefore];
        for (int i = 0; i < countBefore; i++) {
            oldX[i] = cards.get(i).x;
            oldY[i] = cards.get(i).y;
        }

        hand.addCard(newCard);
        final int totalCount = cards.size();
        final float[] targetX = new float[totalCount];
        final float[] targetY = new float[totalCount];
        for (int i = 0; i < totalCount; i++) {
            targetX[i] = cards.get(i).x;
            targetY[i] = cards.get(i).y;
        }

        for (int i = 0; i < countBefore; i++) {
            cards.get(i).setPosition(oldX[i], oldY[i]);
        }
        final float startX = getWidth() - DECK_X_OFFSET;
        final float startY = DECK_Y;
        cards.get(totalCount - 1).setPosition(startX, startY);

        final long startTime = SystemClock.uptimeMillis();
        final long duration = 450;

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float frac = (float) elapsed / duration;
                if (frac >= 1f) frac = 1f;

                for (int i = 0; i < countBefore; i++) {
                    cards.get(i).setPosition(oldX[i] + (targetX[i] - oldX[i]) * frac, 
                                            oldY[i] + (targetY[i] - oldY[i]) * frac);
                }
                int last = totalCount - 1;
                cards.get(last).setPosition(startX + (targetX[last] - startX) * frac, 
                                          startY + (targetY[last] - startY) * frac);
                
                invalidate();
                if (frac < 1f) {
                    handler.postDelayed(this, 16); 
                } else {
                    isAnimating = false;
                    if (onFinish != null) onFinish.run();
                }
            }
        });
    }

    private void endRound() {
        gameActive = false;
        dealerTurn = true;
        invalidate();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showResultDialog();
            }
        }, 1500);
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
                .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startNewGame();
                    }
                })
                .setNegativeButton("לא", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getContext() instanceof Activity) ((Activity) getContext()).finish();
                    }
                })
                .setCancelable(false).show();
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
        
        // ציור הכפתורים באמצעות המתודה האחודה draw
        standButton.draw(canvas);
        hitButton.draw(canvas);
        doubleButton.draw(canvas);
        splitButton.draw(canvas);
        returnButton.draw(canvas);

        playerHand.drawAll(canvas);
        if (isSplit && playerHand2 != null) playerHand2.drawAll(canvas);
        if (bitmapCardBack != null) canvas.drawBitmap(bitmapCardBack, getWidth() - DECK_X_OFFSET, DECK_Y, null);

        if (dealerTurn) {
            dealerHand.drawAll(canvas);
        } else if (!dealerHand.getCards().isEmpty()) {
            dealerHand.getCards().get(0).drawCard(canvas);
            if (dealerHand.getCards().size() > 1 && bitmapCardBack != null) {
                Card hiddenCard = dealerHand.getCards().get(1);
                canvas.drawBitmap(bitmapCardBack, hiddenCard.x, hiddenCard.y, null);
            }
        }

        Paint p = new Paint();
        p.setColor(Color.WHITE); p.setTextSize(50); p.setAntiAlias(true); p.setFakeBoldText(true);

        if (isSplit && playerHand2 != null) {
            // יד 1
            String p1Label = "יד 1: " + playerHand.getValue();
            if (!playingSecondHand && gameActive && !dealerTurn) {
                p.setColor(Color.GREEN);
                p1Label += " <---";
            } else {
                p.setColor(Color.WHITE);
            }
            canvas.drawText(p1Label, playerHand.getStartX(), playerHand.getStartY() - 30, p);

            // יד 2
            String p2Label = "יד 2: " + playerHand2.getValue();
            if (playingSecondHand && gameActive && !dealerTurn) {
                p.setColor(Color.GREEN);
                p2Label += " <---";
            } else {
                p.setColor(Color.WHITE);
            }
            canvas.drawText(p2Label, playerHand2.getStartX(), playerHand2.getStartY() - 30, p);
            
            p.setColor(Color.WHITE); // Reset for dealer
        } else {
            canvas.drawText("שחקן: " + playerHand.getValue(), playerHand.getStartX(), playerHand.getStartY() - 30, p);
        }

        if (dealerTurn) canvas.drawText("דילר: " + dealerHand.getValue(), dealerHand.getStartX(), dealerHand.getStartY() - 30, p);
        else canvas.drawText("דילר: ?", dealerHand.getStartX(), dealerHand.getStartY() - 30, p);
        
        Paint cp = new Paint();
        cp.setColor(Color.YELLOW); cp.setTextSize(55); cp.setAntiAlias(true); cp.setFakeBoldText(true);
        canvas.drawText("גטונים: " + currentChips, getWidth() - 380, 990, cp);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN || isAnimating) return true;
        float x = event.getX(), y = event.getY();
        if (hitButton.contains(x, y)) handleHit();
        else if (standButton.contains(x, y)) handleStand();
        else if (doubleButton.contains(x, y)) performDouble();
        else if (splitButton.contains(x, y)) performSplit();
        else if (returnButton.contains(x, y)) {
            saveChipsToFirebase();
            context.startActivity(new Intent(context, MainActivity.class));
            if (context instanceof Activity) ((Activity) context).finish();
        }
        return true;
    }

    private void handleHit() {
        if (!gameActive || dealerTurn || isAnimating) return;
        final Hand activeHand = playingSecondHand ? playerHand2 : playerHand;
        addCardWithAnimation(activeHand, deck.drawCard(), new Runnable() {
            @Override
            public void run() {
                if (activeHand.isBust()) {
                    if (isSplit && !playingSecondHand) {
                        playingSecondHand = true;
                        Toast.makeText(context, "יד 1 נשרפה! עוברים ליד 2", Toast.LENGTH_SHORT).show();
                    } else startDealerTurn();
                }
                invalidate();
            }
        });
    }

    private void handleStand() {
        if (!gameActive || dealerTurn || isAnimating) return;
        if (isSplit && !playingSecondHand) {
            playingSecondHand = true;
            Toast.makeText(context, "עוברים ליד 2", Toast.LENGTH_SHORT).show();
        } else startDealerTurn();
        invalidate();
    }

    private void startDealerTurn() {
        dealerTurn = true;
        drawDealerCardRecursive();
    }

    private void drawDealerCardRecursive() {
        if (dealerHand.getValue() < 17) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    addCardWithAnimation(dealerHand, deck.drawCard(), new Runnable() {
                        @Override public void run() { drawDealerCardRecursive(); }
                    });
                }
            }, 300);
        } else {
            endRound();
        }
    }

    private void performDouble() {
        if (currentChips < 10 || isAnimating) return;
        currentChips -= 10; isDoubled = true; saveChipsToFirebase();
        addCardWithAnimation(playerHand, deck.drawCard(), new Runnable() {
            @Override
            public void run() {
                if (playerHand.isBust()) endRound(); else startDealerTurn();
            }
        });
    }

    private void performSplit() {
        if (currentChips < 10 || isAnimating) return;
        currentChips -= 10; saveChipsToFirebase();
        isSplit = true; playingSecondHand = false;
        playerHand2 = new Hand(0, 0);
        playerHand2.addCard(playerHand.getCards().remove(1));
        updateHandPositions();
        addCardWithAnimation(playerHand, deck.drawCard(), new Runnable() {
            @Override
            public void run() {
                addCardWithAnimation(playerHand2, deck.drawCard(), null);
            }
        });
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
}
