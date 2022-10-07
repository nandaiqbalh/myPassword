package com.nandaiqbalh.mypassword.presentation.ui.enterappkey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nandaiqbalh.mypassword.R
import com.nandaiqbalh.mypassword.databinding.FragmentEnterAppKeyBottomSheetBinding
import com.nandaiqbalh.mypassword.di.ServiceLocator
import com.nandaiqbalh.mypassword.utils.viewModelFactory

class EnterAppKeyBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentEnterAppKeyBottomSheetBinding
    private var listener : OnAppKeyConfirmedListener? = null

    fun setListener(listener: OnAppKeyConfirmedListener){
        this.listener = listener
    }

    private val viewModel: EnterAppKeyViewModel by viewModelFactory {
        EnterAppKeyViewModel(ServiceLocator.provideLocalRepository(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEnterAppKeyBottomSheetBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
    }

    private fun checkPassword() {
        if (validateForm()) {
            val appKey = binding.etAppKey.text.toString().trim()
            val isAppKeyCorrect = viewModel.checkIsAppKeyCorrect(appKey)
            listener?.onAppKeyConfirmed(isAppKeyCorrect)
            if(isAppKeyCorrect){
                dismiss()
            }else{
                Toast.makeText(requireContext(), getString(R.string.error_text_app_key_not_correct), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setClickListeners() {
        binding.btnConfirmAppKey.setOnClickListener { checkPassword() }
    }

    private fun validateForm(): Boolean {
        val appKey = binding.etAppKey.text.toString()
        var isFormValid = true
        if (appKey.isEmpty()) {
            isFormValid = false
            binding.tilAppKey.isErrorEnabled = true
            binding.tilAppKey.error = getString(R.string.error_empty_app_key)
        } else {
            binding.tilAppKey.isErrorEnabled = false
        }
        return isFormValid
    }
}

interface OnAppKeyConfirmedListener{
    fun onAppKeyConfirmed(isAppKeyCorrect : Boolean)
}