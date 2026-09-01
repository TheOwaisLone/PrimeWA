package com.wmods.wppenhacer.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wmods.wppenhacer.App
import com.wmods.wppenhacer.BuildConfig
import com.wmods.wppenhacer.R
import com.wmods.wppenhacer.activities.MainActivity
import com.wmods.wppenhacer.databinding.FragmentFeaturesHubBinding

class FeaturesHubFragment : Fragment() {

    private var _binding: FragmentFeaturesHubBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeaturesHubBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = activity as? MainActivity

        // 10 Dedicated Category Card Click Handlers
        binding.cardGeneralFeatures.setOnClickListener {
            mainActivity?.navigateToCategory(GeneralFragment())
        }

        binding.cardConversationFeatures.setOnClickListener {
            mainActivity?.navigateToCategory(ConversationFragment())
        }

        binding.cardStatusFeatures.setOnClickListener {
            mainActivity?.navigateToCategory(StatusFragment())
        }

        binding.cardHomeCustomFeatures.setOnClickListener {
            mainActivity?.navigateToCategory(HomeCustomizationFragment())
        }

        binding.cardPrivacyFeatures.setOnClickListener {
            mainActivity?.navigateToCategory(PrivacyFragment())
        }

        binding.cardCallsFeatures.setOnClickListener {
            mainActivity?.navigateToCategory(CallsFragment())
        }

        binding.cardCustomizationFeatures.setOnClickListener {
            mainActivity?.navigateToCategory(CustomizationFragment())
        }

        binding.cardMediaFeatures.setOnClickListener {
            mainActivity?.navigateToCategory(MediaFragment())
        }

        binding.cardRecordingsFeatures.setOnClickListener {
            mainActivity?.navigateToCategory(RecordingsFragment())
        }

        binding.cardMiscFeatures.setOnClickListener {
            mainActivity?.navigateToCategory(MiscFragment())
        }

        // Hero Status Card Details Toggle
        binding.statusCardHeader.setOnClickListener {
            val isVisible = binding.statusDetailsContainer.visibility == View.VISIBLE
            if (isVisible) {
                binding.statusDetailsContainer.visibility = View.GONE
                binding.statusExpandIcon.animate().rotation(0f).setDuration(200).start()
            } else {
                binding.statusDetailsContainer.visibility = View.VISIBLE
                binding.statusExpandIcon.animate().rotation(180f).setDuration(200).start()
            }
        }

        // Reboot buttons
        binding.rebootBtn.setOnClickListener {
            context?.let { ctx -> restartWpp(ctx, "com.whatsapp") }
        }

        binding.rebootBtn2.setOnClickListener {
            context?.let { ctx -> restartWpp(ctx, "com.whatsapp.w4b") }
        }

        // Format supported version ranges cleanly
        context?.let { ctx ->
            if (App.isOriginalPackage) {
                val wppVersions = ctx.resources.getStringArray(R.array.supported_versions_wpp)
                binding.listWpp.text = "WhatsApp: ${formatVersionRange(wppVersions)}"
            } else {
                binding.listWpp.visibility = View.GONE
            }
            val bizVersions = ctx.resources.getStringArray(R.array.supported_versions_business)
            binding.listBusiness.text = "Business: ${formatVersionRange(bizVersions)}"
        }

        updateModuleStatusUI()
    }

    private fun updateModuleStatusUI() {
        val isModuleActive = MainActivity.isXposedEnabled()
        if (!isModuleActive) {
            binding.statusIcon.setImageResource(R.drawable.ic_round_error_outline_24)
            binding.statusTitle.setText(R.string.module_not_enabled)
            binding.statusSummary.setText(R.string.module_not_enabled_summary)
        } else {
            binding.statusIcon.setImageResource(R.drawable.ic_round_check_circle_24)
            binding.statusTitle.setText(R.string.module_enabled)
            binding.statusSummary.setText(R.string.module_enabled_sum)
        }
    }

    private fun formatVersionRange(versions: Array<String>): String {
        return if (versions.isNotEmpty()) {
            val first = versions.first()
            val last = versions.last()
            if (first != last) "$first - $last" else first
        } else ""
    }

    private fun restartWpp(context: Context, packageName: String) {
        val intent = Intent("${BuildConfig.APPLICATION_ID}.WHATSAPP.RESTART")
        intent.putExtra("PKG", packageName)
        context.sendBroadcast(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
