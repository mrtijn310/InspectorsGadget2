package glass.app.team11.helloglass2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.glass.app.Card;
import com.google.android.glass.content.Intents;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.io.File;

/**
 * An {@link Activity} showing a tuggable "Hello World!" card.
 * <p>
 * The main content view is composed of a one-card {@link CardScrollView} that provides tugging
 * feedback to the user when swipe gestures are detected.
 * If your Glassware intends to intercept swipe gestures, you should set the content view directly
 * and use a {@link com.google.android.glass.touchpad.GestureDetector}.
 * @see <a href="https://developers.google.com/glass/develop/gdk/touch">GDK Developer Guide</a>
 */



public class MainActivity extends Activity {

    /** {@link CardScrollView} to use as the main content view. */
    private CardScrollView mCardScroller;
    private GestureDetector mGestureDetector;
    private CameraView cameraView;



    /** "Hello World!" {@link View} generated by {@link #buildView()}. */
    private View mView;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        mView = buildView();
        CameraView cameraView = new CameraView(this);
        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(new CardScrollAdapter() {
            @Override
            public int getCount() {
                return 1;
            }
            @Override
            public Object getItem(int position) {
                return mView;
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return mView;
            }
            @Override
            public int getPosition(Object item) {
                if (mView.equals(item)) {
                    return 0;
                }
                return AdapterView.INVALID_POSITION;
            }
        });

        // Handle the TAP event.
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openOptionsMenu();
            }
        });

        mGestureDetector = createGestureDetector(this);
        setContentView(mCardScroller);
    }

    public void findDevelopers(String platform){
        Intent resultsIntent = new Intent(this, ResultsActivity.class);
        resultsIntent.putExtra(ResultsActivity.SEARCH, platform);
        startActivity(resultsIntent);
    }

    private static final int TAKE_PICTURE_REQUEST = 1;
    private static final int ACTION_VIDEO_CAPTURE = 2;

    public void takePicture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PICTURE_REQUEST);
    }

    public void recordVideo(){
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, ACTION_VIDEO_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
            String thumbnailPath = data.getStringExtra(Intents.EXTRA_THUMBNAIL_FILE_PATH);
            String picturePath = data.getStringExtra(Intents.EXTRA_PICTURE_FILE_PATH);

            processPictureWhenReady(picturePath);
            // TODO: Show the thumbnail to the user while the full picture is being
            // processed.
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void processPictureWhenReady(final String picturePath) {
        final File pictureFile = new File(picturePath);

        if (pictureFile.exists()) {
            // The picture is ready; process it.
        } else {
            // The file does not exist yet. Before starting the file observer, you
            // can update your UI to let the user know that the application is
            // waiting for the picture (for example, by displaying the thumbnail
            // image and a progress indicator).
//            ViewGroup vg = (ViewGroup)(cameraView.getParent());
//            vg.removeAllViews();
//            RelativeLayout layout = new RelativeLayout(this);
//            ProgressBar progressBar = new ProgressBar(this,null,android.R.attr.progressBarStyleLarge);
//            progressBar.setIndeterminate(true);
//            progressBar.setVisibility(View.VISIBLE);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
//                    , ViewGroup.LayoutParams.MATCH_PARENT);
//
//            params.addRule(RelativeLayout.CENTER_IN_PARENT);
//            layout.addView(progressBar,params);
//
//            setContentView(layout);
//

            final File parentDirectory = pictureFile.getParentFile();
            FileObserver observer = new FileObserver(parentDirectory.getPath(),
                    FileObserver.CLOSE_WRITE | FileObserver.MOVED_TO) {
                // Protect against additional pending events after CLOSE_WRITE
                // or MOVED_TO is handled.
                private boolean isFileWritten;

                @Override
                public void onEvent(int event, String path) {
                    if (!isFileWritten) {
                        // For safety, make sure that the file that was created in
                        // the directory is actually the one that we're expecting.
                        File affectedFile = new File(parentDirectory, path);
                        isFileWritten = affectedFile.equals(pictureFile);

                        if (isFileWritten) {
                            stopWatching();

                            // Now that the file is ready, recursively call
                            // processPictureWhenReady again (on the UI thread).
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    processPictureWhenReady(picturePath);
                                }
                            });
                        }
                    }
                    if (isFileWritten)
                    {


                    }

                }
            };
            observer.startWatching();
        }
    }






























