package app.pilo.android.api;

import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import app.pilo.android.models.User;
import cz.msebera.android.httpclient.Header;

public class UserApi {

    public void login(Context context, String email, String password, final RequestHandler.RequestHandlerWithModel<User> requestHandler) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("email", email);
        requestParams.put("password", password);


        PiloApi.post(context,PiloApi.LOGIN, requestParams, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String status;
                    User user = null;
                    if (!response.isNull("error"))
                        status = response.getString("error");
                    else {
                        user = JsonParser.userJsonParser(response.getJSONObject("data"));
                        status = "success";
                    }
                    requestHandler.onGetInfo(status, user);
                } catch (JSONException e) {
                    e.printStackTrace();
                    requestHandler.onGetError();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                requestHandler.onGetError();
            }
        });
    }


    public void register(Context context,String email, String password, final RequestHandler.RequestHandlerWithStatus requestHandler) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("email", email);
        requestParams.add("password", password);

        PiloApi.post(context,PiloApi.REGISTER, requestParams, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String status = response.getString("status");
                    requestHandler.onGetInfo(status);
                } catch (JSONException e) {
                    e.printStackTrace();
                    requestHandler.onGetError();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                requestHandler.onGetError();
            }
        });
    }

}
