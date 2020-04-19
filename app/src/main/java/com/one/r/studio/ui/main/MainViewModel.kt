package com.one.r.studio.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.one.r.studio.BlockDataSource
import kotlinx.coroutines.launch
import one.block.eosiojavarpcprovider.implementations.EosioJavaRpcProviderImpl
import java.math.BigInteger

class MainViewModel(val blockDataSource: BlockDataSource) : ViewModel() {
    var currentHeadblock = 1.toBigInteger()
    suspend fun getHeadBlock(): BigInteger {
        viewModelScope.launch {
            currentHeadblock = blockDataSource.getHeadBlock();
        }
        return currentHeadblock;
    }
}

object MainVModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(BlockDataSource(
            EosioJavaRpcProviderImpl("https://api.testnet.eos.io")
        )) as T
    }

}