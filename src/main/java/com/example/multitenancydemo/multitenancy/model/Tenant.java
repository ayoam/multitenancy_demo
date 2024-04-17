package com.example.multitenancydemo.multitenancy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {
    @Id
    @Column(name = "tenant_id")
    private String tenantId;
}
