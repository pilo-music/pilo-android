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
import app.pilo.android.models.SingleAlbum;
import app.pilo.android.models.SinglePlaylist;
import app.pilo.android.repositories.UserRepo;

public class PlaylistApi {
    private Context context;

    public PlaylistApi(Context context) {
        this.context = context;
    }

    public void get() {

    }

    public void single(String slug, final HttpHandler.RequestHandler requestHandler) {
        StringBuilder url = new StringBuilder(PiloApi.PLAYLIST_SINGLE);
        url.append("?").append("slug").append("=").append(slug);
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.toString(), null,
                response -> {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        boolean status = response.getBoolean("status");
                        String message = response.getString("message");
                        if (status) {
                            SinglePlaylist singlePlaylist = JsonParser.singlePlaylistParser(data);
                            requestHandler.onGetInfo(singlePlaylist, message, status);
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

    public void add() {

    }

    public void edit() {

    }

    public void delete() {

    }

    public void musics() {

    }

}
