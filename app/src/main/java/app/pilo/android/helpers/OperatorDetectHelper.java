package app.pilo.android.helpers;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.Arrays;
import java.util.List;

import app.pilo.android.R;

public class OperatorDetectHelper {
    private String[] operators = new String[]{"irancell", "mtn", "mci", "rightel", "righ", "rtl", "iran"};
    private Context context;
    private View view;
    UserSharedPrefManager userSharedPrefManager;

    public OperatorDetectHelper(Context context, View view) {
        this.context = context;
        this.view = view;
        userSharedPrefManager = new UserSharedPrefManager(context);
        this.check();
    }


    private void check() {
        if (userSharedPrefManager.getFirstLunch()) {
            if (!isIran(context)) {
                LinearLayout ll_first_lunch = view.findViewById(R.id.ll_first_lunch);
                LinearLayout ll_first_lunch_send = view.findViewById(R.id.ll_first_lunch_send);
                EditText et_first_lunch = view.findViewById(R.id.et_first_lunch);
                ll_first_lunch.setVisibility(View.VISIBLE);

                ll_first_lunch_send.setOnClickListener(view1 -> {
                    if (et_first_lunch.getText().toString().equals("یلدا") || et_first_lunch.getText().toString().equals("yalda")) {
                        userSharedPrefManager.setFirstLunch(false);
                        ll_first_lunch.setVisibility(View.GONE);
                        userSharedPrefManager.setFirstLunch(false);
                    }
                });
            } else {
                userSharedPrefManager.setFirstLunch(false);
            }
        }
    }


    private boolean isIran(Context context) {
        List<String> list = Arrays.asList(operators);
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String carrierName = manager.getNetworkOperatorName();

        return list.contains(carrierName.toLowerCase());
    }
}