//    @Override
//    protected void onActivityResult(int resultCode, Intent data)
//    {
//        // Handle photos
//
//            String picturePath = data.getStringExtra(CameraManager.EXTRA_PICTURE_FILE_PATH);
//            processPictureWhenReady(picturePath);
//
////        // Handle videos
////        if (requestCode == TAKE_VIDEO_REQUEST && resultCode == RESULT_OK)
////        {
////            String picturePath = data.getStringExtra(CameraManager.EXTRA_VIDEO_FILE_PATH);
////            processPictureWhenReady(picturePath);
////        }
//
//        super.onActivityResult(resultCode, data);
//    }
//
//    private void processPictureWhenReady(final String picturePath)
//    {
//        final File pictureFile = new File(picturePath);
//
//        if (pictureFile.exists())
//        {
//            Intent shareIntent = new Intent(this, BluetoothClient.class);
//            shareIntent.putExtra(SHARE_PICTURE, picturePath);
//            startActivity(shareIntent);
//            finish();
//        }
//        else
//        {
//            // The file does not exist yet. Before starting the file observer, you
//            // can update your UI to let the user know that the application is
//            // waiting for the picture (for example, by displaying the thumbnail
//            // image and a progress indicator).
//            ViewGroup vg = (ViewGroup)(cameraView.getParent());
//            vg.removeAllViews();
//            RelativeLayout layout = new RelativeLayout(this);
//            ProgressBar progressBar = new ProgressBar(this,null,android.R.attr.progressBarStyleLarge);
//            progressBar.setIndeterminate(true);
//            progressBar.setVisibility(View.VISIBLE);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
//                    , ViewGroup.LayoutParams.MATCH_PARENT);
//
//            params.addRule(RelativeLayout.CENTER_IN_PARENT);
//            layout.addView(progressBar,params);
//
//            setContentView(layout);
//
//            final File parentDirectory = pictureFile.getParentFile();
//
//            observer = new FileObserver(parentDirectory.getPath()) {
//                // Protect against additional pending events after CLOSE_WRITE is
//                // handled.
//                private boolean isFileWritten;
//
//                @Override
//                public void onEvent(int event, String path) {
//                    if (!isFileWritten) {
//                        // For safety, make sure that the file that was created in
//                        // the directory is actually the one that we're expecting.
//                        File affectedFile = new File(parentDirectory, path);
//
//                        isFileWritten = (event == FileObserver.CLOSE_WRITE
//                                && affectedFile.equals(pictureFile));
//
//                        if (isFileWritten) {
//                            stopWatching();
//
//                            // Now that the file is ready, recursively call
//                            // processPictureWhenReady again (on the UI thread).
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    processPictureWhenReady(picturePath);
//                                }
//                            });
//                        }
//                    }
//                }
//            };
//
//            observer.startWatching();
//        }
//    }






















    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {


        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS || featureId ==  Window.FEATURE_OPTIONS_PANEL) {
            switch (item.getItemId()) {
                case R.id.take_picture:
                    takePicture();
                    break;
                case R.id.record_video:
                    recordVideo();
                    break;
                case R.id.find_ios:
                    findDevelopers("iOS");
                    break;
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
    }

    @Override
    protected void onPause() {
        mCardScroller.deactivate();
        super.onPause();
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu){
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS || featureId ==  Window.FEATURE_OPTIONS_PANEL) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreatePanelMenu(featureId, menu);
    }

    /**
     * Builds a Glass styled "Hello World!" view using the {@link CardBuilder} class.
     */
    private View buildView() {
        Card card = new Card(this);
        card.setText(R.string.app_name);
        card.setImageLayout(Card.ImageLayout.LEFT);
        card.addImage(R.drawable.portofrotterdamlogo);
        return card.getView();
    }

    private GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);

        //Create a base listener for generic gestures
        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {
                    openOptionsMenu();
                    return true;
                } else if (gesture == Gesture.TWO_TAP) {
                    // do something on two finger tap
                    return true;
                } else if (gesture == Gesture.SWIPE_RIGHT) {
                    // do something on right (forward) swipe
                    return true;
                } else if (gesture == Gesture.SWIPE_LEFT) {
                    // do something on left (backwards) swipe
                    return true;
                } else if (gesture == Gesture.SWIPE_DOWN){
                    finish();
                }
                return false;
            }
        });

        gestureDetector.setFingerListener(new GestureDetector.FingerListener() {
            @Override
            public void onFingerCountChanged(int previousCount, int currentCount) {
                // do something on finger count changes
            }
        });

        gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
            @Override
            public boolean onScroll(float displacement, float delta, float velocity) {
                // do something on scrolling
                return true;
            }
        });

        return gestureDetector;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }
}