/***
 Copyright (c) 2016 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.
 */

package com.commonsware.cwac.netsecurity;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.net.ssl.X509TrustManager;

/**
 * android.net.http.X509TrustManagerExtensions defines two additional
 * methods that modern Android X509TrustManager implementations should
 * implement. However, while X509TrustManagerExtensions was written
 * as a wrapper, X509TrustManagerExtensions is just a plain class,
 * not an extension of X509TrustManager.
 *
 * This is stupid.
 *
 * So, this interface does what should have been done originally,
 * defining an extended interface for those additional methods.
 */
public interface X509Extensions extends X509TrustManager {
  /**
   * Verifies the given certificate chain.
   *
   * See android.net.http.X509TrustManagerExtensions for details.
   */
  List<X509Certificate> checkServerTrusted(X509Certificate[] chain,
                                           String authType,
                                           String host)
    throws CertificateException;

  /**
   * Checks whether a CA certificate is added by an user.
   *
   * See android.net.http.X509TrustManagerExtensions for details.
   */
  boolean isUserAddedCertificate (X509Certificate cert);
}
