package com.company.libraryapp;

import android.app.ProgressDialog;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserPrebook extends AppCompatActivity {


    private TextInputLayout editCardNo1, editBid5;
    private FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    private ProgressDialog p;
    private boolean res1, res2;
    private User U = new User();
    private Book B1 = new Book();
    private int cardNo;

    private static final String TAG = "MyTag";


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_preebook);
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        Button prebookButton = (Button) findViewById(R.id.prebookButton);
        editBid5 = (TextInputLayout) findViewById(R.id.editBid5);
        p = new ProgressDialog(this);

        prebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preBook();
            }
        });
    }


    private boolean verifyBid() {
        String t = editBid5.getEditText().getText().toString().trim();
        if (t.isEmpty()) {
            editBid5.setErrorEnabled(true);
            editBid5.setError("Book Id Required");
            return true;
        } else {
            editBid5.setErrorEnabled(false);
            return false;
        }
    }


    private void getBook(int bookId, final AdminReturnBook.OnBookReceivedListener listener) {
        db.collection("Book").whereEqualTo("id", bookId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() == 1) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            B1 = doc.toObject(Book.class);
                        }
//                        Toast.makeText(UserPrebook.this, "Book found", Toast.LENGTH_SHORT).show();
                        // Call the method that processes the book
                        listener.onBookReceived(true);
                    } else {
                        p.cancel();
                        Toast.makeText(UserPrebook.this, "No Such Book !", Toast.LENGTH_SHORT).show();
                        listener.onBookReceived(false);
                    }
                } else {
                    p.cancel();
                    Toast.makeText(UserPrebook.this, "Try Again !", Toast.LENGTH_SHORT).show();
                    listener.onBookReceived(false);
                }
            }
        });
    }

    private void preBook() {

        Log.d("abc", "invoked");
        if (verifyBid()) {
            return;
        }

        final int bookId = Integer.parseInt(editBid5.getEditText().getText().toString().trim());

        p.setMessage("Please Wait !");
        p.show();
        db.document("User/" + firebaseAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    U = task.getResult().toObject(User.class);

                    Log.d(TAG, "Does user contain book?: " + U.getBook().contains(bookId));
                    if (!U.getBook().contains(bookId)) {

                        if (B1.getAvailable()>0) {
                            getBook(bookId, new AdminReturnBook.OnBookReceivedListener() {
                                @Override
                                public void onBookReceived(boolean result) {
                                    if (result) {


                                        cardNo = U.getCard();
                                        Log.d(TAG, "CardNo: " + cardNo);
                                        List<Integer> l = new ArrayList<Integer>(B1.getPrebook());
                                        Log.d(TAG, "L: " + l);

                                        if (!l.contains(cardNo)) {
                                            p.cancel();
                                            l.add(cardNo);
                                            Log.d(TAG, "Updated L: " + l);
                                            B1.setPrebook(l);
                                            Log.d(TAG, "Updated prebook: " + B1.getPrebook());

                                            DocumentReference bookRef = db.collection("Book").document(String.valueOf(bookId));
                                            Log.d(TAG, "Bookref: " + bookRef.getId());
                                            bookRef.update("prebook", l)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                            Toast.makeText(UserPrebook.this, "Pre-book successful. Wait for Admin to issue", Toast.LENGTH_LONG).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error updating document", e);
                                                        }
                                                    });

                                        } else {
                                            p.cancel();
                                            Toast.makeText(UserPrebook.this, "Book has already been pre-booked by you", Toast.LENGTH_LONG).show();

                                        }
                                    }
                                    else {
                                        p.cancel();
                                    }

                                }


//                                Toast.makeText(UserPrebook.this, Arrays.toString(new List[]{B1.getPrebook()}), Toast.LENGTH_SHORT).show();
//                    l = U.getFine();
//                    l.add(0);
//                    U.setFine(l);
//                    l = U.getRe();
//                    l.add(1);
//                    U.setRe(l);

                            });


                        }
                        else {p.cancel();
                            Toast.makeText(UserPrebook.this, "Pre-book limit has been reached for this book", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        p.cancel();
                        Toast.makeText(UserPrebook.this, "You already have this book", Toast.LENGTH_SHORT).show();

                }
                }


            }
        });
    }
}
