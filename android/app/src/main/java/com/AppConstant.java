package com;


import com.oppwa.mobile.connect.provider.Connect;
import com.rnhyperpay.BuildConfig;

public class AppConstant {
    public static Connect.ProviderMode getHyperPayMode(){
        return BuildConfig.DEBUG ? Connect.ProviderMode.TEST : Connect.ProviderMode.LIVE;
    }
}
