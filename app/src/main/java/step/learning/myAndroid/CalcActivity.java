package step.learning.myAndroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CalcActivity extends AppCompatActivity {
    private Button calc_btn_multi;
    private Button calc_btn_min;
    private Button calc_btn_plus;
    private Button calc_btn_div;
    private Button calc_btn_1;
    private Button calc_btn_2;
    private Button calc_btn_3;
    private Button calc_btn_4;
    private Button calc_btn_5;
    private Button calc_btn_6;
    private Button calc_btn_7;
    private Button calc_btn_8;
    private Button calc_btn_9;
    private Button calc_btn_0;
    private Button calc_btn_comma;
    private Button calc_btn_c;
    private Button calc_btn_equals;
    private TextView tv_exp;
    private TextView tv_res;
    private StringBuffer num = new StringBuffer();
    private double result = 0;
    private boolean flag = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);
        tv_exp = findViewById(R.id.tv_exp);
        tv_res = findViewById(R.id.tv_res);
        setOnClick();
    }
    private void mainClick(View view) {
        View foundView = findViewById(view.getId());
        String temp = tv_exp.getText().toString();                //содержимое exp
        int length = temp.length();
        if (foundView instanceof Button) {
            Button button = (Button) foundView;
            String buttonText = button.getText().toString();     //содержимое button
            if (!flag || buttonText.equals("C")) {
                num.delete(0, num.length());
                result = 0;
                tv_exp.setText("");
                tv_res.setText("0");
                flag = true;
                if (buttonText.equals("C")) return;
            }
            if (buttonText.equals("=")) {
                tv_res.setText(Double.toString(result));
                flag = false;
            }
            if (buttonText.length() > 0 && Character.isDigit(buttonText.charAt(0))) { // если button цифра
                tv_exp.setText(tv_exp.getText() + buttonText);
                num.append(buttonText);
            } else {                                                                    // если button знак
                if (length == 0) return;
                if (Character.isDigit(temp.charAt(length - 1))) {                     // если посл. tv_exp цифра
                    if (buttonText.equals(",")) {                                     // цифра + button ","
                        String str = num.toString();
                        if (str.contains(".")) return;
                        num.append(".");
                        tv_exp.setText(tv_exp.getText() + buttonText);
                        return;
                    } else {                                                             // цифра + button знак
                        try {
                            result += Double.parseDouble(num.toString());
                            tv_res.setText(Double.toString(result));
                        } catch (NumberFormatException e) {
                            e.getMessage();
                        }
                        tv_exp.setText(tv_exp.getText() + buttonText);
                        num.delete(0, num.length());
                    }
                } else {
                    if (buttonText.equals(",")) return;
                    tv_exp.setText(tv_exp.getText().toString().substring(0, length - 1) + buttonText);
                    num.delete(0, num.length());
                }
            }
        }
    }

    private void setOnClick() {
        calc_btn_1 = findViewById(R.id.calc_btn_1);
        calc_btn_1.setOnClickListener(this::mainClick);

        calc_btn_2 = findViewById(R.id.calc_btn_2);
        calc_btn_2.setOnClickListener(this::mainClick);

        calc_btn_3 = findViewById(R.id.calc_btn_3);
        calc_btn_3.setOnClickListener(this::mainClick);

        calc_btn_4 = findViewById(R.id.calc_btn_4);
        calc_btn_4.setOnClickListener(this::mainClick);

        calc_btn_5 = findViewById(R.id.calc_btn_5);
        calc_btn_5.setOnClickListener(this::mainClick);

        calc_btn_6 = findViewById(R.id.calc_btn_6);
        calc_btn_6.setOnClickListener(this::mainClick);

        calc_btn_7 = findViewById(R.id.calc_btn_7);
        calc_btn_7.setOnClickListener(this::mainClick);

        calc_btn_8 = findViewById(R.id.calc_btn_8);
        calc_btn_8.setOnClickListener(this::mainClick);

        calc_btn_9 = findViewById(R.id.calc_btn_9);
        calc_btn_9.setOnClickListener(this::mainClick);

        calc_btn_0 = findViewById(R.id.calc_btn_0);
        calc_btn_0.setOnClickListener(this::mainClick);

        calc_btn_multi = findViewById(R.id.calc_btn_multi);
        calc_btn_multi.setOnClickListener(this::mainClick);

        calc_btn_min = findViewById(R.id.calc_btn_min);
        calc_btn_min.setOnClickListener(this::mainClick);

        calc_btn_plus = findViewById(R.id.calc_btn_plus);
        calc_btn_plus.setOnClickListener(this::mainClick);

        calc_btn_div = findViewById(R.id.calc_btn_div);
        calc_btn_div.setOnClickListener(this::mainClick);

        calc_btn_comma = findViewById(R.id.calc_btn_comma);
        calc_btn_comma.setOnClickListener(this::mainClick);

        calc_btn_equals = findViewById(R.id.calc_btn_equals);
        calc_btn_equals.setOnClickListener(this::mainClick);

        calc_btn_c = findViewById(R.id.calc_btn_c);
        calc_btn_c.setOnClickListener(this::mainClick);
    }
}