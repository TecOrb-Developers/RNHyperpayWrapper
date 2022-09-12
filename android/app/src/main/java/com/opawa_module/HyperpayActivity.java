package com.opawa_module;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.AppConstant;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ViewManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


import com.oppwa.mobile.connect.checkout.dialog.CheckoutActivity;
import com.oppwa.mobile.connect.checkout.meta.CheckoutSettings;
import com.oppwa.mobile.connect.exception.PaymentError;
import com.oppwa.mobile.connect.provider.Connect;
import com.oppwa.mobile.connect.provider.Transaction;
import com.oppwa.mobile.connect.provider.TransactionType;

public class HyperpayActivity extends AppCompatActivity implements ReactPackage {
    String checkoutID;
    String resourcePath;
    OppwaNativeMethodModule oppwaNativeMethodModule;

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    @Override
    public List<NativeModule> createNativeModules(
            ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        oppwaNativeMethodModule = new OppwaNativeMethodModule(reactContext);

        modules.add(oppwaNativeMethodModule);

        return modules;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Set<String> paymentBrands = new LinkedHashSet<String>();
        paymentBrands.add("VISA");
        paymentBrands.add("MASTER");
        Intent intentGet = getIntent();
        if (intentGet.hasExtra("checkoutId")) {
            checkoutID = intentGet.getStringExtra("checkoutId");
        }
        CheckoutSettings checkoutSettings = new CheckoutSettings(checkoutID, paymentBrands, AppConstant.getHyperPayMode() );
        // Set shopper result URL
        checkoutSettings.setShopperResultUrl("com.hyperpayactivity://result");
        Intent intent = checkoutSettings.createCheckoutActivityIntent(this);
        startActivityForResult(intent, CheckoutActivity.REQUEST_CODE_CHECKOUT);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getScheme().equals("com.hyperpayactivity")) {
            String checkoutId = intent.getData().getQueryParameter("id");
            WritableMap dataMap = Arguments.createMap();
            dataMap.putString("resourcePath", resourcePath);
            if (resourcePath != null)
                OppwaNativeMethodModule.onSuccess.invoke(dataMap);
            Log.d("resourcePath", "onNewIntent: " + checkoutId);
            Log.d("resourcePath", "onNewIntent: " + intent.getData());
            /* request payment status */
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case CheckoutActivity.RESULT_OK:
                /* transaction completed */
                Transaction transaction = data.getParcelableExtra(CheckoutActivity.CHECKOUT_RESULT_TRANSACTION);

                /* resource path if needed */
                String resourcePath = data.getStringExtra(CheckoutActivity.CHECKOUT_RESULT_RESOURCE_PATH);
                OppwaNativeMethodModule.onSuccess.invoke(resourcePath);
                if (transaction.getTransactionType() == TransactionType.SYNC) {
                    /* check the result of synchronous transaction */

                } else {
                    /* wait for the asynchronous transaction callback in the onNewIntent() */
                }

                break;
            case CheckoutActivity.RESULT_CANCELED:
                /* shopper cancelled the checkout process */
                break;
            case CheckoutActivity.RESULT_ERROR:
                /* error occurred */
                PaymentError error = data.getParcelableExtra(CheckoutActivity.CHECKOUT_RESULT_ERROR);
        }
        finish();
        
    }



//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (resultCode) {
//            case CheckoutActivity.RESULT_OK:
//                /* transaction completed */
//                Transaction transaction = data.getParcelableExtra(CheckoutActivity.CHECKOUT_RESULT_TRANSACTION);
//                /* resource path if needed */
//                resourcePath = data.getStringExtra(CheckoutActivity.CHECKOUT_RESULT_RESOURCE_PATH);
//                Log.d("resourcePath", "onActivityResult: " + resourcePath);
//                WritableMap dataMap = Arguments.createMap();
//                dataMap.putString("resourcePath", resourcePath);
//                if (transaction.getTransactionType() == TransactionType.SYNC) {
//                    /* check the result of synchronous transaction */
//                    // OppwaNativeMethodModule.onSuccessPayment(dataMap);
//                    OppwaNativeMethodModule.onSuccess.invoke(dataMap);
//                } else {
//                    OppwaNativeMethodModule.onSuccess.invoke(dataMap);
//                    /* wait for the asynchronous transaction callback in the onNewIntent() */
//                }
//                break;
//            case CheckoutActivity.RESULT_CANCELED:
//                OppwaNativeMethodModule.onFail.invoke("There is an error while process payment");
//                // NativeMethodModule.onFail.invoke("There is an error while process payment");
//                /* shopper canceled the checkout process */
//                break;
//            case CheckoutActivity.RESULT_ERROR:
//                /* error occurred */
//             //   PaymentError error = data.getParcelableExtra(CheckoutActivity.CHECKOUT_RESULT_ERROR);
//
////                OppwaNativeMethodModule.onFailPayment(error);
//               OppwaNativeMethodModule.onFail.invoke("therree is error");
//        }
//        finish();
//    }
}