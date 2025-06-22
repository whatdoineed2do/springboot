# WebClient SSL Setup

`WebClient` requires a valid SSL certificate to establish secure connections. Below are the steps to set up the SSL certificate for `WebClient`.  Because its not using the default keystore (nor the `javax.net.ssl.trust/keystore` values), we need to import the certificate manually (see `WebConfig.java`).

For example, to import the SSL certificate for `google.com`, you can use the following commands:
```
echo | openssl s_client \
    -connect google.com:443 \
    -servername google.com 2>/dev/null | \
    openssl x509 -outform PEM > google.crt

keytool -import -noprompt -trustcacerts \
    -alias google \
    -file google.crt \
    -keystore src/main/resources/keystore/keystore.jks \
    -storepass changeit \
    -storetype JKS
```