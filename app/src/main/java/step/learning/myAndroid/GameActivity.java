package step.learning.myAndroid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private final int[][] cells = new int[4][4];
    private static final int N = 4;
    private final TextView[][] tvCells = new TextView[N][N];
    private final Random random = new Random();
    private int score;
    private TextView tvScore;
    private TextView tvBest;
    private Animation spanSellAnimation;
    private Animation collapseSellAnimation;
    private MediaPlayer spawnSound;
    private CheckBox checkBox;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        tvScore = findViewById(R.id.tv_game_score);
        tvBest = findViewById(R.id.tv_game_best);
        checkBox = findViewById(R.id.game_checkbox_sound);
        findViewById(R.id.game_btn_undo).setOnClickListener(this::UndoClick);
        // завантажуємо анімацію
        spanSellAnimation = AnimationUtils.loadAnimation(
                GameActivity.this,
                R.anim.game_spwn_cell
        );
        // ініціалізуємо анімацію
        spanSellAnimation.reset();
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
                        if (moveBottom()) {
                            spawnCell();
                            showField();
                        } else if (freeCellIndex()) {
                            gameOver();
                        } else {
                            Toast.makeText(GameActivity.this,
                                    R.string.game_toast_no_move, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onSwipeLeft() {
                        if (moveLeft()) {
                            spawnCell();
                            showField();
                        } else if (freeCellIndex()) {
                            gameOver();
                        } else {
                            Toast.makeText(GameActivity.this,
                                    R.string.game_toast_no_move, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onSwipeRight() {
                        if (moveRight()) {
                            spawnCell();
                            showField();
                        } else if (freeCellIndex()) {
                            gameOver();
                        } else {
                            Toast.makeText(GameActivity.this,
                                    R.string.game_toast_no_move, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onSwipeTop() {
                        if (moveTop()) {
                            spawnCell();
                            showField();
                        } else if (freeCellIndex()) {
                            gameOver();
                        } else {
                            Toast.makeText(GameActivity.this,
                                    R.string.game_toast_no_move, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        spawnCell();
        spawnCell();
        showField();
    }

    private void UndoClick(View view) {

    }

    /**
     * Поява нового числа на полі
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
        cells[x][y] = random.nextInt(10) == 0 ? 2 : 4;
        tvCells[x][y].startAnimation(spanSellAnimation);
        if(checkBox.isChecked())
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
    private void gameOver(){
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N-1; j++) {
                if (cells[i][j] == cells[i][j + 1]) {
                    return;
                }
            }
        }
        for (int i = 0; i < N-1; i++) {
            for (int j = 0; j < N; j++)  {
                if (cells[i][j] == cells[i + 1][j]) {
                    return;
                }
            }
        }
        if (Integer.parseInt(tvScore.getText().toString()) > Integer.parseInt(tvBest.getText().toString()))
            tvBest.setText(tvScore.getText().toString());
        showGameOverDialog();
    }
    private boolean freeCellIndex() {
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
            return true;
        }
        return false;
    }
    private void showGameOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.game_over);
        builder.setMessage(R.string.game_over_dialog);
        builder.setPositiveButton(R.string.game_over_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(R.string.game_over_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
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