package com.alianza.pruebatecnica.cliente.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDto {


    private Long id;

    @NotBlank(message = "La clave compartida no puede estar en blanco")
    private String sharedKey;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El telefono es obligatorio")
    private String telefono;

    @NotBlank(message = "El correo electrónico no puede estar en blanco")
    private String email;

    @NotNull(message = "La fecha de inicio no puede ser nula")
    private Date fechaInicio;

    @NotNull(message = "La fecha de fin no puede ser nula")
    private Date fechaFin;;
}
