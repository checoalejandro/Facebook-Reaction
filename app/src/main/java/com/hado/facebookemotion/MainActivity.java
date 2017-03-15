package com.hado.facebookemotion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnLike, btnHide;
    ReactionView reactionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btnLike = (Button) findViewById(R.id.btn_like);
        btnHide = (Button) findViewById(R.id.btnHide);
        reactionView = (ReactionView) findViewById(R.id.view_reaction);
        reactionView.setVisibility(View.INVISIBLE);

        btnHide.setOnClickListener(view -> reactionView.hide());
        btnLike.setOnClickListener(view -> reactionView.show());
    }
}
