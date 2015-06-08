package net.techguild.base.data.event;

public class NetworkErrorEvent {
    public boolean permanent;

    public NetworkErrorEvent(boolean permanent) {
        this.permanent = permanent;
    }
}
