package org.parent.grpcserviceseeyaa.security.rsa;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;


@ConfigurationProperties(prefix = "jwt")
public record RSAKeyRecord(Resource rsaPublicKey, Resource rsaPrivateKey) { }
