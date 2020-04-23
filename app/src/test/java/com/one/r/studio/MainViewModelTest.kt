package com.one.r.studio

import com.one.r.studio.ui.main.MainViewModel
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import one.block.eosiojavarpcprovider.implementations.EosioJavaRpcProviderImpl
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import kotlin.test.assertEquals
import kotlin.test.assertTrue


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Extensions(
    ExtendWith(TestCoroutineExtension::class),
    ExtendWith(InstantTaskExecutorExtension::class)
)
class MainViewModelTest {
    private val dispatcher = TestCoroutineDispatcher()
    private val rpcProviderImpl =
        BlockDataSource(
            EosioJavaRpcProviderImpl("https://api.testnet.eos.io")
        )
    private val sut = MainViewModel(rpcProviderImpl, dispatcher)
    @Test
    fun sanity() {
        runBlockingTest {
            assertTrue(sut.getCurrentHeadBlock() > 1.toBigInteger())
        }
    }

    @Test
    fun `head block live data updates`() {
        sut.headblock.observeForTesting {
            assertTrue(sut.headblock.value!! > 1.toBigInteger())
        }
    }

    @Test
    internal fun `can get last 20 blocks`() {
        val blocksToFetch = 20
        runBlockingTest {
            val headBlock = sut.getCurrentHeadBlock()
            val expectedLastBlock = headBlock - (blocksToFetch - 1).toBigInteger()
            sut.getNBlocksFromBlock(headBlock, blocksToFetch)
            val blocksLiveData = sut.blocks.getOrAwaitValue()
            assertEquals(blocksToFetch, blocksLiveData.size)
            assertEquals(expectedLastBlock, blocksLiveData[blocksLiveData.size - 1].blockNum)
        }
    }
}
