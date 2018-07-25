/*******************************************************************************
 * Copyright (c) 2015 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package gameontext.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Handle generation of JWT auth credentials for the GameOn environment.
 */
@Component
public class JWTSigner {

    @Value("${jwt.keystore.location}")
    protected String keyStore;
    @Value("${jwt.keystore.password}")
    protected String keyStorePW;
    @Value("${jwt.keystore.alias}")
    protected String keyStoreAlias;

    protected static Key signingKey = null;

    /**
     * Obtain the key we'll use to sign the jwts we issue.
     *
     * @throws IOException
     *             if there are any issues with the keystore processing.
     */
    private synchronized void getKeyStoreInfo() throws IOException {
        try {
            // load up the keystore..
            FileInputStream is = new FileInputStream(keyStore);
            KeyStore signingKeystore = KeyStore.getInstance(KeyStore.getDefaultType());
            signingKeystore.load(is, keyStorePW.toCharArray());

            // grab the key we'll use to sign
            signingKey = signingKeystore.getKey(keyStoreAlias, keyStorePW.toCharArray());

        } catch (KeyStoreException e) {
            throw new IOException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        } catch (CertificateException e) {
            throw new IOException(e);
        } catch (UnrecoverableKeyException e) {
            throw new IOException(e);
        }

    }

    /**
     * Obtain a JWT with the details passed, signed appropriately
     *
     * @param id UserID to encode.
     * @param name Human Readable Name for User
     * @param email Email Address for User
     * @return jwt encoded as string, ready to send to http.
     * @throws IOException
     *             if there are keystore issues.
     */
    public String createJwt(String id,
                            String name,
                            String email) throws IOException {
        if (signingKey == null) {
            getKeyStoreInfo();
        }

        Claims onwardsClaims = Jwts.claims();

        // Set the subject as the id
        onwardsClaims.setSubject(id);
        onwardsClaims.put("id",id);
        onwardsClaims.put("name", name);
        onwardsClaims.put("email", email);

        // We'll use this claim to know this is a user token
        onwardsClaims.setAudience("client");

        // we set creation time to 24hrs ago, to avoid timezone issues in the
        // browser verification of the jwt.
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.HOUR, -24);
        onwardsClaims.setIssuedAt(calendar1.getTime());

        // client JWT has 24 hrs validity from now.
        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.HOUR, 24);
        onwardsClaims.setExpiration(calendar2.getTime());

        // finally build the new jwt, using the claims we just built, signing it with our
        // signing key, and adding a key hint as kid to the encryption header, which is
        // optional, but can be used by the receivers of the jwt to know which key
        // they should verifiy it with.
        String newJwt = Jwts.builder().setHeaderParam("kid", "playerssl").setClaims(onwardsClaims)
                .signWith(SignatureAlgorithm.RS256, signingKey).compact();

        return newJwt;
    }

}
