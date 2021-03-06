package com.one.r.studio

import kotlinx.coroutines.*
import one.block.eosiojava.interfaces.IRPCProvider
import one.block.eosiojava.models.rpcProvider.request.GetBlockRequest
import one.block.eosiojava.models.rpcProvider.response.GetBlockResponse
import java.math.BigInteger

interface EOSIODataSource {
    suspend fun getHeadBlock(): BigInteger
    suspend fun getNBlocksFromBlock(blockNum: BigInteger, blockCount: Int): List<GetBlockResponse>
}

open class BlockRequestFactory {
    open fun getBlockRequest(blockNum: BigInteger): GetBlockRequest {
        return GetBlockRequest(
            blockNum.toString()
        )
    }
}

class BlockDataSource(
    private val rpcProvider: IRPCProvider,
    var requestFactory: BlockRequestFactory = BlockRequestFactory()
) :
    EOSIODataSource {
    override suspend fun getHeadBlock(): BigInteger = rpcProvider.info.headBlockNum

    fun getBlock(blockNum: BigInteger): GetBlockResponse =
        rpcProvider.getBlock(requestFactory.getBlockRequest(blockNum))

    override suspend fun getNBlocksFromBlock(
        blockNum: BigInteger,
        blockCount: Int
    ): List<GetBlockResponse> {
        val deferred = mutableListOf<Deferred<GetBlockResponse>>()
        coroutineScope {
            val block = blockNum.toInt()
            for (x in block downTo (block - (blockCount - 1)) step 1) {
                deferred.add(async { getBlock(x.toBigInteger()) })
            }
        }
        return deferred.awaitAll()
    }
}