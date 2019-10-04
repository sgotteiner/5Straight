package com.sagi_apps.a5straight;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.shapes.Shape;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    static final int WHITE = R.drawable.white_background_with_dot;

    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    Board board = null;
    boolean isCreateGame;
    private String nameAdmin, nameJoin, idKeyBoard;
    private Player myPlayer;
    private ValueEventListener eventListener;

    private Button btnBiggerButtons, btnSmallerButtons;
    private TextView textViewInfo;
    private TableLayout tableLayout;
    private TableRow tableRow;
    private int[][] arrImages = new int[Constants.LENGTH_OF_LINE + 1][Constants.LENGTH_OF_LINE + 1];
    private boolean isFinishGame = false;
    private int indexI, indexJ;
    private int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        textViewInfo = findViewById(R.id.textViewInfo);

        loadInfoFromLastActivity();
        getPlayerFromServer();

        if (isCreateGame)
            createNewBoardInFirbase();
        else
            joinGame();

        setArrImages();

        tableLayout = findViewById(R.id.tableLayout);

        for (indexI = 0; indexI < Constants.LENGTH_OF_LINE; indexI++) {
            // Creation row
            tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            //tableRow.setLayoutParams(new TableLayout.LayoutParams(size*100, size*100));

            for (indexJ = 0; indexJ < Constants.LENGTH_OF_LINE; indexJ++) {

                // Creation  imageView
                final ImageButton imageView = new ImageButton(this);

                String s = (indexI + Constants.LENGTH_OF_LINE) + "" + indexJ;
                imageView.setId(Integer.valueOf(s));
                imageView.setLayoutParams(new TableRow.LayoutParams(size, size));
                //imageView.setImageDrawable(getResources().getDrawable(R.drawable.white_shape));
                imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.white_shape));
                //imageView.setPadding(50, 50, 50, 50);
