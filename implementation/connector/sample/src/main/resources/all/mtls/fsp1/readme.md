# Create FSP1's CA
Create FSP1's CA private key 
```
openssl genrsa -out fsp1-ca.key 4096
```
Create CA self-signed cert (10y)
```
openssl req -x509 -new -nodes \
-key fsp1-ca.key \
-sha256 \
-days 3650 \
-out fsp1-ca.crt \
-subj "/C=US/ST=State/L=City/O=MyOrg/OU=FSP1CA/CN=FSP1 Root CA"
```

# Issue FSP1's server cert
Create FSP1's server private key
```
openssl genrsa -out fsp1.key 2048
```
Create CSR for FSP1
```
openssl req -new \
-key fsp1.key \
-out fsp1.csr \
-subj "/C=US/ST=State/L=City/O=MyOrg/OU=Server/CN=fsp1.myorg.local"
```
Sign with FSP1's CA
```
openssl x509 -req \
-in fsp1.csr \
-CA fsp1-ca.crt -CAkey fsp1-ca.key \
-CAcreateserial \
-out fsp1.crt \
-days 365 \
-sha256
```
# Bundle into PKCS12
```
openssl pkcs12 -export \
-in fsp1.crt \
-inkey fsp1.key \
-certfile fsp1-ca.crt \
-name fsp1 \
-out fsp1-keystore.p12
```
(Optional) Convert .p12 file into PEM
```
openssl pkcs12 \
  -in fsp1-keystore.p12 \
  -out fsp1-keystore.pem \
  -nodes
```
(Optional) If you want to keep .p12 in Base64
```
openssl base64 \
  -in fsp1-keystore.p12 \
  -out fsp1-keystore.p12.b64
```
# Prepare for mTLS
Import FSP2's CA into FSP1's trust store
```
openssl pkcs12 \
  -export \
  -nokeys \
  -in fsp2-ca.crt \
  -out fsp1-truststore.p12
```
Create base64 of trust store
```
openssl base64 \
  -in fsp1-truststore.p12 \
  -out fsp1-truststore.p12.b64
```