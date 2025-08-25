package co.com.crediya.solicitudes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import co.com.crediya.solicitudes.model.cliente.gateways.ClienteRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.CatalogoPrestamoRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.usecase.crearsolicitud.CrearSolicitudUseCase;

@Configuration
@ComponentScan(basePackages = "co.com.crediya.solicitudes.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false,
        lazyInit = true)
public class UseCasesConfig {

    @Bean
    public CrearSolicitudUseCase crearSolicitudUseCase(SolicitudRepository solicitudRepository, ClienteRepository clienteRepository, CatalogoPrestamoRepository catalogoPrestamoRepository) {
        return new CrearSolicitudUseCase(solicitudRepository, clienteRepository, catalogoPrestamoRepository);
    }
}
