package com.shardul.esewazone.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shardul.esewazone.databinding.BottomSheetDeleteCartBinding

class DeleteFromBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetDeleteCartBinding? = null
    private val binding get() = _binding!!
    private var onDeleteClicked: (() -> Unit)? = null
    fun setOnDeleteListener(listener: () -> Unit) {
        onDeleteClicked = listener
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = BottomSheetDeleteCartBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnDelete.setOnClickListener {
            onDeleteClicked?.invoke()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}