package jo.dis.player.cache.event;

import jo.dis.player.BaseEvent;

/**
 * Created by diszhou on 2016/7/15.
 */
public class ProxyCacheExceptionEvent implements BaseEvent {

    private Throwable throwable;

    public ProxyCacheExceptionEvent(Throwable e) {
        this.throwable = e;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
