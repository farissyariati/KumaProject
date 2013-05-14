package com.farissyariati.kuma.utility;

import java.security.AccessController;
import java.security.Provider;

public class MyJSSEProvider extends Provider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyJSSEProvider() {
		super("HarmonyJSSE", 1.0, "Harmony JSSE Provider");
		AccessController
				.doPrivileged(new java.security.PrivilegedAction<Void>() {
					@Override
					public Void run() {
						// TODO Auto-generated method stub
						put("SSLContext.TLS",
								"org.apache.harmony.xnet.provider.jsse.SSLContextImpl");
						put("Alg.Alias.SSLContext.TLSv1", "TLS");
						put("KeyManagerFactory.X509",
								"org.apache.harmony.xnet.provider.jsse.KeyManagerFactoryImpl");
						put("TrustManagerFactory.X509",
								"org.apache.harmony.xnet.provider.jsse.TrustManagerFactoryImpl");
						return null;
					}
				});
	}

}
