#### MAKE PROJECT IN REACT

STEP#1 - install npx react-native init LearnHyperPay

#### IMPLEMENT Hyper Pay on both android and IOS using SDK

        Using HyperPay SDK implement native module for online Payment like visa , master , mada card

#### IMPLEMENT NATIVE MODUULE IN REACT NATIVE

    1- Setting up in iOS

        STEP#1 - First, drag the two files, OPPWAMobile.framework and OPPWAMobile-Resources.bundle, into the Frameworks folder inside your React Native project, make sure you have the option Copy items if needed checked.

        STEP#2 - custom native module header and implementation files. Create a new file called RCTOppwaNativeModule.h

        and add the following to it:
        //  RCTOppwaNativeModule.h
        #import <React/RCTBridgeModule.h>
        @interface RCTOppwaNativeModule : NSObject <RCTBridgeModule>
        @end

        STEP#3 - Next up, let’s start implementing the native module. Create the corresponding implementation file, RCTOppwaNativeModule.m, in the same folder and include the following content

        // RCTOppwaNativeModule.m
        #import "RCTOppwaNativeModule.h"
        @implementation RCTCalendarModule
        // To export a module named RCTOppwaNativeModule
        RCT_EXPORT_MODULE();

        @end

        -----------
        RCT_EXPORT_METHOD(openHyperPay:(NSDictionary *)indic createDialog:(RCTResponseSenderBlock)doneCallback createDialog:(RCTResponseSenderBlock)cancelCallback) {

        }

        openHyperPay called from js file like

        const { openHyperPay } = ReactNative.NativeModules;

        there are three arguments
         a) checkout id
         b) callback success
         c) callback failure

         Adding code in RCTOppwaNativeModule.m
        -> Importing libraries:
        #import “UIKit/UIKit.h”
        #import <OPPWAMobile/OPPWAMobile.h>

        -> Calling function
        @implementation NativeMethod{
       RCTResponseSenderBlock onDoneClick;
       RCTResponseSenderBlock onCancelClick;
       UIViewController *rootViewController;
       NSString *isRedirect;
      OPPPaymentProvider *provider;
       }

       -> Add openHyperPay method:

       RCT_EXPORT_METHOD(openHyperPay:(NSDictionary *)indic createDialog:(RCTResponseSenderBlock)doneCallback createDialog:(RCTResponseSenderBlock)cancelCallback) {

onDoneClick = doneCallback;
onCancelClick = cancelCallback;
NSArray \*events = @[];

provider = [OPPPaymentProvider paymentProviderWithMode:OPPProviderModeTest];

OPPCheckoutSettings \*checkoutSettings = [[OPPCheckoutSettings alloc] init];

// Set available payment brands for your shop
checkoutSettings.paymentBrands = @[@"VISA", @"MASTER"];
// Set shopper result URL
checkoutSettings.shopperResultURL = @"com.appsinvo.goejar.payment://result";

checkoutProvider = [OPPCheckoutProvider checkoutProviderWithPaymentProvider:provider checkoutID:indic[@"checkoutId"]
settings:checkoutSettings];
dispatch*async(dispatch_get_main_queue(), ^{
[checkoutProvider presentCheckoutForSubmittingTransactionCompletionHandler:^(OPPTransaction * \_Nullable transaction, NSError \_ \_Nullable error) {
if (error) {
// Executed in case of failure of the transaction for any reason
if (isRedirect && ![isRedirect isEqualToString:@"1"]) {
self->onCancelClick(@[@"cancel", events]);
}
} else if (transaction.type == OPPTransactionTypeSynchronous) {
// Send request to your server to obtain the status of the synchronous transaction
// You can use transaction.resourcePath or just checkout id to do it
NSDictionary \*responeDic = @{@"resourcePath" : transaction.resourcePath};
self->onDoneClick(@[responeDic, events]);
NSLog(@"%@", transaction.resourcePath);
} else {
// The SDK opens transaction.redirectUrl in a browser
// See 'Asynchronous Payments' guide for more details

      }
    } cancelHandler:^{
      self->onCancelClick(@[@"cancel", events]);
      // Executed if the shopper closes the payment page prematurely
    }];
     });

}
-> Register a custom URL scheme

      ~ Under URL Schemes, enter your app switch return URL scheme. This scheme must start with your app's Bundle ID. For example, if the app bundle ID is com.companyname.appname, then your URL scheme could be com.companyname.appname.payments.
      ~ Add scheme URL to a whitelist in your app's Info.plist:
      <key>LSApplicationQueriesSchemes</key>
      <array>
          <string>com.companyname.appname.payments</string>
      </array>

      Important: You are responsible to close checkout by calling method dismissCheckoutAnimated:completion: of OPPCheckoutProvider class. If you have no access to the OPPCheckoutProvider instance from the app delegate, you can use broadcast notifications to handle result in the view controller.

