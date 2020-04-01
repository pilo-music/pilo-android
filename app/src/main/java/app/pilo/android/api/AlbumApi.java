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

import app.pilo.android.models.Album;
import app.pilo.android.models.SingleAlbum;

public class AlbumApi {
    private Context context;

    public AlbumApi(Context context) {
        this.context = context;
    }

    public void get(HashMap<String, Object> params, final HttpHandler.RequestHandler requestHandler) {
        JSONObject jsonObject = new JSONObject();
        try {
            for (Map.Entry<String, Object> item : params.entrySet()) {
                jsonObject.put(item.getKey(), item.getValue());
            }
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, PiloApi.ALBUM_GET, jsonObject,
                    response -> {
                        try {
                            JSONArray data = response.getJSONArray("data");
                            boolean status = response.getBoolean("status");
                            String message = response.getString("message");
                            if (status) {
                                List<Album> albums = new ArrayList<>();
                                for (int i = 0; i < data.length(); i++) {
                                    Album album = JsonParser.albumParser(data.getJSONObject(i));
                                    if (album != null)
                                        albums.add(album);
                                }
                                requestHandler.onGetInfo(albums, message, status);
                            } else
                                requestHandler.onGetInfo(null, message, status);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            requestHandler.onGetError(null);
                        }
                    }, requestHandler::onGetError);
            request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(context).add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void single(HashMap<String, Object> params, final HttpHandler.RequestHandler requestHandler) {
        JSONObject jsonObject = new JSONObject();
        try {
            for (Map.Entry<String, Object> item : params.entrySet()) {
                jsonObject.put(item.getKey(), item.getValue());
            }
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, PiloApi.ALBUM_SINGLE, jsonObject,
                    response -> {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            boolean status = response.getBoolean("status");
                            String message = response.getString("message");
                            if (status) {
                                SingleAlbum singleAlbum = JsonParser.singleAlbumParser(data);
                                requestHandler.onGetInfo(singleAlbum, message, status);
                            } else
                                requestHandler.onGetInfo(null, message, status);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            requestHandler.onGetError(null);
                        }
                    }, requestHandler::onGetError);
            request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(context).add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
