package com.github.philippheuer.credentialmanager.webserver;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ratpack.server.RatpackServer;

@Slf4j
public class WebServer {

    public static void main(String[] args) {
        WebServer webServer = new WebServer();
        webServer.startAuthListener();
    }

    /**
     * Holds the RatPack Server Instance
     */
    private RatpackServer ratpackServer = null;

    /**
     * Starts the Auth Listener
     */
    public void startAuthListener() {
        // make sure old listeners are killed
        stopAuthListener();

        // start listener
        try {
            this.ratpackServer = RatpackServer.start(spec -> spec
                    .serverConfig(s -> s.port(8080))
                    .handlers(chain -> chain
                            .get(ctx -> ctx.render("Hello Devoxx!"))
                            .get("/oauth2", ctx -> {


                                ctx.render("Welcome");
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
