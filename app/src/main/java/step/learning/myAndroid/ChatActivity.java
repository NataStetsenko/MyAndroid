package step.learning.myAndroid;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import step.learning.myAndroid.orm.ChatMessage;
import step.learning.myAndroid.orm.ChatResponse;

public class ChatActivity extends AppCompatActivity {
    private final String chatUrl = "https://chat.momentfor.fun/";
    private LinearLayout chatContainer;
    private ScrollView svContainer;
    private final List<ChatMessage> chatMessages = new ArrayList<>();
    private boolean flag = true;
    private EditText etNik;
    private EditText etMessage;
    private TextView tvCount;
    private Handler handler;
    private final String nikFilename = "nik.saved" ;
    private final Map<String, String> emoji = new HashMap<String, String>() { {
        put( ":)", new String( Character.toChars(0x1f600) ) );
        put( ":(", new String( Character.toChars(0x1f612) ) );
    } } ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_chat );
        chatContainer = findViewById( R.id.chat_layout_container );
        svContainer = findViewById( R.id.chat_sv_container );
        findViewById(R.id.chat_btn_send).setOnClickListener(this::ButtonClick);
        etMessage = findViewById(R.id.chat_edit_text_message);
        etNik = findViewById( R.id.chat_et_nik ) ;
        tvCount =  findViewById(R.id.chat_count);
        findViewById(R.id.chat_btn_save_nik).setOnClickListener(this::saveNikClick);
        handler = new Handler();
        handler.post( this::updateChat );
        tryLoadNik();

    }

    private void saveNikClick( View view ) {
        try(FileOutputStream outputStream = openFileOutput( nikFilename, Context.MODE_PRIVATE );
            DataOutputStream writer = new DataOutputStream( outputStream )
        ) {
            User user = new User() ;
            user.setNik( etNik.getText().toString() ) ;
            // writer.writeUTF( user.toJson() ) ;
            writer.writeUTF( new Gson().toJson( user ) ) ;
            writer.flush() ;
        }
        catch( Exception ex ) {
            Log.e( "saveNikClick", Objects.requireNonNull( ex.getMessage() ) ) ;
        }
    }
    private void tryLoadNik() {
        try(FileInputStream inputStream = openFileInput( nikFilename );
            DataInputStream reader = new DataInputStream( inputStream )
        ) {
            String json = reader.readUTF() ;
            Log.d( "tryLoadNik", "read: " + json ) ;
            User user = new Gson().fromJson( json, User.class ) ; // new User( json ) ;
            etNik.setText( user.getNik() ) ;
        }
        catch( Exception ex ) {
            Log.e( "tryLoadNik", Objects.requireNonNull( ex.getMessage() ) ) ;
        }
    }
    private void updateChat() {
        new Thread(this::loadChatMessages).start();
        handler.postDelayed(this::updateChat, 3000);
    }


    private void ButtonClick(View view) {
        String nik = etNik.getText().toString();
        String message = etMessage.getText().toString();
        if (nik.isEmpty()) {
            Toast.makeText(this, "Введіть нік у чаті", Toast.LENGTH_SHORT).show();
            etNik.requestFocus();
            return;
        }
        if (message.isEmpty()) {
            Toast.makeText(this, "Введіть повідомлення", Toast.LENGTH_SHORT).show();
            etMessage.requestFocus();
            return;
        }
        new Thread( () -> sendChatMessage( nik, message ) ).start();

    }

    private void sendChatMessage(String nik, String message) {
        try {
            //1. соединение
            URL url = new URL(chatUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept","*/*");
            connection.setChunkedStreamingMode(0);
            // 2. Формуємо запит - пишемо до Output
            OutputStream connectionOutput = connection.getOutputStream() ;
            String body = String.format( "author=%s&msg=%s",
                    URLEncoder.encode( nik, StandardCharsets.UTF_8.name() ),
                    URLEncoder.encode( message, StandardCharsets.UTF_8.name() ) ) ;
            connectionOutput.write( body.getBytes( StandardCharsets.UTF_8 ) ) ;
            connectionOutput.flush() ;  // надсилання запиту
            connectionOutput.close() ;  // закриваємо, або вживаємо try(resource)

            // 3. Одержуємо відповідь - перевіряємо статус та читаємо Input
            int status = connection.getResponseCode();
            if (status == 201) {
                runOnUiThread( () -> etMessage.setText( "" ) );
                Log.d("sendChatMessage", "Request OK");
            } else {
                InputStream inputStream = connection.getInputStream();
                String error = streamToString(inputStream);
                Log.d("sendChatMessage", "status = " + status + "" + error);
                return;
            }
            connection.disconnect();
            if (status == 201) {
                loadChatMessages();
            }
        } catch (Exception ex) {
            Log.d("sendChatMessage", ex.getMessage());
        }

    }

    private void showChatMessages() {
        boolean needScroll = false ;
        for( ChatMessage chatMessage : chatMessages ) {
            if( chatMessage.getView() == null ) {
                chatContainer.addView( chatMessageView( chatMessage ) );
                needScroll = true ;  // є нові повідомлення - треба прокрутити контейнер
            }
        }
        if( needScroll ) {
            svContainer.post( () -> svContainer.fullScroll( View.FOCUS_DOWN ) ) ;
        }
    }
    private View chatMessageView( ChatMessage chatMessage ) {
        tvCount.setText(str);
        String nik = etNik.getText().toString();
        boolean isMine = nik.equals( chatMessage.getAuthor() ) ;
        LinearLayout messageContainer = new LinearLayout( ChatActivity.this );
        messageContainer.setTag(chatMessage);
        chatMessage.setView(messageContainer);
        messageContainer.setOrientation( LinearLayout.VERTICAL );
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        containerParams.setMargins(8, 10, 8, 10);
        messageContainer.setLayoutParams( containerParams );
        if(isMine){
            containerParams.gravity = Gravity.RIGHT;
            messageContainer.setBackgroundResource(R.drawable.message_left);
        }else {
            messageContainer.setBackgroundResource(R.drawable.message_right);
        }

        messageContainer.setPadding(30, 0, 30, 0);

        messageContainer.setMinimumWidth(400);

        Typeface typeface = ResourcesCompat.getFont(this, R.font.mountains_of_christmas);

        TextView tv = new TextView( ChatActivity.this );
        tv.setTextAppearance(R.style.message);
        tv.setPadding(10,15,0,0);
        tv.setText( chatMessage.getAuthor() );
        tv.setTypeface(typeface);
        messageContainer.addView( tv );


        typeface = ResourcesCompat.getFont(this, R.font.amaticregular);
        tv = new TextView( ChatActivity.this );
        // new String(Character.toChars(0x1f604))
        // U+1F600 \ u{1F600}
        String messageText = chatMessage.getText();
        for(String smiley:emoji.keySet()) {
            messageText = messageText.replace(smiley, emoji.get( smiley));
        }
        tv.setText(messageText);
        tv.setTextAppearance(R.style.message);
        tv.setTypeface(typeface);
        messageContainer.addView( tv );
        tv = new TextView( ChatActivity.this );
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = calendar.getTime();
        Date date = convertToDate(chatMessage.getMoment());
        if (date != null && date.before(yesterday )) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            tv.setText( format.format(date));
        }else {
            tv.setText( chatMessage.getMoment());
        }

        tv.setPadding(10,10,30,5);
        tv.setGravity(Gravity.RIGHT);
        tv.setTextAppearance(R.style.message_date);
        tv.setTypeface(typeface);
        messageContainer.addView( tv );

        return messageContainer;
    }
    private  String str ="0";
    private void loadChatMessages() {
        try( InputStream inputStream = new URL( chatUrl ).openStream() ) {
            ChatResponse chatResponse = ChatResponse.fromJsonString(
                    streamToString( inputStream ) );

            // Перевіряємо на нові повідомлення, оновлюємо (за потреби) колекцію chatMessages
            boolean wasNewMessages = false;
            int count = 0;
            chatResponse.getData().sort(Comparator.comparing(ChatMessage::getDate));
            for( ChatMessage message : chatResponse.getData() ) {
                if( chatMessages.stream().noneMatch( m -> m.getId().equals( message.getId() ) ) ) {
                    // це нове повідомлення (немає у колекції)
                    count++;


                    chatMessages.add( message );
                    wasNewMessages = true;
                }
            }
            if(count!=chatResponse.data.size()) {

                str = String.valueOf(count);
            }
            if( wasNewMessages ) {
                runOnUiThread( this::showChatMessages );
            }
        }
        catch( NetworkOnMainThreadException ignored ) {
            Log.e( "loadChatMessages", "NetworkOnMainThreadException" );
        }
        catch( MalformedURLException ex ) {
            Log.e( "loadChatMessages", "URL parse error: " + ex.getMessage() );
        }
        catch( IOException ex ) {
            Log.e( "loadChatMessages", "IO error: " + ex.getMessage() );
        }
    }

    private String streamToString( InputStream inputStream ) throws IOException {
        ByteArrayOutputStream builder = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesReceived;
        while( ( bytesReceived = inputStream.read( buffer ) ) > 0 ) {
            builder.write( buffer, 0 , bytesReceived );
        }
        return builder.toString();
    }


    public Date convertToDate(String moment) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return dateFormat.parse(moment);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    class User {
        private String nik ;

        public String toJson() {
            // return String.format( "{\"nik\": \"%s\"}", nik ) ;
            return new Gson().toJson( this ) ;
        }
        public User( String json ) throws JSONException {
            JSONObject obj = new JSONObject( json ) ;
            setNik( obj.getString( "nik" ) ) ;
        }

        public User() {
        }

        public String getNik() {
            return nik;
        }

        public void setNik( String nik ) {
            this.nik = nik;
        }
    }
}