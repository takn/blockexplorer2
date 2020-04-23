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

    val _headBlock: MutableLiveData<BigInteger> = MutableLiveData()
    val headblock: LiveData<BigInteger> = _headBlock
    val _blocks: MutableLiveData<List<GetBlockResponse>> = MutableLiveData()
    val blocks: LiveData<List<GetBlockResponse>> = _blocks

//hmmmm when this needs to be refreshed, how do you do it?
//    val blocks2: LiveData<List<GetBlockResponse>> = liveData(defaultDispatcher) {
//        currentHeadblock = blockDataSource.getHeadBlock();
//        emit(blockDataSource.getNBlocksFromBlock(currentHeadblock, 20))
//    }

//    var currentHeadblock = 1.toBigInteger()

//    suspend fun getCurrentHeadBlock(): BigInteger {
//        viewModelScope.launch(defaultDispatcher) {
//            currentHeadblock = blockDataSource.getHeadBlock();
//        }
//        return currentHeadblock;
//    }

    suspend fun getNBlocksFromBlock(blockNum: BigInteger, count: Int) {
        viewModelScope.launch(defaultDispatcher) {
            _blocks.postValue(blockDataSource.getNBlocksFromBlock(blockNum, count))
        }
    }

    fun refreshBlocks() {
        viewModelScope.launch(defaultDispatcher) {
            val hb = blockDataSource.getHeadBlock()
            _blocks.postValue(blockDataSource.getNBlocksFromBlock(hb, 20))
            _headBlock.postValue(hb)
        }
    }
}

@Suppress("UNCHECKED_CAST")
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