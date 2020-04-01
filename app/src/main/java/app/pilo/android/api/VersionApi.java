package app.pilo.android.api;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VersionApi {
    private Context context;

    public VersionApi(Context context) {
        this.context = context;
    }

    public void version(final HttpHandler.RequestHandlerWithData requestHandler) {
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, PiloApi.VERSION, null,
                response -> {
                    try {
                        boolean status = response.getBoolean("status");
                        String message = response.getString("message");
                        JSONObject data = response.getJSONObject("data");
                        Map<String, Object> version = new HashMap<>();
                        if (status) {
                            version.put("version", data.getInt("version"));
                            version.put("min_version", data.getInt("min_version"));
                            version.put("update_title", data.getString("update_title"));
                            version.put("update_description", data.getString("update_description"));
                            version.put("update_link", data.getString("update_link"));
                        }
                        requestHandler.onGetInfo(version, message, status);
                    } catch (JSONException e) {
                        requestHandler.onGetError(null);
                    }
                }, requestHandler::onGetError);
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }
}
