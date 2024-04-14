package com.alianza.pruebatecnica.cliente.manager;



import com.alianza.pruebatecnica.cliente.dto.ClienteDto;

import java.util.List;

public interface ClienteManager {

    List<ClienteDto> obtenerTodosLosCliente();
    ClienteDto obtenetClientePorSharedKey(String sharedKey);
    ClienteDto guardarCliente(ClienteDto cliente);
    ClienteDto actualizarCliente(ClienteDto cliente);

}
