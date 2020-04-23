@file:Suppress("UNCHECKED_CAST")

package com.one.r.studio.ui.main

import androidx.lifecycle.*
import com.one.r.studio.BlockDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import one.block.eosiojava.models.rpcProvider.response.GetBlockResponse
import one.block.eosiojavarpcprovider.implementations.EosioJavaRpcProviderImpl
import java.math.BigInteger

class MainViewModel(
    private val blockDataSource: BlockDataSource,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    val headblock: LiveData<BigInteger> = liveData(defaultDispatcher) {
        emit(blockDataSource.getHeadBlock())

    }
    val _blocks: MutableLiveData<List<GetBlockResponse>> = MutableLiveData()
    val blocks: LiveData<List<GetBlockResponse>> = _blocks

    var currentHeadblock = 1.toBigInteger()

    suspend fun getCurrentHeadBlock(): BigInteger {
        viewModelScope.launch {
            currentHeadblock = blockDataSource.getHeadBlock();
        }
        return currentHeadblock;
    }

    fun getNBlocksFromBlock(blockNum: BigInteger, count: Int) {
        viewModelScope.launch {
            _blocks.postValue(blockDataSource.getNBlocksFromBlock(blockNum, count))
        }
    }
}

object MainViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(
            BlockDataSource(
                EosioJavaRpcProviderImpl("https://api.testnet.eos.io")
            ),
            Dispatchers.IO
        ) as T
    }

}