package cc.colorcat.vangogh;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void generic() throws Exception {
    }

    private static class TaskComparator implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            return o1.stableKey().compareTo(o2.stableKey());
        }
    }
}