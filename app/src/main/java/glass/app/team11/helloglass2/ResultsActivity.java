package glass.app.team11.helloglass2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.glass.app.Card;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An {@link Activity} showing a tuggable "Hello World!" card.
 * <p>
 * The main content view is composed of a one-card {@link CardScrollView} that provides tugging
 * feedback to the user when swipe gestures are detected.
 * If your Glassware intends to intercept swipe gestures, you should set the content view directly
 * and use a {@link com.google.android.glass.touchpad.GestureDetector}.
 * @see <a href="https://developers.google.com/glass/develop/gdk/touch">GDK Developer Guide</a>
 */
public class ResultsActivity extends Activity {
    public static final String UNNumber = "1234";
    public static final String UNDescription = "description";
    public HashMap UNMap = new HashMap();


    private String mPlatform="Android";


    private List<Card> mCards;
    private GestureDetector mGestureDetector;
    /** {@link CardScrollView} to use as the main content view. */
    private CardScrollView mCardScroller;

    /** "Hello World!" {@link View} generated by {@link #buildView()}. */
    private View mView;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        mCardScroller = new CardScrollView(this);
        mCards = new ArrayList<Card>();

        UNMap.put("1428", "Sodium");
        UNMap.put("2734", "Amines, liquid, corrosive, flammable, n.o.s. or Polyamines, liquid, corrosive, flammable, n.o.s.");
        UNMap.put("2796", "Battery fluid, acid or Sulfuric acid with not more than 51 percent acid");
        UNMap.put("3077", "Environmentally hazardous substance, solid, n.o.s. (not including waste)");
        UNMap.put("3166", "Vehicle, flammable gas powered");

        UNMap.put("3163", "Liquefied gas, n.o.s.");
        UNMap.put("3174", "Titanium disulphide");
        UNMap.put("1713", "Zinc cyanide");

        if(getIntent().hasExtra(UNNumber)){
            mPlatform = getIntent().getStringExtra(UNNumber);
        }

        findCode(mPlatform);
        mCardScroller.setAdapter(new DeveloperAdapter(mCards));

        // Handle the TAP event.
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openOptionsMenu();
            }
        });

//        android.view.GestureDetector mGestureDetector = createGestureDetector(this);
        setContentView(mCardScroller);
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu){
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS || featureId ==  Window.FEATURE_OPTIONS_PANEL) {
            getMenuInflater().inflate(R.menu.developer, menu);
            return true;
        }
        return super.onCreatePanelMenu(featureId, menu);
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

    /**
     * Builds a Glass styled "Hello World!" view using the {@link CardBuilder} class.
     */
    private View buildView() {
        CardBuilder card = new CardBuilder(this, CardBuilder.Layout.TEXT);

        card.setText(R.string.hello_world);
        return card.getView();
    }

    private void findCode(String code){
//        for (int i=1; i<=10; i++){
        Card card1 = new Card(this);
        String text1 = "UN number: " + code;
        card1.setText(text1);
        card1.setTimestamp("time");

        Card card2 = new Card(this);
        String text2 = "Name"  + "\n";
        text2 += UNMap.get(code).toString();
        card2.setText(text2);

        mCards.add(card1);
        mCards.add(card2);

        mCardScroller.setSelection(0);
    }

    private com.google.android.glass.touchpad.GestureDetector createGestureDetector(Context context) {
        com.google.android.glass.touchpad.GestureDetector gestureDetector = new com.google.android.glass.touchpad.GestureDetector(context);

        //Create a base listener for generic gestures
        gestureDetector.setBaseListener( new com.google.android.glass.touchpad.GestureDetector.BaseListener() {
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

        gestureDetector.setFingerListener(new com.google.android.glass.touchpad.GestureDetector.FingerListener() {
            @Override
            public void onFingerCountChanged(int previousCount, int currentCount) {
                // do something on finger count changes
            }
        });

        gestureDetector.setScrollListener(new com.google.android.glass.touchpad.GestureDetector.ScrollListener() {
            @Override
            public boolean onScroll(float displacement, float delta, float velocity) {
                // do something on scrolling
                return true;
            }
        });

        return gestureDetector;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS || featureId ==  Window.FEATURE_OPTIONS_PANEL) {
            switch (item.getItemId()) {
               case R.id.developer_hire:
                    Toast.makeText(getApplicationContext(), "Message", Toast.LENGTH_LONG).show();
                    break;
                case R.id.go_back:
                    break;
                case R.id.c_0001:
                    Toast.makeText(getApplicationContext(), "0001", Toast.LENGTH_LONG).show();
                    break;
                case R.id.c_0002:
                    Toast.makeText(getApplicationContext(), "0002", Toast.LENGTH_LONG).show();
                    break;
                case R.id.c_0003:
                    Toast.makeText(getApplicationContext(), "0003", Toast.LENGTH_LONG).show();
                    break;
//                case R.id.developer_hire:
//                    Toast.makeText(getApplicationContext(), "Message", Toast.LENGTH_LONG).show();
//                    break;
//
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

}
