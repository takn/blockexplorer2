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
            EosioJavaRpcProviderImpl("https://api.jungle.alohaeos.com")
        )
    private val sut = MainViewModel(rpcProviderImpl, dispatcher)
    @Test
    fun sanity() {
        sut.headBlock.observeForTesting {
            runBlockingTest {
                sut.updateCurrentBlock()
                sut.headBlock.value?.let {
                    assertTrue(it > 1.toBigInteger())
                }
            }
        }
    }

    @Test
    fun `head block live data updates`() {
        sut.headBlock.observeForTesting {
            runBlockingTest {
                sut.headBlock.value?.let {
                    assertTrue(it > 1.toBigInteger())
                }
            }
        }
    }

    @Test
    internal fun `can get last 20 blocks`() {
        val blocksToFetch = 20
        sut.blocks.observeForTesting {
            runBlockingTest {
                sut.refreshBlocks()
                val headBlock = sut.headBlock.getOrAwaitValue()
                val expectedLastBlock = headBlock - (blocksToFetch - 1).toBigInteger()
                val blocksLiveData = sut.blocks.getOrAwaitValue()
                assertEquals(blocksToFetch, blocksLiveData.size)
                assertEquals(expectedLastBlock, blocksLiveData[blocksLiveData.size - 1].blockNum)
            }
        }
    }
}
