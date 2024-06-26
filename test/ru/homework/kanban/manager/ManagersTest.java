package ru.homework.kanban.manager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Менеджер")
class ManagersTest {

    @Test
    @DisplayName("getDefaultHistory не равен null при возврате")
    void shouldGetDefaultHistoryNotNull(){
        assertNotNull(Managers.getDefaultHistory());
    }

    @Test
    @DisplayName("getDefault не равен null при возврате")
    void shouldGetDefaultNotNull2(){
        assertNotNull(Managers.getDefault());
    }
}