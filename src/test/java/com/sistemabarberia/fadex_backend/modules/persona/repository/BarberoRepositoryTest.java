package com.sistemabarberia.fadex_backend.modules.persona.repository;

import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;
import com.sistemabarberia.fadex_backend.modules.barbero.repository.BarberoRepository;
import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.flyway.enabled=false"  //
})
public class BarberoRepositoryTest {
    @Autowired
    private BarberoRepository barberoRepository;
    @Autowired
    private PersonaRepository personaRepository;

    private Barbero barbero;
    private Persona persona;

    @BeforeEach
    void setUp() {

        persona = Persona.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .telefono("987654321")
                .email("juan@gmail.com")
                .build();

        persona = personaRepository.save(persona);

        barbero = Barbero.builder()
                .sueldo(BigDecimal.valueOf(1200))
                .experiencia(2)
                .fotoUrl("...")
                .persona(persona) // ahora sí válida
                .comision(BigDecimal.valueOf(0.13))
                .fechaIngreso(LocalDate.now())
                .descripcion("")
                .build();
    }


    @Test
    void deberiaGuardarBarberoCorrectamente() {
        Barbero guardado = barberoRepository.save(barbero);
        assertThat(guardado.getBarberoId()).isNotNull();
        assertThat(guardado.getExperiencia()).isEqualTo(2);
        assertThat(guardado.getFotoUrl()).isEqualTo("...");
    }

    @Test
    void deberiaBuscarBarberoPorId() {
        Barbero guardado = barberoRepository.save(barbero);
        Optional<Barbero> resultado = barberoRepository.findById(guardado.getBarberoId());
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getBarberoId()).isEqualTo(guardado.getBarberoId());
    }

    @Test
    void deberiaEliminarBarbero() {

        Barbero guardado = barberoRepository.save(barbero);
        barberoRepository.delete(guardado);
        Optional<Barbero> resultado = barberoRepository.findById(guardado.getBarberoId());

        assertThat(resultado).isEmpty();
    }

    @Test
    void deberiaListarTodosLosBarberos() {
        barberoRepository.save(barbero);
        var lista = barberoRepository.findAll();
        assertThat(lista).hasSize(1);
    }
}