//                imageView.setBackgroundColor(0xfff);//TODO nice frame
                imageView.setOnClickListener(GameActivity.this);

                tableRow.addView(imageView);
            }
            tableLayout.addView(tableRow);
        }


        //TODO progressbar instead of buttons
        btnBiggerButtons = findViewById(R.id.btnBiggerButtons);
        btnBiggerButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (size <= 190)
                    size += 20;
                setImagesSize();
            }
        });
        btnSmallerButtons = findViewById(R.id.btnSmallerButtons);
        btnSmallerButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (size >= 110)
                    size -= 20;
                setImagesSize();
            }
        });
    }

    private void setImagesSize() {
        ImageView imageView;
        for (indexI = 0; indexI < Constants.LENGTH_OF_LINE; indexI++) {
            for (indexJ = 0; indexJ < Constants.LENGTH_OF_LINE; indexJ++) {
                String s = (indexI + Constants.LENGTH_OF_LINE) + "" + indexJ;
                int id = Integer.valueOf(s);
                imageView = findViewById(id);
                imageView.setLayoutParams(new TableRow.LayoutParams(size, size));
//                tableRow.setLayoutParams(new TableLayout.LayoutParams(size * 100, size * 100));
            }
        }
    }

    private void setArrImages() {
        size = 150;
        for (indexI = 0; indexI < Constants.LENGTH_OF_LINE; indexI++) {
            for (indexJ = 0; indexJ < Constants.LENGTH_OF_LINE; indexJ++) {
                arrImages[indexI][indexJ] = WHITE;
            }
        }
    }

    @Override
    public void onClick(View view) {
        for (indexI = 0; indexI < Constants.LENGTH_OF_LINE; indexI++) {
            for (indexJ = 0; indexJ < Constants.LENGTH_OF_LINE; indexJ++) {
                String s = (indexI + Constants.LENGTH_OF_LINE) + "" + indexJ;
                if (view.getId() == Integer.parseInt(s)) {
                    if (arrImages[indexI][indexJ] != WHITE) {
                        Toast.makeText(this, "you can't press the same button twice", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if ((board.isTurnAdmin() && !isCreateGame) || (!board.isTurnAdmin() && isCreateGame)) {
                        Toast.makeText(this, "it is not your turn", Toast.LENGTH_SHORT).show();
                    } else {
                        if (board.isTurnAdmin()) {
                            //view.setBackgroundDrawable(getResources().getDrawable(Constants.X));
                            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.x_shape));
                            //view.setBackgroundResource(Constants.X);
                            arrImages[indexI][indexJ] = Constants.X;
                            myRef.child(Constants.NAME_TABLE_BOARDS).child(board.getIdKey()).setValue(board);
                        }
                        if (!board.isTurnAdmin()) {
                            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.o_shape));
                            arrImages[indexI][indexJ] = Constants.O;
                            myRef.child(Constants.NAME_TABLE_BOARDS).child(board.getIdKey()).setValue(board);
                        }
                        checkWinner(indexI, indexJ);
                        putPressInDatabase(view.getId());
                    }
                }
            }
        }
    }//TODO when pressing change the color of the pressed button

    private void putPressInDatabase(int id) {

        if(!isFinishGame) {
            board.setTurnAdmin(!board.isTurnAdmin());
        }
        board.setPress(id);
        myRef.child(Constants.NAME_TABLE_BOARDS).child(board.getIdKey()).setValue(board);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private boolean checkWinRowRight(int i, int j) {
        if (i < 4)
            return false;
        else for (int m = 0; m < 5; m++) {
            if (arrImages[i - m][j] != arrImages[i][j])
                return false;
        }
        return true;

    }

    private boolean checkWinRowLeft(int i, int j) {
        if (Constants.LENGTH_OF_LINE - i < 4)
            return false;
        else for (int m = 0; m < 5; m++) {
            if (arrImages[i + m][j] != arrImages[i][j])
                return false;
        }
        return true;
    }

    private boolean checkWinColomnUp(int i, int j) {
        if (Constants.LENGTH_OF_LINE - j < 4)
            return false;
        else for (int m = 0; m < 5; m++) {
            if (arrImages[i][j + m] != arrImages[i][j])
                return false;
        }
        return true;
    }

    private boolean checkWinColomnDown(int i, int j) {
        if (j < 4)
            return false;
        else for (int m = 0; m < 5; m++) {
            if (arrImages[i][j - m] != arrImages[i][j])
                return false;
        }
        return true;
    }

    private boolean checkWinDiagonalRightDown(int i, int j) {
        if (i < 4 || Constants.LENGTH_OF_LINE - j < 4)
            return false;
        else for (int m = 0; m < 5; m++) {
            if (arrImages[i - m][j + m] != arrImages[i][j])
                return false;
        }
        return true;
    }

    private boolean checkWinDiagonalRightUp(int i, int j) {
        if (Constants.LENGTH_OF_LINE - i < 4 || Constants.LENGTH_OF_LINE - j < 4)
            return false;
        else for (int m = 0; m < 5; m++) {
            if (arrImages[i + m][j + m] != arrImages[i][j])
                return false;
        }
        return true;
    }

    private boolean checkWinDiagonalLeftDown(int i, int j) {
        if (i < 4 || j < 4)
            return false;
        else for (int m = 0; m < 5; m++) {
            if (arrImages[i - m][j - m] != arrImages[i][j])
                return false;
        }
        return true;
    }

    private boolean checkWinDiagonalLeftUp(int i, int j) {
        if (Constants.LENGTH_OF_LINE - i < 4 || j < 4)
            return false;
        else for (int m = 0; m < 5; m++) {
            if (arrImages[i + m][j - m] != arrImages[i][j])
                return false;
        }
        return true;
    }

    private void loadInfoFromLastActivity() {
        Intent intent = getIntent();
        nameAdmin = intent.getStringExtra("nameAdmin");
        idKeyBoard = intent.getStringExtra("keyBoard");
        isCreateGame = intent.getBooleanExtra("isCreateGame", false);
    }


    private void joinGame() {

        Query query = myRef.child(Constants.NAME_TABLE_BOARDS).child(idKeyBoard);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                board = dataSnapshot.getValue(Board.class);
                updateMyNameToFirebase();
                startListnerToBoard();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Override
    protected void onStop() {
        if (board != null) {
            board.setActive(false);
            board.setNameJoin("Not connected");
            myRef.child(Constants.NAME_TABLE_BOARDS).child(idKeyBoard).setValue(board);
        }
        super.onStop();

        finish();
    }

    private void getPlayerFromServer() {
        Query query = myRef.child(Constants.NAME_TABLE_PLAYERS).child(Utils.getIdKeyPlayer(this));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myPlayer = dataSnapshot.getValue(Player.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void updateMyNameToFirebase() {
        board.setNameJoin(nameJoin);
        myRef.child(Constants.NAME_TABLE_BOARDS).child(idKeyBoard).setValue(board);
    }

    private void startListnerToBoard() {

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                board = dataSnapshot.getValue(Board.class);

                if (!board.isActive()) {

                    Toast.makeText(GameActivity.this, "other player is not connected\nyou won", Toast.LENGTH_SHORT).show();
                    myRef.child(Constants.NAME_TABLE_BOARDS).child(idKeyBoard).removeEventListener(eventListener);


                    removeThisGame(2000);
                    return;
                }
                String nameJoin = "?", nameAdmin = "?";

//                if (!board.getNameAdmin().equals("?"))
//                    nameAdmin = board.getNameAdmin().substring(0, 1).toUpperCase() + board.getNameAdmin().substring(1).toLowerCase();
//                if (!board.getNameJoin().equals("?"))
                //                  nameJoin = board.getNameJoin().substring(0, 1).toUpperCase() + board.getNameJoin().substring(1).toLowerCase();


                textViewInfo.setText(nameAdmin + " vs " + nameJoin);

                if(!isFinishGame) {
                    ImageView imageView = findViewById(board.getPress());
                    if (imageView != null) {
                        if (isCreateGame && board.isTurnAdmin()) {
                            imageView.setBackgroundDrawable(getResources().getDrawable(Constants.O));
                        }
                        if (!isCreateGame && !board.isTurnAdmin()) {
                            imageView.setBackgroundDrawable(getResources().getDrawable(Constants.X));
                        }
                        if (board.isFinishGame())
                            win();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        myRef.child(Constants.NAME_TABLE_BOARDS).child(idKeyBoard).addValueEventListener(eventListener);
    }

    private void win() {
        if (board.isTurnAdmin()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.trophy);
            builder.setTitle("X won!!!");
            builder.setPositiveButton("rematch", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent it = new Intent(GameActivity.this, GameActivity.class);
                    startActivity(it);
                    finish();
                }
            });
            builder.create().show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.trophy);
            builder.setTitle("O won!!!");
            builder.setPositiveButton("rematch", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent it = new Intent(GameActivity.this, GameActivity.class);
                    startActivity(it);
                    finish();
                }
            });
            builder.create().show();
        }
    }

    private void checkWinner(int indexI, int indexJ) {

        if (checkWinRowLeft(indexI, indexJ) ||
                checkWinRowRight(indexI, indexJ)
                || checkWinColomnUp(indexI, indexJ) ||
                checkWinColomnDown(indexI, indexJ)
                || checkWinDiagonalLeftDown(indexI, indexJ) ||
                checkWinDiagonalLeftUp(indexI, indexJ) ||
                checkWinDiagonalRightDown(indexI, indexJ) ||
                checkWinDiagonalRightUp(indexI, indexJ)) {

            board.setFinishGame(true);
            myRef.child(Constants.NAME_TABLE_BOARDS).child(board.getIdKey()).setValue(board);

            isFinishGame=true;

            win();
        }
    }


    private void createNewBoardInFirbase() {

        String idKeyBoard = myRef.child(Constants.NAME_TABLE_BOARDS).push().getKey();
        board = new Board();
        board.setIdKey(idKeyBoard);
        this.idKeyBoard = idKeyBoard;
        board.setNameAdmin(nameAdmin);
        board.setNameJoin("?");
        board.setTurnAdmin(true);
        board.setActive(true);

        myRef.child(Constants.NAME_TABLE_BOARDS).child(idKeyBoard).setValue(board);

        startListnerToBoard();

    }

