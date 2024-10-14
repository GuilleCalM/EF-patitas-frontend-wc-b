package pe.edu.cibertec.patitas_frontend_wc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.cibertec.patitas_frontend_wc.client.AutenticationClient;
import pe.edu.cibertec.patitas_frontend_wc.dto.LoginRequestDTO;
import pe.edu.cibertec.patitas_frontend_wc.dto.LoginResponseDTO;
import pe.edu.cibertec.patitas_frontend_wc.dto.LogoutRequestDTO;
import pe.edu.cibertec.patitas_frontend_wc.dto.LogoutResponseDTO;
import pe.edu.cibertec.patitas_frontend_wc.viewmodel.LoginModel;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "http://localhost:5173")
public class LoginControllerAsync {

    @Autowired
    WebClient webClientAutenticacion;

    @PostMapping("/autenticar-async")
    public Mono<LoginResponseDTO> autenticar(@RequestBody LoginRequestDTO loginRequestDTO) {

        // validar campos de entrada
        if (loginRequestDTO.tipoDocumento() == null || loginRequestDTO.tipoDocumento().trim().length() == 0 ||
                loginRequestDTO.numeroDocumento() == null || loginRequestDTO.numeroDocumento().trim().length() == 0 ||
                loginRequestDTO.password() == null || loginRequestDTO.password().trim().length() == 0){
            return Mono.just(new LoginResponseDTO("01", "Error: Debe completar correctamente sus credenciales", "", ""));
        }

        try {

            // consumir servicio backend de autenticacion
            return webClientAutenticacion.post()
                    .uri("/login")
                    .body(Mono.just(loginRequestDTO), LoginRequestDTO.class)
                    .retrieve()
                    .bodyToMono(LoginResponseDTO.class)
                    .flatMap(response -> {

                        if(response.codigo().equals("00")){
                            return Mono.just(new LoginResponseDTO("00", "", response.nombreUsuario(), ""));
                        } else {
                            return Mono.just(new LoginResponseDTO("02", "Error: Autenticación fallida", "", ""));
                        }

                    });

        } catch(Exception e) {

            System.out.println(e.getMessage());
            return Mono.just(new LoginResponseDTO("99", "Error: Ocurrió un problema en la autenticación", "", ""));

        }

    }

    @Autowired
    AutenticationClient autenticationClient;

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDTO> logoutFeign(@RequestBody LogoutRequestDTO logoutRequestDTO) {

        if (logoutRequestDTO.tipoDocumento() == null || logoutRequestDTO.tipoDocumento().trim().isEmpty() ||
                logoutRequestDTO.numeroDocumento() == null || logoutRequestDTO.numeroDocumento().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LogoutResponseDTO(false, null,
                    "Error: No hay parametros válidos para el logout"));
        }

        try {
            ResponseEntity<LogoutResponseDTO> response = autenticationClient.logout(logoutRequestDTO);

            if (response.getBody().resultado()) {
                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new LogoutResponseDTO(false, null, "Error: No se pudo cerrar la sesión"));
            }

        } catch (Exception e) {
            System.out.println("Error en la desconexión: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LogoutResponseDTO(false, null, "Error: Ocurrió un problema al cerrar la sesión"));
        }
    }
}
