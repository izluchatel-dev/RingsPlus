package ru.ringsplus.app.firebase;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import androidx.annotation.NonNull;
import ru.ringsplus.app.R;

import static ru.ringsplus.app.firebase.FireBaseCalendar.FIREBASE_CALENDAR_PATH;

public class FireBaseClearOrders {

    private DatabaseReference mCalendarReference;

    public FireBaseClearOrders(Context context) {
        mCalendarReference = FirebaseDatabase.getInstance().getReference(FIREBASE_CALENDAR_PATH);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        String currentMonthYear = String.valueOf(month) + String.valueOf(year);

        month--;
        if (month <= 0) {
            year--;
            month = 12;
        }
        String previousFirstMonthYear = String.valueOf(month) + String.valueOf(year);

        month--;
        if (month <= 0) {
            year--;
            month = 12;
        }
        String previousSecondMonthYear = String.valueOf(month) + String.valueOf(year);

        mCalendarReference.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot :snapshot.getChildren()) {
                    String key = postSnapshot.getKey();

                    if (key != null) {
                        if ((!key.equals(currentMonthYear)) && (!key.equals(previousFirstMonthYear)) && (!key.equals(previousSecondMonthYear))) {
                            postSnapshot.getRef().removeValue();
                        }
                    }
                }

                Toast.makeText(context, context.getString(R.string.clear_successfull), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
