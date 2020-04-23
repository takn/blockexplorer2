package com.one.r.studio.ui.main

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.one.r.studio.R
import com.one.r.studio.databinding.MainFragmentBinding
import kotlinx.android.synthetic.main.item_block.view.*
import one.block.eosiojava.models.rpcProvider.response.GetBlockResponse

class MainFragment : Fragment() {
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater.inflate(R.menu.menu, menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var lm = LinearLayoutManager(activity)
        val blockAdapter = BlockAdapter()

        val actionBar = (activity as AppCompatActivity)?.supportActionBar
        binding.recyclerView.apply {
            layoutManager = lm
            adapter = blockAdapter
        }

        viewModel = MainViewModelFactory.create(MainViewModel::class.java)
        viewModel.headBlock.observe(
            viewLifecycleOwner,
            Observer {
                actionBar?.title = "current block: $it"
            })


        viewModel.blocks.observe(
            viewLifecycleOwner,
            Observer {
                blockAdapter.update(it)
            })

        viewModel.updateCurrentBlock()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.refresh) {
            viewModel.refreshBlocks()
        }
        return super.onOptionsItemSelected(item)
    }

}

class BlockAdapter : RecyclerView.Adapter<BlockAdapter.ViewHolder>() {
    private var data = listOf<GetBlockResponse>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_block, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun update(blocks: List<GetBlockResponse>) {
        data = blocks
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: GetBlockResponse) {
            itemView.timeStamp.text = String.format("timestamp: %s", data.timestamp)
            itemView.blockId.text = String.format("block num: %d", data.blockNum)
            itemView.idHash.text = String.format("block id: %s", data.id)
            itemView.actionCount.text = String.format("transactions: %d", data.transactions.size)

            //populate details
            itemView.detail1.text = data.producerSignature
            itemView.detail2.text = data.scheduleVersion.toString()
            itemView.detail3.text = data.previous
            itemView.setOnClickListener {
                if (itemView.detailsContainer.visibility == View.VISIBLE) {
                    itemView.detailsContainer.visibility = View.GONE
                } else {
                    itemView.detailsContainer.visibility = View.VISIBLE
                }
            }
        }

    }

}