iOS 10-12, AppDelegate

      - (BOOL)application:(UIApplication *)app openURL:(NSURL *)url options:(NSDictionary *)options {
    if ([url.scheme caseInsensitiveCompare:@"com.companyname.appname.payments"] == NSOrderedSame) {
        [checkoutProvider dismissCheckoutAnimated:YES completion:^{
            // request payment status
        }];
        return YES;
    } else {
        return NO;
    }

}

iOS 13 and newer, SceneDelegate

- (void)scene:(UIScene _)scene openURLContexts:(NSSet<UIOpenURLContext _> *)URLContexts {
  NSURL *url = [[URLContexts allObjects] firstObject].URL;
  if ([url.scheme caseInsensitiveCompare:@"com.companyname.appname.payments"] == NSOrderedSame) {
  [checkoutProvider dismissCheckoutAnimated:YES completion:^{
  // request payment status
  }];
  }
  }

2- Setting up in Android

          STEP#1 - Import oppwa.mobile.aar into your project or copy oppwa.mobile folder inside android folder
          STEP#2 - now import line
                   include ':app',':oppwa.mobile' //copy paste in setting.gradle file

          STEP#3 - implement dependencies to your in android>app>build.gradle file
                  implementation project(':oppwa.mobile')
                  implementation 'com.android.support:support-v4:28.0.0'
                  implementation 'com.android.support:appcompat-v7:28.0.0'
                  implementation 'com.android.support:design:28.0.0'
                  implementation 'com.android.support:customtabs:28.0.0'
                  implementation 'com.google.android.gms:play-services-wallet:16.0.1'

          STEP#4 - Build a Navtive Bridge Android
          The first step is to create the (OppwaNativeMethodModule.java) Java file inside android/app/src/main/java/com/opawa_module folder .

        package com.opawa_module; // replace com.your-app-name with your app’s name
        import com.facebook.react.bridge.NativeModule;
        import com.facebook.react.bridge.ReactApplicationContext;
        import com.facebook.react.bridge.ReactContext;
        import com.facebook.react.bridge.ReactContextBaseJavaModule;
        import com.facebook.react.bridge.ReactMethod;
        import java.util.Map;
        import java.util.HashMap;

        public class OppwaNativeMethodModule extends ReactContextBaseJavaModule {
          OppwaNativeMethodModule(ReactApplicationContext context) {
           super(context);
          }
        }




        STEP#5 - All Java/Kotlin native modules in Android need to implement the getName() method. This method returns a string, which represents the name of the native module. The native module can then be accessed in JavaScript using its name. For example, in the below code snippet, getName() returns "OppwaNativeMethodModule".

        // add to OppwaNativeMethodModule.java
        @Override
        public String getName() {
            return "OppwaNativeMethodModule";
        }

        STEP#6 - Export a Native Method to JavaScript
        Next you will need to add a method to your native module that will create calendar events and can be invoked in JavaScript. All native module methods meant to be invoked from JavaScript must be annotated with @ReactMethod.

        @ReactMethod
        public void openHyperPay(ReadableMap data,Callback onSuccess, Callback onFail) {

        }

        STEP#7 - ADD oppwa method inside the openHyperPay native method

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

        STEP#8 - Using class HyperpayActivity

        public class HyperpayActivity extends AppCompatActivity {

String checkoutID;
@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_hyperpay);
Set<String> paymentBrands = new LinkedHashSet<String>();
paymentBrands.add("VISA");
paymentBrands.add("MASTER");
Intent intentGet = getIntent();
if (intentGet.hasExtra("checkoutId")) {
checkoutID = intentGet.getStringExtra("checkoutId");
}
CheckoutSettings checkoutSettings = new CheckoutSettings(checkoutID, paymentBrands, Connect.ProviderMode.TEST);
// Set shopper result URL
checkoutSettings.setShopperResultUrl("com.simicart://result");
Intent intent = checkoutSettings.createCheckoutActivityIntent(this);
startActivityForResult(intent, CheckoutActivity.REQUEST_CODE_CHECKOUT);
}
@Override
protected void onNewIntent(Intent intent) {
super.onNewIntent(intent);
if (intent.getScheme().equals("com.simicart")) {
String checkoutId = intent.getData().getQueryParameter("id");
Log.d("resourcePath", "onNewIntent: " + checkoutId);
Log.d("resourcePath", "onNewIntent: " + intent.getData());
/_ request payment status _/
}
}
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
super.onActivityResult(requestCode, resultCode, data);
switch (resultCode) {
case CheckoutActivity.RESULT_OK:
/_ transaction completed _/
Transaction transaction = data.getParcelableExtra(CheckoutActivity.CHECKOUT_RESULT_TRANSACTION);
/_ resource path if needed _/
String resourcePath = data.getStringExtra(CheckoutActivity.CHECKOUT_RESULT_RESOURCE_PATH);
Log.d("resourcePath", "onActivityResult: " + resourcePath);
WritableMap dataMap = Arguments.createMap();
dataMap.putString("resourcePath", resourcePath);
NativeMethodModule.onSuccess.invoke(dataMap);
if (transaction.getTransactionType() == TransactionType.SYNC) {
/_ check the result of synchronous transaction _/
} else {
/_ wait for the asynchronous transaction callback in the onNewIntent() _/
}
break;
case CheckoutActivity.RESULT_CANCELED:
NativeMethodModule.onFail.invoke("There is an error while process payment");
/_ shopper canceled the checkout process _/
break;
case CheckoutActivity.RESULT_ERROR:
/_ error occurred _/
PaymentError error = data.getParcelableExtra(CheckoutActivity.CHECKOUT_RESULT_ERROR);
NativeMethodModule.onFail.invoke("Payment Fail");
}
finish();
}

      STEP#8 - Setting up asynchronous payment:

      Register a custom URL scheme
      Define the name of your custom scheme (for example companyname) and add intent filter to your target activity in the AndroidManifest.xml

      <activity
    android:name="YOUR_ACTIVITY"
    android:launchMode="singleTask">

    <intent-filter>
        <data android:scheme="companyname"/>

        <action android:name="android.intent.action.VIEW"/>

        <category android:name="android.intent.category.DEFAULT"/>
        <category android:name="android.intent.category.BROWSABLE"/>
    </intent-filter>
    </activity>



### STEP#4 - On the JavaScript side --

  const onPay = () => {
    let pyData = {
      checkoutId: "79918FDF967F3151782CA924E766CD7A.uat01-vm-tx04" // PASS CHECKOUT ID HERE
    }

    if (Platform.OS === 'android') {
      console.log("payment details", NativeModules)
      NativeModules.OppwaNativeMethodModule.openHyperPay(pyData, (res) => {
        console.log("response android native", res)
      }, (err) => {
        console.log("error android native", err)
      })
    } else {
      NativeModules.OppwaNativeModule.openHyperPay(pyData, (res) => {
        console.log("response ios native", res)
        setIsPath(res.resourcePath)
      }, (err) => {
        console.log("error ios native", err)
      })

    }

  }





### STEP#5 - After getting succesfully response find resource path on response -

base_url_live = 'https://oppwa.com'
base_url_dev =  "https://test.oppwa.com"

--> call api get api base_url + resouce_path to get response payment status

