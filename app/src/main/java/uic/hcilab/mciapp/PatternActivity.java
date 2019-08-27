package uic.hcilab.mciapp;

import com.reginald.patternlockview.PatternLockView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PatternActivity extends AppCompatActivity {

    private static final String TAG = "DemoActivity";

    static PatternLockView.Password pwd1_1 = new PatternLockView.Password(Arrays.asList(0, 1, 2));
    static PatternLockView.Password pwd1_2 = new PatternLockView.Password(Arrays.asList(3, 4, 6, 7, 8));
    static PatternLockView.Password pwd1_3 = new PatternLockView.Password(Arrays.asList(4, 1, 2, 5, 7, 8));
    static PatternLockView.Password pwd1_4 = new PatternLockView.Password(Arrays.asList(4, 7, 8, 5));
    static PatternLockView.Password pwd1_5 = new PatternLockView.Password(Arrays.asList(2, 4, 6, 7, 8));
    static PatternLockView.Password pwd1_6 = new PatternLockView.Password(Arrays.asList(0, 1, 2, 5, 4));
    static PatternLockView.Password pwd1_7 = new PatternLockView.Password(Arrays.asList(2, 5, 8, 7, 6));
    static PatternLockView.Password pwd1_8 = new PatternLockView.Password(Arrays.asList(4, 3, 6, 7));
    static PatternLockView.Password pwd1_9 = new PatternLockView.Password(Arrays.asList(4, 8, 7, 5, 2));
    static PatternLockView.Password pwd1_10 = new PatternLockView.Password(Arrays.asList(3, 4, 5));

    //////////////////////////////////////////////////////
    static PatternLockView.Password pwd2_1 = new PatternLockView.Password(Arrays.asList(0, 1, 4));
    static PatternLockView.Password pwd2_2 = new PatternLockView.Password(Arrays.asList(3, 4, 7, 8));
    static PatternLockView.Password pwd2_3 = new PatternLockView.Password(Arrays.asList(4, 3, 0, 1, 2, 5));
    static PatternLockView.Password pwd2_4 = new PatternLockView.Password(Arrays.asList(4, 7, 8, 5));
    static PatternLockView.Password pwd2_5 = new PatternLockView.Password(Arrays.asList(2, 4, 6, 7, 8));
    static PatternLockView.Password pwd2_6 = new PatternLockView.Password(Arrays.asList(0, 1, 2, 5, 4));
    static PatternLockView.Password pwd2_7 = new PatternLockView.Password(Arrays.asList(2, 5, 8, 7, 6));
    static PatternLockView.Password pwd2_8 = new PatternLockView.Password(Arrays.asList(4, 3, 6, 7));
    static PatternLockView.Password pwd2_9 = new PatternLockView.Password(Arrays.asList(4, 8, 7, 5, 2));
    static PatternLockView.Password pwd2_10 = new PatternLockView.Password(Arrays.asList(3, 4, 5));

    ///////////////////////////////////////////////////////
    static PatternLockView.Password pwd3_1 = new PatternLockView.Password(Arrays.asList(0, 1, 2));
    static PatternLockView.Password pwd3_2 = new PatternLockView.Password(Arrays.asList(3, 4, 6, 7, 8));
    static PatternLockView.Password pwd3_3 = new PatternLockView.Password(Arrays.asList(4, 1, 2, 5, 7, 8));
    static PatternLockView.Password pwd3_4 = new PatternLockView.Password(Arrays.asList(4, 7, 8, 5));
    static PatternLockView.Password pwd3_5 = new PatternLockView.Password(Arrays.asList(2, 4, 6, 7, 8));
    static PatternLockView.Password pwd3_6 = new PatternLockView.Password(Arrays.asList(0, 1, 2, 5, 4));
    static PatternLockView.Password pwd3_7 = new PatternLockView.Password(Arrays.asList(2, 5, 8, 7, 6));
    static PatternLockView.Password pwd3_8 = new PatternLockView.Password(Arrays.asList(4, 3, 6, 7));
    static PatternLockView.Password pwd3_9 = new PatternLockView.Password(Arrays.asList(4, 8, 7, 5, 2));
    static PatternLockView.Password pwd3_10 = new PatternLockView.Password(Arrays.asList(6, 7, 8));

    List<PatternLockView.Password> pwdList1 = new ArrayList<PatternLockView.Password>();
    List<PatternLockView.Password> pwdList2 = new ArrayList<PatternLockView.Password>();
    List<PatternLockView.Password> pwdList3 = new ArrayList<PatternLockView.Password>();

    List<List<PatternLockView.Password>> pwdListList = new ArrayList<List<PatternLockView.Password>>();

    private PatternLockView mCurLockView;
    private PatternLockView mCircleLockView;
    private PatternLockView.Password mPassword;

    int taskCount = 0;
    int trialCount = 0;
    int NUM_OF_TRIAL =3;

    WriteSDcard wr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern);

        pwdList1.add(pwd1_1);
        pwdList1.add(pwd1_2);
        pwdList1.add(pwd1_3);
        pwdList1.add(pwd1_4);
        pwdList1.add(pwd1_5);
        pwdList1.add(pwd1_6);
        pwdList1.add(pwd1_7);
        pwdList1.add(pwd1_8);
        pwdList1.add(pwd1_9);
        pwdList1.add(pwd1_10);

        pwdList2.add(pwd2_1);
        pwdList2.add(pwd2_2);
        pwdList2.add(pwd2_3);
        pwdList2.add(pwd2_4);
        pwdList2.add(pwd2_5);
        pwdList2.add(pwd2_6);
        pwdList2.add(pwd2_7);
        pwdList2.add(pwd2_8);
        pwdList2.add(pwd2_9);
        pwdList2.add(pwd2_10);

        pwdList3.add(pwd3_1);
        pwdList3.add(pwd3_2);
        pwdList3.add(pwd3_3);
        pwdList3.add(pwd3_4);
        pwdList3.add(pwd3_5);
        pwdList3.add(pwd3_6);
        pwdList3.add(pwd3_7);
        pwdList3.add(pwd3_8);
        pwdList3.add(pwd3_9);
        pwdList3.add(pwd3_10);

        pwdListList.add(pwdList1);
        pwdListList.add(pwdList2);
        pwdListList.add(pwdList3);

        mPassword = pwdListList.get(trialCount).get(taskCount);
        mCircleLockView = (PatternLockView) findViewById(R.id.lock_view_circle);
        mCurLockView = mCircleLockView;
        mCurLockView.setPatternVisible(true);

        wr = new WriteSDcard();
        switchLockViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mCurLockView.showPassword(mPassword.list);
        mCurLockView.showPasswordGuideline(mPassword.list);
        //mPasswordTextView.setText("show password: " + mPassword.string);
    }

    private void switchLockViews() {
        mCurLockView.setVisibility(View.VISIBLE);
        //mPassword = pwdList1.get(0);
        //mCurLockView = mCurLockView == mCircleLockView ? mDotLockView : mCircleLockView;
        /*
        // config the PatternLockView with code

        // set size
        mCurLockView.setSize(5);
        // set finish timeout, if 0, reset the lock view immediately after user input one password
        mCircleLockView.setFinishTimeout(2000);
        // set whether user can interrupt the finish timeout.
        // if true, the lock view will be reset when user touch a new node.
        // if false, the lock view will be reset only when the finish timeout expires
        mCircleLockView.setFinishInterruptable(false);
        // set whether the lock view can auto link the nodes in the path of two selected nodes
        mCircleLockView.setAutoLinkEnabled(false);
        */

        mCurLockView.reset();
        mCurLockView.showPasswordGuideline(mPassword.list);

        mCurLockView.setCallBack(new PatternLockView.CallBack() {
            @Override
            public int onFinish(PatternLockView.Password password) {
                if(taskCount < pwdListList.get(trialCount).size()-1)
                    taskCount++;
                else{
                        trialCount++;
                        taskCount = 0;
                }

                if(trialCount > NUM_OF_TRIAL-1)
                {
                    Intent intent = new Intent(PatternActivity.this, AfterPatternActivity.class);
                    PatternActivity.this.startActivity(intent);
                }

                mPassword = pwdListList.get(trialCount).get(taskCount);
                mCurLockView.reset();
                mCurLockView.showPasswordGuideline(mPassword.list);


                Log.d(TAG, "password length " + password.list.size());
                if (password.string.length() != 0) {
//                    mPasswordTextView.setText("password is " + password.string);

                } else {
//                    mPasswordTextView.setText("please enter your password!");

                }



                ((TextView)findViewById(R.id.pattern_count)).setText(10 - taskCount + " out of 10 left.");

                if (mPassword.equals(password)) {
                    return PatternLockView.CODE_PASSWORD_CORRECT;
                } else {
                    mPassword = password;
                    return PatternLockView.CODE_PASSWORD_ERROR;
                }

            }
        });

        mCurLockView.setOnNodeTouchListener(new PatternLockView.OnNodeTouchListener() {
            @Override
            public void onNodeTouched(int NodeId) {
                //mCurLockView.showPasswordGuideline(mPassword.list);
                Log.d(TAG, "node " + NodeId + " has touched!");
            }
            });
//        mCurLockView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int action = event.getActionMasked();
//                int index = event.getActionIndex();
//                int pointerId = event.getPointerId(index);
//
//                switch(action) {
//                    case MotionEvent.ACTION_DOWN:
//                        wr.writeToSDFile(PatternActivity.super.getBaseContext(),"Touch_down"+event.getX() + ","+event.getY());
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        wr.writeToSDFile(PatternActivity.super.getBaseContext(),"Touch_move"+event.getX() + ","+event.getY());
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        wr.writeToSDFile(PatternActivity.super.getBaseContext(),"Touch_up"+event.getX() + ","+event.getY());
//                        break;
//                }
//                return true;
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // DO NOT FORGET TO CALL IT!
        //mCurLockView.stopPasswordAnim();
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
            case R.id.zoomAct:
                intent = new Intent(this, ZoomActivity.class);
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
            case R.id.keyboardAct:
                intent = new Intent(this, KeyboardActivity.class);
                this.startActivity(intent);
                break;
            case R.id.speechAct:
                intent = new Intent(this, SpeechActivity.class);
                this.startActivity(intent);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
