# Spring Social GameOn Auth.

Implementation of GameOn Auth project, using Spring Social.

*NOTE:* Still a work in progress, but successful replacement of Gameon Auth confirmed using Docker Compose.

## Overview

Initial Urls:
- `/auth/facebook`
- `/auth/twitter`
- `/auth/github?scope=user:email`
- `/auth/google?scope=profile%20email`
- `/auth/dummy` (only active during local development)

Redirect Urls: (for configuring within social apps, prefix the host/port of this app)
- `/auth/facebook`
- `/auth/twitter`
- `/auth/github`
- `/auth/google`

Addtional Urls:
- `/auth/PublicCertificate`  serves pub cert for frontend use.

Old-Auth Compat urls:
- `/auth/FacebookAuth`
- `/auth/GoogleAuth`
- `/auth/TwitterAuth`
- `/auth/GithubAuth`
- `/auth/DummyAuth`

## Flow:

Browser goes to appropriate initial url, gets bounced to remote service to sign in, then back to redirect url, which reads tokens etc, and forwards browser to `/auth/token` endpoint.

`/auth/token` endpoint is a RestController, that uses spring social to obtain the connection and then uses that to pull unique Id, name, and email.

Where possible this uses standard spring social stuff to do its job. This works as expected for Facebook, and Google, and is pretty straight forward.

GitHub and Twitter were oddly annoying re email, and needed invocation of new endpoints, so the
appropriate Template has to be instantiated, and used to get the reply. Corresponding Java bindings
for the reply JSON are in the model package.

*New* Dummy auth!
Added a daft set of endpoints that emulate a (really dumb) OAuth2 Provider =)

- `/auth/dummy/fake/auth`
- `/auth/dummy/fake/token`

And added a dummy spring-social provider plugged in to use those URLs, and mapped the endpoints and provider to only do their magic when we're running in local dev mode (wooh!).

*Note:* All urls need to start /auth to emulate the Old-Auth context root approach, otherwise GameOn Proxy would need updating to know how to route traffic to this service. As this is currently intended to be a drop in replacement, to enable A/B testing, canary deployment etc, it was better to keep urls compatible with Old-Auth. This also includes acutators which are moved to `/auth` in this project, eg `/auth/health` )

## Testing this module in/out of Gameon.

*Note:* _This section will improve as GameOn integration is tidied up, with proper rebuild support etc_

To run standalone, (check/configure application-local.properties for your environment)
This even tries to do dynamic recompiles if you edit code while it's running..

`./gradlew bootRun`

To run as part of GameOn, (for now), edit docker-compose.yml and alter the auth: block to read

```
  auth:
    image: gameontext/gameon-auth-spring-social:latest
    volumes:
      - 'keystore:/opt/ibm/wlp/usr/servers/defaultServer/resources/security'
      - 'keystore:/opt/ol/wlp/usr/servers/defaultServer/resources/security'
      - 'keystore:/etc/cert'
    depends_on:
      kafka:
        condition: service_healthy
    env_file: gameon.${DOCKER_MACHINE_NAME}env
    container_name: auth
    networks:
      - gameontext
```
(eg basically update the image label, and add the last keystore line).

Build code.

`./gradlew build`

Build docker image

`docker build -t gameontext/gameon-auth-spring-social:latest build/docker`

(Eg, build context root for docker is `build/docker` AFTER a build has been completed, this differs from `auth-wlpcfg` in Old-Auth)

Replace running auth with new auth.. (assuming this project is present as submodule of gameon root repo.)

`docker-compose -f ../docker/docker-compose.yml stop auth`
`docker-compose -f ../docker/docker-compose.yml rm auth`
`docker-compose -f ../docker/docker-compose.yml up auth`

This will improve as time goes on..

## Flow:

*TODO*

- Old-Auth supported a redirect_url query param option, that's unsupported here currently.
- Old-Auth allowed setting a userid as part of the query param, which is also unsupported here.
- ~~Dummy Auth support!!~~
- ~~Update yaml's to pull the social creds from env vars.~~
- ~~Have `/token` endpoint controller create and sign a jwt as the old GameOn Auth used to.~~
- ~~Have `/token` endpoint foward to the auth success url appending the token~~
- ~~Add gameon auth service compatible initial url rest handler that replies with redirects to the new initial urls?~~
- ~~Dockerise the whole thing using Game On Auth as a template.~~
- Dynamically adapt login support based on configured providers?
- ~~Actuators for healthcheck ?~~
- Tests? Maybe?
- Travis build etc.



