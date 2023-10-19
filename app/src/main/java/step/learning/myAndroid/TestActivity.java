package step.learning.myAndroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.gson.annotations.SerializedName;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TestActivity extends AppCompatActivity {

    private Button btn_temp;
    private EditText edit_text;
    private TextView tv_result;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        btn_temp = findViewById(R.id.btn_temp);
        edit_text = findViewById(R.id.edit_text);
        tv_result = findViewById(R.id.tv_result);
        btn_temp.setOnClickListener(view -> {
            if (edit_text.getText().toString().trim().isEmpty()) {
                Toast.makeText(TestActivity.this, R.string.no_input, Toast.LENGTH_LONG).show();
            } else {
                String city = edit_text.getText().toString().trim();
                String apiKey = "b08aa3ebaee036a3beee53edc090be62";
                String units = "metric";
                String language = "ua";
                url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey + "&units=" + units + "&lang=" + language;
                tv_result.setText(url);
            }
        });
    }
//    private void loadUlrData() {
//        try( InputStream stream = new URL(nbuRaterUrl).openStream();) {
//            ByteArrayOutputStream builder = new ByteArrayOutputStream();
//            byte[] buffer = new byte[1024 * 16];
//            int receivedLength;
//            while ((receivedLength = stream.read(buffer)) > 0) {
//                builder.write(buffer, 0, receivedLength);
//            }
//            runOnUiThread(() ->
//                    tvJson.setText(builder.toString()));
//        } catch (MalformedURLException e) {
//            tvJson.setText(e.getMessage());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (NetworkOnMainThreadException ignored) {
//            tvJson.setText("Відкриття з'єднання з UI (основного) потоку");
//        }
//    }
}


