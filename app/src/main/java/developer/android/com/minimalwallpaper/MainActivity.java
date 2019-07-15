package developer.android.com.minimalwallpaper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private GridView gv;
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private int j = 0;

    private GestureDetector detector;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private int thumb[] = {
            R.drawable.img2, R.drawable.img3,
            R.drawable.img4, R.drawable.img5,
            R.drawable.img6, R.drawable.img7,
            R.drawable.img8, R.drawable.img1,
            R.drawable.img2, R.drawable.img3,
            R.drawable.img4, R.drawable.img5,
            R.drawable.img6, R.drawable.img7,
            R.drawable.img8, R.drawable.img1,
            R.drawable.img2, R.drawable.img3,
            R.drawable.img4, R.drawable.img5,
            R.drawable.img6, R.drawable.img7
    };
    private ImageView expandedImageVeiw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        detector = new GestureDetector(this, new SwipeGestureDetected());


        gv = (GridView) findViewById(R.id.grid_view);
        gv.setAdapter(new ImageAdapter(this));
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                j = pos;
                zoomImageFromThumb(v, thumb[pos]);

            }
        });
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);


    }
    class ImageAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;

        public ImageAdapter(MainActivity activity) {
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return thumb.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItem = convertView;
            int p = position;

            if (listItem == null) {
                listItem = layoutInflater.inflate(R.layout.single_grid_item, null);
            }
            ImageView iv = (ImageView) listItem.findViewById(R.id.thumb);

            iv.setBackgroundResource(thumb[p]);
            return listItem;
        }
    }
     private void zoomImageFromThumb(final View thumbView, int imageResId){

            if(mCurrentAnimator != null){
                mCurrentAnimator.cancel();
            }
            expandedImageVeiw = (ImageView) findViewById(R.id.expanded_image);
            expandedImageVeiw.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(detector.onTouchEvent(event)){
                        return true;
                    }
                    else
                        return false;
                }
            });


            expandedImageVeiw.setImageResource(imageResId);
            final Rect startBounds = new Rect();
            final Rect finalBounds = new Rect();
            final Point globalOffset = new Point();

            thumbView.getGlobalVisibleRect(startBounds);
            findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);

            startBounds.offset(-globalOffset.x, -globalOffset.y);
            finalBounds.offset(-globalOffset.x, -globalOffset.y);

            final float startScale;

            if((float)finalBounds.width() / finalBounds.height() > (float) startBounds.width() / startBounds.height()){
                startScale = (float) startBounds.height() / finalBounds.height();
                float startWidth = startScale * finalBounds.width();
                float deltaWidth = (startWidth - startBounds.width()) / 2;
                startBounds.left -= deltaWidth;
                startBounds.right += deltaWidth;
            }
            else {
                startScale = (float) startBounds.width() / finalBounds.width();
                float startHeight = startScale * finalBounds.height();
                float deltaHeight = (startHeight = startBounds.height()) / 2;
                startBounds.top -= deltaHeight;
                startBounds.bottom += deltaHeight;
            }
            thumbView.setAlpha(0f);
            expandedImageVeiw.setVisibility(View.VISIBLE);

            expandedImageVeiw.setPivotX(0f);
            expandedImageVeiw.setPivotY(0f);

            AnimatorSet animatorSet = new AnimatorSet();

            animatorSet.play(
                    ObjectAnimator.ofFloat(expandedImageVeiw, View.X, startBounds.left, finalBounds.left)).with(
                    ObjectAnimator.ofFloat(expandedImageVeiw, View.Y, startBounds.top, finalBounds.top)).with(
                    ObjectAnimator.ofFloat(expandedImageVeiw, View.X, startBounds.left, finalBounds.left)).with(
                    ObjectAnimator.ofFloat(expandedImageVeiw, View.SCALE_X, startScale, 1f)).with(
                    ObjectAnimator.ofFloat(expandedImageVeiw, View.SCALE_Y, startScale, 1f));

            animatorSet.setDuration(mShortAnimationDuration);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation){
                    mCurrentAnimator = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mCurrentAnimator = null;
                }
            });
            animatorSet.start();
            mCurrentAnimator = animatorSet;
            final float startScaleFinal = startScale;
            expandedImageVeiw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mCurrentAnimator != null){
                        mCurrentAnimator.cancel();
                    }
                    AnimatorSet set = new AnimatorSet();
                    set.play(
                            ObjectAnimator.ofFloat(expandedImageVeiw, View.X, startBounds.left))
                            .with(ObjectAnimator.ofFloat(expandedImageVeiw, View.Y, startBounds.top))
                            .with(ObjectAnimator.ofFloat(expandedImageVeiw, View.SCALE_X, startScaleFinal)).with(
                            ObjectAnimator.ofFloat(expandedImageVeiw, View.SCALE_Y, startScaleFinal)
                    );
                    set.setDuration(mShortAnimationDuration);
                    set.setInterpolator(new DecelerateInterpolator());
                    set.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation){
                            thumbView.setAlpha(1f);
                            expandedImageVeiw.setVisibility(View.GONE);
                            mCurrentAnimator = null;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            thumbView.setAlpha(1f);
                            expandedImageVeiw.setVisibility(View.GONE);
                            mCurrentAnimator = null;
                        }
                    });
                    set.start();
                    mCurrentAnimator = set;



                }
            });


        }
        private class SwipeGestureDetected extends GestureDetector.SimpleOnGestureListener{
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
                try{
                    if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY){
                        if (thumb.length > j){
                            j++;
                            if(j < thumb.length){
                                expandedImageVeiw.setImageResource(thumb[j]);
                                return true;
                            }
                            else {
                                j = 0;
                                expandedImageVeiw.setImageResource(thumb[j]);
                                return true;
                            }
                        }else if(e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY){
                            if(j > 0){
                                j--;
                                expandedImageVeiw.setImageResource(thumb[j]);
                                return true;
                            }
                            else {
                                j = thumb.length - 1;
                                expandedImageVeiw.setImageResource(thumb[j]);
                                return true;
                            }
                        }
                    }
                    }catch(Exception e){
                    e.printStackTrace();


                }
                return false;
            }
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save:{

            }
                return true;
            case R.id.download:{

            }
                return true;
            case R.id.share:{

            }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
