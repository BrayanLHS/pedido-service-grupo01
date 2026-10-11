package pe.edu.cibertec.pedido_service_grupo01.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoResponseDTO {
    private Long idPedido;
    private String codigoSeguimiento;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaEmision;

    private Long clienteId;
    private String estadoPedido;
    private List<DetallePedidoDTO> listaDetalles = new ArrayList<>();
    private Double montoTotalCalculado;

    public PedidoResponseDTO() {
    }

    public PedidoResponseDTO(Long idPedido, String codigoSeguimiento, LocalDateTime fechaEmision,
                             Long clienteId, String estadoPedido, List<DetallePedidoDTO> listaDetalles,
                             Double montoTotalCalculado) {
        this.idPedido = idPedido;
        this.codigoSeguimiento = codigoSeguimiento;
        this.fechaEmision = fechaEmision;
        this.clienteId = clienteId;
        this.estadoPedido = estadoPedido;
        this.listaDetalles = listaDetalles != null ? listaDetalles : new ArrayList<>();
        this.montoTotalCalculado = montoTotalCalculado;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public String getCodigoSeguimiento() {
        return codigoSeguimiento;
    }

    public void setCodigoSeguimiento(String codigoSeguimiento) {
        this.codigoSeguimiento = codigoSeguimiento;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getEstadoPedido() {
        return estadoPedido;
    }

    public void setEstadoPedido(String estadoPedido) {
        this.estadoPedido = estadoPedido;
    }

    public List<DetallePedidoDTO> getListaDetalles() {
        return listaDetalles;
    }

    public void setListaDetalles(List<DetallePedidoDTO> listaDetalles) {
        this.listaDetalles = listaDetalles;
    }

    public Double getMontoTotalCalculado() {
        return montoTotalCalculado;
    }

    public void setMontoTotalCalculado(Double montoTotalCalculado) {
        this.montoTotalCalculado = montoTotalCalculado;
    }
}
