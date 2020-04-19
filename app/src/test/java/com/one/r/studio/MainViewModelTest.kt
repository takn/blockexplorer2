package com.one.r.studio

import com.one.r.studio.ui.main.MainViewModel
import kotlinx.coroutines.test.runBlockingTest
import one.block.eosiojavarpcprovider.implementations.EosioJavaRpcProviderImpl
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import kotlin.test.assertTrue


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Extensions(
    ExtendWith(TestCoroutineExtension::class),
    ExtendWith(InstantTaskExecutorExtension::class)
)
class MainViewModelTest {
    private val rpcProviderImpl =
        BlockDataSource(
            EosioJavaRpcProviderImpl("https://api.testnet.eos.io")
        )
    private val sut = MainViewModel(rpcProviderImpl)
    @Test
    fun sanity() {
        runBlockingTest {
            assertTrue(sut.getHeadBlock() > 1.toBigInteger());
        }
    }
}
