#!/bin/bash

export CONTAINER_NAME=auth

if [ "$ETCDCTL_ENDPOINT" != "" ]; then
  echo Setting up etcd...
  echo "** Testing etcd is accessible"
  etcdctl --debug ls
  RC=$?

  while [ $RC -ne 0 ]; do
      sleep 15

      # recheck condition
      echo "** Re-testing etcd connection"
      etcdctl --debug ls
      RC=$?
  done
  echo "etcdctl returned sucessfully, continuing"
  mkdir -p /etc/cert
  etcdctl get /proxy/third-party-ssl-cert > /etc/cert/cert.pem

  #export SYSTEM_ID=$(etcdctl get /global/system_id)

  export TWITTER_CONSUMER_KEY=$(etcdctl get /auth/twitter/id)
  export TWITTER_CONSUMER_SECRET=$(etcdctl get /auth/twitter/secret)
  export FACEBOOK_APP_ID=$(etcdctl get /auth/facebook/id)
  export FACEBOOK_APP_SECRET=$(etcdctl get /auth/facebook/secret)
  export GOOGLE_APP_ID=$(etcdctl get /auth/google/id)
  export GOOGLE_APP_SECRET=$(etcdctl get /auth/google/secret)
  export GITHUB_APP_ID=$(etcdctl get /auth/github/id)
  export GITHUB_APP_SECRET=$(etcdctl get /auth/github/secret)

  export FRONT_END_SUCCESS_CALLBACK=$(etcdctl get /auth/callback)
  export FRONT_END_FAIL_CALLBACK=$(etcdctl get /auth/failcallback)
  export FRONT_END_AUTH_URL=$(etcdctl get /auth/url)

  GAMEON_MODE=$(etcdctl get /global/mode)
  export GAMEON_MODE=${GAMEON_MODE:-production}
  export TARGET_PLATFORM=$(etcdctl get /global/targetPlatform)

fi

if [ -f /etc/cert/cert.pem ]; then
  # Container has requested we use the supplied cert for auth.
  echo "Building keystore/truststore from cert.pem"
  echo "-converting pem to pkcs12"
  openssl pkcs12 -passin pass:keystore -passout pass:keystore -export -out /etc/cert/cert.pkcs12 -in /etc/cert/cert.pem
  echo "-creating dummy key.jks"
  keytool -genkey -storepass testOnlyKeystore -keypass wefwef -keyalg RSA -alias endeca -keystore /etc/cert/key.jks -dname CN=rsssl,OU=unknown,O=unknown,L=unknown,ST=unknown,C=CA
  echo "-emptying key.jks"
  keytool -delete -storepass testOnlyKeystore -alias endeca -keystore /etc/cert/key.jks
  echo "-importing pkcs12 to key.jks"
  keytool -v -importkeystore -srcalias 1 -alias 1 -destalias default -noprompt -srcstorepass keystore -deststorepass testOnlyKeystore -srckeypass keystore -destkeypass testOnlyKeystore -srckeystore cert.pkcs12 -srcstoretype PKCS12 -destkeystore /etc/cert/key.jks -deststoretype JKS
  echo "-importing pem to jvm truststore"
  keytool -import -v -trustcacerts -alias default -file /etc/cert/cert.pem -storepass changeit -keypass keystore -noprompt -keystore $JAVA_HOME/lib/security/cacerts
  echo "done"
else
  # No cert.pem? we're running local/debug, and need to create one.
  echo "-creating dir for cert"
  mkdir -p /etc/cert
  if [ -f /etc/cert/key.jks ]; then
    echo "-using supplied keystore"
  else
    echo "-generating local cert for test/debug."
    keytool -genkey -storepass testOnlyKeystore -keypass testOnlyKeystore -keyalg RSA -alias default -keystore /etc/cert/key.jks -dname CN=rsssl,OU=unknown,O=unknown,L=unknown,ST=unknown,C=CA
  fi
fi

java -Djava.security.egd=file:/dev/./urandom -jar /app.jar
