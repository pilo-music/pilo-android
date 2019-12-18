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
import java.util.List;

import app.pilo.android.models.Artist;
import app.pilo.android.models.SingleArtist;

public class ArtistApi {

    private Context context;

    public ArtistApi(Context context) {
        this.context = context;
    }


    public void get(int page, final RequestHandler.RequestHandlerWithList<Artist> requestHandler) {
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, PiloApi.ARTISTS_GET + page , null,
                response -> {
                    try {
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            JSONArray data = response.getJSONArray("data");
                            List<Artist> artists = new ArrayList<>();
                            for (int i = 0; i < data.length(); i++) {
                                Artist artist = JsonParser.artistParser(data.getJSONObject(i));
                                if (artist != null)
                                    artists.add(artist);
                            }
                            requestHandler.onGetInfo(status, artists);
                        } else
                            requestHandler.onGetInfo(status, null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        requestHandler.onGetError(null);
                    }
                }, requestHandler::onGetError);
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }

    public void single(String slug, final RequestHandler.RequestHandlerWithModel<SingleArtist> requestHandler) {
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, PiloApi.ARTIST_GET + slug, null,
                response -> {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            SingleArtist singleArtist = JsonParser.singleArtistParser(data);
                            if (singleArtist != null)
                                requestHandler.onGetInfo(status, singleArtist);
                            else
                                requestHandler.onGetError(null);
                        } else {
                            requestHandler.onGetInfo(status, null);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        requestHandler.onGetError(null);
                    }
                }, requestHandler::onGetError);
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }

}
