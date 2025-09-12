package co.com.crediya.solicitudes.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;


import co.com.crediya.solicitudes.model.cliente.gateways.ClienteRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.CapacidadEndeudamientoRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.CatalogoPrestamoRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;


class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        new ApplicationContextRunner()
                .withUserConfiguration(UseCasesConfig.class)
                .withBean(SolicitudRepository.class, () -> Mockito.mock(SolicitudRepository.class))
                .withBean(ClienteRepository.class, () -> Mockito.mock(ClienteRepository.class))
                .withBean(CatalogoPrestamoRepository.class, () -> Mockito.mock(CatalogoPrestamoRepository.class))
                .withBean(CapacidadEndeudamientoRepository.class, () -> Mockito.mock(CapacidadEndeudamientoRepository.class))
                .run(context -> {
                    assertThat(context).hasBean("crearSolicitudUseCase");
                });
    }


}