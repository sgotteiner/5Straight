package com.sagi_apps.a5straight;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class GameListActivity extends AppCompatActivity {

    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    String nameJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        Intent intent=getIntent();
        nameJoin=intent.getStringExtra("name");

        getAllOpenGame();
    }

    private void getAllOpenGame() {
        Query query = myRef.child(Constants.NAME_TABLE_BOARDS).orderByChild("idKey");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Board board = dataSnapshot.getValue(Board.class);
                if (!board.getIdKey().equals("")) {
                    Intent intent = new Intent(GameListActivity.this, GameActivity.class);
                    //intent.putExtra("name", name);
                    intent.putExtra("isCreateGame", false);
                    intent.putExtra("keyBoard", board.getIdKey());
                    intent.putExtra("nameJoin", nameJoin);
                    startActivity(intent);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //Board board = dataSnapshot.getValue(Board.class);
                //gameBusyRemoveFromList(board);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
