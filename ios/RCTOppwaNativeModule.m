//
//  RCTOppwaNativeModule.m
//  GoEjar
//
//  Created by Tecorb on 12/08/22.
//

#import <Foundation/Foundation.h>

#import "RCTOppwaNativeModule.h"
#import <React/RCTLog.h>

OPPPaymentProvider *provider;

@implementation RCTOppwaNativeModule{
 RCTResponseSenderBlock onDoneClick;
 RCTResponseSenderBlock onCancelClick;
 UIViewController *rootViewController;
 NSString *isRedirect;
 OPPPaymentProvider *provider;
 PKPaymentAuthorizationStatus paymentAuthorizationStatus;
 NSError *error;
}


RCT_EXPORT_MODULE();
OPPCheckoutProvider *checkoutProvider;
NSString *resoursePath;
NSArray<PKPaymentSummaryItem *> *summaryItems;

- (instancetype)init{
    self = [super init];
    if (self) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(getStatusOder:) name:@"getStatusOrder" object:nil];
    }
    return self;
}




- (void)getStatusOder:(NSNotification*)noti{
  [checkoutProvider dismissCheckoutAnimated:YES completion:^{
    self->isRedirect = @"1";
    NSURL *url = noti.object;
    NSString *urlString = [url absoluteString];
    NSLog(@"%@", urlString);
    if (![urlString isEqualToString:@"org.reactjs.native.example.RNHyperPay.payments://result"]) {
      NSArray *events = @[];
      NSDictionary *responeDic = @{@"url" : urlString};
      self->onDoneClick(@[responeDic, events]);
    }
  }];
}


- (void)scene:(UIScene *)scene openURLContexts:(NSSet<UIOpenURLContext *> *)URLContexts {
  NSURL *url = [[URLContexts allObjects] firstObject].URL;
  if ([url.scheme caseInsensitiveCompare:@"org.reactjs.native.example.RNHyperPay.payments"] == NSOrderedSame) {
    [checkoutProvider dismissCheckoutAnimated:YES completion:^{
      // request payment status
    }];
  }
}




RCT_EXPORT_METHOD(openHyperPay:(NSDictionary *)indic createDialog:(RCTResponseSenderBlock)doneCallback createDialog:(RCTResponseSenderBlock)cancelCallback) {
  onDoneClick = doneCallback;
  onCancelClick = cancelCallback;
  NSArray *events = @[];
  
  provider = [OPPPaymentProvider paymentProviderWithMode:OPPProviderModeTest];
  
  OPPCheckoutSettings *checkoutSettings = [[OPPCheckoutSettings alloc] init];

  // Set available payment brands for your shop
  checkoutSettings.paymentBrands = @[@"VISA", @"MASTER"];
  // Set shopper result URL
  checkoutSettings.shopperResultURL = @"org.reactjs.native.example.RNHyperPay.payments://result";
  
  checkoutProvider = [OPPCheckoutProvider checkoutProviderWithPaymentProvider:provider checkoutID:indic[@"checkoutId"]
                                                                                          settings:checkoutSettings];
  dispatch_async(dispatch_get_main_queue(), ^{
    [checkoutProvider presentCheckoutForSubmittingTransactionCompletionHandler:^(OPPTransaction * _Nullable transaction, NSError * _Nullable error) {
      if (error) {
        // Executed in case of failure of the transaction for any reason
        if (isRedirect && ![isRedirect isEqualToString:@"1"]) {
          self->onCancelClick(@[@"cancel", events]);
        }
      } else if (transaction.type == OPPTransactionTypeSynchronous)  {
        // Send request to your server to obtain the status of the synchronous transaction
        // You can use transaction.resourcePath or just checkout id to do it
        NSDictionary *responeDic = @{@"resourcePath" : transaction.resourcePath};
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

@end
