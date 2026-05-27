package com.example.blackjack_ful;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * מתאם (Adapter) עבור ה-RecyclerView שמציג את טבלת השיאים.
 * תפקידו לקחת את רשימת האובייקטים (MyDetailsInFb) ולחבר אותם לתצוגה הויזואלית (custom_layout).
 */
public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.UserViewHolder> {

    private Context context;
    private List<MyDetailsInFb> recordsList; // רשימת הנתונים להצגה

    public RecordAdapter(Context context, List<MyDetailsInFb> recordsList) {
        this.context = context;
        this.recordsList = recordsList;
    }

    /**
     * יצירת ה-ViewHolder: מנפח (Inflate) את ה-XML של שורה בודדת ברשימה.
     */
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        // טעינת הקובץ custom_layout.xml שמגדיר איך נראית שורה אחת בטבלה
        View view = inflater.inflate(R.layout.custom_layout, null);
        return new UserViewHolder(view);
    }

    /**
     * חיבור הנתונים לתצוגה: נקרא עבור כל שורה שמוצגת על המסך.
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        // שליפת הנתונים של השחקן במיקום הנוכחי ברשימה
        MyDetailsInFb record = recordsList.get(position);

        // הצבת השם והניקוד (גטונים) בתוך ה-TextViews המתאימים
        holder.tvName.setText(record.getName());
        holder.tvRecord.setText(""+record.getChips());
    }

    /**
     * מחזירה את מספר הפריטים הכולל ברשימה.
     */
    @Override
    public int getItemCount() {
        return recordsList.size();
    }

    /**
     * מחלקה פנימית המחזיקה את הרכיבים של שורה בודדת כדי לחסוך קריאות ל-findViewById.
     */
    public class UserViewHolder extends RecyclerView.ViewHolder{
        TextView tvName, tvRecord;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            // קישור רכיבי הטקסט מה-XML של השורה (custom_layout)
            tvName = itemView.findViewById(R.id.tvName);
            tvRecord = itemView.findViewById(R.id.tvScore);
        }
    }
}
