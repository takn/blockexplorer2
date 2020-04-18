package com.one.r.studio

import com.one.r.studio.ui.main.MainViewModel
import org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MainViewModelTest {
    private val sut = MainViewModel()
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun sanity(){
        assertEquals("12345",sut.getHeadBlock());
    }
}
