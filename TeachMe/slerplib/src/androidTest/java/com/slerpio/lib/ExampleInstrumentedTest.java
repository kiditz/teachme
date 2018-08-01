package com.slerpio.lib;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import android.util.Log;
import com.slerpio.lib.core.Domain;
import com.slerpio.lib.messaging.Stomp;
import com.slerpio.lib.messaging.Subscription;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private static final String TAG = ExampleInstrumentedTest.class.getName();

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        Stomp stomp = new Stomp(appContext, "ws://192.168.1.126:5004/messaging", new Subscription.ListenerWSNetwork() {
            @Override
            public void onState(int state) {
                Log.d(TAG, "onState: " + state);
            }
        });
        stomp.connect();
        stomp.subscribe(new Subscription("/material_comment/get_comment/51/0/10", new Subscription.ListenerSubscriptionAdapter() {

            @Override
            public void onMessage(Map<String, String> headers, Domain body) {
                Log.d(TAG, "onMessage: " + body);
                assertNotNull(body);
                assertNotEquals(body.isEmpty(), false);
            }
        }));
        assertEquals("com.slerpio.lib.test", appContext.getPackageName());
    }
}
