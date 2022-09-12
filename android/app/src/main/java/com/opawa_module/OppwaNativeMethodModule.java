package com.opawa_module; // replace com.your-app-name with your app’s name

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import android.content.Intent;
import android.util.Log;
import com.facebook.react.bridge.Callback;

public class OppwaNativeMethodModule extends ReactContextBaseJavaModule {

    public static Callback onSuccess, onFail;

    public static void onSuccessPayment(WritableMap writableMap) {
        Log.d("==>", writableMap.getString("resourcePath"));
    }

//    public static void onFailPayment(PaymentError message) {
//        Log.d("==>", message.getErrorMessage());
//    }


    OppwaNativeMethodModule(ReactApplicationContext context) {
        super(context);
    }

    @Override
    public String getName() {
        return "OppwaNativeMethodModule";
    }

    @ReactMethod
    public void openHyperPay(ReadableMap data,Callback onSuccess, Callback onFail) {
        this.onSuccess = onSuccess;
        this.onFail = onFail;
        Intent intent = new Intent(getCurrentActivity(), HyperpayActivity.class);
        if (data.hasKey("checkoutId")) {
            intent.putExtra("checkoutId", data.getString("checkoutId"));
        }
        getCurrentActivity().startActivity(intent);
    }


    @ReactMethod
    public void createPaymentEvent(String name, String location) {
        ///   Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        Log.d("CalendarModule", "Create event called with name: " + name
                + " and location: " + location);
    }
}
