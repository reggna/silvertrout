package silvertrout.commons;

import java.util.UUID;

/**
 * @author Jonas "Jaif" Färdig
 *
 */
public interface Callback {
    /**
     *
     * @param id Unique id that can identify this callback
     * @param args Optional arguments to the callback method
     */
    public void callback(UUID id, String[] args);
}
