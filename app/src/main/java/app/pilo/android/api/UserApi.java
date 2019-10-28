package app.pilo.android.api;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.pilo.android.models.Album;
import app.pilo.android.models.User;
import cz.msebera.android.httpclient.Header;

public class UserApi {

    public void login(String email, String password, final RequestHandler.RequestHandlerWithModel<User> requestHandler) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("email", email);
        requestParams.add("password", password);

        PiloApi.post(PiloApi.LOGIN, requestParams, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String status = response.getString("status");
                    requestHandler.onGetInfo(status,null);
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


    public void register(String email, String password, final RequestHandler.RequestHandlerWithStatus requestHandler) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("email", email);
        requestParams.add("password", password);

        PiloApi.post(PiloApi.REGISTER, requestParams, null, new JsonHttpResponseHandler() {
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
