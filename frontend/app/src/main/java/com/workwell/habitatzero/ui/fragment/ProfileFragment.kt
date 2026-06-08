package com.workwell.habitatzero.ui.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.workwell.habitatzero.R
import com.workwell.habitatzero.ui.LoginActivity
import com.workwell.habitatzero.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels()

    // Views
    private lateinit var imgAvatar: ImageView
    private lateinit var tvEmail: TextView
    private lateinit var tvCredentialId: TextView
    private lateinit var tvSolsCount: TextView
    private lateinit var tvEfficiencyPct: TextView
    private lateinit var progressEfficiency: ProgressBar
    private lateinit var btnLogout: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgAvatar        = view.findViewById(R.id.imgAvatar)
        tvEmail          = view.findViewById(R.id.tvUserEmail)
        tvCredentialId   = view.findViewById(R.id.tvCredentialId)
        tvSolsCount      = view.findViewById(R.id.tvSolsCount)
        tvEfficiencyPct  = view.findViewById(R.id.tvEfficiencyPct)
        progressEfficiency = view.findViewById(R.id.progressEfficiency)
        btnLogout        = view.findViewById(R.id.btnLogout)

        setupSettingsRows(view)
        setupLogout()
        setupObservers()
        loadProfile()
    }

    override fun onResume() {
        super.onResume()
        // Reload in case estufas changed since last visit
        loadProfile()
    }

    // ─── Data loading ─────────────────────────────────────────────────────────

    private fun loadProfile() {
        val prefs = requireContext().getSharedPreferences("HabitatZeroPrefs", Context.MODE_PRIVATE)
        val email = prefs.getString("user_email", "") ?: ""
        if (email.isNotBlank()) {
            viewModel.carregarPerfil(email)
        }
    }

    // ─── Observers ────────────────────────────────────────────────────────────

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            tvEmail.text = state.email.ifBlank { getString(R.string.profile_email_placeholder) }

            // Build credential ID from email prefix
            val prefix = state.email.split("@").first().take(8).uppercase()
            tvCredentialId.text = getString(R.string.profile_credential_id, prefix)

            // Initials avatar
            if (state.initials.isNotBlank()) {
                imgAvatar.setImageBitmap(buildInitialsBitmap(state.initials))
            }

            // Efficiency stat
            tvEfficiencyPct.text = getString(R.string.profile_efficiency_pct, state.efficiencyPct)
            progressEfficiency.progress = state.efficiencyPct
        }

        viewModel.solsCount.observe(viewLifecycleOwner) { sols ->
            tvSolsCount.text = sols.toString()
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }

    // ─── Settings rows ────────────────────────────────────────────────────────

    private fun setupSettingsRows(view: View) {
        val toastMsg = getString(R.string.profile_settings_wip)
        listOf(
            R.id.rowProtocoloSeguranca,
            R.id.rowNotificacoesBiometricas,
            R.id.rowSincronizacaoTerra,
            R.id.rowPreferenciasInterface
        ).forEach { id ->
            view.findViewById<View>(id).setOnClickListener {
                // TODO: wire each row to its settings destination when available
                Toast.makeText(requireContext(), toastMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ─── Logout ───────────────────────────────────────────────────────────────

    private fun setupLogout() {
        btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.profile_logout_title)
                .setMessage(R.string.profile_logout_message)
                .setPositiveButton(R.string.profile_logout_confirm) { _, _ -> performLogout() }
                .setNegativeButton(R.string.profile_logout_cancel, null)
                .show()
        }
    }

    private fun performLogout() {
        // Clear all stored session data and navigate to login, clearing the back stack
        requireContext().getSharedPreferences("HabitatZeroPrefs", Context.MODE_PRIVATE)
            .edit()
            .remove("token")
            .remove("user_email")
            .apply()

        startActivity(Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }

    // ─── Initials avatar ──────────────────────────────────────────────────────

    private fun buildInitialsBitmap(initials: String): Bitmap {
        val size = resources.getDimensionPixelSize(R.dimen.avatar_size)
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)

        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = resources.getColor(R.color.bg_card_dark, null)
        }
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, bgPaint)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = resources.getColor(R.color.accent_cyan, null)
            textSize = size * 0.38f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }
        val yOffset = (textPaint.descent() - textPaint.ascent()) / 2 - textPaint.descent()
        canvas.drawText(initials, size / 2f, size / 2f + yOffset, textPaint)

        return bmp
    }
}
