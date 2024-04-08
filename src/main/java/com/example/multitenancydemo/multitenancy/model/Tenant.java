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
    @GeneratedValue(strategy=GenerationType.UUID)
    @Column(name = "tenant_id")
    private UUID tenantId;
    @Column(name = "schema_name")
    private String schemaName;
}
