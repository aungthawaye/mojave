# Create FSP2's CA
Create FSP2's CA private key 
```
openssl genrsa -out fsp2-ca.key 4096
```
Create CA self-signed cert (10y)
```
openssl req -x509 -new -nodes \
-key fsp2-ca.key \
-sha256 \
-days 3650 \
-out fsp2-ca.crt \
-subj "/C=US/ST=State/L=City/O=MyOrg/OU=FSP2CA/CN=FSP2 Root CA"
```

# Issue FSP2's server cert
Create FSP2's server private key
```
openssl genrsa -out fsp2.key 2048
```
Create CSR for FSP2
```
openssl req -new \
-key fsp2.key \
-out fsp2.csr \
-subj "/C=US/ST=State/L=City/O=MyOrg/OU=Server/CN=fsp2.myorg.local"
```
Sign with FSP2's CA
```
openssl x509 -req \
-in fsp2.csr \
-CA fsp2-ca.crt -CAkey fsp2-ca.key \
-CAcreateserial \
-out fsp2.crt \
-days 365 \
-sha256
```
# Bundle into PKCS12
```
openssl pkcs12 -export \
-in fsp2.crt \
-inkey fsp2.key \
-certfile fsp2-ca.crt \
-name fsp2 \
-out fsp2-keystore.p12
```
(Optional) Convert .p12 file into PEM
```
openssl pkcs12 \
  -in fsp2-keystore.p12 \
  -out fsp2-keystore.pem \
  -nodes
```
(Optional) If you want to keep .p12 in Base64
```
openssl base64 \
  -in fsp2-keystore.p12 \
  -out fsp2-keystore.p12.b64
```
# Prepare for mTLS
Import FSP1's CA into FSP2's trust store
```
openssl pkcs12 \
  -export \
  -nokeys \
  -in fsp1-ca.crt \
  -out fsp2-truststore.p12
```
Create base64 of trust store
```
openssl base64 \
  -in fsp2-truststore.p12 \
  -out fsp2-truststore.p12.b64
```