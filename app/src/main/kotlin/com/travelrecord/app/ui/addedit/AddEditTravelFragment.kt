package com.travelrecord.app.ui.addedit

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.travelrecord.app.R
import com.travelrecord.app.data.entity.TravelRecord
import com.travelrecord.app.databinding.FragmentAddEditTravelBinding

/**
 * Fragment for adding or editing travel records
 */
class AddEditTravelFragment : Fragment() {
    
    private var _binding: FragmentAddEditTravelBinding? = null
    private val binding get() = _binding!!
    
    private val args: AddEditTravelFragmentArgs by navArgs()
    
    private var isEditMode = false
    private var currentRecord: TravelRecord? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        isEditMode = args.travelId != null
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditTravelBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        if (isEditMode) {
            // TODO: Load existing record for editing
            // For now, just show placeholder
            binding.editTitle.setText("示例旅游")
            binding.editPurpose.setText("商务出差")
            binding.editTask.setText("参加会议")
        }
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.buttonSave.setOnClickListener {
            saveTravelRecord()
        }
        
        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun saveTravelRecord() {
        val title = binding.editTitle.text.toString().trim()
        val purpose = binding.editPurpose.text.toString().trim()
        val task = binding.editTask.text.toString().trim()
        
        if (title.isEmpty()) {
            binding.editTitle.error = getString(R.string.error_title_required)
            return
        }
        
        if (purpose.isEmpty()) {
            binding.editPurpose.error = getString(R.string.error_purpose_required)
            return
        }
        
        // Create or update record
        val record = if (isEditMode && currentRecord != null) {
            currentRecord!!.copy(
                title = title,
                purpose = purpose,
                task = task
            ).withUpdatedTimestamp()
        } else {
            TravelRecord(
                title = title,
                purpose = purpose,
                task = task,
                startDate = System.currentTimeMillis()
            )
        }
        
        // TODO: Save to repository
        Snackbar.make(binding.root, getString(R.string.save_success), Snackbar.LENGTH_SHORT).show()
        
        // Navigate back
        findNavController().navigateUp()
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add_edit, menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                saveTravelRecord()
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