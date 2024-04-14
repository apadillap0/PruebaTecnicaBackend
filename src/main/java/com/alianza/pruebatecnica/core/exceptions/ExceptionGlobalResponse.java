package com.alianza.pruebatecnica.core.exceptions;


import com.alianza.pruebatecnica.core.dto.GenericResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ExceptionGlobalResponse {

    GenericResponseDTO result;

    @ExceptionHandler(ResponseStatusException.class)
    protected ResponseEntity<Object> handleHttpServer(ResponseStatusException ex) {
        String[] datos = ex.getMessage().split("\"");
        String mensaje = ex.getMessage();
        for (String cadena:datos) {
            if(!cadena.contains("SERVER")){
                mensaje = cadena;
            }
        }
        HttpStatus httpStatus = HttpStatus.valueOf(ex.getStatusCode().value());
        result = new GenericResponseDTO("Error" , false,  mensaje, httpStatus);
        return new ResponseEntity<>(result, httpStatus);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<GenericResponseDTO> exception(Exception e) {
        result = new GenericResponseDTO("Error" , false, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
