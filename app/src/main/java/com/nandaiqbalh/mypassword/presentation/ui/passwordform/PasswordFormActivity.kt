package com.nandaiqbalh.mypassword.presentation.ui.passwordform

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.nandaiqbalh.mypassword.R
import com.nandaiqbalh.mypassword.data.local.database.entity.PasswordEntity
import com.nandaiqbalh.mypassword.databinding.ActivityPasswordFormBinding
import com.nandaiqbalh.mypassword.di.ServiceLocator
import com.nandaiqbalh.mypassword.utils.viewModelFactory
import com.nandaiqbalh.mypassword.wrapper.Resource

class PasswordFormActivity : AppCompatActivity() {

    private val passwordId: Int? by lazy {
        intent?.getIntExtra(ARG_PASSWORD_ID, UNSET_PASSWORD_ID)
    }

    private val binding: ActivityPasswordFormBinding by lazy {
        ActivityPasswordFormBinding.inflate(layoutInflater)
    }

    private val viewModel: PasswordFormViewModel by viewModelFactory {
        PasswordFormViewModel(ServiceLocator.provideLocalRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initToolbar()
        observeData()
        setOnClickListener()
        getInitialData()
    }

    private fun setOnClickListener() {
        binding.btnSave.setOnClickListener {
            saveData()
        }
    }

    private fun bindDataToForm(data: PasswordEntity?) {
        data?.let {
            binding.etPassword.setText(data.password)
            binding.etEmail.setText(data.email)
            binding.etAppName.setText(data.appName)
            binding.etDesc.setText(data.description)
            binding.etUsername.setText(data.username)
        }
    }

    private fun parseFormIntoEntity(): PasswordEntity {
        return PasswordEntity(
            email = binding.etEmail.text.toString().trim(),
            password = binding.etPassword.text.toString().trim(),
            appName = binding.etAppName.text.toString().trim(),
            description = binding.etDesc.text.toString().trim(),
            username = binding.etUsername.text.toString().trim(),
        ).apply {
            if (isEditAction()) {
                passwordId?.let {
                    id = it
                }
            }
        }
    }

    private fun checkFormValidation(): Boolean {
        val appName = binding.etAppName.text.toString()
        val email = binding.etEmail.text.toString()
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()
        var isFormValid = true
        if (appName.isEmpty()) {
            isFormValid = false
            binding.tilAppName.isErrorEnabled = true
            binding.tilAppName.error = getString(R.string.text_error_empty_app_name)
        } else {
            binding.tilAppName.isErrorEnabled = false
        }
        if (email.isEmpty()) {
            isFormValid = false
            binding.tilEmail.isErrorEnabled = true
            binding.tilEmail.error = getString(R.string.text_error_empty_email)
        } else {
            binding.tilEmail.isErrorEnabled = false
        }
        if (username.isEmpty()) {
            isFormValid = false
            binding.tilUsername.isErrorEnabled = true
            binding.tilUsername.error = getString(R.string.text_error_empty_username)
        } else {
            binding.tilUsername.isErrorEnabled = false
        }
        if (password.isEmpty()) {
            isFormValid = false
            binding.tilPassword.isErrorEnabled = true
            binding.tilPassword.error = getString(R.string.text_error_empty_password)
        } else {
            binding.tilPassword.isErrorEnabled = false
        }
        return isFormValid
    }

    private fun getInitialData() {
        if (isEditAction()) {
            passwordId?.let {
                viewModel.getPasswordById(it)
            }
        }
    }

    private fun saveData() {
        if (checkFormValidation()) {
            if (isEditAction()) {
                viewModel.updatePassword(parseFormIntoEntity())
            } else {
                viewModel.insertNewPassword(parseFormIntoEntity())
            }
        }
    }

    private fun isEditAction(): Boolean {
        return passwordId != null && passwordId != UNSET_PASSWORD_ID
    }

    private fun observeData() {
        viewModel.detailDataResult.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    //do nothing
                    setLoading(true)
                }
                is Resource.Success -> {
                    setLoading(false)
                    bindDataToForm(it.payload)
                }
                is Resource.Error -> {
                    setLoading(false)
                    finish()
                    Toast.makeText(this, "Error when get data", Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.updateResult.observe(this) {
            when (it) {
                is Resource.Loading<*> -> {
                    setFormEnabled(false)
                    setLoading(true)
                }
                is Resource.Success<*> -> {
                    setFormEnabled(true)
                    setLoading(false)
                    finish()
                    Toast.makeText(this, "Update data Success", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error<*> -> {
                    setFormEnabled(true)
                    setLoading(false)
                    finish()
                    Toast.makeText(this, "Error when get data", Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.insertResult.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    //do nothing
                    setFormEnabled(false)
                    setLoading(true)
                }
                is Resource.Success -> {
                    setFormEnabled(true)
                    setLoading(false)
                    finish()
                    Toast.makeText(this, "Add New data Success", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    setFormEnabled(true)
                    setLoading(false)
                    finish()
                    Toast.makeText(this, "Error when get data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.pbForm.isVisible = isLoading
        binding.clForm.isVisible = !isLoading
    }

    private fun setFormEnabled(isFormEnabled: Boolean) {
        with(binding) {
            tilPassword.isEnabled = isFormEnabled
            tilEmail.isEnabled = isFormEnabled
            tilUsername.isEnabled = isFormEnabled
            tilAppName.isEnabled = isFormEnabled
            tilDesc.isEnabled = isFormEnabled
            btnSave.isEnabled = isFormEnabled
        }
    }

    private fun initToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title =
            if (isEditAction())
                getString(R.string.text_toolbar_edit)
            else
                getString(R.string.text_toolbar_insert)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    companion object {
        private const val ARG_PASSWORD_ID = "ARG_PASSWORD_ID"
        private const val UNSET_PASSWORD_ID = -1

        @JvmStatic
        fun newInstance(context: Context, passwordId: Int? = null): Intent =
            Intent(context, PasswordFormActivity::class.java).apply {
                passwordId?.let {
                    putExtra(ARG_PASSWORD_ID, it)
                }
            }
    }
}
