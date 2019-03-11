package mike.sucks.gameengine19;

import mike.sucks.gameengine19.Pool;
import mike.sucks.gameengine19.TouchEvent;

public class TouchEventPool extends Pool<TouchEvent> {

    @Override
    protected TouchEvent newItem() {
        return new TouchEvent();
    }
}
