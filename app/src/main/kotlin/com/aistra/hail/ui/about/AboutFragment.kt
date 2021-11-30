package com.aistra.hail.ui.about

import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.aistra.hail.R
import com.aistra.hail.app.HailData
import com.aistra.hail.databinding.FragmentAboutBinding
import com.aistra.hail.ui.main.MainFragment
import com.aistra.hail.utils.HUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class AboutFragment : MainFragment(), View.OnClickListener {
    private lateinit var aboutViewModel: AboutViewModel
    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        binding.descVersion.text = HailData.VERSION
        aboutViewModel = ViewModelProvider(this).get(AboutViewModel::class.java)
        aboutViewModel.time.observe(viewLifecycleOwner) {
            binding.descTime.text = it
        }
        binding.actionLibre.setOnClickListener(this)
        binding.actionTelegram.setOnClickListener(this)
        binding.actionQq.setOnClickListener(this)
        binding.actionCoolapk.setOnClickListener(this)
        binding.actionDonate.setOnClickListener(this)
        binding.actionGithub.setOnClickListener(this)
        binding.actionTranslate.setOnClickListener(this)
        binding.actionLicenses.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(view: View) {
        when (view) {
            binding.actionLibre -> HUI.launchBrowser(HailData.URL_WHY_FREE_SOFTWARE)
            binding.actionTelegram -> HUI.launchBrowser(HailData.URL_TELEGRAM)
            binding.actionQq -> HUI.launchBrowser(HailData.URL_QQ)
            binding.actionCoolapk -> HUI.launchBrowser(HailData.URL_COOLAPK)
            binding.actionDonate -> onDonate()
            binding.actionGithub -> HUI.launchBrowser(HailData.URL_GITHUB)
            binding.actionTranslate -> HUI.launchBrowser(HailData.URL_README)
            binding.actionLicenses -> MaterialAlertDialogBuilder(activity).setTitle(R.string.action_licenses)
                .setView(MaterialTextView(activity).apply {
                    val padding = resources.getDimensionPixelOffset(R.dimen.dialog_padding)
                    setPadding(padding, 0, padding, 0)
                    text = resources.openRawResource(R.raw.licenses).bufferedReader().readText()
                    Linkify.addLinks(this, Linkify.EMAIL_ADDRESSES or Linkify.WEB_URLS)
                })
                .setPositiveButton(android.R.string.ok, null)
                .create().show()
        }
    }

    private fun onDonate() {
        MaterialAlertDialogBuilder(activity).setTitle(R.string.title_donate)
            .setSingleChoiceItems(R.array.donate_payment_entries, 0) { dialog, which ->
                dialog.cancel()
                when (which) {
                    0 -> if (HUI.launchBrowser(HailData.URL_ALIPAY_API).not()) {
                        HUI.launchBrowser(HailData.URL_ALIPAY)
                    }
                    1 -> MaterialAlertDialogBuilder(activity).setTitle(R.string.title_donate)
                        .setView(ShapeableImageView(activity).apply {
                            val padding =
                                resources.getDimensionPixelOffset(R.dimen.dialog_padding)
                            setPadding(0, padding, 0, padding)
                            setImageResource(R.mipmap.qr_wechat)
                        })
                        .setPositiveButton(R.string.donate_wechat_scan) { _, _ ->
                            app.packageManager.getLaunchIntentForPackage("com.tencent.mm")
                                ?.let {
                                    it.putExtra("LauncherUI.From.Scaner.Shortcut", true)
                                    startActivity(it)
                                } ?: HUI.showToast(R.string.app_not_installed)
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .create().show()
                    2 -> MaterialAlertDialogBuilder(activity).setTitle(R.string.title_donate)
                        .setMessage(R.string.donate_bilibili_msg)
                        .setPositiveButton(R.string.donate_bilibili_space) { _, _ ->
                            HUI.launchBrowser(HailData.URL_BILIBILI)
                        }
                        .setNegativeButton(R.string.donate_bilibili_cancel, null)
                        .create().show()
                    3 -> HUI.launchBrowser(HailData.URL_PAYPAL)
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}