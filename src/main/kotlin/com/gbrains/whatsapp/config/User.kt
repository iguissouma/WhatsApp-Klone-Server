package com.gbrains.whatsapp.config

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import kotlin.streams.toList

class User : UserDetails {

    private var username: String
    private var password: String? = null
    var enabled: Boolean? = null

    var roles: List<Role> = emptyList()

    constructor(username: String, password: String, enabled: Boolean?, roles: List<Role>) {
        this.username = username
        this.password = password
        this.enabled = enabled
        this.roles = roles
    }

    constructor(username: String) {
        this.username = username
    }

    override fun getUsername(): String {
        return username
    }

    fun setUsername(username: String) {
        this.username = username
    }

    override fun isAccountNonExpired(): Boolean {
        return false
    }

    override fun isAccountNonLocked(): Boolean {
        return false
    }

    override fun isCredentialsNonExpired(): Boolean {
        return false
    }

    override fun isEnabled(): Boolean {
        return this.enabled!!
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return this.roles.stream().map { authority -> SimpleGrantedAuthority(authority.name) }.toList()
    }

    @JsonIgnore
    override fun getPassword(): String? {
        return password
    }

    @JsonProperty
    fun setPassword(password: String) {
        this.password = password
    }
}
