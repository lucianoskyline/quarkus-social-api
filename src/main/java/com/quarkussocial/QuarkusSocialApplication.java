package com.quarkussocial;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(
                title = "API Quarkus Social",
                version = "1.0.0",
                contact = @Contact(
                        name = "Luciano Lopes"
                ),
                license = @License(
                        name = "Apache 2.0"
                )
        )
)
public class QuarkusSocialApplication extends Application {
}
