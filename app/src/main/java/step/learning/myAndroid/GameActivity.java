package step.learning.myAndroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        tvScore = findViewById(R.id.tv_game_score);
        tvBest = findViewById(R.id.tv_game_best);
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
        TableLayout tableLayout = findViewById(R.id.game_table);
        tableLayout.post( () -> {    int margin = 7 ;
            int w = this.getWindow().getDecorView().getWidth() - 2 * margin;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( w, w ) ;
            layoutParams.setMargins( 7, 50, 7, 50 );
            layoutParams.gravity = Gravity.CENTER;
            tableLayout.setLayoutParams( layoutParams ) ;} ) ;


        tableLayout.setOnTouchListener(
                new OnSwipeListener(GameActivity.this) {
                    @Override
                    public void onSwipeBottom() {
                        Toast.makeText(GameActivity.this,
                                "onSwipeBottom", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSwipeLeft() {
                        Toast.makeText(GameActivity.this,
                                "onSwipeLeft", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSwipeRight() {
                        Toast.makeText(GameActivity.this,
                                "onSwipeRight", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSwipeTop() {
                        Toast.makeText(GameActivity.this,
                                "onSwipeTop", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        spawnCell();
        spawnCell();
        showField();
    }
    private boolean spawnCell() {
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
        cells[x][y] = random.nextInt(10) == 0 ? 32 : 128;
        return true;
    }
//відображення та підбір стилів
    private void showField(){
       Resources resources = getResources();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                tvCells[i][j].setText(String.valueOf(cells[i][j]));
                tvCells[i][j].setTextAppearance(
                        resources.getIdentifier(
                                "game_tv_"+cells[i][j],
                                "style", getPackageName()
                        )
                );
                tvCells[i][j].setBackgroundResource(
                        resources.getIdentifier(
                                "game_tv_"+cells[i][j],
                                "drawable",
                                getPackageName()
                        )
                );
            }
        }
        int randomScore = random.nextInt(1000) + 1;
        tvScore.setText(String.valueOf(randomScore));
        if (Integer.parseInt(tvScore.getText().toString()) > Integer.parseInt(tvBest.getText().toString()))
            tvBest.setText(tvScore.getText().toString());

    }
}
