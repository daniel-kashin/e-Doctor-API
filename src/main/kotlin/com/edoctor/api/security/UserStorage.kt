package com.edoctor.api.security

import com.edoctor.api.entities.User
import org.springframework.stereotype.Repository

@Repository
class UserStorage {

    private val hashMap: HashMap<String, User> = hashMapOf()

    fun save(user: User) = hashMap.put(user.email, user)

    fun findUserByEmail(email: String): User? = hashMap[email]

}