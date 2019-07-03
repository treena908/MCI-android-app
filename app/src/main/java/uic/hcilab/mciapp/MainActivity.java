package uic.hcilab.mciapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Class[] activitiyArr = {AlbumActivity.class};
    List<Class> activitiyList = new ArrayList<Class>(Arrays.asList(activitiyArr));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

//        Collections.shuffle(activitiyList);
//        try {
//            Field f = activitiyList.get(0).getDeclaredField("NEXT_ACT");
//            f.setAccessible(true);
//            f.set(this, activitiyList.get(1));
//
//            f = activitiyList.get(1).getDeclaredField("NEXT_ACT");
//            f.setAccessible(true);
//            f.set(this, activitiyList.get(2));
//
//            f = activitiyList.get(2).getDeclaredField("NEXT_ACT");
//            f.setAccessible(true);
//            f.set(this, activitiyList.get(3));
//
//            f = activitiyList.get(3).getDeclaredField("NEXT_ACT");
//            f.setAccessible(true);
//            f.set(this, EndActivity.class);
//        }catch(NoSuchFieldException e)
//        {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }

//        String str = activitiyList.get(0).getName()+'\n';//+
//                activitiyList.get(1).getName()+'\n'+
//                activitiyList.get(2).getName()+'\n'+
//                activitiyList.get(3).getName()+'\n';
//        TextView order_tv = (TextView)findViewById(R.id.order_text);
//        order_tv.setText(str);
//
//        final Button button = (Button) findViewById(R.id.start_button);
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, activitiyList.get(0));
//                MainActivity.this.startActivity(intent);
//            }
//        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.albumAct:
                Intent intent = new Intent(this, AlbumActivity.class);
                this.startActivity(intent);
                break;
            case R.id.patternAct:
                intent = new Intent(this, PatternActivity.class);
                this.startActivity(intent);
                break;
            case R.id.handwritingAct:
                intent = new Intent(this, HandwritingActivity.class);
                this.startActivity(intent);
                break;
            case R.id.zoomAct:
                intent = new Intent(this, ZoomActivity.class);
                this.startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

}
