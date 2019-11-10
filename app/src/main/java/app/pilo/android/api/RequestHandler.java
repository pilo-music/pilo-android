package app.pilo.android.api;

import androidx.annotation.Nullable;

import com.android.volley.error.VolleyError;

import java.util.List;

public interface RequestHandler{

    interface RequestHandlerWithStatus {
        void onGetInfo(String status);

        void onGetError(@Nullable VolleyError error);
    }

    interface RequestHandlerWithData {
        void onGetInfo(String status, String data);

        void onGetError(@Nullable VolleyError error);
    }

    interface RequestHandlerWithModel<T> {
        void onGetInfo(String status, T data);

        void onGetError(@Nullable VolleyError error);
    }

    interface RequestHandlerWithList<T> {
        void onGetInfo(String status, List<T> list);

        void onGetError(@Nullable VolleyError error);
    }
}