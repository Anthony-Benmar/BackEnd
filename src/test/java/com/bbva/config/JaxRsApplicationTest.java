package com.bbva.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JaxRsApplicationTest {
    @BeforeEach
    void setUp() {

    }

    @Test
    void getClasses() {
        // Given
        JaxRsApplication jaxRsApplication = new JaxRsApplication();

        // When
        final Set<Class<?>> classes = jaxRsApplication.getClasses();

        // Then
        assertEquals(25, classes.size());
    }
}