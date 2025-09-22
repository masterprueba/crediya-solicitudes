package co.com.crediya.solicitudes.secrets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AwsRdsSecret {
    private String username;
    private String password;
    private String engine;
    private String host;
    private Integer port;
    private String dbInstanceIdentifier;
    private String dbname;
    private String sslMode;
}
