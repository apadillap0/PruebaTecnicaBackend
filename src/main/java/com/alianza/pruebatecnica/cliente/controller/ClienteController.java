package com.alianza.pruebatecnica.cliente.controller;



import com.alianza.pruebatecnica.cliente.dto.ClienteDto;
import com.alianza.pruebatecnica.cliente.manager.ClienteManager;
import com.alianza.pruebatecnica.core.dto.GenericResponseDTO;
import com.alianza.pruebatecnica.core.utils.UtilConstantes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("cliente")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ClienteController {

    @Autowired
    private  ClienteManager clienteManager;
    private GenericResponseDTO genericResponse;

    @GetMapping()
    public ResponseEntity<GenericResponseDTO> obtenerClientes() {
        genericResponse = new GenericResponseDTO(clienteManager.obtenerTodosLosCliente(), HttpStatus.OK);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/buscar")
    public ResponseEntity<GenericResponseDTO> buscarClientePorSharedKey(@RequestParam String sharedKey) {
        genericResponse = new GenericResponseDTO(clienteManager.obtenetClientePorSharedKey(sharedKey), HttpStatus.OK);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<GenericResponseDTO> crearCliente(@RequestBody ClienteDto clienteDto) {
        genericResponse = new GenericResponseDTO(clienteManager.guardarCliente(clienteDto), HttpStatus.OK);
        genericResponse.setTitle(UtilConstantes.TITTLE_CREATED);
        genericResponse.setMessage(UtilConstantes.RESPONSE_CREATED);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<GenericResponseDTO> actualizarCliente(@RequestBody ClienteDto clienteDto) {
        genericResponse = new GenericResponseDTO(clienteManager.actualizarCliente(clienteDto), HttpStatus.OK);
        genericResponse.setTitle(UtilConstantes.TITTLE_UPDATE);
        genericResponse.setMessage(UtilConstantes.RESPONSE_UPDATE);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

}
