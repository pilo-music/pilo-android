package app.pilo.android.helpers;

import com.loopj.android.http.AsyncHttpClient;

public class Singleton {

    private static AsyncHttpClient ASYNC_HTTP_INSTANCE = new AsyncHttpClient();

    public static AsyncHttpClient getAsyncHttpInstance() {
        return(ASYNC_HTTP_INSTANCE);
    }

}
