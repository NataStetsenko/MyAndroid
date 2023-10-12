package step.learning.myAndroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CalcActivity extends AppCompatActivity {
    //сохранение данных при повороте
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tvResult.setText(savedInstanceState.getCharSequence("result"));
        tvExpression.setText(savedInstanceState.getCharSequence("expression"));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("result", tvResult.getText());
        outState.putCharSequence("expression", tvExpression.getText());
    }
    private final int MAX_DIGITS = 10;
    private TextView tvResult ;
    private TextView tvExpression ;
    private double operand = 0;
    private double operand2  = 0;
    private boolean flag = true;
    private boolean flag2 = true;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_calc );
        tvResult = findViewById( R.id.calc_tv_result );
        tvExpression = findViewById( R.id.calc_tv_expression );
        tvResult.setText( R.string.calc_btn_0 ) ;
        tvExpression.setText("");

        for( int i = 0; i < 10; i++ ) {
            findViewById(
                    getResources().getIdentifier(
                            "calc_btn_" + i,
                            "id",
                            getPackageName()
                    ) ).setOnClickListener( this::digitClick );
        }
        findViewById( R.id.calc_btn_c ).setOnClickListener( this::clearClickC);
        findViewById( R.id.calc_btn_ce ).setOnClickListener( this::clearClickCE);
        findViewById( R.id.calc_btn_inverse ).setOnClickListener( this::inverseClick );
        findViewById(R.id.calc_btn_comma).setOnClickListener( this::commaClick );
        findViewById(R.id.calc_btn_plus).setOnClickListener( this::mainClick );
        findViewById(R.id.calc_btn_minus).setOnClickListener( this::mainClick );
        findViewById(R.id.calc_btn_multiplication).setOnClickListener( this::mainClick );
        findViewById(R.id.calc_btn_divide).setOnClickListener( this::mainClick );
        findViewById(R.id.calc_btn_equal).setOnClickListener( this::mainClick );
        findViewById(R.id.calc_btn_square).setOnClickListener( this::sqrClick );
        findViewById(R.id.calc_btn_backspace).setOnClickListener( this::backspaceClick );
        findViewById(R.id.calc_btn_sqrt).setOnClickListener( this::sqrtClick );

    }
    private void sqrtClick( View view ) {
        String str = tvResult.getText().toString();
        double arg = getResult(str);
        showResult(arg);
        str = "sqrt(" + showResult(arg) + ")";
        tvExpression.setText(str);
        tvResult.setText(showResult(Math.sqrt(arg)));
    }
    private void backspaceClick( View view ) {
        String str = tvResult.getText().toString();
//        if (str.length()>0)
//        tvResult.setText(tvResult.getText().toString().substring(0, str.length() - 1));
    }
    private void inverseClick( View view ) {
        String str = tvResult.getText().toString() ;
        double arg = getResult(str) ;
        if( arg == 0 ) {
            tvResult.setText( R.string.calc_div_zero_message);
        }
        else {
            str = "1/" +  showResult(arg) + "=";
            tvExpression.setText(str);
            tvResult.setText(showResult(1/arg));
        }
    }

    private void sqrClick(View view) {
        String str = tvResult.getText().toString();
        double arg = getResult(str);
        showResult(arg);
        str = "sqr(" + showResult(arg) + ")";
        tvExpression.setText(str);
        tvResult.setText(showResult(Math.pow(arg, 2)));
    }
    private void digitClick( View view ) {
        String expression = tvExpression.getText().toString() ;
        if (expression.contains("=")) {
            tvResult.setText("");
            tvExpression.setText("");
        }
        int length = expression.length();
        if (flag&&length > 0 && !Character.isDigit(expression.charAt(length - 1))){// знак в конце expression
           tvResult.setText("");
           flag = false;
        }
        String str = tvResult.getText().toString() ;
        if( str.equals( getString( R.string.calc_btn_0 ) ) ) {
            str = "";
        }
        str += ( ( Button ) view ).getText();
        tvResult.setText( str );
        operand2  = getResult(str);

    }
    private void mainClick( View view ) {
        String exp = tvExpression.getText().toString();
//        if (flag2 && exp.length() > 0 && !Character.isDigit(exp.charAt(exp.length() - 1))) {
//           // tvResult.setText(tvResult.getText().toString().substring(0, exp.length() - 1) + ((Button) view).getText());
//            tvExpression.setText("");
//            flag2=false;
//        }

        String str = tvResult.getText().toString();
        if (!Character.isDigit(str.charAt(str.length() - 1))) // знак в конце tvResult
            tvResult.setText(tvResult.getText().toString().substring(0, str.length() - 1));
        exp = tvExpression.getText().toString();
        if (exp.length() > 0) {
            String  lastChar = exp.substring(exp.length() - 1);
            if (lastChar.equals(getString(R.string.calc_btn_plus))) {
                operand += getResult(str);
            } else if (lastChar.equals(getString(R.string.calc_btn_minus))) {
                operand -= getResult(str);
            } else if (lastChar.equals(getString(R.string.calc_btn_multiplication))) {
                operand *= getResult(str);
            } else if (lastChar.equals(getString(R.string.calc_btn_divide))) {
                if (getResult(str) == 0){
                    tvResult.setText(R.string.calc_div_zero_message);return;
                }
                else{
                    operand /= getResult(str);
                }
            } else {
            operand = getResult(str);
            str += ((Button) view).getText();
        }
            if(((Button) view).getText().toString().equals("=")) {
                tvResult.setText(showResult(operand));
                str = exp+str + ((Button) view).getText()+ showResult(operand);
                tvExpression.setText(str);
                flag = true; return;
            }
            tvResult.setText(showResult(operand));
            str = showResult(operand) + ((Button) view).getText();
        } else {
            operand = getResult(str);
            str += ((Button) view).getText();
        }
        tvExpression.setText(str);
        flag = true;
    }
    private double getResult(String str) {
        str = str.replaceAll( getString( R.string.calc_btn_0 ), "0" );
        str = str.replaceAll( getString( R.string.calc_btn_comma ), "." );
        return Double.parseDouble( str );
    }
    private String showResult(double res) {
        String str =  String.valueOf( res );
        if(str.length()>MAX_DIGITS){
            str = str.substring(0,MAX_DIGITS);
        }
        String[] parts = str.split("\\."); //убрала нули
        if (parts.length == 2) {
            String num = parts[1];
            int intValue = Integer.parseInt(num);
            if(intValue==0) {
                return  parts[0];
            }
        }
        return str;
    }
    private void clearClickC( View view ) {
        tvResult.setText( R.string.calc_btn_0 ) ;
        operand  = 0;
        tvExpression.setText("");
    }
    private void clearClickCE( View view ) {
        tvResult.setText( R.string.calc_btn_0 ) ;
    }
    private void commaClick( View view ) {
        String str = tvResult.getText().toString() ;
        if (str.contains(",")||str.contains(".")) return;
        str += ( ( Button ) view ).getText();
        tvResult.setText( str );
    }
}

