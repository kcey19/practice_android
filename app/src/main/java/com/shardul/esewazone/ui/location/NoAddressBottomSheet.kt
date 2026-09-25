package com.shardul.esewazone.ui.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shardul.esewazone.databinding.DialogNoAddressBinding

class NoAddressBottomSheet : BottomSheetDialogFragment() {

    private var _binding: DialogNoAddressBinding? = null
    private val binding get() = _binding!!

    private var onSetAddressClick: (() -> Unit)? = null
    fun setOnSetAddressListener(listener: () -> Unit) {
        onSetAddressClick = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogNoAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSetAddress.setOnClickListener {
            onSetAddressClick?.invoke()
            dismiss()
        }
        binding.btnCancelAddress.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}