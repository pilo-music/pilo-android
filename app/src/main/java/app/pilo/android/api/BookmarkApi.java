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
import app.pilo.android.models.Bookmark;
import app.pilo.android.repositories.UserRepo;

public class BookmarkApi {
    private Context context;

    public BookmarkApi(Context context) {
        this.context = context;
    }

    public void get(final RequestHandler.RequestHandlerWithList<Bookmark> requestHandler) {
        JSONObject requestJsonObject = new JSONObject();
        String token = UserRepo.getInstance(context).get().getAccess_token();
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, PiloApi.BOOKMARKS_GET + "?token=" + token, requestJsonObject,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            List<Bookmark> bookmarks = new ArrayList<>();
                            for (int i = 0; i < data.length(); i++) {
                                Bookmark bookmark = JsonParser.bookmarkParser(data.getJSONObject(i));
                                if (bookmark != null)
                                    bookmarks.add(bookmark);
                            }
                            requestHandler.onGetInfo(status, bookmarks);
                        } else
                            requestHandler.onGetInfo(status, null);
                    } catch (JSONException e) {
                        requestHandler.onGetError(null);
                    }
                }, requestHandler::onGetError);
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }

}
