package com.sagi_apps.a5straight;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    private RecyclerView horizontal_recycler_view;
    private HorizontalAdapter horizontalAdapter;
    private List<GameToChoose> gamesList;

    private Button btnCreate, btnJoin;
    private boolean isCreateGame;
    private EditText edtName;
    private TextView txtHighestScore, txtPlayerRank;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtName = findViewById(R.id.edtName);
        loadNameIfExist();

        btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCreateGame = true;
                showGameActivity(isCreateGame, "");
            }
        });
        btnJoin = findViewById(R.id.btnJoin);
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCreateGame = false;
                showGameActivity(isCreateGame, "");
            }
        });

        horizontal_recycler_view = (RecyclerView) findViewById(R.id.horizontal_recycler_view);
        horizontal_recycler_view.setHasFixedSize(true);

        gamesList = fill_with_data();


        horizontalAdapter = new HorizontalAdapter(gamesList, getApplication());

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        horizontal_recycler_view.setLayoutManager(horizontalLayoutManager);
        horizontal_recycler_view.setAdapter(horizontalAdapter);

    }

    private List<GameToChoose> fill_with_data() {
        gamesList = new ArrayList<>();

        gamesList.add(new GameToChoose("sagi (3)"));
        gamesList.add(new GameToChoose("שירלי (7)"));
        gamesList.add(new GameToChoose("sagi (3)"));
        gamesList.add(new GameToChoose("sagi (3)"));
        gamesList.add(new GameToChoose("sagi (3)"));


        return gamesList;
    }

    private int rank;

    private void getPlayerRank() {
        myRef.child(Constants.NAME_TABLE_PLAYERS).orderByChild("idKeyPlayers").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Player player = dataSnapshot.getValue(Player.class);
                if (player.getIdKeyPlayers().toString().equals(Utils.getIdKeyPlayer(MainActivity.this))) {
                    rank = player.getScore();
                    txtPlayerRank.setText("rank: " + rank);
                    return;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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

    private void createNewPlayer(String name) {
        String idKeyPlayer = myRef.child(Constants.NAME_TABLE_PLAYERS).push().getKey();
        Player player = new Player(name, 0, idKeyPlayer);
        myRef.child(Constants.NAME_TABLE_PLAYERS).child(idKeyPlayer).setValue(player);

        Utils.saveIdPlayer(this, idKeyPlayer);
    }

    private void showGameActivity(boolean isCreateGame, String idKeyBoard) {

        String name = edtName.getText().toString();
        Intent intent;
        if (name.equals("")) {
            Toast.makeText(MainActivity.this, "must enter name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Utils.getIdKeyPlayer(this).equals(""))
            createNewPlayer(name);
        else
            getPlayerFromServer();


        if (isCreateGame) {
            intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("rank", rank);
            intent.putExtra("keyBoard", idKeyBoard);

        } else {
            intent = new Intent(MainActivity.this, GameListActivity.class);
        }
        Utils.saveName(name, MainActivity.this);
        intent.putExtra("name", name);
        intent.putExtra("isCreateGame", isCreateGame);
        startActivity(intent);
        finish();

    }

    private void getPlayerFromServer() {
        Query query = myRef.child(Constants.NAME_TABLE_PLAYERS).child(Utils.getIdKeyPlayer(this));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Player player = dataSnapshot.getValue(Player.class);
                updateNewNameToFirebase(player);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void updateNewNameToFirebase(Player player) {
        player.setName(edtName.getText().toString());
        myRef.child(Constants.NAME_TABLE_PLAYERS).child(Utils.getIdKeyPlayer(this)).setValue(player);
    }

    private void loadNameIfExist() {
        String name = Utils.getName(MainActivity.this);
        edtName.setText(name);
    }

    private int higestscor = 0;

    private void getHighestScore() {

        myRef.child(Constants.NAME_TABLE_PLAYERS).orderByChild("score").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Player player = dataSnapshot.getValue(Player.class);
                if (higestscor < player.getScore())
                    higestscor = player.getScore();
                updateText();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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

    private void updateText() {
        txtHighestScore.setText("the highest rank is " + higestscor);
    }

}

