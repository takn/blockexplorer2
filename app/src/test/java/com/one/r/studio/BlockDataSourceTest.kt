package com.one.r.studio

import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.test.runBlockingTest
import one.block.eosiojava.error.rpcProvider.GetBlockRpcError
import one.block.eosiojava.interfaces.IRPCProvider
import one.block.eosiojava.models.rpcProvider.request.GetBlockRequest
import one.block.eosiojava.models.rpcProvider.response.GetBlockResponse
import one.block.eosiojava.models.rpcProvider.response.GetInfoResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigInteger
import kotlin.test.assertFailsWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlockDataSourceTest {
    private val expectedBlockNum = BigInteger.valueOf(12345)
    private val requestBlockNum = BigInteger.valueOf(1)

    private val blockRequest = mock<GetBlockRequest> {
        on { blockNumOrId } doReturn expectedBlockNum.toString()
    }
    private val mockRequestFactory = mock<BlockRequestFactory> {
        on { getBlockRequest(any()) } doReturn blockRequest
    }
    private val blockResponse = mock<GetBlockResponse> {
        on { blockNum } doReturn expectedBlockNum
    }
    private val info = mock<GetInfoResponse> {
        on { headBlockNum } doReturn expectedBlockNum
    }
    private val rpcMock = mock<IRPCProvider> {
        on { info } doReturn info
        on { getBlock(any()) } doReturn blockResponse
    }
    private val sut = BlockDataSource(rpcMock, mockRequestFactory)

    @Test
    fun `can get head block num`() = runBlockingTest {
        var result = sut.getHeadBlock()
        assertEquals(expectedBlockNum, result)
    }


    @Test
    fun `can get single block by block number`() = runBlockingTest {
        var result = sut.getBlock(requestBlockNum)
        assertEquals(expectedBlockNum, result.blockNum)
    }


    @Test
    fun `can get list of blocks`() {
        val expectedPreviousBlock = expectedBlockNum.minus(1.toBigInteger())
        val req1 = GetBlockRequest(expectedBlockNum.toString())
        val req2 = GetBlockRequest(expectedBlockNum.minus(1.toBigInteger()).toString())

        whenever(mockRequestFactory.getBlockRequest(eq(expectedBlockNum)))
            .thenReturn(req1)
        whenever(mockRequestFactory.getBlockRequest(eq(expectedPreviousBlock)))
            .thenReturn(req2)

        val response = mock<GetBlockResponse> {
            on { blockNum } doReturn expectedPreviousBlock
        }

        whenever(rpcMock.getBlock(eq(req2))).thenReturn(response)
        runBlockingTest {
            var result = sut.getNBlocksFromBlock(expectedBlockNum, 2)
            assertEquals(2, result.size)
            assertEquals(expectedBlockNum, result[0].blockNum)
            assertEquals(expectedPreviousBlock, result[1].blockNum)
            verify(rpcMock, times(1)).getBlock(eq(req1))
            verify(rpcMock, times(1)).getBlock(eq(req2))
            verify(mockRequestFactory).getBlockRequest(expectedPreviousBlock)
            verify(mockRequestFactory).getBlockRequest(expectedBlockNum)
        }
    }

    @Test
    internal fun `block not found throws error`() {
        val req1 = GetBlockRequest(expectedBlockNum.toString())
        whenever(mockRequestFactory.getBlockRequest(eq(expectedBlockNum)))
            .thenReturn(req1)
        whenever(rpcMock.getBlock(eq(req1))).thenThrow(GetBlockRpcError())
        runBlockingTest {
            assertFailsWith<GetBlockRpcError> { sut.getBlock(expectedBlockNum) }
        }

    }
}

