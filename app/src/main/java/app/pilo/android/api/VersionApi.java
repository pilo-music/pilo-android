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

import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.repositories.UserRepo;

public class VersionApi {
    private Context context;

    public VersionApi(Context context) {
        this.context = context;
    }

    public void get(final HttpHandler.RequestHandler requestHandler) {
        JSONObject jsonObject = new JSONObject();
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, PiloApi.VERSION, jsonObject,
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
                        e.printStackTrace();
                        requestHandler.onGetError(null);
                    }
                }, requestHandler::onGetError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Accept", "application/json");
                params.put("Content-Language", new UserSharedPrefManager(context).getLocal());
                params.put("Authorization", "Bearer " + UserRepo.getInstance(context).get().getAccess_token());
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }
}
