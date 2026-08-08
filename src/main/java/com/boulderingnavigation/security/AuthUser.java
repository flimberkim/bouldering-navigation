package com.boulderingnavigation.security;

/**
 * Authenticated principal carried in the SecurityContext. Deliberately not the
 * JPA {@code User} entity, so a lazy-loading proxy never leaks into the
 * security context or gets serialized by accident.
 */
public record AuthUser(Long id, String email, String nickname) {
}
