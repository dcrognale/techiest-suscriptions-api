package com.app.stripe.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "users", schema = "auth")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "email")
    private String email;

    // Agregamos un campo para guardar el nombre temporalmente en metadata si es necesario
    @Column(name = "raw_user_meta_data", columnDefinition = "jsonb")
    private String rawUserMetaData;

    // Supabase auth.users suele requerir otros campos dependiendo de cómo esté configurado,
    // pero con esto podemos probar el insert básico o adaptarlo luego.
}
