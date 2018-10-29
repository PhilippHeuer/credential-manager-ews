# *CredentialManager* - Embedded WebServer (EWS)

# Description

A embedded WebServer for the CredentialManager to handle redirect responses and store the tokens.

Should be used for GUI Applications that don't have a website the user could be redirected to.

# Import

# Initialization

## Overwrite the auth controller when building the CredentialManager

```java
CredentialManager credentialManager = CredentialManagerBuilder.builder()
    ...
    .withAuthenticationController(new GUIAuthController())
    ...
    .build();
```

## License

Released under the [MIT License](./LICENSE).
