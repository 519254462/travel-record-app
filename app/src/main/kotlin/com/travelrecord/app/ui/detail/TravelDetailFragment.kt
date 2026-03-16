package com.travelrecord.app.ui.detail

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.travelrecord.app.R
import com.travelrecord.app.TravelRecordApplication
import com.travelrecord.app.databinding.FragmentTravelDetailBinding
import com.travelrecord.app.viewmodel.TravelDetailViewModel
import com.travelrecord.app.viewmodel.TravelDetailViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment displaying travel record details with processes and expenses
 */
class TravelDetailFragment : Fragment() {
    
    private var _binding: FragmentTravelDetailBinding? = null
    private val binding get() = _binding!!
    
    private val args: TravelDetailFragmentArgs by navArgs()
    
    private lateinit var processAdapter: TravelProcessAdapter
    private lateinit var expenseAdapter: ExpenseItemAdapter
    
    private val viewModel: TravelDetailViewModel by viewModels {
        TravelDetailViewModelFactory(
            (requireActivity().application as TravelRecordApplication).repository
        )
    }
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTravelDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerViews()
        setupClickListeners()
        observeViewModel()
        
        // Load travel details
        viewModel.loadTravelDetail(args.travelId)
    }
    
    private fun setupRecyclerViews() {
        // Setup processes RecyclerView
        processAdapter = TravelProcessAdapter(
            onEditClick = { process ->
                // TODO: Show edit dialog
                Snackbar.make(binding.root, "编辑过程: ${process.description}", Snackbar.LENGTH_SHORT).show()
            },
            onDeleteClick = { process ->
                viewModel.deleteProcess(process.id)
            }
        )
        
        binding.recyclerViewProcesses.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = processAdapter
        }
        
        // Setup expenses RecyclerView
        expenseAdapter = ExpenseItemAdapter(
            onEditClick = { expense ->
                // TODO: Show edit dialog
                Snackbar.make(binding.root, "编辑开销: ${expense.description}", Snackbar.LENGTH_SHORT).show()
            },
            onDeleteClick = { expense ->
                viewModel.deleteExpense(expense.id)
            }
        )
        
        binding.recyclerViewExpenses.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = expenseAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAddProcess.setOnClickListener {
            showAddProcessDialog()
        }
        
        binding.fabAddExpense.setOnClickListener {
            showAddExpenseDialog()
        }
    }
    
    private fun observeViewModel() {
        // Observe travel record
        viewModel.travelRecord.observe(viewLifecycleOwner) { record ->
            record?.let { updateTravelRecordUI(it) }
        }
        
        // Observe processes
        viewModel.processes.observe(viewLifecycleOwner) { processes ->
            processAdapter.submitList(processes)
            updateProcessesEmptyState(processes.isEmpty())
        }
        
        // Observe expenses
        viewModel.expenses.observe(viewLifecycleOwner) { expenses ->
            expenseAdapter.submitList(expenses)
            updateExpensesEmptyState(expenses.isEmpty())
        }
        
        // Observe loading states
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // TODO: Show/hide loading indicator
        }
        
        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
        
        // Observe success messages
        viewModel.successMessage.observe(viewLifecycleOwner) { successMessage ->
            successMessage?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                viewModel.clearSuccess()
            }
        }
        
        // Observe total expense
        viewModel.totalExpense.observe(viewLifecycleOwner) { totalExpense ->
            binding.textTotalExpense.text = "总开销: ¥${String.format("%.2f", totalExpense)}"
        }
    }
    
    private fun updateTravelRecordUI(record: com.travelrecord.app.data.entity.TravelRecord) {
        binding.apply {
            textTitle.text = record.title
            textPurpose.text = record.purpose
            textTask.text = record.task
            textStartDate.text = dateFormat.format(Date(record.startDate))
            
            if (record.endDate != null) {
                textEndDate.text = dateFormat.format(Date(record.endDate))
            } else {
                textEndDate.text = "进行中"
            }
        }
    }
    
    private fun updateProcessesEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.textNoProcesses.visibility = View.VISIBLE
            binding.recyclerViewProcesses.visibility = View.GONE
        } else {
            binding.textNoProcesses.visibility = View.GONE
            binding.recyclerViewProcesses.visibility = View.VISIBLE
        }
    }
    
    private fun updateExpensesEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.textNoExpenses.visibility = View.VISIBLE
            binding.recyclerViewExpenses.visibility = View.GONE
        } else {
            binding.textNoExpenses.visibility = View.GONE
            binding.recyclerViewExpenses.visibility = View.VISIBLE
        }
    }
    
    private fun showAddProcessDialog() {
        // Simple implementation - in a real app, use a proper dialog
        val description = "新的过程记录 - ${dateFormat.format(Date())}"
        viewModel.addProcess(description)
    }
    
    private fun showAddExpenseDialog() {
        // Simple implementation - in a real app, use a proper dialog
        viewModel.addExpense(100.0, "其他", "新的开销记录")
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_travel_detail, menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                val action = TravelDetailFragmentDirections
                    .actionDetailToEdit(args.travelId)
                findNavController().navigate(action)
                true
            }
            R.id.action_refresh -> {
                viewModel.refresh()
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}