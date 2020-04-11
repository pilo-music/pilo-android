package app.pilo.android.api;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;

import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Bookmark;
import app.pilo.android.repositories.UserRepo;

public class BookmarkApi {
    private Context context;

    public BookmarkApi(Context context) {
        this.context = context;
    }

    public void get(@Nullable HashMap<String, Object> params, final HttpHandler.RequestHandler requestHandler) {
        StringBuilder url = new StringBuilder(PiloApi.BOOKMARK_GET);
        if (params != null) {
            int index = 0;
            for (Map.Entry<String, Object> item : params.entrySet()) {
                if (index != 0) {
                    url.append("&").append(item.getKey()).append("=").append(item.getValue());
                } else {
                    url.append("?").append(item.getKey()).append("=").append(item.getValue());
                }
                index++;
            }
        }
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.toString(), null,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        boolean status = response.getBoolean("status");
                        String message = response.getString("message");
                        if (status) {
                            List<Bookmark> bookmarks = new ArrayList<>();
                            for (int i = 0; i < data.length(); i++) {
                                Bookmark bookmark = JsonParser.bookmarkParser(data.getJSONObject(i));
                                if (bookmark != null)
                                    bookmarks.add(bookmark);
                            }
                            requestHandler.onGetInfo(bookmarks, message, status);
                        } else
                            requestHandler.onGetInfo(null, message, status);
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
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }

    public void bookmark(String slug, String type, String action, final HttpHandler.RequestHandler requestHandler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("slug", slug);
            jsonObject.put("type", type);
            jsonObject.put("action", action);
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, PiloApi.ARTIST_GET, jsonObject,
                    response -> {
                        try {
                            boolean status = response.getBoolean("status");
                            String message = response.getString("message");
                            requestHandler.onGetInfo(null, message, status);
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
            request.setShouldCache(false);
            request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(context).add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
