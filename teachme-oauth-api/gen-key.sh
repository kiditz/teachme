keytool -genkeypair -alias jwt -keyalg RSA -dname "CN=jwt, L=Brisbane, S=Brisbane, C=AU" -keypass $1 -keystore jwt.jks -storepass $2