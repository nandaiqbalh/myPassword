package com.nandaiqbalh.mypassword.presentation.ui.passwordlist

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.nandaiqbalh.mypassword.R
import com.nandaiqbalh.mypassword.data.local.database.entity.PasswordEntity
import com.nandaiqbalh.mypassword.databinding.ActivityPasswordListBinding
import com.nandaiqbalh.mypassword.di.ServiceLocator
import com.nandaiqbalh.mypassword.presentation.ui.createappkey.CreateAppKeyBottomSheet
import com.nandaiqbalh.mypassword.presentation.ui.createappkey.OnAppKeyChangedListener
import com.nandaiqbalh.mypassword.presentation.ui.enterappkey.EnterAppKeyBottomSheet
import com.nandaiqbalh.mypassword.presentation.ui.enterappkey.OnAppKeyConfirmedListener
import com.nandaiqbalh.mypassword.presentation.ui.passwordform.PasswordFormActivity
import com.nandaiqbalh.mypassword.presentation.ui.passwordlist.adapter.PasswordItemClickListener
import com.nandaiqbalh.mypassword.presentation.ui.passwordlist.adapter.PasswordListAdapter
import com.nandaiqbalh.mypassword.utils.ClipboardUtils
import com.nandaiqbalh.mypassword.utils.viewModelFactory
import com.nandaiqbalh.mypassword.wrapper.Resource

class PasswordListActivity : AppCompatActivity() {

    private val binding: ActivityPasswordListBinding by lazy {
        ActivityPasswordListBinding.inflate(layoutInflater)
    }
    private val viewModel: PasswordListViewModel by viewModelFactory {
        PasswordListViewModel(ServiceLocator.provideLocalRepository(this))
    }
    private val adapter: PasswordListAdapter by lazy {
        PasswordListAdapter(object : PasswordItemClickListener {
            override fun onItemClicked(item: PasswordEntity) {
                Toast.makeText(this@PasswordListActivity, "Item Clicked", Toast.LENGTH_SHORT).show()
            }

            override fun onItemLongClicked(item: PasswordEntity) {
                ClipboardUtils.copyToClipboard(
                    this@PasswordListActivity,
                    item.username ?: item.email ?: "",
                    item.password.orEmpty()
                )
                Toast.makeText(
                    this@PasswordListActivity,
                    "Password Copied to Clipboard",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onDeleteMenuClicked(item: PasswordEntity) {
                viewModel.deletePassword(item)
            }

            override fun onEditMenuClicked(item: PasswordEntity) {
                startActivity(PasswordFormActivity.newInstance(this@PasswordListActivity, item.id))
            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initList()
        setOnClickListeners()
        observeData()
    }

    private fun setOnClickListeners() {
        binding.fabAddData.setOnClickListener {
            startActivity(PasswordFormActivity.newInstance(this))
        }
    }

    private fun getData() {
        viewModel.getPasswordList()
    }

    private fun initList() {
        binding.rvPasswordList.apply {
            layoutManager = LinearLayoutManager(this@PasswordListActivity)
            adapter = this@PasswordListActivity.adapter
        }
    }

    private fun observeData() {
        viewModel.passwordListResult.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    setLoadingState(true)
                    setErrorState(false)
                }
                is Resource.Success -> {
                    setLoadingState(false)
                    setErrorState(false)
                    bindDataToAdapter(it.payload)
                }
                is Resource.Error -> {
                    setLoadingState(false)
                    setErrorState(true, it.exception?.message.orEmpty())
                }
            }
        }
        viewModel.deleteResult.observe(this) {
            when (it) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    Toast.makeText(this, "Delete Password Success", Toast.LENGTH_SHORT).show()
                    getData()
                }
                is Resource.Error -> {
                    Toast.makeText(this, "Delete Password Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun bindDataToAdapter(data: List<PasswordEntity>?) {
        if (data.isNullOrEmpty()) {
            adapter.clearItems()
            setErrorState(true, getString(R.string.text_error_password_list_empty))
        } else {
            adapter.setItems(data)
        }
    }

    private fun setErrorState(isError: Boolean, errorMsg: String = "") {
        binding.tvError.text = errorMsg
        binding.tvError.isVisible = isError
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.pbPasswordList.isVisible = isLoading
        binding.rvPasswordList.isVisible = !isLoading
    }

    override fun onResume() {
        super.onResume()
        getData()
        binding.root.isVisible = false
        if (viewModel.checkIfAppKeyIsExist()) {
            showDialogEnterAppKey(false) { isPasswordCorrect ->
                if (isPasswordCorrect) {
                    binding.root.isVisible = true
                }
            }
        } else {
            showCreateNewAppKeyDialog(false) {
                Toast.makeText(this, "App Key Created", Toast.LENGTH_SHORT).show()
                binding.root.isVisible = true
            }
        }
    }

    private fun changeAppKey() {
        if (viewModel.checkIfAppKeyIsExist()) {
            showDialogEnterAppKey { isPasswordCorrect ->
                if (isPasswordCorrect) {
                    showCreateNewAppKeyDialog {
                        Toast.makeText(this, "App Key Changed", Toast.LENGTH_SHORT).show()
                        binding.root.isVisible = true
                    }
                }
            }
        }
    }

    private fun showCreateNewAppKeyDialog(
        isCancelable: Boolean = true,
        onAppKeyChanged: () -> Unit
    ) {
        val currentDialog =
            supportFragmentManager.findFragmentByTag(CreateAppKeyBottomSheet::class.java.simpleName)
        if (currentDialog == null) {
            CreateAppKeyBottomSheet().apply {
                setListener(object : OnAppKeyChangedListener {
                    override fun onAppKeyChanged() {
                        onAppKeyChanged.invoke()
                    }
                })
                this.isCancelable = isCancelable
            }.show(supportFragmentManager, CreateAppKeyBottomSheet::class.java.simpleName)
        }
    }

    private fun showDialogEnterAppKey(
        isCancelable: Boolean = true,
        onAppKeyConfirmed: (Boolean) -> Unit
    ) {
        val currentDialog =
            supportFragmentManager.findFragmentByTag(EnterAppKeyBottomSheet::class.java.simpleName)
        if (currentDialog == null) {
            EnterAppKeyBottomSheet().apply {
                setListener(object : OnAppKeyConfirmedListener {
                    override fun onAppKeyConfirmed(isPasswordCorrect: Boolean) {
                        onAppKeyConfirmed.invoke(isPasswordCorrect)
                    }
                })
                this.isCancelable = isCancelable
            }.show(supportFragmentManager, EnterAppKeyBottomSheet::class.java.simpleName)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_action_change_app_key -> {
                changeAppKey()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}