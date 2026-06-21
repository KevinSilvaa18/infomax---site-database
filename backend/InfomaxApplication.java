package com.infomax.os;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class InfomaxApplication {

    public static void main(String[] args) {
        SpringApplication.run(InfomaxApplication.class, args);
    }
}

// Model Class to represent an Order of Service (OS)
class ServiceOrder {
    private Long id;
    private String clientName;
    private String clientPhone;
    private String deviceModel;
    private String reportedIssue;
    private String status;
    private String createdAt;

    public ServiceOrder() {
    }

    public ServiceOrder(Long id, String clientName, String clientPhone, String deviceModel, String reportedIssue,
            String status) {
        this.id = id;
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.deviceModel = deviceModel;
        this.reportedIssue = reportedIssue;
        this.status = status;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        this.createdAt = dtf.format(LocalDateTime.now());
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getReportedIssue() {
        return reportedIssue;
    }

    public void setReportedIssue(String reportedIssue) {
        this.reportedIssue = reportedIssue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

// REST Controller to handle frontend requests
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*") // Allows the admin.html file to communicate with this Java server
class OrderController {

    // Simulating a Database in memory for demonstration purposes
    private final List<ServiceOrder> database = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong(1024); // Starting OS number from 1024

    public OrderController() {
        // Seed the "database" with initial fake data to match our frontend
        database.add(new ServiceOrder(counter.getAndIncrement(), "Carlos Eduardo", "(19) 98888-1234", "iPhone 13 Pro",
                "Tela quebrada e touch falhando", "Aguardando Aprovação"));
        database.add(new ServiceOrder(counter.getAndIncrement(), "Mariana Silva", "(19) 97777-5678",
                "Notebook Dell Inspiron", "Não liga, led pisca", "Em Reparo"));
        database.add(new ServiceOrder(counter.getAndIncrement(), "João Pedro", "(19) 96666-9012", "Samsung S22 Ultra",
                "Troca de bateria", "Pronto p/ Retirada"));
    }

    // Endpoint to Get all Service Orders
    @GetMapping
    public ResponseEntity<List<ServiceOrder>> getAllOrders() {
        // In a real application, you would fetch from a database repository here
        return new ResponseEntity<>(database, HttpStatus.OK);
    }

    // Endpoint to Create a new Service Order
    @PostMapping
    public ResponseEntity<ServiceOrder> createOrder(@RequestBody ServiceOrder newOrder) {
        // 1. Validate the incoming data
        if (newOrder.getClientName() == null || newOrder.getDeviceModel() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // 2. Assign a new ID and default status
        newOrder.setId(counter.getAndIncrement());
        if (newOrder.getStatus() == null || newOrder.getStatus().isEmpty()) {
            newOrder.setStatus("Orçamento");
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        newOrder.setCreatedAt(dtf.format(LocalDateTime.now()));

        // 3. Save to database
        database.add(0, newOrder); // Add to the top of the list

        // 4. Return the saved object to the frontend
        return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
    }
}