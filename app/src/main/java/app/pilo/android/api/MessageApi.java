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

import app.pilo.android.models.Like;
import app.pilo.android.models.Message;
import app.pilo.android.repositories.UserRepo;

public class MessageApi {
    private Context context;

    public MessageApi(Context context) {
        this.context = context;
    }

    public void get(final RequestHandler.RequestHandlerWithList<Message> requestHandler) {
        JSONObject requestJsonObject = new JSONObject();
        String token = UserRepo.getInstance(context).get().getAccess_token();
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, PiloApi.MESSAGES_GET + "?token=" + token, requestJsonObject,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            List<Message> messages = new ArrayList<>();
                            for (int i = 0; i < data.length(); i++) {
                                Message message = JsonParser.messageJsonParser(data.getJSONObject(i));
                                if (message != null)
                                    messages.add(message);
                            }
                            requestHandler.onGetInfo(status, messages);
                        } else
                            requestHandler.onGetInfo(status, null);
                    } catch (JSONException e) {
                        requestHandler.onGetError(null);
                    }
                }, requestHandler::onGetError);
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }


    public void send(String subject, String text, final RequestHandler.RequestHandlerWithStatus requestHandler) {
        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put("subject", subject);
            requestJsonObject.put("text", text);
            requestJsonObject.put("type", 1);
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, PiloApi.CONTACTUS, requestJsonObject,
                    response -> {
                        try {
                            String status = response.getString("status");
                            requestHandler.onGetInfo(status);
                        } catch (JSONException e) {
                            requestHandler.onGetError(null);
                        }
                    }, requestHandler::onGetError);
            request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(context).add(request);
        } catch (JSONException e) {
            requestHandler.onGetError(null);
        }
    }

}
