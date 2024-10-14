package pe.edu.cibertec.patitas_frontend_wc.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pe.edu.cibertec.patitas_frontend_wc.config.FeignClientConfig;
import pe.edu.cibertec.patitas_frontend_wc.dto.LoginRequestDTO;
import pe.edu.cibertec.patitas_frontend_wc.dto.LoginResponseDTO;
import pe.edu.cibertec.patitas_frontend_wc.dto.LogoutRequestDTO;
import pe.edu.cibertec.patitas_frontend_wc.dto.LogoutResponseDTO;

@FeignClient(name = "autenticacion", url = "http://localhost:8081/autenticacion", configuration = FeignClientConfig.class)
public interface AutenticationClient {

    @PostMapping("/login")
    ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO);

    @PostMapping("/logout")
    ResponseEntity<LogoutResponseDTO> logout(@RequestBody LogoutRequestDTO logoutRequestDTO);
}
