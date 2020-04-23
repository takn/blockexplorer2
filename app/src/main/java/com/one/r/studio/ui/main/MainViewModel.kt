package com.one.r.studio.ui.main

import androidx.lifecycle.*
import com.one.r.studio.BlockDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import one.block.eosiojava.models.rpcProvider.response.GetBlockResponse
import one.block.eosiojavarpcprovider.implementations.EosioJavaRpcProviderImpl
import java.math.BigInteger

class MainViewModel(
    private val blockDataSource: BlockDataSource,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _headBlock: MutableLiveData<BigInteger> = MutableLiveData()
    private val _blocks: MutableLiveData<List<GetBlockResponse>> = MutableLiveData()
    val headBlock: LiveData<BigInteger> = _headBlock
    val blocks: LiveData<List<GetBlockResponse>> = _blocks

    fun refreshBlocks() {
        viewModelScope.launch(defaultDispatcher) {
            val hb = blockDataSource.getHeadBlock()
            _headBlock.postValue(hb)
            _blocks.postValue(blockDataSource.getNBlocksFromBlock(hb, 20))
        }
    }

    fun updateCurrentBlock() {
        viewModelScope.launch(defaultDispatcher) {
            while (true) {
                delay(1000L)
                _headBlock.postValue(blockDataSource.getHeadBlock())
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
object MainViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(
            BlockDataSource(
                EosioJavaRpcProviderImpl("https://api.jungle.alohaeos.com")
            ),
            Dispatchers.IO
        ) as T
    }

}