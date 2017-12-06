package cc.colorcat.vangogh;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("cc.colorcat.vangogh.test", appContext.getPackageName());
    }

    @Test
    public void generic() throws Exception {
    }

    @Test
    public void clone_test() throws Exception {
    }

    private static class TaskComparator implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            return o1.stableKey().compareTo(o2.stableKey());
        }
    }
}
