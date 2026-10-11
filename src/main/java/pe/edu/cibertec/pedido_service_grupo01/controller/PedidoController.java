package pe.edu.cibertec.pedido_service_grupo01.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.cibertec.pedido_service_grupo01.dto.PedidoResponseDTO;
import pe.edu.cibertec.pedido_service_grupo01.dto.ResumenConsolidadoClienteDTO;
import pe.edu.cibertec.pedido_service_grupo01.service.PedidoService;

import java.util.LinkedHashMap;
import java.util.Map;

@RefreshScope
@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Value("${ecom.impuesto-igv:18}")
    private String impuestoIgv;

    @Value("${ecom.monto-minimo-envio-gratis:100}")
    private String montoMinimoEnvioGratis;

    @Value("${ecom.mensaje-bienvenida:Bienvenido al sistema de pedidos (Local)}")
    private String mensajeBienvenida;

    @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/pedidos_default}")
    private String datasourceUrl;

    @GetMapping("/{idPedido}")
    public ResponseEntity<PedidoResponseDTO> obtenerPedido(@PathVariable Long idPedido) {
        PedidoResponseDTO pedido = pedidoService.obtenerPedidoPorId(idPedido);
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/verificar-y-completar/{idPedido}")
    public ResponseEntity<PedidoResponseDTO> verificarYCompletar(@PathVariable Long idPedido) {
        PedidoResponseDTO pedido = pedidoService.verificarYCompletar(idPedido);
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/consolidado-cliente/{clienteId}")
    public ResponseEntity<ResumenConsolidadoClienteDTO> consolidadoCliente(@PathVariable Long clienteId) {
        ResumenConsolidadoClienteDTO resumen = pedidoService.consolidadoCliente(clienteId);
        if (resumen == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resumen);
    }

    @GetMapping("/configuracion")
    public ResponseEntity<Map<String, String>> obtenerConfiguracion() {
        Map<String, String> configuracion = new LinkedHashMap<>();
        configuracion.put("impuesto-igv", impuestoIgv);
        configuracion.put("monto-minimo-envio-gratis", montoMinimoEnvioGratis);
        configuracion.put("mensaje-bienvenida", mensajeBienvenida);
        configuracion.put("datasource-url", datasourceUrl);
        return ResponseEntity.ok(configuracion);
    }
}
