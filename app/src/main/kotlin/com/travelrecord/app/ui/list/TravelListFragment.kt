package com.travelrecord.app.ui.list

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.travelrecord.app.R
import com.travelrecord.app.TravelRecordApplication
import com.travelrecord.app.databinding.FragmentTravelListBinding
import com.travelrecord.app.viewmodel.TravelListViewModel
import com.travelrecord.app.viewmodel.TravelListViewModelFactory

/**
 * Fragment displaying list of travel records
 * Supports search, navigation to details, and adding new records
 */
class TravelListFragment : Fragment() {
    
    private var _binding: FragmentTravelListBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var adapter: TravelListAdapter
    
    private val viewModel: TravelListViewModel by viewModels {
        TravelListViewModelFactory(
            (requireActivity().application as TravelRecordApplication).repository
        )
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTravelListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupFab()
        observeViewModel()
        setupSwipeRefresh()
    }
    
    private fun setupRecyclerView() {
        adapter = TravelListAdapter(
            onItemClick = { travelRecord ->
                val action = TravelListFragmentDirections
                    .actionListToDetail(travelRecord.id)
                findNavController().navigate(action)
            },
            onDeleteClick = { travelRecord ->
                viewModel.deleteTravelRecord(travelRecord.id)
            }
        )
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@TravelListFragment.adapter
        }
    }
    
    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            val action = TravelListFragmentDirections.actionListToAdd()
            findNavController().navigate(action)
        }
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }
    
    private fun observeViewModel() {
        // Observe travel records
        viewModel.travelRecords.observe(viewLifecycleOwner) { records ->
            adapter.submitList(records)
            updateEmptyState(records.isEmpty())
        }
        
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }
        
        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
        
        // Observe statistics
        viewModel.recordCount.observe(viewLifecycleOwner) { count ->
            updateStatistics(count, viewModel.totalExpenseSum.value ?: 0.0)
        }
        
        viewModel.totalExpenseSum.observe(viewLifecycleOwner) { totalSum ->
            updateStatistics(viewModel.recordCount.value ?: 0, totalSum)
        }
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.recyclerView.visibility = View.GONE
            binding.emptyStateLayout.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyStateLayout.visibility = View.GONE
        }
    }
    
    private fun updateStatistics(count: Int, totalSum: Double) {
        binding.textStatistics.text = getString(
            R.string.statistics_format,
            count,
            totalSum
        )
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_travel_list, menu)
        
        // Setup search
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchTravelRecords(it) }
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    viewModel.clearSearch()
                }
                return true
            }
        })
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                viewModel.refresh()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}