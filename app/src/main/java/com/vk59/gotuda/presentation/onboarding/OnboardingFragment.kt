package com.vk59.gotuda.presentation.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.vk59.gotuda.R
import com.vk59.gotuda.core.utils.Event
import com.vk59.gotuda.databinding.FragmentOnboardingBinding
import com.vk59.gotuda.design.ErrorSnackbarFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.ConnectException

@AndroidEntryPoint
class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {

  private val binding: FragmentOnboardingBinding by viewBinding(FragmentOnboardingBinding::bind)

  private val viewModel: OnboardingViewModel by viewModels()

  private val loginResultHandler =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
      val task = GoogleSignIn.getSignedInAccountFromIntent(result?.data)
      try {
        if (task.isCanceled) {
          throw GoogleAuthorizationCancelledException(ConnectException())
        }
        authorizeWithAccount(task.result)
      } catch (t: Throwable) {
        viewModel.error(GoogleAuthorizationException(t))
      }
    }

  override fun onStart() {
    super.onStart()
    val account = GoogleSignIn.getLastSignedInAccount(requireContext())
    Timber.d("${account?.displayName}  ${account?.email}")
    account?.let {
      authorizeWithAccount(it)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initButton()
    observeStatus()
  }

  private fun observeStatus() {
    lifecycleScope.launch {
      viewModel.listenToEvent().collectLatest {
        when (it) {
          is Event.Error -> {
            when (it.throwable) {
              is ConnectException -> {
                ErrorSnackbarFactory(binding.root).create(
                  R.drawable.ic_connection_off,
                  getString(R.string.no_connection)
                ).show()
              }

              else -> {
                ErrorSnackbarFactory(binding.root).create(
                  R.drawable.ic_warning,
                  getString(R.string.something_went_wrong)
                ).show()
              }
            }
            binding.signInButton.setProgressing(false)
            Timber.e(it.throwable)
            ErrorSnackbarFactory(binding.root)
              .create(R.drawable.ic_warning, getString(R.string.something_went_wrong))
              .show()
          }

          is Event.Success -> {
            binding.signInButton.setProgressing(false)
          }

          is Event.Loading -> {
            binding.signInButton.setProgressing(true)
          }
        }
      }
    }
  }

  private fun initButton() {
    binding.signInButton.setOnClickListener {
      loginResultHandler.launch(getSignInIntent())
    }
  }

  private fun getSignInIntent(): Intent {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestEmail()
      .build()

    val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

    return mGoogleSignInClient.signInIntent
  }

  private fun authorizeWithAccount(account: GoogleSignInAccount) {
    viewModel.authorize(account)
  }
}