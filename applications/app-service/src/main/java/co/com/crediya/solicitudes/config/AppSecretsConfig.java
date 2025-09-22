package co.com.crediya.solicitudes.config;

import co.com.bancolombia.secretsmanager.api.GenericManagerAsync;
import co.com.bancolombia.secretsmanager.api.exceptions.SecretException;
import co.com.crediya.solicitudes.secrets.AwsRdsSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppSecretsConfig {
    @Bean
    public AwsRdsSecret awsRdsSecret(GenericManagerAsync sm,
                                     @Value("${aws.rds}") String name) throws SecretException {
        AwsRdsSecret s = sm.getSecret(name, AwsRdsSecret.class).block();
        if (s == null) throw new IllegalStateException("No se pudo leer el secret " + name);
        return s;
    }

}
