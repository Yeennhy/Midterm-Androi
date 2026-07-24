package com.example.midterm.ui.common

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.midterm.R
import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.data.model.Voucher
import com.example.midterm.data.model.VoucherType
import com.example.midterm.databinding.ItemVoucherBinding

/**
 * Visual treatment for a voucher's badge box, driven by [Voucher.type] and whether
 * [Voucher.effectiveBadgeText] is a percentage ("20% OFF") or a flat bonus amount ("20K OFF") —
 * matching the Figma spec where flat one-time bonuses get a muted gray treatment instead
 * of the type's usual accent color.
 */
private data class BadgeStyle(
    val boxColorRes: Int,
    val textColorRes: Int,
    val iconRes: Int,
    val iconTintColorRes: Int
)

class VoucherAdapter(
    private val onItemClick: (Voucher) -> Unit
) : ListAdapter<Voucher, VoucherAdapter.ViewHolder>(DiffCallback()) {

    var selectedVoucherCode: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    var accessibilityMode: AccessibilityMode = AccessibilityMode.ACCESSIBLE
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    fun setSelectedCode(code: String?) {
        selectedVoucherCode = code
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVoucherBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, item.code == selectedVoucherCode, accessibilityMode)
    }

    inner class ViewHolder(
        private val binding: ItemVoucherBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(voucher: Voucher, isSelected: Boolean, mode: AccessibilityMode) {
            val context = binding.root.context
            val badgeText = voucher.effectiveBadgeText
            val isFlatBonus = voucher.type == VoucherType.PRODUCT && !badgeText.contains("%")
            val style = when {
                voucher.type == VoucherType.DELIVERY ->
                    BadgeStyle(R.color.landing_button, R.color.white, R.drawable.ic_truck, R.color.white)
                isFlatBonus ->
                    BadgeStyle(R.color.divider, R.color.landing_description, R.drawable.ic_ticket, R.color.landing_description)
                else ->
                    BadgeStyle(R.color.landing_subtitle, R.color.white, R.drawable.ic_tag, R.color.white)
            }

            binding.badgeBox.setBackgroundColor(ContextCompat.getColor(context, style.boxColorRes))
            binding.ivVoucherIcon.setImageResource(style.iconRes)
            binding.ivVoucherIcon.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, style.iconTintColorRes))
            binding.tvBadge.setTextColor(ContextCompat.getColor(context, style.textColorRes))
            binding.tvBadge.text = badgeText
            binding.tvTitle.text = voucher.title.ifEmpty { voucher.code }
            binding.tvDescription.text = voucher.description
            binding.tvExpiry.text = voucher.effectiveExpiryLabel
            binding.tvCode.text = voucher.code
            binding.radioDot.visibility = if (isSelected) View.VISIBLE else View.INVISIBLE
            binding.root.setOnClickListener { onItemClick(voucher) }

            if (mode == AccessibilityMode.ACCESSIBLE) {
                binding.root.restoreToAccessibilityTree()
                val stateLabel = if (isSelected) "Selected" else "Not selected"
                binding.root.groupForAccessibility(
                    label = "$badgeText. ${voucher.title}. ${voucher.description}. " +
                        "${voucher.effectiveExpiryLabel}. Code ${voucher.code}. $stateLabel."
                )
            } else {
                binding.root.pruneFromAccessibilityTree()
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Voucher>() {
        override fun areItemsTheSame(old: Voucher, new: Voucher) = old.code == new.code
        override fun areContentsTheSame(old: Voucher, new: Voucher) = old == new
    }
}
