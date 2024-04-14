package com.alianza.pruebatecnica.cliente.manager;



import com.alianza.pruebatecnica.cliente.data.ClienteRepository;
import com.alianza.pruebatecnica.cliente.dto.ClienteDto;
import com.alianza.pruebatecnica.cliente.entity.Cliente;
import com.alianza.pruebatecnica.cliente.utils.MensajeClienteEnum;
import com.alianza.pruebatecnica.core.manager.Log;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import java.util.Set;

@Service
public class ClienteManagerImpl implements ClienteManager{


    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private Log log;


    @Override
    public List<ClienteDto> obtenerTodosLosCliente() {
        log.info("Obteniendo todos los clientes.");
        List<ClienteDto> clienteDtoList = clienteRepository.findAll().stream()
                .map(this::mapearClienteBasico)
                .collect(Collectors.toList());
        log.info("Clientes obtenidos exitosamente.");
        return clienteDtoList;
    }

    @Override
    public ClienteDto obtenetClientePorSharedKey(String sharedKey) {
        log.info("Obteniendo cliente por Shared Key", sharedKey);
        Optional<Cliente> clienteOptional = clienteRepository.findBySharedKey(sharedKey);
        if(clienteOptional.isPresent()){
            log.info("Cliente encontrado por Shared Key", sharedKey);
            return mapearClienteBasico(clienteOptional.get());
        }
        String mensajeError = MensajeClienteEnum.ERROR204.getMensajeConParametros(sharedKey);
        log.error(mensajeError);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, mensajeError);
    }

    @Override
    public ClienteDto guardarCliente(ClienteDto clienteDto) {
        log.info("Guardando nuevo cliente", clienteDto.getNombre());
        Cliente cliente = construirClienteNuevo(clienteDto);
        validarCliente(clienteDto);
        Cliente clienteGuardado = clienteRepository.save(cliente);
        log.info("Cliente guardado exitosamente");
        return mapearClienteBasico(clienteGuardado);
    }

    private Cliente construirClienteNuevo(ClienteDto clienteDto){
        Cliente cliente =  Cliente.builder()
                .sharedKey(clienteDto.getSharedKey())
                .telefono(clienteDto.getTelefono())
                .nombre(clienteDto.getNombre())
                .email(clienteDto.getEmail())
                .fechaInicio(clienteDto.getFechaInicio())
                .fechaFin(clienteDto.getFechaFin())
                .build();
        return cliente;
    }

    private void validarCliente(ClienteDto clienteDto){
            Set<ConstraintViolation<ClienteDto>> violations = validator.validate(clienteDto);
            if (!violations.isEmpty()) {
                String errorMessage = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining("; "));
                log.warm(errorMessage);
                throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
            }

            Optional<Cliente> clienteByEmail = clienteRepository.findByEmail(clienteDto.getEmail());
            clienteByEmail.ifPresent(c -> {
                if (!c.getId().equals(clienteDto.getId())) {
                    String mensajeError = MensajeClienteEnum.ERROR202.getMensajeConParametros(clienteDto.getEmail());
                    log.warm(mensajeError);
                    throw new ResponseStatusException(HttpStatus.CONFLICT, mensajeError);
                }
            });

            Optional<Cliente> clienteBySharedKey = clienteRepository.findBySharedKey(clienteDto.getSharedKey());
            clienteBySharedKey.ifPresent(c -> {
                if (!c.getId().equals(clienteDto.getId())) {
                    String mensajeError = MensajeClienteEnum.ERROR201.getMensajeConParametros(clienteDto.getSharedKey());
                    log.warm(mensajeError);
                    throw new ResponseStatusException(HttpStatus.CONFLICT, mensajeError);
                }
            });
    }

    @Override
    public ClienteDto actualizarCliente(ClienteDto clienteDto) {
        log.info("Actualizando cliente", clienteDto.getId().toString());
        validarCliente(clienteDto);
        Optional<Cliente> clienteOptional = clienteRepository.findById(clienteDto.getId());
        if(clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();
            cliente.setSharedKey(clienteDto.getSharedKey());
            cliente.setNombre(clienteDto.getNombre());
            cliente.setTelefono(clienteDto.getTelefono());
            cliente.setEmail(clienteDto.getEmail());
            cliente.setFechaInicio(clienteDto.getFechaInicio());
            cliente.setFechaFin(clienteDto.getFechaFin());
            Cliente clienteActualizado = clienteRepository.save(cliente);
            log.info("Cliente actualizado exitosamente");
            return mapearClienteBasico(clienteActualizado);
        }
        String mensajeError = MensajeClienteEnum.ERROR203.getMensajeConParametros();
        log.error(mensajeError);
        throw new ResponseStatusException(HttpStatus.CONFLICT, mensajeError);
    }

    private ClienteDto mapearClienteBasico(Cliente cliente) {
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setId(cliente.getId());
        clienteDto.setSharedKey(cliente.getSharedKey());
        clienteDto.setNombre(cliente.getNombre());
        clienteDto.setTelefono(cliente.getTelefono());
        clienteDto.setEmail(cliente.getEmail());
        clienteDto.setFechaInicio(cliente.getFechaInicio());
        clienteDto.setFechaFin(cliente.getFechaFin());
        return clienteDto;
    }
}
