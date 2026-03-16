ate(record.startDate))
            
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