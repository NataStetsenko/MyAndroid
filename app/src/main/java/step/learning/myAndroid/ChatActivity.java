package step.learning.myAndroid;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import step.learning.myAndroid.orm.ChatMessage;
import step.learning.myAndroid.orm.ChatResponse;

public class ChatActivity extends AppCompatActivity {
    private final String chatUrl = "https://chat.momentfor.fun/";
    private LinearLayout chatContainer;
    private final List<ChatMessage> chatMessages = new ArrayList<>();
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_chat );
        chatContainer = findViewById( R.id.chat_layout_container );

        new Thread( this::loadChatMessages ).start();
    }
    private void showChatMessages() {
        for( ChatMessage chatMessage : chatMessages ) {
            chatContainer.addView( chatMessageView( chatMessage ) );
        }
    }
    private View chatMessageView( ChatMessage chatMessage ) {
        LinearLayout messageContainer = new LinearLayout( ChatActivity.this );

        messageContainer.setOrientation( LinearLayout.VERTICAL );
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        containerParams.setMargins(8, 10, 8, 10);
        messageContainer.setLayoutParams( containerParams );
        if(flag){
            containerParams.gravity = Gravity.RIGHT;
            messageContainer.setBackgroundResource(R.drawable.message_left);
            flag = false;
        }else {
            messageContainer.setBackgroundResource(R.drawable.message_right);
            flag=true;
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
        tv.setText( chatMessage.getText() );
        tv.setTextAppearance(R.style.message);
        tv.setTypeface(typeface);
        messageContainer.addView( tv );

        tv = new TextView( ChatActivity.this );
        Date currentDate = new Date();
        Date date = convertToDate(chatMessage.getMoment());
        if (date != null && date.before(currentDate)) {
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
    /*
    Д.З. Створити дизайн для відображення повідомлень двох типів:
    "свої" - вирівнювання праворуч, свої кольори, рамка НЕ скруглена у правому-нижньому куті
    "інші" - по лівому краю, нескруглений кут лівий-нижній
    Додати виведення часу повідомлення (*) в інтелектуальній формі: якщо сьогодні то
    тільки час, якщо на день раніше - "учора", інакше повна дата-час
    Для випробування вивести різні стилі через один
     */

    private void loadChatMessages() {
        try( InputStream inputStream = new URL( chatUrl ).openStream() ) {
            ChatResponse chatResponse = ChatResponse.fromJsonString(
                    streamToString( inputStream ) );

            // Перевіряємо на нові повідомлення, оновлюємо (за потреби) колекцію chatMessages
            boolean wasNewMessages = false;
            for( ChatMessage message : chatResponse.getData() ) {
                if( chatMessages.stream().noneMatch( m -> m.getId().equals( message.getId() ) ) ) {
                    // це нове повідомлення (немає у колекції)
                    chatMessages.add( message );
                    wasNewMessages = true;
                }
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
}