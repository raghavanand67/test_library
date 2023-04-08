package com.company.libraryapp;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserSeeMyBooks extends AppCompatActivity {


    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private TextView ifNoBook1;
    private User U = new User();
    private Book B = new Book();
    private ProgressDialog progressDialog;
    private long daysBetween;
    int fine = 0;


    List<Integer> l = new ArrayList<Integer>();
    MyBook myBook=new MyBook();
    RecyclerView.Adapter adapter;
    public int i,j;

    private List<MyBook> myBooks=new ArrayList<>();

    private static final String TAG = "MyTag";



    RecyclerView recyclerView;
    //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_see_my_books);
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        ifNoBook1 = (TextView) findViewById(R.id.ifNoBook1);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);
        recyclerView=(RecyclerView)findViewById(R.id.recycle1) ;
        progressDialog.show();
        showBook();

        recyclerView.setLayoutManager(new LinearLayoutManager(getParent()));
        adapter=new MyBookAdapter(myBooks);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void setBook(int i)
    {
        j=0;

        db.document("Book/"+U.getBook().get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                B=task.getResult().toObject(Book.class);

                Date d = new Date();
                Date i = new Date();
                d=U.getDate().get(j).toDate();
                Log.d("msg", "Date-pre: "+d);
                Calendar c=Calendar.getInstance();
                c.setTime(d);
                c.add(Calendar.DAY_OF_MONTH,14);
                d=c.getTime();

                i=U.getDate().get(j).toDate();

                LocalDate currentDate = LocalDate.now();

                LocalDate date1 = i.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate date2 = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                if(currentDate.isAfter(date2)) {
                    daysBetween = ChronoUnit.DAYS.between(date2, currentDate);
                    Log.d("msg", "Working");
                    Log.d("msg", "Days: "+daysBetween);
                    fine = (int) (10*daysBetween);
                    Log.d("msg", "Fine: "+fine);

                }

                else{
                    daysBetween = 0;
                    fine= (int) (10*daysBetween);
                }


                Log.d("msg", "date1: "+currentDate);
                Log.d("msg", "date2: "+date2);



                myBooks.add(new MyBook(U.getBook().get(j),B.getTitle(),B.getType(),U.getDate().get(j).toDate(),d,fine));
                adapter.notifyDataSetChanged();
                j++;
            }

        });


    }


    private void showBook()
    {


        db.document("User/" + firebaseAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {


                    U = task.getResult().toObject(User.class);



                    String email = "sarthak.mirji@xaviers.edu.in";
                    Query query = db.collection("User").whereEqualTo("email", email);
                    Log.d(TAG, "Query: " + query.toString());

//                    Log.d(TAG, "Number of documents: " + task.getResult().size());

                        Toast.makeText(UserSeeMyBooks.this, "Damn damn damn", Toast.LENGTH_LONG).show();
                        Log.d("DocObject", "Msg: ");
//                        Log.d(TAG, "User document data: " + U.getData());
                        if (U == null) {
                            Log.w(TAG, "Failed to map document to User object");
                        } else {
                            Log.d(TAG, "User object: " + U.toString());
                        }

                    }
                    Toast.makeText(UserSeeMyBooks.this, firebaseAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();



                    Log.d("User", "Books: " + U.getBook().toString());
                    Log.d("User",  U.toString());
                    Log.d("User", "Name: "+U.getName());

                    if (!U.getBook().isEmpty()) {

                        l = U.getBook();
                        for (i = 0; i < l.size(); i++) {

                            setBook(i);
                        }

                        progressDialog.cancel();

                    }
                    else
                    {
                        progressDialog.cancel();
                        ifNoBook1.setText("YOU HAVE NO ISSUED BOOKS !");
                        ifNoBook1.setTextSize(18);
                    }
                }
            });


    }

}