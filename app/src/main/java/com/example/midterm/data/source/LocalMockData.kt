package com.example.midterm.data.source

import com.example.midterm.data.model.Address
import com.example.midterm.data.model.CartItem
import com.example.midterm.data.model.PaymentMethod
import com.example.midterm.data.model.Product
import com.example.midterm.data.model.ProductVariant
import com.example.midterm.data.model.Voucher
import com.example.midterm.data.model.VoucherType

/**
 * Central mock data source supplying predefined products, vouchers,
 * payment methods, and addresses for the seminar demo.
 *
 * MVVM Layer: This is the "source of truth" that repositories read from.
 */
object LocalMockData {

    // Standard delivery fee charged when no DELIVERY voucher is applied.
    const val DEFAULT_SHIPPING_FEE: Long = 30_000L

    // ── Product Variants ─────────────────────────────────────
    val notebookVariants: List<ProductVariant> = listOf(
        ProductVariant("v-nb-1", "A5 • Dotted"),
        ProductVariant("v-nb-2", "A5 • Lined"),
        ProductVariant("v-nb-3", "A4 • Dotted")
    )

    val penVariants: List<ProductVariant> = listOf(
        ProductVariant("v-pen-1", "0.5mm • Earth Tones"),
        ProductVariant("v-pen-2", "0.5mm • Pastel"),
        ProductVariant("v-pen-3", "0.7mm • Black")
    )

    val weightVariants: List<ProductVariant> = listOf(
        ProductVariant("v-w-1", "300g • Polished"),
        ProductVariant("v-w-2", "500g • Matte Brass")
    )

    // ── Products (Figma Catalog) ──────────────────────────────
    val products: List<Product> = listOf(
        Product(
            id = "p1",
            name = "Linen Hardcover Notebook",
            price = 50_000L,
            category = "Paper Goods",
            imageResId = android.R.drawable.ic_menu_edit,
            variants = notebookVariants
        ),
        Product(
            id = "p2",
            name = "Matte Gel Pen Set",
            price = 60_000L,
            category = "Stationery",
            imageResId = android.R.drawable.ic_menu_edit,
            variants = penVariants
        ),
        Product(
            id = "p3",
            name = "Brushed Brass Weight",
            price = 100_000L,
            category = "Desk Accessories",
            imageResId = android.R.drawable.ic_menu_edit,
            variants = weightVariants
        ),
        Product(
            id = "p4",
            name = "Men's T-Shirt",
            price = 120_000L,
            category = "Fashion",
            imageResId = android.R.drawable.ic_menu_edit
        )
    )

    // ── Initial Cart Items (Figma Mockup State) ─────────────────
    val initialCartItems: List<CartItem> = listOf(
        CartItem(
            product = products[0],
            quantity = 1,
            isSelected = true,
            selectedVariant = notebookVariants[0] // A5 • Dotted
        ),
        CartItem(
            product = products[1],
            quantity = 2,
            isSelected = true,
            selectedVariant = penVariants[0] // 0.5mm • Earth Tones
        ),
        CartItem(
            product = products[2],
            quantity = 1,
            isSelected = false,
            selectedVariant = weightVariants[0] // 300g • Polished
        )
    )

    // ── Vouchers (Figma Mockup Vouchers) ────────────────────────
    val vouchers: List<Voucher> = listOf(
        // Product Vouchers
        Voucher(
            code = "MUJI_ZEN_20",
            type = VoucherType.PRODUCT,
            value = 20,
            minSpend = 30_000L,
            color = 0xFF2D4F43.toInt(),
            title = "Storewide Special",
            description = "Min. spend 30k • Cap 50k",
            expiryText = "EXPIRES 30 SEP",
            discountBadge = "20% OFF"
        ),
        Voucher(
            code = "GIFT50",
            type = VoucherType.PRODUCT,
            value = 50,
            minSpend = 0L,
            color = 0xFF2D4F43.toInt(),
            title = "Grand Opening Special",
            description = "No minimum spend",
            expiryText = "EXPIRES IN 2 DAYS",
            discountBadge = "50% OFF"
        ),
        Voucher(
            code = "NEWFRIEND20",
            type = VoucherType.PRODUCT,
            value = 20_000,
            minSpend = 0L,
            color = 0xFFD8D2C5.toInt(),
            title = "New Member Bonus",
            description = "New customers only",
            expiryText = "ONE-TIME USE",
            discountBadge = "20K OFF",
            isFixedValue = true
        ),

        // Delivery Vouchers
        Voucher(
            code = "DELIVERY_20",
            type = VoucherType.DELIVERY,
            value = 20,
            minSpend = 100_000L,
            color = 0xFFA84323.toInt(),
            title = "Standard Delivery",
            description = "Min. spend 100k • Cap 10k",
            expiryText = "EXPIRES 30 SEP",
            discountBadge = "20% OFF"
        ),
        Voucher(
            code = "FREESHIP_NOW",
            type = VoucherType.DELIVERY,
            value = 100,
            minSpend = 0L,
            color = 0xFFA84323.toInt(),
            title = "Standard Delivery",
            description = "No minimum spend",
            expiryText = "EXPIRES IN 2 DAYS",
            discountBadge = "FREE"
        ),
        Voucher(
            code = "WELCOME",
            type = VoucherType.DELIVERY,
            value = 15_000,
            minSpend = 0L,
            color = 0xFFA84323.toInt(),
            title = "New Member Bonus",
            description = "New customers only",
            expiryText = "ONE-TIME USE",
            discountBadge = "15K OFF",
            isFixedValue = true
        ),

        // Secret / Hidden Vouchers (Unlocked via Manual Input)
        Voucher(
            code = "SUMMER24",
            type = VoucherType.PRODUCT,
            value = 25,
            minSpend = 50_000L,
            color = 0xFF2D4F43.toInt(),
            title = "Summer Sale 2024",
            description = "Exclusive 25% OFF summer deal",
            expiryText = "EXPIRES 31 AUG",
            discountBadge = "25% OFF",
            isHidden = true
        ),
        Voucher(
            code = "VIP2026",
            type = VoucherType.PRODUCT,
            value = 30,
            minSpend = 0L,
            color = 0xFF9C27B0.toInt(),
            title = "VIP Member Special",
            description = "Exclusive 30% OFF for VIPs",
            expiryText = "LIMITED TIME",
            discountBadge = "30% OFF",
            isHidden = true
        )
    )

    // ── Payment Methods ──────────────────────────────────────
    val paymentMethods: List<PaymentMethod> = listOf(
        PaymentMethod("cash", "Cash", android.R.drawable.ic_menu_directions, "Pay on delivery"),
        PaymentMethod("bank", "Bank Transfer", android.R.drawable.ic_menu_directions, "Pay via bank transfer"),
        PaymentMethod("momo", "MoMo Wallet", android.R.drawable.ic_menu_directions, "Pay via e-wallet")
    )

    // ── Default Address ──────────────────────────────────────
    val defaultAddress: Address = Address(
        street = "123 Nguyen Hue Street",
        ward = "Ben Nghe Ward",
        district = "District 1",
        city = "Ho Chi Minh City",
        isDefault = true
    )
}

