package com.nandaiqbalh.mypassword.presentation.ui.passwordlist

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.nandaiqbalh.mypassword.R
import com.nandaiqbalh.mypassword.databinding.ActivityPasswordListBinding
import com.nandaiqbalh.mypassword.di.ServiceLocator
import com.nandaiqbalh.mypassword.presentation.ui.createappkey.CreateAppKeyBottomSheet
import com.nandaiqbalh.mypassword.presentation.ui.createappkey.OnAppKeyChangedListener
import com.nandaiqbalh.mypassword.presentation.ui.enterappkey.EnterAppKeyBottomSheet
import com.nandaiqbalh.mypassword.presentation.ui.enterappkey.OnAppKeyConfirmedListener
import com.nandaiqbalh.mypassword.utils.viewModelFactory

class PasswordListActivity : AppCompatActivity() {

    private val binding: ActivityPasswordListBinding by lazy {
        ActivityPasswordListBinding.inflate(layoutInflater)
    }

    private val viewModel: PasswordListViewModel by viewModelFactory {
        PasswordListViewModel(ServiceLocator.providerLocalRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
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
        CreateAppKeyBottomSheet().apply {
            setListener(object : OnAppKeyChangedListener {
                override fun onAppKeyChanged() {
                    onAppKeyChanged.invoke()
                }
            })
            this.isCancelable = isCancelable
        }.show(supportFragmentManager, null)
    }

    private fun showDialogEnterAppKey(
        isCancelable: Boolean = true,
        onAppKeyConfirmed: (Boolean) -> Unit
    ) {
        EnterAppKeyBottomSheet().apply {
            setListener(object : OnAppKeyConfirmedListener {
                override fun onAppKeyConfirmed(isAppKeyCorrect: Boolean) {
                    onAppKeyConfirmed.invoke(isAppKeyCorrect)
                }
            })
            this.isCancelable = isCancelable
        }.show(supportFragmentManager, null)
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
