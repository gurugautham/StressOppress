package com.solutions.stressoppress;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class LinkActivity extends AppCompatActivity {

    private String user, genre, emotion;
    static final String USER = "0",GENRE = "1",EMOTION = "2";
    private TextView link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        setContentView(R.layout.activity_link);

        link = (TextView) findViewById(R.id.linkText);

        user = intent.getStringExtra(CameraActivity.USER);
        genre = intent.getStringExtra(CameraActivity.GENRE);
        emotion = intent.getStringExtra(CameraActivity.EMOTION);

        // set link to some link related to genre
        String[] links = {"https://www.youtube.com/watch?v=cjeORm4LMDk&list=RDGMEMHDXYb1_DDSgDsobPsOFxpA",
                "https://www.youtube.com/watch?v=gdx7gN1UyX0&list=RDGMEMQ1dJ7wXfLlqCjwV0xfSNbA",
                "https://www.youtube.com/watch?v=o1C3mVUkAt8&list=RDQMxB1sq10nzuM",
                "https://www.youtube.com/watch?v=9E6b3swbnWg&list=RDQMqk8OvYGJVWM",
                "https://www.youtube.com/watch?v=OMOGaugKpzs&list=RDGMEMJQXQAmqrnmK1SEjY_rKBGA",
                "https://www.youtube.com/watch?v=3XvJFW0DHbU&list=RDQMud-fiV4iQ80",
                "https://www.youtube.com/watch?v=uCUpvTMis-Y&list=RDQMcchAnbSQesA",
                "https://www.youtube.com/watch?v=4zAThXFOy2c&list=PLjzeyhEA84sQKuXp-rpM1dFuL2aQM_a3S"};

        final String[] genres = {"Hip Hop","Pop","Country","Classical","Rock","Jazz","Romance","Blues"};

        for (int i=0;i<8;i++){
            if (genres[i].equals(genre)){
                //link.setText(links[i]);
                break;
            }
        }

    }


    public void toRatingScreen(View v){


        Intent intent = new Intent(LinkActivity.this, RatingActivity.class);
        intent.putExtra(EMOTION,emotion);
        intent.putExtra(GENRE,genre);
        intent.putExtra(USER,user);
        LinkActivity.this.startActivity(intent);

    }


}
