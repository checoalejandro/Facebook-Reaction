package com.hado.facebookemotion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnLike, btnHide;
    ReactionView reactionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initOptions();
    }

    private void initView() {
        btnLike = (Button) findViewById(R.id.btn_like);
        btnHide = (Button) findViewById(R.id.btnHide);
        reactionView = (ReactionView) findViewById(R.id.view_reaction);
        reactionView.setVisibility(View.INVISIBLE);

        btnHide.setOnClickListener(view -> reactionView.hide());
        btnLike.setOnClickListener(view -> reactionView.show());

        reactionView.setOnItemSelected((iconOption, index) -> Toast.makeText(this, "Option " + index, Toast.LENGTH_SHORT).show());
    }

    private void initOptions() {
        List<IconOption> iconOptions = new ArrayList<>();
        iconOptions.add(new IconOption(reactionView, "Like", R.drawable.like));
        iconOptions.add(new IconOption(reactionView, "Love", R.drawable.love));
        iconOptions.add(new IconOption(reactionView, "Haha", R.drawable.haha));
        iconOptions.add(new IconOption(reactionView, "Wow", R.drawable.wow));
        iconOptions.add(new IconOption(reactionView, "Cry", R.drawable.cry));
        iconOptions.add(new IconOption(reactionView, "Angry", R.drawable.angry));
        iconOptions.add(new IconOption(reactionView, "Angry 2", R.drawable.angry));
        //iconOptions.add(new IconOption(reactionView, "Angry 3", R.drawable.like));
        reactionView.setOptions(iconOptions);
    }
}
