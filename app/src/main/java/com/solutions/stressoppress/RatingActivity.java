package com.solutions.stressoppress;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RatingBar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RatingActivity extends AppCompatActivity{

    private String user,genre,emotion;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        Intent intent = getIntent();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        user = intent.getStringExtra(LinkActivity.USER);
        genre = intent.getStringExtra(LinkActivity.GENRE);
        emotion = intent.getStringExtra(LinkActivity.EMOTION);

    }

    public void returnToStart(View v){

        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        float stars = ratingBar.getRating();

        mDatabase.child(user).child(genre).child(emotion).setValue(stars);

        setContentView(R.layout.activity_main);
        Intent intent = new Intent(RatingActivity.this, MainActivity.class);
        RatingActivity.this.startActivity(intent);

    }

}
