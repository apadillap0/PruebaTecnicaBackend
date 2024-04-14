package com.alianza.pruebatecnica.cliente.manager;

import com.alianza.pruebatecnica.cliente.data.ClienteRepository;
import com.alianza.pruebatecnica.cliente.dto.ClienteDto;
import com.alianza.pruebatecnica.cliente.entity.Cliente;
import com.alianza.pruebatecnica.core.manager.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteManagerImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private Log log;

    @InjectMocks
    private ClienteManagerImpl clienteManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testObtenerTodosLosCliente() {
        List<Cliente> clientes = Arrays.asList(
                new Cliente(1L, "123456", "John Doe","3046613922", "johndoe@example.com", new Date(), new Date()),
                new Cliente(2L, "789012", "Jane Smith","3046613922", "janesmith@example.com", new Date(), new Date())
        );
        when(clienteRepository.findAll()).thenReturn(clientes);

        List<ClienteDto> clienteDtoList = clienteManager.obtenerTodosLosCliente();


        assertEquals(2, clienteDtoList.size());
        assertEquals("John Doe", clienteDtoList.get(0).getNombre());
        assertEquals("Jane Smith", clienteDtoList.get(1).getNombre());
        verify(log, times(2)).info(anyString());
    }

    @Test
    void testObtenetClientePorSharedKey_ClienteEncontrado() {
        String sharedKey = "123456";
        Cliente cliente = new Cliente(1L, sharedKey, "John Doe","3046613922", "johndoe@example.com", new Date(), new Date());
        when(clienteRepository.findBySharedKey(sharedKey)).thenReturn(Optional.of(cliente));

        ClienteDto clienteDto = clienteManager.obtenetClientePorSharedKey(sharedKey);

        assertNotNull(clienteDto);
        assertEquals("John Doe", clienteDto.getNombre());
        verify(log).info("Obteniendo cliente por Shared Key", sharedKey);
        verify(log).info("Cliente encontrado por Shared Key", sharedKey);
    }

    @Test
    void testObtenetClientePorSharedKey_ClienteNoEncontrado() {
        String sharedKeyNoExistente = "clave_no_existente";
        when(clienteRepository.findBySharedKey(eq(sharedKeyNoExistente))).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            clienteManager.obtenetClientePorSharedKey(sharedKeyNoExistente);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }


    @Test
    public void testGuardarClienteExitosamente() {
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setSharedKey("abc123");
        clienteDto.setNombre("Juan Perez");
        clienteDto.setTelefono("3046613922");
        clienteDto.setEmail("juan@example.com");
        clienteDto.setFechaInicio(new Date());
        clienteDto.setFechaFin(new Date());

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setSharedKey("abc123");
        cliente.setNombre("Juan Perez");
        cliente.setTelefono("3046613922");
        cliente.setEmail("juan@example.com");

        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        ClienteDto resultado = clienteManager.guardarCliente(clienteDto);

        assertEquals("Juan Perez", resultado.getNombre());
        assertEquals("juan@example.com", resultado.getEmail());
        assertEquals("abc123", resultado.getSharedKey());
        verify(log, times(1)).info("Guardando nuevo cliente", clienteDto.getNombre());
        verify(log, times(1)).info("Cliente guardado exitosamente");
    }


    @Test
    public void testGuardarClienteConEmailExistente() {
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setEmail("existente@example.com");
        clienteDto.setNombre("Nombre");
        clienteDto.setTelefono("3046613922");
        clienteDto.setSharedKey("sharedKey");
        clienteDto.setFechaInicio(new Date());
        clienteDto.setFechaFin(new Date());
        Cliente clienteExistente = new Cliente();
        clienteExistente.setId(2L);
        when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.of(clienteExistente));
        assertThrows(ResponseStatusException.class, () -> clienteManager.guardarCliente(clienteDto));
    }

    @Test
    public void testGuardarClienteConSharedKeyExistente() {
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setEmail("nuevo@example.com");
        clienteDto.setNombre("Nombre");
        clienteDto.setTelefono("3046613922");
        clienteDto.setSharedKey("sharedKeyExistente");
        clienteDto.setFechaInicio(new Date());
        clienteDto.setFechaFin(new Date());
        Cliente clienteExistente = new Cliente();
        clienteExistente.setId(2L);
        when(clienteRepository.findBySharedKey(anyString())).thenReturn(Optional.of(clienteExistente));
        assertThrows(ResponseStatusException.class, () -> clienteManager.guardarCliente(clienteDto));
    }

    @Test
    public void testGuardarClienteConDatosInvalidos() {
        ClienteDto clienteInvalido = new ClienteDto();
        clienteInvalido.setSharedKey("");
        clienteInvalido.setNombre("");
        clienteInvalido.setTelefono("");
        clienteInvalido.setEmail("");
        clienteInvalido.setFechaInicio(null);
        clienteInvalido.setFechaFin(null);
        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class,
                () -> clienteManager.guardarCliente(clienteInvalido),
                "Se esperaba que lanzara una ResponseStatusException debido a datos inválidos"
        );
        assertEquals(HttpStatus.CONFLICT, thrown.getStatusCode());
        assertTrue(thrown.getMessage().contains("La clave compartida no puede estar en blanco"));
        assertTrue(thrown.getMessage().contains("El nombre es obligatorio"));
        assertTrue(thrown.getMessage().contains("El correo electrónico no puede estar en blanco"));
        assertTrue(thrown.getMessage().contains("La fecha de inicio no puede ser nula"));
        assertTrue(thrown.getMessage().contains("La fecha de fin no puede ser nula"));
        verify(log).warm(anyString());
    }

    @Test
    public void testActualizarClienteExitosamente() {
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setId(1L);
        clienteDto.setSharedKey("nuevaSharedKey");
        clienteDto.setNombre("Nombre Actualizado");
        clienteDto.setTelefono("3046613922");
        clienteDto.setEmail("email@actualizado.com");
        clienteDto.setFechaInicio(new Date());
        clienteDto.setFechaFin(new Date());
        Cliente clienteExistente = new Cliente();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArguments()[0]);
        ClienteDto resultado = clienteManager.actualizarCliente(clienteDto);
        assertEquals(clienteDto.getNombre(), resultado.getNombre());
        verify(log, times(1)).info("Actualizando cliente", clienteDto.getId().toString());
        verify(log, times(1)).info("Cliente actualizado exitosamente");
    }

    @Test
    public void testActualizarClienteNoEncontrado() {
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setId(999L);
        clienteDto.setSharedKey("nuevaSharedKey");
        clienteDto.setNombre("Nombre Actualizado");
        clienteDto.setTelefono("3046613922");
        clienteDto.setEmail("email@actualizado.com");
        clienteDto.setFechaInicio(new Date());
        clienteDto.setFechaFin(new Date());
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());
        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class,
                () -> clienteManager.actualizarCliente(clienteDto),
                "Se esperaba que lanzara una ResponseStatusException"
        );
        assertEquals(HttpStatus.CONFLICT, thrown.getStatusCode());
        verify(clienteRepository).findById(999L);
        verify(log).info(eq("Actualizando cliente"), anyString());
    }
}
