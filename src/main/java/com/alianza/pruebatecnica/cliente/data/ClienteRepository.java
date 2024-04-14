package com.alianza.pruebatecnica.cliente.data;

import com.alianza.pruebatecnica.cliente.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {


    Optional<Cliente> findBySharedKey(String sharedKey);

    Optional<Cliente> findByEmail(String email);


}
