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
import android.view.View;
import android.widget.Toast;

public class CarouselPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener{

    public final static float BIG_SCALE = 1.0f;
    public final static float SMALL_SCALE = 0.7f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
    private AlbumActivity context;
    private FragmentManager fragmentManager;
    private float scale;
    int previousState;
    int trialCount;
    int NUM_OF_TRIAL =5;
    public static int startPoint = 0;
    int endPoint = 9;

    public static int TaskOrder = 0;
    int taskCount = 0;


    public CarouselPagerAdapter(AlbumActivity context, FragmentManager fm) {
        super(fm);
        this.fragmentManager = fm;
        this.context = context;

        ShowAlert("(→) Flicking Left to Right");
        startPoint = 0;
        endPoint =9;
        ItemFragment.imageArray = new int[]{
                R.mipmap.f1,  R.mipmap.f2,  R.mipmap.f3,
                R.mipmap.f4,  R.mipmap.f5,  R.mipmap.f6,
                R.mipmap.f7,  R.mipmap.f8,  R.mipmap.f9,
                R.mipmap.f10};
        trialCount = 0;

        context.pager.setAdapter(context.adapter);
        context.pager.setCurrentItem(startPoint);

    }

    @Override
    public Fragment getItem(int position) {
        // make the first pager bigger than others
        try {
            if (position == AlbumActivity.FIRST_PAGE)
                scale = BIG_SCALE;
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
    }

    @Override
    public void onPageSelected(int position) {
        if(position==endPoint)
        {
            trialCount++;
            context.writeToSDFile("Finish");

            Toast.makeText(context, "Complete "+trialCount+"of "+NUM_OF_TRIAL , Toast.LENGTH_SHORT).show();
            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    context.pager.setCurrentItem(startPoint);
//                }
//            }, 800);

            ShowAlert("(←) Flicking Right to Left");


//            if(trialCount == NUM_OF_TRIAL)
//            {
//                if(TaskOrder==0) {
//                    ShowAlert("(←) Flicking Right to Left");
//                    ItemFragment.imageArray = new int[]{
//                            R.mipmap.f10, R.mipmap.f9, R.mipmap.f8,
//                            R.mipmap.f7, R.mipmap.f6, R.mipmap.f5,
//                            R.mipmap.f4, R.mipmap.f3, R.mipmap.f2,
//                            R.mipmap.f1};
//
//                    taskCount++;
//                    if(taskCount==1)
//                        TaskOrder =1;
//                    else if(taskCount==2)
//                    {
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                Intent intent = new Intent(context, AlbumActivity.NEXT_ACT);
//                                //intent.putExtra(“text”,String.valueOf(editText.getText()));
//                                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
//                            }
//                        }, 800);
//                    }
//
//                    startPoint = 9;
//                    endPoint = 0;
//                    trialCount = 0;
//                    context.pager.setAdapter(context.adapter);
//                    context.pager.setCurrentItem(startPoint);
//
//
//                }
//                else if(TaskOrder ==1)
//                {
//                    ShowAlert("(→) Flicking Left to Right");
//                    ItemFragment.imageArray = new int[]{
//                            R.mipmap.f1,  R.mipmap.f2,  R.mipmap.f3,
//                            R.mipmap.f4,  R.mipmap.f5,  R.mipmap.f6,
//                            R.mipmap.f7,  R.mipmap.f8,  R.mipmap.f9,
//                            R.mipmap.f10};
//
//                    taskCount++;
//                    if(taskCount==1)
//                        TaskOrder =0;
//                    else if(taskCount==2) {
//
//                        Intent intent = new Intent(context, AlbumActivity.NEXT_ACT);
//                        //intent.putExtra(“text”,String.valueOf(editText.getText()));
//                        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
//                    }
//                    startPoint = 0;
//                    endPoint =9;
//                    trialCount = 0;
//                    context.pager.setAdapter(context.adapter);
//                    context.pager.setCurrentItem(startPoint);
//
//                }
//            }
        }

        if(position == startPoint)
            ShowAlert("(→) Flicking Left to Right");
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (previousState == ViewPager.SCROLL_STATE_IDLE
                && state == ViewPager.SCROLL_STATE_DRAGGING)
            context.writeToSDFile("Touch");
        else if (previousState == ViewPager.SCROLL_STATE_DRAGGING
                && state == ViewPager.SCROLL_STATE_SETTLING)
            context.writeToSDFile("Release");

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
        alertDialog.setTitle("Alert");
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