package br.com.gs.habitatzero;

import org.junit.jupiter.api.Test;

// @SpringBootTest removed — that annotation attempts to load the full application
// context (including database connection), which requires MySQL to be running.
// All meaningful business logic is tested in isolation by the service-level unit
// tests (ColonoServiceTest, EstufaServiceTest, etc.) using Mockito mocks.
class HabitatzeroApplicationTests {

    @Test
    void placeholder() {
        // intentionally empty — exists only to keep Surefire happy with a valid test class
    }
}
