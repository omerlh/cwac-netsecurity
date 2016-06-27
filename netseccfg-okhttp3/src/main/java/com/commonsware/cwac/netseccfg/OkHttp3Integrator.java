package com.commonsware.cwac.netseccfg;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttp3Integrator {
  static public OkHttpClient.Builder applyTo(TrustManagerBuilder tmb,
                                              OkHttpClient.Builder builder)
    throws NoSuchAlgorithmException, KeyManagementException {
    CompositeTrustManager trustManager=tmb.build();

    if (trustManager.size()>0) {
      SSLContext ssl=SSLContext.getInstance("TLS");
      X509Interceptor interceptor=new X509Interceptor(trustManager, tmb);

      ssl.init(null, new TrustManager[]{trustManager}, null);
      builder.sslSocketFactory(ssl.getSocketFactory(), trustManager);
      builder.addInterceptor(interceptor);
      builder.addNetworkInterceptor(interceptor);
    }

    return(builder);
  }

  static private class X509Interceptor implements Interceptor {
    private final CompositeTrustManager trustManager;
    private final TrustManagerBuilder builder;

    private X509Interceptor(CompositeTrustManager trustManager,
                            TrustManagerBuilder builder) {
      this.trustManager=trustManager;
      this.builder=builder;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
      Request request=chain.request();
      String host=request.url().host();

      if (request.url().scheme().equals("http") &&
        !builder.isCleartextTrafficPermitted(host)) {
        throw new CleartextAttemptException("Cleartext blocked for "+request.url());
      }

      trustManager.setHost(host);

      return(chain.proceed(request));
    }
  }

  static public class CleartextAttemptException
    extends RuntimeException {
    public CleartextAttemptException(String message) {
      super(message);
    }
  }
}
