package pe.edu.cibertec.patitas_frontend_wc.dto;

import java.util.Date;

public record LogoutResponseDTO(boolean resultado, Date fecha, String mensajeError) {
}
