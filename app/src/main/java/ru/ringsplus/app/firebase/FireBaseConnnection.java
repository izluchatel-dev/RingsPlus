package ru.ringsplus.app.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

public class FireBaseConnnection {

    public static final String FIREBASE_CONNECTION_INFO = ".info/connected";

    public static void setConnectedChecker(WaitProgressBarInterface waitProgressBarInterface) {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(FIREBASE_CONNECTION_INFO);
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    waitProgressBarInterface.onShowProgressBar(false);
                } else {
                    waitProgressBarInterface.onShowProgressBar(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
