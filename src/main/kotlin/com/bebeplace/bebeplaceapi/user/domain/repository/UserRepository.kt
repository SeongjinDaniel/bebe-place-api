package com.bebeplace.bebeplaceapi.user.domain.repository

import com.bebeplace.bebeplaceapi.common.types.Email
import com.bebeplace.bebeplaceapi.user.domain.model.User
import java.util.*

interface UserRepository {
    fun save(user: User): User
    fun findById(id: UUID): User?
    fun findByEmail(email: Email): User?
    fun existsByEmail(email: Email): Boolean
    fun delete(user: User)
}