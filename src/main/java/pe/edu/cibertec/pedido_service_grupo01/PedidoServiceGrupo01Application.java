package pe.edu.cibertec.pedido_service_grupo01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PedidoServiceGrupo01Application {

    public static void main(String[] args) {
        SpringApplication.run(PedidoServiceGrupo01Application.class, args);
    }
}
