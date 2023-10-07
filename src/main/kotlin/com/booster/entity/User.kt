package com.booster.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class User (
    @Id
    var email: String,
    var name: String,
    var password: String
) {
    constructor() : this("", "", "")
}
