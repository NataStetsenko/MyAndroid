package step.learning.myAndroid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
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
import java.util.Locale;
import java.util.Objects;

import okhttp3.Response;
import step.learning.myAndroid.orm.NbuRate;
import step.learning.myAndroid.orm.NbuRateResponse;

public class RatesActivity extends AppCompatActivity {
private final String nbuRatesUrl = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
private TextView tvJson;
private NbuRateResponse nbuRateResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rates);
        tvJson = findViewById(R.id.rates_tv_json);
        new Thread(this::loadUrlData).start();
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
//    private void showResponse() {
//        LinearLayout container = findViewById(R.id.rates_container);
//        for (NbuRate nbuRate: nbuRateResponse.getRates()){
//            TextView tv = new TextView(this);
//            tv.setText(String.format(Locale.UK, "%s (%s) %f грн",
//                    nbuRate.getCc(), nbuRate.getTxt(), nbuRate.getRate()));
//            tv.setPadding(10,5,10,5);
//            tv.setOnClickListener(this::OnClick);
//            tv.setTag(nbuRate);
//            container.addView(tv);
//        }
//    }
private void showResponse() {
    GridLayout container = findViewById(R.id.rates_container);
    container.removeAllViews(); // Очистить все существующие элементы

    int columnCount = 3;
    for (NbuRate nbuRate: nbuRateResponse.getRates()){
        TextView tv = new TextView(this);
        tv.setBackgroundResource(R.drawable.rate);
        tv.setTextAppearance(R.style.rates_style);
        tv.setGravity(Gravity.CENTER);
        tv.setText(String.format(Locale.UK, "%s",
                nbuRate.getCc()));
        tv.setPadding(10, 5, 10, 5);
        tv.setWidth(290);
        tv.setHeight(290);
        tv.setOnClickListener(this::OnClick);
        tv.setTag(nbuRate);
        // Создаем параметры для TextView, указывая, что элемент должен занимать одну часть строки
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        tv.setLayoutParams(params);
        params.setMargins(10, 10, 10, 10);
        tv.setLayoutParams(params);
        container.addView(tv);
        container.setColumnCount(columnCount);
    }
}
    private void OnClick(View view) {
        NbuRate nbuRate = (NbuRate)view.getTag();
        showRateDialog(nbuRate);
//        Toast.makeText(this, "123", Toast.LENGTH_LONG).show();
    }

    private void showResponseTxt() {
        StringBuilder sb = new StringBuilder();
        for (NbuRate nbuRate: nbuRateResponse.getRates()){
            sb.append(String.format(Locale.UK, "%s(%s)%f грн\n",
                    nbuRate.getCc(), nbuRate.getTxt(), nbuRate.getRate()));

        }
        tvJson.setText(sb.toString());
    }
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