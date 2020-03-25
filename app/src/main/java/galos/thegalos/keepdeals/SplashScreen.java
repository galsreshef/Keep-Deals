package galos.thegalos.keepdeals;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        Thread splash_screen = new Thread() {
            public void run() {
                try {
                    sleep(2500);
                } catch(Exception e) {
                    e.printStackTrace();
                } finally {
                    Intent main_menu = new Intent(SplashScreen.this,MainMenu.class);
                    startActivity(main_menu);
                }
            }
        };
        splash_screen.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}

