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
import app.pilo.android.models.User;
import app.pilo.android.repositories.UserRepo;

public class UserApi {
    private Context context;

    public UserApi(Context context) {
        this.context = context;
    }

    public void login(String email, String password, final HttpHandler.RequestHandler requestHandler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, PiloApi.LIKE_ADD, jsonObject,
                    response -> {
                        try {
                            boolean status = response.getBoolean("status");
                            String message = response.getString("message");
                            JSONObject data = response.getJSONObject("data");
                            Map<String, Object> user = new HashMap<>();
                            if (status) {
                                if (data.getString("status").equals("login")) {
                                    user.put("user", JsonParser.userParser(data));
                                }
                                user.put("status", data.getString("status"));
                                requestHandler.onGetInfo(user, message, status);
                            } else {
                                requestHandler.onGetInfo(null, message, status);
                            }
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
                    return params;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(context).add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void register(String name, String email, String password, String confirm, final HttpHandler.RequestHandler requestHandler) {
        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put("name", name);
            requestJsonObject.put("email", email);
            requestJsonObject.put("password", password);
            requestJsonObject.put("password_confirmation", confirm);
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, PiloApi.REGISTER, requestJsonObject,
                    response -> {
                        try {
                            boolean status = response.getBoolean("status");
                            String message = response.getString("message");
                            JSONObject data = response.getJSONObject("data");
                            if (status) {
                                User user = JsonParser.userParser(data);
                                requestHandler.onGetInfo(user, message, status);
                            } else {
                                requestHandler.onGetInfo(null, message, status);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            requestHandler.onGetError(null);
                        }
                    }, requestHandler::onGetError);
            request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(context).add(request);
        } catch (JSONException e) {
            requestHandler.onGetError(null);
        }
    }

    public void verify(String email, String code, final HttpHandler.RequestHandler requestHandler) {
        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put("email", email);
            requestJsonObject.put("code", code);
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, PiloApi.VERSION, requestJsonObject,
                    response -> {
                        try {
                            boolean status = response.getBoolean("status");
                            String message = response.getString("message");
                            JSONObject data = response.getJSONObject("data");
                            if (status) {
                                User user = JsonParser.userParser(data);
                                requestHandler.onGetInfo(user, message, status);
                            } else {
                                requestHandler.onGetInfo(null, message, status);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                            user = JsonParser.userParser(response.getJSONObject("data"));

                        requestHandler.onGetInfo(status, user);
                    } catch (JSONException e) {
                        requestHandler.onGetError(null);
                    }
                }, requestHandler::onGetError);
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }

}
