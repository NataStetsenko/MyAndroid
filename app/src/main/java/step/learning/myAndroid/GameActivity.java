package step.learning.myAndroid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private static final int N = 4;
    private final int[][] cells = new int[N][N] ;
    private final int[][] prevCells = new int[N][N] ;  // for UNDO action
    private final int[][] tmpCells = new int[N][N] ;  // for UNDO action
    private final TextView[][] tvCells = new TextView[N][N] ;
    private final Random random = new Random() ;

    private int score ;
    private int prevScore ;
    private int tmpScore ;
    private int bestScore;
    private int prevBestScore ;
    private int tmpBestScore ;
    private TextView tvScore ;
    private TextView tvBestScore ;
    private Animation spawnCellAnimation ;
    private Animation collapseSellAnimation ;
    private MediaPlayer spawnSound ;
    private static final String bestScoreFilename = "best_score" ;
    private CheckBox checkBox;
    private boolean flagForCell2048 = true;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        tvScore = findViewById(R.id.tv_game_score);
        tvBestScore = findViewById(R.id.tv_game_best);
        checkBox = findViewById(R.id.game_checkbox_sound);

        // завантажуємо анімацію
        spawnCellAnimation = AnimationUtils.loadAnimation(
                GameActivity.this,
                R.anim.game_spwn_cell
        );
        // ініціалізуємо анімацію
        spawnCellAnimation.reset();
        collapseSellAnimation = AnimationUtils.loadAnimation(
                GameActivity.this,
                R.anim.game_collapse_cell
        );
        collapseSellAnimation.reset();

        // Збираємо посилання на комірки ігрового поля
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                tvCells[i][j] = findViewById(
                        getResources().getIdentifier(
                                "game_cell_" + i + j,
                                "id",
                                getPackageName()
                        ));
            }
        }

        spawnSound = MediaPlayer.create(GameActivity.this, R.raw.jump_00);
        TableLayout tableLayout = findViewById(R.id.game_table);
        tableLayout.post(() -> { // відкладений запуск (на кадр прорисовки)
            int margin = 7;
            int w = this.getWindow().getDecorView().getWidth() - 2 * margin;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(w, w);
            layoutParams.setMargins(7, 50, 7, 50);
            layoutParams.gravity = Gravity.CENTER;
            tableLayout.setLayoutParams(layoutParams);
        });
        tableLayout.setOnTouchListener(
                new OnSwipeListener(GameActivity.this) {
                    @Override
                    public void onSwipeBottom() {
                        processMove(MoveDirection.BOTTOM);
                    }

                    @Override
                    public void onSwipeLeft() {
                        processMove(MoveDirection.LEFT);
                    }

                    @Override
                    public void onSwipeRight() {
                        processMove(MoveDirection.RIGHT);
                    }

                    @Override
                    public void onSwipeTop() {
                        processMove(MoveDirection.TOP);
                    }
                }
        );
        findViewById(R.id.game_btn_undo).setOnClickListener(this::undoMoveClick);
        findViewById(R.id.game_btn_new).setOnClickListener(this::newGameClick);
        loadBestScore();
        startNewGame();
    }


    private void newGameClick(View view) {
        // TODO: вивести повідомлення-підтвердження
        findViewById(R.id.game_btn_undo).setEnabled(false);
        showNewGameDialog();
    }

    private void undoMoveClick(View view) {
        for (int i = 0; i < N; i++) {
            System.arraycopy(prevCells[i], 0, cells[i], 0, N);
        }
        score = prevScore;
        bestScore = prevBestScore;
        saveBestScore();
        showField();
    }

    private void saveBestScore() {
        /* Android має розподілену файлову систему. У застосунку є вільний
         *  доступ до приватних файлів, які є частиною роботи та автоматично
         *  видаляються разом з застосунком. Є спільні ресурси (картинки, завантаження
         *  тощо) доступ до яких зазначається у маніфесті та має погоджуватись
         *  дозволом користувача. Інші файли можуть виявитись недоступними. */
        try (FileOutputStream outputStream = openFileOutput(bestScoreFilename, Context.MODE_PRIVATE);
             DataOutputStream writer = new DataOutputStream(outputStream)) {
            writer.writeInt(bestScore);
            writer.flush();
            Log.d("bestScoreFilename", "save ok");
        } catch (IOException e) {
            Log.e("bestScoreFilename", e.getMessage());
        }
    }
    private void loadBestScore() {
        try( FileInputStream inputStream = openFileInput( bestScoreFilename ) ;
             DataInputStream reader = new DataInputStream( inputStream )
        ) {
            bestScore = reader.readInt() ;
            Log.d( "loadBestScore", "Best score read: " + bestScore ) ;
        }
        catch( IOException ex ) {
            bestScore = 0 ;
            Log.e( "loadBestScore", Objects.requireNonNull( ex.getMessage() ) ) ;
        }
    }

    private void startNewGame() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                cells[i][j] = 0;
            }
        }
        score = 0;
        loadBestScore();
        tvBestScore.setText(getString(R.string.BEST2, bestScore ));
        spawnCell();
        spawnCell();
        showField();
    }

    private void processMove(MoveDirection direction) {
        for( int i = 0; i < N; i++ ) {
            System.arraycopy( cells[ i ], 0, tmpCells[ i ], 0, N );
        }
          tmpScore = score;
          tmpBestScore = bestScore;
        if (move(direction)) {
            for( int i = 0; i < N; i++ ) {
                System.arraycopy( tmpCells[ i ], 0, prevCells[ i ], 0, N );
            }
            findViewById(R.id.game_btn_undo).setEnabled(true);
            prevScore = tmpScore;
            prevBestScore = tmpBestScore;
            spawnCell();
            showField();
            if(flagForCell2048 && cell2048()){
                showCell2048Dialog();
                flagForCell2048 = false;
            }
            if (isGameFail()) {
                showFailDialog();
            } else {
                if (score > bestScore) {
                    bestScore = score;
                    saveBestScore();
                    tvBestScore.setText( getString( R.string.BEST2, bestScore ) ) ;
                }
            }
        } else {
            Toast.makeText(GameActivity.this,
                    R.string.game_toast_no_move, Toast.LENGTH_SHORT).show();
        }
    }
    private boolean move(MoveDirection direction) {
        switch (direction) {
            case BOTTOM:
                return moveBottom();
            case LEFT:
                return moveLeft();
            case RIGHT:
                return moveRight();
            case TOP:
                return moveTop();
        }
        return false;
    }

    /**
     * Поява нового числа на полі
     *
     * @return чи додалось число (є вільні комірки)
     */
    private boolean spawnCell() {
        // оскільки не відомо де і скільки порожніх комірок, шукаємо їх всі
        List<Integer> freeCellIndex = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (cells[i][j] == 0) {
                    freeCellIndex.add(10 * i + j);
                }
            }
        }
        int cnt = freeCellIndex.size();
        if (cnt == 0) {
            return false;
        }
        int randomIndex = random.nextInt(cnt);
        int randomCellIndex = freeCellIndex.get(randomIndex);
        int x = randomCellIndex / 10;
        int y = randomCellIndex % 10;
        cells[x][y] = random.nextInt(10) == 0 ? 4 : 2;
        tvCells[x][y].startAnimation(spawnCellAnimation);
        if (checkBox.isChecked())
            spawnSound.start();
        return true;
    }

    //відображення та підбір стилів
    private void showField() {
        Resources resources = getResources();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                tvCells[i][j].setText(String.valueOf(cells[i][j]));
                tvCells[i][j].setTextAppearance(
                        resources.getIdentifier(
                                "game_tv_" + cells[i][j],
                                "style", getPackageName()
                        )
                );
                tvCells[i][j].setBackgroundResource(
                        resources.getIdentifier(
                                "game_tv_" + cells[i][j],
                                "drawable",
                                getPackageName()
                        )
                );
            }
        }
        tvScore.setText(getString(R.string.SCORE2, score));
        tvBestScore.setText( getString( R.string.BEST2, bestScore ) ) ;
    }

    private boolean moveRight() {
        boolean result = false;
        // все переміщуємо ліворуч
        boolean needRepeat;
        for (int i = 0; i < N; i++) {
            do {
                needRepeat = false;
                for (int j = N - 1; j > 0; j--) {
                    if (cells[i][j] == 0 && cells[i][j - 1] != 0) {
                        cells[i][j] = cells[i][j - 1];
                        cells[i][j - 1] = 0;
                        needRepeat = true;
                        result = true;
                    }
                }
            } while (needRepeat);
            for (int j = N - 1; j > 0; j--) {
                if (cells[i][j] != 0 && cells[i][j] == cells[i][j - 1]) {

                    cells[i][j] *= 2;
                    //Animation
                    tvCells[i][j].startAnimation(collapseSellAnimation);
                    score += cells[i][j];
                    for (int k = j - 1; k > 0; k--) {
                        cells[i][k] = cells[i][k - 1];
                    }
                    cells[i][0] = 0;
                    result = true;
                }
            }
        }
        return result;
    }

    private boolean moveLeft() {
        boolean result = false;
        // все переміщуємо ліворуч
        boolean needRepeat;
        for (int i = 0; i < N; i++) {
            do {
                needRepeat = false;
                for (int j = 0; j < N - 1; j++) {
                    if (cells[i][j] == 0 && cells[i][j + 1] != 0) {
                        cells[i][j] = cells[i][j + 1];
                        cells[i][j + 1] = 0;
                        needRepeat = true;
                        result = true;
                    }
                }
            } while (needRepeat);
            for (int j = 0; j < N - 1; j++) {
                if (cells[i][j] != 0 && cells[i][j] == cells[i][j + 1]) {
                    cells[i][j] *= 2;
                    //Animation
                    tvCells[i][j].startAnimation(collapseSellAnimation);
                    score += cells[i][j];
                    for (int k = j + 1; k < N - 1; k++) {
                        cells[i][k] = cells[i][k + 1];
                    }
                    cells[i][N - 1] = 0;
                    result = true;
                }
            }
        }
        //перевіряемо
        //переміщуємо ліворуч
        return result;
    }

    private boolean moveTop() {
        boolean result = false;
        boolean needRepeat;
        for (int j = 0; j < N; j++) {
            do {
                needRepeat = false;
                for (int i = 0; i < N - 1; i++) {
                    if (cells[i][j] == 0 && cells[i + 1][j] != 0) {
                        cells[i][j] = cells[i + 1][j];
                        cells[i + 1][j] = 0;
                        needRepeat = true;
                        result = true;
                    }
                }
            } while (needRepeat);
            for (int i = 0; i < N - 1; i++) {
                if (cells[i][j] != 0 && cells[i][j] == cells[i + 1][j]) {
                    cells[i][j] *= 2;
                    //Animation
                    tvCells[i][j].startAnimation(collapseSellAnimation);
                    score += cells[i][j];
                    for (int k = j + 1; k < N - 1; k++) {
                        cells[k][j] = cells[k + 1][j];
                    }
                    cells[N - 1][j] = 0;
                    result = true;
                }
            }
        }
        return result;
    }

    private boolean moveBottom() {
        boolean result = false;
        boolean needRepeat;
        for (int j = 0; j < N; j++) {
            do {
                needRepeat = false;
                for (int i = N - 1; i > 0; i--) {
                    if (cells[i][j] == 0 && cells[i - 1][j] != 0) {
                        cells[i][j] = cells[i - 1][j];
                        cells[i - 1][j] = 0;
                        needRepeat = true;
                        result = true;
                    }
                }
            } while (needRepeat);
            for (int i = N - 1; i > 0; i--) {
                if (cells[i][j] != 0 && cells[i][j] == cells[i - 1][j]) {
                    cells[i][j] *= 2;
                    tvCells[i][j].startAnimation(collapseSellAnimation);
                    score += cells[i][j];
                    for (int k = i - 1; k > 0; k--) {
                        cells[k][j] = cells[k - 1][j];
                    }
                    cells[0][j] = 0;
                    result = true;
                }
            }
        }
        return result;
    }

    private boolean isGameFail() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (cells[i][j] == 0) {
                    return false;
                }
            }
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N - 1; j++) {
                if (cells[i][j] == cells[i][j + 1]) {
                    return false;
                }
            }
        }
        for (int i = 0; i < N - 1; i++) {
            for (int j = 0; j < N; j++) {
                if (cells[i][j] == cells[i + 1][j]) {
                    return false;
                }
            }
        }
        return true;
    }
    private boolean cell2048() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (cells[i][j] == 16) {
                    return true;
                }
            }
        }
        return false;
    }
    private void showFailDialog() {
        new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setIcon(android.R.drawable.ic_menu_info_details)
                .setTitle(R.string.game_over)
                .setMessage(R.string.game_over_dialog)
                .setCancelable(false)
                .setPositiveButton(R.string.game_over_yes,
                        (DialogInterface dialog, int whichButton) ->
                                startNewGame())
                .setNegativeButton(R.string.game_over_no,
                        (DialogInterface dialog, int whichButton) ->
                                finish())
                .setNeutralButton(R.string.game_over_undo,
                        (DialogInterface dialog, int whichButton) ->
                                dialog.dismiss())
                .show();
    }
    private void showNewGameDialog() {
        new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setIcon(android.R.drawable.ic_menu_help)
                .setTitle(R.string.game_new_game)
                .setMessage(R.string.game_new_game_dialog)
                .setCancelable(false)
                .setPositiveButton(R.string.game_over_yes,
                        (DialogInterface dialog, int whichButton) ->
                                startNewGame())
                .setNegativeButton(R.string.game_over_no,
                        (DialogInterface dialog, int whichButton) ->
                                dialog.dismiss())
                .show();
    }
    private void showCell2048Dialog() {
        new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setIcon(android.R.drawable.ic_menu_myplaces)
                .setTitle(R.string.game_2048)
                .setMessage(R.string.game_2048_dialog)
                .setCancelable(false)
                .setPositiveButton(R.string.game_over_yes,
                        (DialogInterface dialog, int whichButton) ->
                                dialog.dismiss())
                .setNegativeButton(R.string.game_over_no,
                        (DialogInterface dialog, int whichButton) ->
                                finish())
                .show();
    }

    private enum MoveDirection {BOTTOM, LEFT, RIGHT, TOP}
}
/*
Анімації (double-anim) - плавні переходи числових параметрів
між початковим та кінцевим значеннями. Закладаються декларативно (у xml)
та проробляються ОС.
Створюємо ресурсну папку (anim, назва важлива)
у ній - game_spawn_cell.xml (див. коментарі у ньому)
Завантажуємо анімацію (onCreate)  та ініціалізуємо її
Призначаємо (викликаємо) анімацію при появі комірки (див. spawnCell)
 */