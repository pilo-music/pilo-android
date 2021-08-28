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
    private final Context context;

    public UserApi(Context context) {
        this.context = context;
    }

    public void login(final HttpHandler.RequestHandler requestHandler) {
        JSONObject jsonObject = new JSONObject();
        try {
            //todo remove this
            jsonObject.put("email", "senatorblack1@gmail.com");
            jsonObject.put("password", "123456");
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, PiloApi.LOGIN, jsonObject,
                    response -> {
                        try {
                            boolean status = response.getBoolean("status");
                            String message = response.getString("message");
                            if (status) {
                                JSONObject data = response.getJSONObject("data");
                                User user = JsonParser.userParser(data);
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
            request.setShouldCache(false);
            request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(context).add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public void loginNew(String phone, final HttpHandler.RequestHandler requestHandler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone", phone);
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, PiloApi.LOGIN, jsonObject,
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


    public void verify(String phone, String code, final HttpHandler.RequestHandler requestHandler) {
        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put("phone", phone);
            requestJsonObject.put("code", code);
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, PiloApi.VERIFY, requestJsonObject,
                    response -> {
                        try {
                            boolean status = response.getBoolean("status");
                            String message = response.getString("message");
                            if (status) {
                                JSONObject data = response.getJSONObject("data");
                                User user = JsonParser.userParser(data);
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
            request.setShouldCache(false);
            request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(context).add(request);
        } catch (JSONException e) {
            requestHandler.onGetError(null);
        }
    }

    public void update(HashMap<String, Object> params, final HttpHandler.RequestHandler requestHandler) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (params != null) {
                for (Map.Entry<String, Object> item : params.entrySet()) {
                    jsonObject.put(item.getKey(), item.getValue());
                }
            }
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, PiloApi.UPDATE_PROFILE, jsonObject,
                    response -> {
                        try {
                            boolean status = response.getBoolean("status");
                            String message = response.getString("message");
                            if (status) {
                                JSONObject data = response.getJSONObject("data");
                                User user = JsonParser.userParser(data);
                                requestHandler.onGetInfo(user, message, status);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void me(final HttpHandler.RequestHandler requestHandler) {
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, PiloApi.ME, null,
                response -> {
                    try {
                        boolean status = response.getBoolean("status");
                        String message = response.getString("message");
                        if (status) {
                            JSONObject data = response.getJSONObject("data");
                            User user = JsonParser.userParser(data);
                            requestHandler.onGetInfo(user, message, status);
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

}
