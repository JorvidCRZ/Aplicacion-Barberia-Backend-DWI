package com.sistemabarberia.fadex_backend.modules.persona.repository;

import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;
import com.sistemabarberia.fadex_backend.modules.barbero.repository.BarberoRepository;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
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
public class ClienteRepositoryTest {
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private PersonaRepository personaRepository;

    private Cliente cliente;
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

        cliente = Cliente.builder()
                .clienteId(1)
                .persona(persona)
                .fechaRegistro(LocalDate.now())
                .build();
    }


    @Test
    void deberiaGuardarClienteCorrectamente() {
        Cliente guardado = clienteRepository.save(cliente);
        assertThat(guardado.getClienteId()).isNotNull();
        assertThat(guardado.getFechaRegistro()).isEqualTo(LocalDate.now());
    }

    @Test
    void deberiaBuscarClientePorId() {
        Cliente guardado = clienteRepository.save(cliente);
        Optional<Cliente> resultado = clienteRepository.findById(guardado.getClienteId());
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getClienteId()).isEqualTo(guardado.getClienteId());
    }

    @Test
    void deberiaEliminarCliente() {

        Cliente guardado = clienteRepository.save(cliente);
        clienteRepository.delete(guardado);
        Optional<Cliente> resultado = clienteRepository.findById(guardado.getClienteId());

        assertThat(resultado).isEmpty();
    }

    @Test
    void deberiaListarTodosLosClientes() {
        clienteRepository.save(cliente);
        var lista = clienteRepository.findAll();
        assertThat(lista).hasSize(1);
    }
}




