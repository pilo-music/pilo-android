package app.pilo.android.api;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import app.pilo.android.models.User;
import app.pilo.android.repositories.UserRepo;

public class UserApi {
    private Context context;

    public UserApi(Context context) {
        this.context = context;
    }

    public void login(String email, String password, final RequestHandler.RequestHandlerWithModel<User> requestHandler) {
        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put("email", email);
            requestJsonObject.put("password", password);
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, PiloApi.LOGIN, requestJsonObject,
                    response -> {
                        try {
                            String status;
                            User user = null;
                            status = response.getString("status");
                            if (status.equals("success"))
                                user = JsonParser.userJsonParser(response.getJSONObject("data"));

                            requestHandler.onGetInfo(status, user);
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


    public void register(String email, String password, final RequestHandler.RequestHandlerWithStatus requestHandler) {
        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put("email", email);
            requestJsonObject.put("password", password);
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, PiloApi.REGISTER, requestJsonObject,
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

    public void forgotPassword(String email, final RequestHandler.RequestHandlerWithData requestHandler) {
        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put("email", email);
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, PiloApi.FORGOT_PASSWORD, requestJsonObject,
                    response -> {
                        try {
                            String status = response.getString("status");
                            String data = response.getString("data");
                            requestHandler.onGetInfo(status, data);
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


    public void update(String name, String password, String image, final RequestHandler.RequestHandlerWithStatus requestHandler) {
        JSONObject requestJsonObject = new JSONObject();
        try {
            if (name != null && !name.equals(""))
                requestJsonObject.put("name", name);
            if (password != null && !password.equals(""))
                requestJsonObject.put("password", password);
            if (image != null && !image.equals(""))
                requestJsonObject.put("image", image);
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, PiloApi.UPDATE_PROFILE, requestJsonObject,
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


    public void me(final RequestHandler.RequestHandlerWithModel<User> requestHandler) {
        JSONObject requestJsonObject = new JSONObject();
        String token = UserRepo.getInstance(context).get().getAccess_token();
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, PiloApi.ME + "?token=" + token, requestJsonObject,
                response -> {
                    try {
                        String status;
                        User user = null;
                        status = response.getString("status");
                        if (status.equals("success"))
                            user = JsonParser.userJsonParser(response.getJSONObject("data"));

                        requestHandler.onGetInfo(status, user);
                    } catch (JSONException e) {
                        requestHandler.onGetError(null);
                    }
                }, requestHandler::onGetError);
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }

}
