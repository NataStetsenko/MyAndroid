package step.learning.myAndroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
private TextView main_tv_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button main_btn = findViewById(R.id.main_btn);
        Button test_btn = findViewById(R.id.app_btn_temp);
        Button app_btn_calc = findViewById(R.id.app_btn_calc);
        main_tv_title =  findViewById(R.id.main_tv_title);
        main_btn.setOnClickListener(this::mainBtnClick);
        test_btn.setOnClickListener(this::testBtnClick);
        app_btn_calc.setOnClickListener(this::calcBtnClick);
        findViewById(R.id.app_btn_2048).setOnClickListener(this::gameBtnClick);
        findViewById(R.id.app_btn_rates).setOnClickListener(this::ratesBtnClick);
        app_btn_calc.setOnClickListener(this::calcBtnClick);
    }

    private void ratesBtnClick(View view) {
        Intent intent = new Intent(this.getApplicationContext(), RatesActivity.class);
        startActivity(intent);
    }

    private void mainBtnClick(View view){
        Intent intent = new Intent(this.getApplicationContext(), ViewsActivity.class);
        startActivity(intent);
    }
    private void testBtnClick(View view){
        Intent intent = new Intent(this.getApplicationContext(), TestActivity.class);
        startActivity(intent);
    }
    private void calcBtnClick(View view){
        Intent intent = new Intent(this.getApplicationContext(), CalcActivity.class);
        startActivity(intent);
    }
    private void gameBtnClick(View view){
        Intent intent = new Intent(this.getApplicationContext(), GameActivity.class);
        startActivity(intent);
    }
}