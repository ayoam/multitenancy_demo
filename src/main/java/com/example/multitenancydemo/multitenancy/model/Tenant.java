package com.example.multitenancydemo.multitenancy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "tenant_id")
    private Long tenantId;
    @Column(name = "schema_name")
    private String schemaName;
}
