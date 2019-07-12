package uic.hcilab.mciapp;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CarouselPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener{

    public final static float BIG_SCALE = 1.0f;
    public final static float SMALL_SCALE = 0.7f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
    private AlbumActivity context;
    private FragmentManager fragmentManager;
    private float scale;
    int previousState;

    public static int startPoint = 0;
    public static int endPoint = 10;
    private VelocityTracker mVelocityTracker = null;
    int trialCount;
    int NUM_OF_TRIAL =3;

    int taskCount = 0;

    TextView album_text;
    WriteSDcard wr = new WriteSDcard();


    public CarouselPagerAdapter(AlbumActivity context, FragmentManager fm) {
        super(fm);
        this.fragmentManager = fm;
        this.context = context;

        album_text = (TextView) context.findViewById(R.id.album_text);

        ShowAlert("(→) Please browse all images.");
        album_text.setText("(→) Please browse all images.");

        context.pager.setAdapter(context.adapter);
        context.pager.setCurrentItem(startPoint);



    }


    @Override
    public Fragment getItem(int position) {
        // make the first pager bigger than others
        try {
            if (position == AlbumActivity.FIRST_PAGE)
                scale = SMALL_SCALE;//BIG_SCALE;
            else
                scale = SMALL_SCALE;

            position = position % AlbumActivity.count;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ItemFragment.newInstance(context, position, scale);
    }

    @Override
    public int getCount() {

        int count = 0;
        try {
            count = AlbumActivity.count * AlbumActivity.LOOPS;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        try {
            if (positionOffset >= 0f && positionOffset <= 1f) {
                CarouselLinearLayout cur = getRootView(position);
                CarouselLinearLayout next = getRootView(position + 1);

                cur.setScaleBoth(BIG_SCALE - DIFF_SCALE * positionOffset);
                next.setScaleBoth(SMALL_SCALE + DIFF_SCALE * positionOffset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Log.i("mystr", positionOffset+"");
    }

    @Override
    public void onPageSelected(int position) {
        if(position == endPoint && taskCount ==0) {
            ShowAlert("(←) Please go back to the first image.");
            album_text.setText("(←) Please go back to the first image.");
            taskCount= 1;
        }
        if(position ==startPoint && taskCount ==1)
        {
            if(trialCount == 2) {
                Intent intent = new Intent(context, AfterAlbumActivity.class);
                context.startActivity(intent);
            }
            else {
                trialCount++;
                taskCount = 0;
                ShowAlert("(→) Please browse all images.");
                album_text.setText("(→) Please browse all images again.");
            }

        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (previousState == ViewPager.SCROLL_STATE_IDLE
                && state == ViewPager.SCROLL_STATE_DRAGGING)
            wr.writeToSDFile(context,"Touch");
        else if (previousState == ViewPager.SCROLL_STATE_DRAGGING
                && state == ViewPager.SCROLL_STATE_SETTLING)
            wr.writeToSDFile(context,"Release");

        previousState = state;

    }


    @SuppressWarnings("ConstantConditions")
    private CarouselLinearLayout getRootView(int position) {

        return (CarouselLinearLayout) fragmentManager.findFragmentByTag(this.getFragmentTag(position))
                .getView().findViewById(R.id.root_container);

    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + context.pager.getId() + ":" + position;
    }


    public void ShowAlert(String str) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("");
        alertDialog.setMessage(str);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}