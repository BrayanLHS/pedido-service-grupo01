package pe.edu.cibertec.pedido_service_grupo01.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pe.edu.cibertec.pedido_service_grupo01.dto.PagoDTO;

import java.util.List;

@FeignClient(name = "PAGO-SERVICE-GRUPO01")
public interface PagoFeignClient {

    @GetMapping("/api/v1/pagos/pedido/{idPedido}")
    PagoDTO obtenerPagoPorPedido(@PathVariable("idPedido") Long idPedido);
    @GetMapping("/api/v1/pagos/cliente/{clienteId}")
    List<PagoDTO> obtenerPagosPorCliente(@PathVariable("clienteId") Long clienteId);
}
