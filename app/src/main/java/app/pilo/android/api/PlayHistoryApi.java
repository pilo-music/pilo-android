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

public class PlayHistoryApi {
    private Context context;

    public PlayHistoryApi(Context context) {
        this.context = context;
    }


    public void add(String slug, String type) {
        if (slug.equals("")) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("slug", slug);
                jsonObject.put("type", type);
                final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, PiloApi.PLAY_HISTORY, jsonObject,
                        response -> {
                        }, null) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<>();
                        params.put("Accept", "application/json");
                        params.put("Content-Language", new UserSharedPrefManager(context).getLocal());
                        params.put("Authorization", "Bearer " + UserRepo.getInstance(context).get().getAccess_token());
                        return params;
                    }
                };
                request.setShouldCache(false);
                request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(context).add(request);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
