package com.company.libraryapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AdminSeePrebook extends AppCompatActivity {


    private TextInputLayout editCardNo1, editBid3;
    private FirebaseFirestore db;
    private ProgressDialog p;
    private boolean res1, res2;
    private User U = new User();
    private Book B1 = new Book();

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_seeprebook);
        FirebaseApp.initializeApp(this);

        Button issueButton = (Button) findViewById(R.id.issueButton);
        editBid3 = (TextInputLayout) findViewById(R.id.editBid3);
        editCardNo1 = (TextInputLayout) findViewById(R.id.editCardNo1);
        db = FirebaseFirestore.getInstance();
        p = new ProgressDialog(this);

        issueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPreBook();
            }
        });
    }


    private boolean verifyCard() {
        String t = editCardNo1.getEditText().getText().toString().trim();
        if (t.isEmpty()) {
            editCardNo1.setErrorEnabled(true);
            editCardNo1.setError("Card No. Required");
            return true;
        } else {
            editCardNo1.setErrorEnabled(false);
            return false;
        }
    }


    private boolean verifyBid() {
        String t = editBid3.getEditText().getText().toString().trim();
        if (t.isEmpty()) {
            editBid3.setErrorEnabled(true);
            editBid3.setError("Book Id Required");
            return true;
        } else {
            editBid3.setErrorEnabled(false);
            return false;
        }
    }


    private void getUser(int cardNo, final AdminReturnBook.OnUserReceivedListener listener) {
        db.collection("User").whereEqualTo("card", cardNo).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    if (task.getResult().size() == 1) {
                        for (QueryDocumentSnapshot doc : task.getResult())
                            U = doc.toObject(User.class);

//                        Toast.makeText(AdminIssueBook.this, "User found", Toast.LENGTH_LONG).show();
                        listener.onUserReceived(true);
                    } else {
                        p.cancel();
                        Toast.makeText(AdminSeePrebook.this, "No Such User !", Toast.LENGTH_SHORT).show();
                        listener.onUserReceived(false);
                    }
                } else {
                    p.cancel();
                    Toast.makeText(AdminSeePrebook.this, "Try Again !", Toast.LENGTH_SHORT).show();
                    listener.onUserReceived(false);
                }
            }
        });
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
//                        Toast.makeText(AdminIssueBook.this, "Book found", Toast.LENGTH_SHORT).show();
                        // Call the method that processes the book
                        listener.onBookReceived(true);
                    } else {
                        p.cancel();
                        Toast.makeText(AdminSeePrebook.this, "No Such Book !", Toast.LENGTH_SHORT).show();
                        listener.onBookReceived(false);
                    }
                } else {
                    p.cancel();
                    Toast.makeText(AdminSeePrebook.this, "Try Again !", Toast.LENGTH_SHORT).show();
                    listener.onBookReceived(false);
                }
            }
        });
    }

    private void checkPreBook() {
        final int cardNo = Integer.parseInt(editCardNo1.getEditText().getText().toString().trim());
        final int bookId = Integer.parseInt(editBid3.getEditText().getText().toString().trim());
        if (verifyBid() | verifyCard()) {
            return;
        }
        p.setMessage("Please Wait !");
        p.show();

        getUser(cardNo, new AdminReturnBook.OnUserReceivedListener() {
            @Override
            public void onUserReceived(boolean result) {
                if (result) {
                    getBook(bookId, new AdminReturnBook.OnBookReceivedListener() {
                        @Override
                        public void onBookReceived(boolean result) {
                            if (result) {
                                if (B1.getPrebook().contains(cardNo)){
                                    if (B1.getAvailable() > 0){
                                        p.cancel();
                                        Toast.makeText(AdminSeePrebook.this, "The user has pre booked the book and can be issued!", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(getApplicationContext(),AdminIssueBook.class));


                                    }
                                    else{
                                        p.cancel();
                                        Toast.makeText(AdminSeePrebook.this, "The user has pre booked the book but cannot be issued!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    p.cancel();
                                    Toast.makeText(AdminSeePrebook.this, "User has not pre booked the book!", Toast.LENGTH_SHORT).show();
                                }
                            }



                        }

                    });
                }
            }
        });
    }


    interface OnUserReceivedListener {
        void onUserReceived(boolean result);
    }

    interface OnBookReceivedListener {
        void onBookReceived(boolean result);
    }
}

