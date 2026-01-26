package com.animalshelter.model

import jakarta.persistence.*

@Entity
@Table(name = "species")
data class Species(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @Column(nullable = false, unique = true, length = 50)
    val name: String
)