package app.pilo.android.api;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import app.pilo.android.models.Music;

public class MusicApi {
    private Context context;

    public MusicApi(Context context) {
        this.context = context;
    }

    public void get(String artist, int page, final RequestHandler.RequestHandlerWithList<Music> requestHandler) {
        String url;
        if (artist != null && !artist.equals("")) {
            url = PiloApi.MUSICS_GET + "artist/" + artist + "/";
        } else
            url = PiloApi.MUSICS_GET;

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url + page, null,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            List<Music> musics = new ArrayList<>();
                            for (int i = 0; i < data.length(); i++) {
                                Music music = JsonParser.singleMusicParser(data.getJSONObject(i));
                                if (music != null)
                                    musics.add(music);
                            }
                            requestHandler.onGetInfo(status, musics);
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
}
