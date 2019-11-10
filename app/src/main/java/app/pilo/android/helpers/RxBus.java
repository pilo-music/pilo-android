package app.pilo.android.helpers;

import io.reactivex.subjects.BehaviorSubject;

public class RxBus {
    private static final BehaviorSubject<Object> behaviorSubject
            = BehaviorSubject.create();


    public static BehaviorSubject<Object> getSubject() {
        return behaviorSubject;
    }
}
