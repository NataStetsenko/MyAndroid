package step.learning.myAndroid;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class OnSwipeListener implements View.OnTouchListener {
    // 2. Використовуємо детектор жестів - не всю роботу беремо на себе
    private final GestureDetector gestureDetector;

    public OnSwipeListener(Context context) {
        // 3. Створюємо конструктор, що вимагає контекст (оточення
        //  активності застосунку)
        this.gestureDetector = new GestureDetector(context, new SwipeGestureListener());
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        // 1. Узагальнена подія - Touch: будь-яка взаємодія з екраном
        // задача - визначити, що це за взаємодія, якщо вона підпадає
        // під ознаки свайпу, то встановити його напрям
//        if(motionEvent.getAction()==MotionEvent.ACTION_UP){
//            return view.performClick();
//        }
        return gestureDetector.onTouchEvent(motionEvent);
    }

    // 4. Створюємо власний клас, який буде "розбирати" дані від жестів
    private final class SwipeGestureListener
            extends GestureDetector.SimpleOnGestureListener {
        // 6. Висока роздільна здатність екранів досить часто призводить
        //  до того, що навіть одиночне торкання екрану враховується як
        //  проведення (Fling) на невелику відстань (~ 1 мм). Тому для
        //  покращення взаємодії слід ввести граничні відстані (та швидкості)
        //  до яких жест не буде вважатись свайпом.
        private static final int MIN_DISTANCE = 100;
        private static final int MIN_VELOCITY = 100;
        //10. обробник, що зупеняє події
        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            return true;
        }

        @Override   // 5. Переозначуємо метод onFling
        public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;  // чи ми обробили подію
            float distanceX = e2.getX() - e1.getX();  // відстань проведення
            float distanceY = e2.getY() - e1.getY();  // по координатах
            // abs - модуль числа (без знака), визначаємо рух швидше
            // горизонтальний чи вертикальний (ідеальних не буває)
            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                // по Х (горизонталі) відстань більша - вважаємо це гориз. свайпом
                // обмеження перевіряємо тільки для осі Х
                if (Math.abs(distanceX) > MIN_DISTANCE &&
                        Math.abs(velocityX) > MIN_VELOCITY) {
                    result = true;
                    if (velocityX>0){ // e1...->...e2
                            onSwipeRight();
                    }
                    else {// e1...<-...e2
                        onSwipeLeft();
                    }
                }
            } else {
                // по Х (горизонталі) відстань менша - вважаємо це вертик. свайпом
                // обмеження перевіряємо тільки для осі Y
                if (Math.abs(distanceY) > MIN_DISTANCE &&
                        Math.abs(velocityY) > MIN_VELOCITY) {
                    result = true;
                    if (velocityY>0){   // e1
                        onSwipeBottom();//e2
                    }
                    else {              //e2
                        onSwipeTop();   // e1
                    }
                }
            }
            return result;
        }
    }
    //------- зовнішній інтерфейс--------
    public void onSwipeBottom(){}
    public void onSwipeLeft(){}
    public void onSwipeRight(){}
    public void onSwipeTop(){}
}
/*
Детектор жестів. Свайпи.
Сенсорний екран має свій набір подій, схожих на події миши,
але з відмінностями. Плюс ряд подій є практично вживаними,
але не реалізованими за замовчанням (свайпи - проведення
пальцем по екрану по прямій лінії: точка дотику - лінія -
точка відпускання)
 */
