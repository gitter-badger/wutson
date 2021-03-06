package com.ataulm.wutson.showdetails;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ataulm.wutson.*;
import com.bumptech.glide.Glide;

public class CharacterView extends RelativeLayout {

    private ImageView actorImageView;
    private TextView characterTextView;
    private TextView actorTextView;

    public CharacterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        View.inflate(getContext(), R.layout.merge_character, this);
        actorImageView = (ImageView) findViewById(R.id.character_image_profile);
        characterTextView = (TextView) findViewById(R.id.character_text_name);
        actorTextView = (TextView) findViewById(R.id.character_text_actor);
    }

    public void display(com.ataulm.wutson.shows.Character character) {
        Glide.with(getContext()).load(character.getActor().getProfileUri().toString()).into(actorImageView);
        characterTextView.setText(character.getName());
        actorTextView.setText(character.getActor().getName());
    }

}
