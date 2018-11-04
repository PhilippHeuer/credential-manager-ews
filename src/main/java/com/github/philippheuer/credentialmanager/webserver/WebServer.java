package com.github.philippheuer.credentialmanager.webserver;

import com.github.philippheuer.credentialmanager.domain.AuthenticationController;
import com.github.philippheuer.credentialmanager.domain.Credential;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ratpack.server.RatpackServer;

@Slf4j
public class WebServer {

    /**
     * Holds the Auth Controller
     */
    @Setter
    private AuthenticationController authenticationController;

    /**
     * Holds the RatPack Server Instance
     */
    private RatpackServer ratpackServer = null;

    /**
     * Server Port
     */
    private final Integer httpPort = 31921;

    /**
     * Starts the Auth Listener
     */
    public void startAuthListener() {
        // make sure old listeners are killed
        stopAuthListener();

        // start listener
        try {
            this.ratpackServer = RatpackServer.start(spec -> spec
                    .serverConfig(s -> s.port(httpPort))
                    .handlers(chain -> chain
                            .get(ctx -> ctx.render("Temporary OAuth2 Server!"))
                            .get("process_oauth2", ctx -> {
                                try {
                                    String oAuth2Code = ctx.getRequest().getQueryParams().get("code");
                                    String oAuth2State = ctx.getRequest().getQueryParams().get("state");
                                    log.debug("Received oauth2 request with code " + oAuth2Code + " and state " + oAuth2State);

                                    if (oAuth2State.contains("|")) {
                                        // contains csrf check & provider name
                                        String providerName = oAuth2State.split("\\|")[0];
                                        String csrfValue = oAuth2State.split("\\|")[1];
                                        log.trace("Provider Name: " + providerName);
                                        log.trace("CSRF: " + csrfValue);

                                        // add credential
                                        log.debug("Trying to find OAuth2IdentityProvider by Name [" + providerName + "]");
                                        OAuth2Credential credential = authenticationController.getCredentialManager().getOAuth2IdentityProviderByName(providerName).get().getCredentialByCode(oAuth2Code);
                                        this.authenticationController.getCredentialManager().addCredential(providerName, credential);
                                    }

                                    ctx.render("Authentication successfully!");
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    ctx.render("Error: " + ex.getMessage());
                                }
                            })
                    )
            );
        } catch (java.net.BindException ex) {
            log.error(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Stops the Auth Listener
     */
    @SneakyThrows
    public void stopAuthListener() {
        if (ratpackServer != null) {
            ratpackServer.stop();
            ratpackServer = null;
        }
    }


}
