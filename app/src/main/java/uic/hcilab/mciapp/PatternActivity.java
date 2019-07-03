package uic.hcilab.mciapp;

import com.reginald.patternlockview.PatternLockView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PatternActivity extends Activity {

    private static final String TAG = "DemoActivity";

    static PatternLockView.Password pwd1 = new PatternLockView.Password(Arrays.asList(0, 1, 2, 3, 4));
    static PatternLockView.Password pwd2 = new PatternLockView.Password(Arrays.asList(0, 1, 4, 3, 8));

    List<PatternLockView.Password> pwdList = new ArrayList<PatternLockView.Password>();

    private PatternLockView mCurLockView;

    private PatternLockView mCircleLockView;

    //private PatternLockView mDotLockView;

    private TextView mPasswordTextView;

    private Button mPatternShowButton;

    //private Button mPatternShowAnimButton;

    private PatternLockView.Password mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern);

        pwdList.add(pwd1);
        pwdList.add(pwd2);
        mPassword = pwdList.get(0);

        mCircleLockView = (PatternLockView) findViewById(R.id.lock_view_circle);
        mCurLockView = mCircleLockView;
        mPasswordTextView = (TextView) findViewById(R.id.password_text);
        mCurLockView.setPatternVisible(true);


//        mSwitchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switchLockViews();
//            }
//        });

//        mPasswordTextView.setText("please enter your password!");

        mPatternShowButton = (Button) findViewById(R.id.show_password_button);
        mPatternShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurLockView.showPasswordGuideline(mPassword.list);
                mPasswordTextView.setText("show password: " + mPassword.string);
            }
        });

//        mPatternShowAnimButton = (Button) findViewById(R.id.switch_show_anim);
//        mPatternShowAnimButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mCurLockView.isPasswordAnim()) {
//                    mCurLockView.stopPasswordAnim();
//                } else {
//                    mPatternShowAnimButton.setText("stop password anim");
//                    mCurLockView.showPasswordWithAnim(mPassword.list, -1, 400l,
//                            new PatternLockView.onAnimFinishListener() {
//                                @Override
//                                public void onFinish(boolean isStopped) {
//                                    mPatternShowAnimButton.setText("start password anim");
//                                }
//                            });
//                    mPasswordTextView.setText("show password animation: " + mPassword.string);
//                }
//            }
//        });

        switchLockViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mCurLockView.showPassword(mPassword.list);
        mCurLockView.showPasswordGuideline(mPassword.list);
        mPasswordTextView.setText("show password: " + mPassword.string);
    }

    private void switchLockViews() {
        mPassword = pwdList.get(0);
        //mCurLockView.stopPasswordAnim();

        //mCurLockView = mCurLockView == mCircleLockView ? mDotLockView : mCircleLockView;
        mCurLockView.setVisibility(View.VISIBLE);

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
//        if (mCurLockView != mCircleLockView) {
//            mCircleLockView.setVisibility(View.GONE);
//            mCircleLockView.setCallBack(null);
//            mCircleLockView.setOnNodeTouchListener(null);
//            mSwitchButton.setText("switch to circle lock view");
//        } else {
//            mDotLockView.setVisibility(View.GONE);
//            mDotLockView.setCallBack(null);
//            mDotLockView.setOnNodeTouchListener(null);
//            mSwitchButton.setText("switch to dot lock view");
//        }

        mCurLockView.setCallBack(new PatternLockView.CallBack() {
            @Override
            public int onFinish(PatternLockView.Password password) {
                mCurLockView.showPasswordGuideline(mPassword.list);
                Log.d(TAG, "password length " + password.list.size());
                if (password.string.length() != 0) {
//                    mPasswordTextView.setText("password is " + password.string);
                } else {
//                    mPasswordTextView.setText("please enter your password!");

                }

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
