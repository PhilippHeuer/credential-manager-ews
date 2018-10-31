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
                                String oAuth2Token = ctx.getRequest().getQueryParams().get("code");
                                String oAuth2State = ctx.getRequest().getQueryParams().get("state");

                                if (oAuth2State.contains("|")) {
                                    // contains csrf check & provider name
                                    String providerName = oAuth2State.split("|")[0];
                                    String csrfValue = oAuth2State.split("|")[1];

                                    Credential credential = new OAuth2Credential(providerName, null, oAuth2Token);
                                    this.authenticationController.getCredentialManager().addCredential(providerName, credential);
                                }

                                ctx.render("Authentication successfully!");
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
