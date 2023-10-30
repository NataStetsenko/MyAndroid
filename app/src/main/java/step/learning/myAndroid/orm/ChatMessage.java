package step.learning.myAndroid.orm;


import android.view.View;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessage {
    private String id;

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

   transient private View view;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public Date getDate(){
        try {
            return dateFormat.parse(getMoment());
        }catch (Exception ignored){
            return null;
        }

    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMoment() {
        return moment;
    }

    public void setMoment(String moment) {
        this.moment = moment;
    }

    private String author;
    private String text;
    private String moment;

    public static ChatMessage fromJson( JSONObject jsonObject ) {
        ChatMessage chatMessage = new ChatMessage() ;
        try {
            for( Field field : ChatMessage.class.getDeclaredFields() ) {
                if( jsonObject.has( field.getName() ) ) {
                    field.set(
                            chatMessage,
                            jsonObject.getString( field.getName() )
                    ) ;
                }
            }
        }
        catch( Exception ignored ) { }
        return chatMessage ;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }


}
