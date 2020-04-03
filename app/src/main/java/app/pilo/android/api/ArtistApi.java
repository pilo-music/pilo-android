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
import app.pilo.android.models.Album;
import app.pilo.android.models.Artist;
import app.pilo.android.models.SingleAlbum;
import app.pilo.android.models.SingleArtist;
import app.pilo.android.repositories.UserRepo;

public class ArtistApi {

    private Context context;

    public ArtistApi(Context context) {
        this.context = context;
    }


    public void get(@Nullable HashMap<String, Object> params, final HttpHandler.RequestHandler requestHandler) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (params != null) {
                for (Map.Entry<String, Object> item : params.entrySet()) {
                    jsonObject.put(item.getKey(), item.getValue());
                }
            }
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, PiloApi.ARTIST_GET, jsonObject,
                    response -> {
                        try {
                            JSONArray data = response.getJSONArray("data");
                            boolean status = response.getBoolean("status");
                            String message = response.getString("message");
                            if (status) {
                                List<Artist> artists = new ArrayList<>();
                                for (int i = 0; i < data.length(); i++) {
                                    Artist artist = JsonParser.artistParser(data.getJSONObject(i));
                                    if (artist != null)
                                        artists.add(artist);
                                }
                                requestHandler.onGetInfo(artists, message, status);
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
            request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(context).add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void single(String slug, final HttpHandler.RequestHandler requestHandler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("slug", slug);
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, PiloApi.ARTIST_SINGLE, jsonObject,
                    response -> {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            boolean status = response.getBoolean("status");
                            String message = response.getString("message");
                            if (status) {
                                SingleArtist singleAlbum = JsonParser.singleArtistParser(data);
                                requestHandler.onGetInfo(singleAlbum, message, status);
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
            request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(context).add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
