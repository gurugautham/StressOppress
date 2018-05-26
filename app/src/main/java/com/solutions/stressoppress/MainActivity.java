package com.solutions.stressoppress;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String user,genre,emotion;
    final static String USER = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);


        // database instantiation
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // manage/check login (frontend)
        // take picture from camera view

    }

    public void goToCamera(){
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        intent.putExtra(USER, user);
        MainActivity.this.startActivity(intent);
    }

    public void goToCreateUserScreen(View v){

        setContentView(R.layout.activity_createuser);

    }

    boolean badCreateUsername = false;

    public void createUser(View v){

        user = ((EditText)findViewById(R.id.createUsername)).getText().toString();
        String password = ((EditText)findViewById(R.id.createPassword)).getText().toString();

        final Context context = this;

        // read Firebase data
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user)){
                    Toast.makeText(context,"Username already exists",Toast.LENGTH_LONG);
                    badCreateUsername = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        mDatabase.addListenerForSingleValueEvent(listener);

        if (badCreateUsername) return;

        //mDatabase.child(user).child("Password").push();
        mDatabase.child(user).child("Password").setValue(password);

        // initialize genre preferences for emotions as 3 (middle rating)
        String[] genres = {"Hip Hop","Pop","Country","Classical","Rock","Jazz","Romance","Blues"};
        String[] emotions = {"Anger","Joy","Sorrow"};

        for (String genre:genres){
            mDatabase.child(user).child(genre).push();
            for (String emotion:emotions){
                mDatabase.child(user).child(genre).child(emotion).setValue(3);
            }
        }

        Toast.makeText(this,"Created user",Toast.LENGTH_LONG);
        setContentView(R.layout.activity_login);

    }

    boolean badUsername = false;
    boolean badPassword = false;

    public void checkLogin(View v){

        user = ((EditText)findViewById(R.id.username)).getText().toString();
        final String password = ((EditText)findViewById(R.id.password)).getText().toString();
        final Context context = this;

        // read Firebase data
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(user)){
                    Toast.makeText(context,"Username does not exist",Toast.LENGTH_LONG);
                    badUsername = true;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        mDatabase.addListenerForSingleValueEvent(listener);
        if (badUsername) {
            setContentView(R.layout.activity_login);
            return;
        }

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!((String)(dataSnapshot.child("Password").getValue())).equals(password)){
                    Toast.makeText(context,"Incorrect password",Toast.LENGTH_LONG);
                    badPassword = true;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        mDatabase.child(user).addListenerForSingleValueEvent(listener);
        if (badPassword){
            setContentView(R.layout.activity_login);
            return;
        }

        Toast.makeText(this,"Successfully logged in",Toast.LENGTH_LONG);
        goToCamera();


    }

    public void goToLogin(View v){

        setContentView(R.layout.activity_login);

    }


}
