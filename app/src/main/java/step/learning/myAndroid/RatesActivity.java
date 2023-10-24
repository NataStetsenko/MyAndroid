package step.learning.myAndroid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import okhttp3.Response;
import step.learning.myAndroid.orm.NbuRate;
import step.learning.myAndroid.orm.NbuRateResponse;

public class RatesActivity extends AppCompatActivity {
private final String nbuRatesUrl = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
private EditText editText;
private NbuRateResponse nbuRateResponse;
private boolean flag= true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rates);
        editText = findViewById(R.id.edit_text);
        findViewById(R.id.rates_btn).setOnClickListener(this::btnClick);
        findViewById(R.id.rate_btn_max).setOnClickListener(this::btnMaxClick);
        findViewById(R.id.rate_btn_min).setOnClickListener(this::btnMinClick);
        findViewById(R.id.rate_btn_edit).setOnClickListener(this::btnEditClick);
        new Thread(this::loadUrlData).start();
    }

    private void btnEditClick(View view) {
        List<NbuRate> ratesTemp = nbuRateResponse.getRates().stream()
                .filter(rate -> rate.getCc().contains(editText.getText().toString().trim()))
                .collect(Collectors.toList());
        showResponseTemp(ratesTemp);
    }
    private void btnMinClick(View view) {
        if(nbuRateResponse==null|| nbuRateResponse.getRates()==null){
            Toast.makeText(this, "123", Toast.LENGTH_SHORT).show();
            return;
        }
        nbuRateResponse.getRates().sort(Comparator.comparingDouble(NbuRate::getRate).reversed());
        showResponse();
    }

    private void btnMaxClick(View view) {
        if(nbuRateResponse==null|| nbuRateResponse.getRates()==null){
            Toast.makeText(this, "123", Toast.LENGTH_SHORT).show();
            return;
        }
        nbuRateResponse.getRates().sort(Comparator.comparingDouble(NbuRate::getRate));
        showResponse();
    }
    private void btnClick(View view) {
        if(nbuRateResponse==null|| nbuRateResponse.getRates()==null){
            Toast.makeText(this, "123", Toast.LENGTH_SHORT).show();
            return;
        }
        nbuRateResponse.getRates().sort((r1, r2) -> Collator.getInstance()
                        .compare(r1.getTxt(), r2.getTxt() ) ) ;
        showResponse();
        //nbuRateResponse.getRates().sort((r1, r2) ->r1.getTxt().compareTo(r2.getTxt()));
    }

    private void loadUrlData() {
        try (InputStream stream = new URL(nbuRatesUrl).openStream()) {
            ByteArrayOutputStream builder = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 16];
            int receivedLength;
            while ((receivedLength = stream.read(buffer)) > 0) {
                builder.write(buffer, 0, receivedLength);
            }
            nbuRateResponse = new NbuRateResponse(new JSONArray(builder.toString()));
            runOnUiThread(this::showResponse);
        } catch (IOException | JSONException ex) {
            Log.e("loadUrlData", Objects.requireNonNull(ex.getMessage()));
        } catch (NetworkOnMainThreadException ignored) {
            Log.e("loadUrlData", getString(R.string.rates_ex_thread));
        }
    }
    private void showResponse() {
        LinearLayout container = findViewById( R.id.rates_container ) ;
        container.removeAllViews();
        Drawable rateBg1 = AppCompatResources.getDrawable(
                getApplicationContext(),            R.drawable.rate   ) ;
        LinearLayout.LayoutParams rateParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT    );
        // Margin - параметри контейнера (Layout), вони задають правила взаємного
        // розміщення елементів в одному контейнері
         rateParams.setMargins( 10, 5, 10, 5 );
         rateParams.gravity = Gravity.CENTER;
         LinearLayout.LayoutParams horizontalMargin = new LinearLayout.LayoutParams(
                 LinearLayout.LayoutParams.WRAP_CONTENT,
                 LinearLayout.LayoutParams.WRAP_CONTENT    );
         horizontalMargin.setMargins( 7, 0, 7, 0 );

        for (NbuRate nbuRate: nbuRateResponse.getRates()){
            LinearLayout line = new LinearLayout(this);
            if(flag){

                line.setBackgroundResource(R.drawable.rate2);
                line.setPadding(15, 15, 15, 15);
                line.setOnClickListener(this::OnClick);
                TextView tv1 = new TextView(this);
                tv1.setText(nbuRate.getCc() + " ");
                tv1.setBackgroundResource(R.drawable.rate);
                tv1.setPadding(15, 10, 15, 10);
                tv1.setTextAppearance(R.style.rates_style2);
                TextView tv2 = new TextView(this);
                tv2.setText(nbuRate.getTxt() + " ");
                tv2.setLayoutParams(horizontalMargin);
                tv2.setBackgroundResource(R.drawable.rate);
                tv2.setPadding(15, 10, 15, 10);
                tv2.setTextAppearance(R.style.rates_style2);
                TextView tv3 = new TextView(this);
                tv3.setText(nbuRate.getRate() + "");
                tv3.setBackgroundResource(R.drawable.rate);
                tv3.setPadding(15, 10, 15, 10);
                tv3.setTextAppearance(R.style.rates_style2);
                line.setLayoutParams(rateParams);
                line.addView(tv1);
                line.addView(tv2);
                line.addView(tv3);
                container.addView(line);
                flag = false;
            }
            else {

                line.setBackgroundResource(R.drawable.rate);
                line.setPadding(15, 15, 15, 15);
                TextView tv1 = new TextView(this);
                tv1.setText(nbuRate.getCc() + " ");
                tv1.setBackgroundResource(R.drawable.rate2);
                tv1.setPadding(15, 10, 15, 10);
                tv1.setTextAppearance(R.style.rates_style);
                TextView tv2 = new TextView(this);
                tv2.setText(nbuRate.getTxt() + " ");
                tv2.setLayoutParams(horizontalMargin);
                tv2.setBackgroundResource(R.drawable.rate2);
                tv2.setPadding(15, 10, 15, 10);
                tv2.setTextAppearance(R.style.rates_style);
                TextView tv3 = new TextView(this);
                tv3.setText(nbuRate.getRate() + "");
                tv3.setBackgroundResource(R.drawable.rate2);
                tv3.setPadding(15, 10, 15, 10);
                tv3.setTextAppearance(R.style.rates_style);
                line.setLayoutParams(rateParams);
                line.addView(tv1);
                line.addView(tv2);
                line.addView(tv3);
                container.addView(line);
                flag = true;
            }

        }
    }
    private void showResponseTemp(List<NbuRate> ratesTemp) {
        LinearLayout container = findViewById( R.id.rates_container ) ;
        container.removeAllViews();
        Drawable rateBg1 = AppCompatResources.getDrawable(
                getApplicationContext(),            R.drawable.rate   ) ;
        LinearLayout.LayoutParams rateParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT    );
        // Margin - параметри контейнера (Layout), вони задають правила взаємного
        // розміщення елементів в одному контейнері
         rateParams.setMargins( 10, 5, 10, 5 );
         rateParams.gravity = Gravity.CENTER;
         LinearLayout.LayoutParams horizontalMargin = new LinearLayout.LayoutParams(
                 LinearLayout.LayoutParams.WRAP_CONTENT,
                 LinearLayout.LayoutParams.WRAP_CONTENT    );
         horizontalMargin.setMargins( 7, 0, 7, 0 );

        for (NbuRate nbuRate: ratesTemp){
            LinearLayout line = new LinearLayout(this);
            if(flag){

                line.setBackgroundResource(R.drawable.rate2);
                line.setPadding(15, 15, 15, 15);
                line.setOnClickListener(this::OnClick);
                TextView tv1 = new TextView(this);
                tv1.setText(nbuRate.getCc() + " ");
                tv1.setBackgroundResource(R.drawable.rate);
                tv1.setPadding(15, 10, 15, 10);
                tv1.setTextAppearance(R.style.rates_style2);
                TextView tv2 = new TextView(this);
                tv2.setText(nbuRate.getTxt() + " ");
                tv2.setLayoutParams(horizontalMargin);
                tv2.setBackgroundResource(R.drawable.rate);
                tv2.setPadding(15, 10, 15, 10);
                tv2.setTextAppearance(R.style.rates_style2);
                TextView tv3 = new TextView(this);
                tv3.setText(nbuRate.getRate() + "");
                tv3.setBackgroundResource(R.drawable.rate);
                tv3.setPadding(15, 10, 15, 10);
                tv3.setTextAppearance(R.style.rates_style2);
                line.setLayoutParams(rateParams);
                line.addView(tv1);
                line.addView(tv2);
                line.addView(tv3);
                container.addView(line);
                flag = false;
            }
            else {

                line.setBackgroundResource(R.drawable.rate);
                line.setPadding(15, 15, 15, 15);
                TextView tv1 = new TextView(this);
                tv1.setText(nbuRate.getCc() + " ");
                tv1.setBackgroundResource(R.drawable.rate2);
                tv1.setPadding(15, 10, 15, 10);
                tv1.setTextAppearance(R.style.rates_style);
                TextView tv2 = new TextView(this);
                tv2.setText(nbuRate.getTxt() + " ");
                tv2.setLayoutParams(horizontalMargin);
                tv2.setBackgroundResource(R.drawable.rate2);
                tv2.setPadding(15, 10, 15, 10);
                tv2.setTextAppearance(R.style.rates_style);
                TextView tv3 = new TextView(this);
                tv3.setText(nbuRate.getRate() + "");
                tv3.setBackgroundResource(R.drawable.rate2);
                tv3.setPadding(15, 10, 15, 10);
                tv3.setTextAppearance(R.style.rates_style);
                line.setLayoutParams(rateParams);
                line.addView(tv1);
                line.addView(tv2);
                line.addView(tv3);
                container.addView(line);
                flag = true;
            }

        }
    }

