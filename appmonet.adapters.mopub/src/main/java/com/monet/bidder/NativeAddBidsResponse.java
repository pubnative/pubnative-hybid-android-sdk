package com.monet.bidder;

import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.RequestParameters;

public class NativeAddBidsResponse {
  private final MoPubNative moPubNative;
  private final RequestParameters requestParameters;

  NativeAddBidsResponse(MoPubNative moPubNative, RequestParameters requestParameters){
    this.moPubNative = moPubNative;
    this.requestParameters = requestParameters;
  }


  public MoPubNative getMoPubNative() {
    return moPubNative;
  }

  public RequestParameters getRequestParameters() {
    return requestParameters;
  }
}
