package com.sistemabarberia.fadex_backend.modules.persona.repository;

import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.flyway.enabled=false"
})
class PersonaRepositoryTest {

    @Autowired
    private PersonaRepository personaRepository;

    private Persona persona;

    @BeforeEach
    void setUp() {

        persona = Persona.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .telefono("987654321")
                .email("juan@gmail.com")
                .build();
    }

    @Test
    void deberiaGuardarPersonaCorrectamente() {

        Persona guardado = personaRepository.save(persona);


        assertThat(guardado.getPersonaId()).isNotNull();
        assertThat(guardado.getNombre()).isEqualTo("Juan");
        assertThat(guardado.getEmail()).isEqualTo("juan@gmail.com");
    }

    @Test
    void deberiaBuscarPersonaPorId() {

        Persona guardado = personaRepository.save(persona);


        Optional<Persona> resultado = personaRepository.findById(guardado.getPersonaId());


        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Juan");
    }

    @Test
    void deberiaEliminarPersona() {

        Persona guardado = personaRepository.save(persona);


        personaRepository.delete(guardado);
        Optional<Persona> resultado = personaRepository.findById(guardado.getPersonaId());


        assertThat(resultado).isEmpty();
    }

    @Test
    void deberiaListarTodasLasPersonas() {
        personaRepository.save(persona);
        personaRepository.save(Persona.builder()
                .nombre("Ana").apellido("Lopez")
                .telefono("912345678").email("ana@gmail.com")
                .build());

        var lista = personaRepository.findAll();

        assertThat(lista).hasSize(2);
    }
}