//private void showResponse() {
//    GridLayout container = findViewById(R.id.rates_container);
//    container.removeAllViews(); // Очистить все существующие элементы
//
//    int columnCount = 3;
//    for (NbuRate nbuRate: nbuRateResponse.getRates()){
//        TextView tv = new TextView(this);
//        tv.setBackgroundResource(R.drawable.rate);
//        tv.setTextAppearance(R.style.rates_style);
//        tv.setGravity(Gravity.CENTER);
//        tv.setText(String.format(Locale.UK, "%s",
//                nbuRate.getCc()));
//        tv.setPadding(10, 5, 10, 5);
//        tv.setWidth(290);
//        tv.setHeight(290);
//        tv.setOnClickListener(this::OnClick);
//        tv.setTag(nbuRate);
//        // Создаем параметры для TextView, указывая, что элемент должен занимать одну часть строки
//        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
//        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
//        tv.setLayoutParams(params);
//        params.setMargins(10, 10, 10, 10);
//        tv.setLayoutParams(params);
//        container.addView(tv);
//        container.setColumnCount(columnCount);
//    }
//}
    private void OnClick(View view) {
        NbuRate nbuRate = (NbuRate)view.getTag();
        showRateDialog(nbuRate);
//        Toast.makeText(this, "123", Toast.LENGTH_LONG).show();
    }

//    private void showResponseTxt() {
//        StringBuilder sb = new StringBuilder();
//        for (NbuRate nbuRate: nbuRateResponse.getRates()){
//            sb.append(String.format(Locale.UK, "%s(%s)%f грн\n",
//                    nbuRate.getCc(), nbuRate.getTxt(), nbuRate.getRate()));
//
//        }
//        tvJson.setText(sb.toString());
//    }
    private void showRateDialog(NbuRate nbuRate) {
        new AlertDialog.Builder(this, R.style.rateDialogStyle)
                .setIcon(android.R.drawable.ic_menu_myplaces)
                .setTitle(R.string.rates_tittle)
                .setMessage(String.format(Locale.UK, "%s(%s)%f грн\n",
                        nbuRate.getCc(), nbuRate.getTxt(), nbuRate.getRate()))
                .setCancelable(false)
                .setPositiveButton(R.string.rates_continue,
                        (DialogInterface dialog, int whichButton) ->
                                dialog.dismiss())
                .show();
    }
}