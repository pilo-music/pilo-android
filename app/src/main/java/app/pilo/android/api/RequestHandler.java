package app.pilo.android.api;

import java.util.List;

public interface RequestHandler{

    interface RequestHandlerWithStatus {
        void onGetInfo(String status);

        void onGetError();
    }

    interface RequestHandlerWithData {
        void onGetInfo(String status, String data);

        void onGetError();
    }

    interface RequestHandlerWithModel<T> {
        void onGetInfo(String status, T data);

        void onGetError();
    }

    interface RequestHandlerWithList<T> {
        void onGetInfo(String status, List<T> list);

        void onGetError();
    }
}