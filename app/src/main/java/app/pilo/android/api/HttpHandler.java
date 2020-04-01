package app.pilo.android.api;

import com.android.volley.error.VolleyError;

import java.util.List;

import androidx.annotation.Nullable;

public interface HttpHandler {
    interface RequestHandlerWithData {
        void onGetInfo(Object data, String message, boolean status);

        void onGetError(@Nullable VolleyError error);
    }
}