//    private void loadAllImages() {

//        for (int index = 0; index < arrImageButtons.length; index++) {
//            arrImageButtons[index].setImageBitmap(BitmapFactory.decodeResource(getResources(), board.getAllImagesStateBtns().get(index)));
//        }
//
//    }
//
//    private void writeTurn(){
//        if(board.isTurnAdmin())
//            textViewInfo.setText(board.getNameAdmin()+"'s turn");
//        else textViewInfo.setText(board.getNameAdmin()+"'s turn");
//    }

//    private void checkWinner() {
//        switch (board.checkWhoWin()) {
//            case Constants.TIE_KEY:
//                myRef.child(Constants.NAME_TABLE_BOARDS).child(idKeyBoard).removeEventListener(eventListener);
//                if (isCreateGame)
//                    removeThisGame(10000);
//                else
//                    exitToMainActivity();
//
//                Toast.makeText(this, "tie", Toast.LENGTH_SHORT).show();
//                break;
//            case Constants.ADMIN_KEY:
//                myRef.child(Constants.NAME_TABLE_BOARDS).child(idKeyBoard).removeEventListener(eventListener);
//
//                if (isCreateGame) {
//                    removeThisGame(10000);
//                    myPlayer.setScore(myPlayer.getScore() + board.getAdminPlayerPoints());
//                    myRef.child(Constants.NAME_TABLE_PLAYERS).child(Utils.getIdKeyPlayer(this)).setValue(myPlayer);
//                } else
//                    exitToMainActivity();
//                Toast.makeText(this, board.getNameAdmin() + " won", Toast.LENGTH_SHORT).show();
//                break;
//            case Constants.JOIN_KEY:
//                myRef.child(Constants.NAME_TABLE_BOARDS).child(idKeyBoard).removeEventListener(eventListener);
//
//                if (!isCreateGame) {
//                    myPlayer.setScore(myPlayer.getScore() + board.getJoinPlayerPoints());
//                    myRef.child(Constants.NAME_TABLE_PLAYERS).child(Utils.getIdKeyPlayer(this)).setValue(myPlayer);
//                    exitToMainActivity();
//                } else
//                    removeThisGame(10000);
//
//                Toast.makeText(this, board.getNameJoin() + " won", Toast.LENGTH_SHORT).show();
//                break;
//        }
//    }

    private void exitToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void removeThisGame(int timeWait) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                myRef.child(Constants.NAME_TABLE_BOARDS).child(idKeyBoard).removeValue();
                exitToMainActivity();
            }
        }, timeWait);

    }

//    private void compareImages(Board board) {
//
//
//        int currentPosition = board.getFirstPosition();
//        int lastPosition = board.getLastPosition();
//
//
//        if (!(board.getAllImagesBtns().get(currentPosition).equals(board.getAllImagesBtns().get(lastPosition)))) {
//            backImageToDefault(currentPosition, lastPosition);
//            return;
//        }
//
//        if (board.isTurnAdmin())
//            board.setAdminPlayerPoints(board.getAdminPlayerPoints() + 1);
//        else
//            board.setJoinPlayerPoints(board.getJoinPlayerPoints() + 1);
//
//
//    }

//    private void backImageToDefault(final int index1, final int index2) {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                board.setTurnAdmin(!board.isTurnAdmin());
//                board.getAllImagesStateBtns().set(index1, 0);
//                board.getAllImagesStateBtns().set(index2, 0);
//                myRef.child(Constants.NAME_TABLE_BOARDS).child(board.getIdKey()).setValue(board);
//            }
//        }, 2000);
//    }


    private int getIdByString(int numId) {
        int id = getResources().getIdentifier("imgBtn" + numId, "id", getPackageName());
        return id;
    }
}
