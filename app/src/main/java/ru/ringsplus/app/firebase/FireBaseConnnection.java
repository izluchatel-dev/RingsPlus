package ru.ringsplus.app.firebase;

import android.app.Activity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import ru.ringsplus.app.utils.ProgressDialogWait;

public class FireBaseConnnection {

    public static final String FIREBASE_CONNECTION_INFO = ".info/connected";

    public static void setConnectedChecker(Activity activity, Boolean reConnected) {
        ProgressDialogWait mProgressDialogWait = new ProgressDialogWait(activity);
        mProgressDialogWait.showDialog();

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(FIREBASE_CONNECTION_INFO);

        if (reConnected) {
            connectedRef.getDatabase().goOffline();
            connectedRef.getDatabase().goOnline();
        }

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    mProgressDialogWait.hideDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mProgressDialogWait.hideDialog();
            }
        });
    }
}
