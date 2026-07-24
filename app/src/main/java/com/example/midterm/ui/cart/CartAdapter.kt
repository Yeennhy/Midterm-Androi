package com.example.midterm.ui.cart

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.midterm.data.model.CartItem
import com.example.midterm.databinding.ItemUnfriendlyCartBinding

class CartAdapter(
    private val listener: CartItemListener
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {

        val binding = ItemUnfriendlyCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(
        private val binding: ItemUnfriendlyCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {

            binding.tvName.text = item.product.name

            val price =
                item.product.price +
                        (item.selectedVariant?.extraPrice ?: 0L)

            binding.tvPrice.text = "${price}đ"

            binding.tvQuantity.text = item.quantity.toString()

            binding.itemVariant.text =
                item.selectedVariant?.name ?: "Default"

            binding.imgProduct.setImageResource(
                item.product.imageResId
            )

            // Update stroke color instead of alpha
            val strokeColor = if (item.isSelected) {
                Color.parseColor("#9F4123")
            } else {
                Color.LTGRAY
            }
            binding.root.strokeColor = strokeColor

            binding.btnPlus.setOnClickListener {
                listener.onIncreaseQuantity(item)
            }

            binding.btnMinus.setOnClickListener {
                listener.onDecreaseQuantity(item)
            }

            binding.itemVariant.setOnClickListener {
                listener.onVariantClick(item)
            }

            binding.root.setOnClickListener {
                listener.onItemClick(item)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CartItem>() {

        override fun areItemsTheSame(
            oldItem: CartItem,
            newItem: CartItem
        ): Boolean {

            return oldItem.product.id == newItem.product.id &&
                    oldItem.selectedVariant?.id ==
                    newItem.selectedVariant?.id
        }

        override fun areContentsTheSame(
            oldItem: CartItem,
            newItem: CartItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    interface CartItemListener {

        fun onIncreaseQuantity(item: CartItem)

        fun onDecreaseQuantity(item: CartItem)

        fun onVariantClick(item: CartItem)

        fun onItemClick(item: CartItem)
    }
}