package bot.eightball.core.general;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class IShop {

    public interface Schedulable {
        ScheduledFuture<?> schedule(ScheduledExecutorService ses);
    }

}
