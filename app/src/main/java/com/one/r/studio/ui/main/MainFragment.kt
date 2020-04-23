package com.one.r.studio.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.one.r.studio.databinding.MainFragmentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope

class MainFragment : Fragment() {
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = MainViewModelFactory.create(MainViewModel::class.java)
        viewModel.headblock.observe(
            viewLifecycleOwner,
            Observer { Log.d("FRAGx", it.toString()) })

        viewModel.blocks.observe(
            viewLifecycleOwner,
            Observer { Log.d("FRAGx", it.size.toString()) })

        binding.button.setOnClickListener {
            //            Log.d("FRAG", viewModel.currentHeadblock.toString())
            viewModel.refreshBlocks();

        }

//        Log.d("FRAG", viewModel.currentHeadblock.toString())
    }

}
