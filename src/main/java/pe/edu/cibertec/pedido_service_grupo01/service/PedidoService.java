package pe.edu.cibertec.pedido_service_grupo01.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.cibertec.pedido_service_grupo01.dto.DetallePedidoDTO;
import pe.edu.cibertec.pedido_service_grupo01.dto.PagoDTO;
import pe.edu.cibertec.pedido_service_grupo01.dto.PedidoResponseDTO;
import pe.edu.cibertec.pedido_service_grupo01.dto.ResumenConsolidadoClienteDTO;
import pe.edu.cibertec.pedido_service_grupo01.feign.PagoFeignClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PagoFeignClient pagoFeignClient;
    private final Map<Long, String> nombresClientes = Map.of(
            1L, "Juan Perez",
            2L, "Maria Lopez",
            3L, "Carlos Ramirez"
    );

    private final List<PedidoResponseDTO> listaPedidos = new ArrayList<>();

    public PedidoService() {
        List<DetallePedidoDTO> detalles100 = new ArrayList<>(List.of(
                new DetallePedidoDTO(1L, "Laptop Lenovo", 1, 200.00, 200.00),
                new DetallePedidoDTO(2L, "Mouse Logitech", 2, 25.00, 50.00)
        ));
        listaPedidos.add(new PedidoResponseDTO(100L, "SEG-2026-0100",
                LocalDateTime.of(2026, 10, 5, 14, 0, 0),
                1L, "PENDIENTE", detalles100, 250.00));
        List<DetallePedidoDTO> detalles101 = new ArrayList<>(List.of(
                new DetallePedidoDTO(3L, "Teclado Redragon", 1, 89.90, 89.90)
        ));
        listaPedidos.add(new PedidoResponseDTO(101L, "SEG-2026-0101",
                LocalDateTime.of(2026, 10, 6, 10, 0, 0),
                2L, "PENDIENTE", detalles101, 89.90));
        List<DetallePedidoDTO> detalles102 = new ArrayList<>(List.of(
                new DetallePedidoDTO(4L, "Monitor Samsung 27\"", 1, 350.00, 350.00),
                new DetallePedidoDTO(5L, "Cable HDMI", 2, 15.00, 30.00),
                new DetallePedidoDTO(6L, "Soporte Monitor", 1, 70.00, 70.00)
        ));
        listaPedidos.add(new PedidoResponseDTO(102L, "SEG-2026-0102",
                LocalDateTime.of(2026, 10, 7, 16, 30, 0),
                1L, "PENDIENTE", detalles102, 450.00));
        List<DetallePedidoDTO> detalles103 = new ArrayList<>(List.of(
                new DetallePedidoDTO(7L, "Audifonos HyperX", 1, 120.50, 120.50)
        ));
        listaPedidos.add(new PedidoResponseDTO(103L, "SEG-2026-0103",
                LocalDateTime.of(2026, 10, 8, 8, 45, 0),
                3L, "PENDIENTE", detalles103, 120.50));
    }
    private void calcularMontos(PedidoResponseDTO pedido) {
        if (pedido == null || pedido.getListaDetalles() == null) {
            return;
        }
        double total = 0.0;
        for (DetallePedidoDTO d : pedido.getListaDetalles()) {
            double sub = (d.getCantidad() != null ? d.getCantidad() : 0) *
                         (d.getPrecioUnitario() != null ? d.getPrecioUnitario() : 0.0);
            sub = Math.round(sub * 100.0) / 100.0;
            d.setSubtotal(sub);
            total += sub;
        }
        pedido.setMontoTotalCalculado(Math.round(total * 100.0) / 100.0);
    }

    public PedidoResponseDTO obtenerPedidoPorId(Long idPedido) {
        if (idPedido == null) {
            return null;
        }
        return listaPedidos.stream()
                .filter(p -> idPedido.equals(p.getIdPedido()))
                .findFirst()
                .map(p -> {
                    calcularMontos(p);
                    return p;
                })
                .orElse(null);
    }
    public PedidoResponseDTO verificarYCompletar(Long idPedido) {
        PedidoResponseDTO pedido = obtenerPedidoPorId(idPedido);
        if (pedido == null) {
            return null;
        }
        PagoDTO pago = null;
        try {
            pago = pagoFeignClient.obtenerPagoPorPedido(idPedido);
        } catch (Exception ex) {
            pago = null;
        }

        if (pago != null && "APROBADO".equalsIgnoreCase(pago.getEstadoPago())) {
            double dif = Math.abs(pago.getMontoPagado() - pedido.getMontoTotalCalculado());
            if (dif < 0.001) {
                pedido.setEstadoPedido("COMPLETADO");
            }
        }
        return pedido;
    }

    public ResumenConsolidadoClienteDTO consolidadoCliente(Long clienteId) {
        if (clienteId == null) {
            return null;
        }

        List<PedidoResponseDTO> pedidosCliente = listaPedidos.stream()
                .filter(p -> clienteId.equals(p.getClienteId()))
                .peek(this::calcularMontos)
                .collect(Collectors.toList());

        List<PagoDTO> pagosCliente;
        try {
            pagosCliente = pagoFeignClient.obtenerPagosPorCliente(clienteId);
            if (pagosCliente == null) {
                pagosCliente = Collections.emptyList();
            }
        } catch (Exception ex) {
            pagosCliente = Collections.emptyList();
        }

        double totalAcumuladoPedidos = pedidosCliente.stream()
                .mapToDouble(p -> p.getMontoTotalCalculado() != null ? p.getMontoTotalCalculado() : 0.0)
                .sum();
        totalAcumuladoPedidos = Math.round(totalAcumuladoPedidos * 100.0) / 100.0;

        double totalPagadoAprobado = pagosCliente.stream()
                .filter(p -> "APROBADO".equalsIgnoreCase(p.getEstadoPago()))
                .mapToDouble(p -> p.getMontoPagado() != null ? p.getMontoPagado() : 0.0)
                .sum();
        totalPagadoAprobado = Math.round(totalPagadoAprobado * 100.0) / 100.0;
        String estadoCuenta;
        if (pedidosCliente.isEmpty()) {
            estadoCuenta = "SIN_PEDIDOS";
        } else if (Math.abs(totalAcumuladoPedidos - totalPagadoAprobado) < 0.001) {
            estadoCuenta = "AL_DIA";
        } else {
            estadoCuenta = "CON_DEUDAS";
        }
        String nombreCliente = nombresClientes.getOrDefault(clienteId, "Cliente #" + clienteId);
        return new ResumenConsolidadoClienteDTO(
                clienteId,
                nombreCliente,
                pedidosCliente.size(),
                totalAcumuladoPedidos,
                pedidosCliente,
                pagosCliente,
                estadoCuenta
        );
    }
}
