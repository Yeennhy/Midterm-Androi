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
    const val DEFAULT_SHIPPING_FEE: Long = 22_000L

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
            discountBadge = "20% OFF",
            badgeText = "20% OFF",
            expiryLabel = "EXPIRES 30 SEP"
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
            discountBadge = "50% OFF",
            badgeText = "50% OFF",
            expiryLabel = "EXPIRES IN 2 DAYS"
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
            badgeText = "20K OFF",
            expiryLabel = "ONE-TIME USE",
            isFixedValue = true
        ),
        Voucher(
            code = "WELCOME",
            type = VoucherType.PRODUCT,
            value = 15,
            minSpend = 0L,
            title = "New Member Bonus",
            description = "New customers only",
            badgeText = "15K OFF",
            expiryLabel = "ONE-TIME USE"
        ),
        Voucher(
            code = "SALE10",
            type = VoucherType.PRODUCT,
            value = 10,
            minSpend = 50_000L,
            title = "Weekend Sale",
            description = "Min. spend 50k",
            badgeText = "10% OFF",
            expiryLabel = "EXPIRES 5 AUG"
        ),
        Voucher(
            code = "SALE30",
            type = VoucherType.PRODUCT,
            value = 30,
            minSpend = 150_000L,
            title = "Flash Sale",
            description = "Min. spend 150k • Cap 50k",
            badgeText = "30% OFF",
            expiryLabel = "EXPIRES IN 3 DAYS"
        ),
        Voucher(
            code = "STUDENT15",
            type = VoucherType.PRODUCT,
            value = 15,
            minSpend = 0L,
            title = "Student Discount",
            description = "Valid with student ID",
            badgeText = "15% OFF",
            expiryLabel = "NO EXPIRY"
        ),
        Voucher(
            code = "LOYALTY25",
            type = VoucherType.PRODUCT,
            value = 25,
            minSpend = 100_000L,
            title = "Loyalty Reward",
            description = "For returning customers",
            badgeText = "25% OFF",
            expiryLabel = "EXPIRES 31 DEC"
        ),
        Voucher(
            code = "MEGA40",
            type = VoucherType.PRODUCT,
            value = 40,
            minSpend = 300_000L,
            title = "Mega Sale",
            description = "Min. spend 300k • Cap 80k",
            badgeText = "40% OFF",
            expiryLabel = "EXPIRES 20 OCT"
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
            discountBadge = "20% OFF",
            badgeText = "20% OFF",
            expiryLabel = "EXPIRES 30 SEP"
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
            discountBadge = "FREE",
            badgeText = "FREE",
            expiryLabel = "EXPIRES IN 2 DAYS"
        ),
        Voucher(
            code = "FREESHIP50",
            type = VoucherType.DELIVERY,
            value = 100,
            minSpend = 50_000L,
            title = "Standard Delivery",
            description = "Min. spend 50k",
            badgeText = "FREE",
            expiryLabel = "EXPIRES 10 AUG"
        ),
        Voucher(
            code = "EXPRESS10",
            type = VoucherType.DELIVERY,
            value = 10,
            minSpend = 0L,
            title = "Express Delivery",
            description = "Cap 15k",
            badgeText = "10% OFF",
            expiryLabel = "EXPIRES IN 5 DAYS"
        ),
        Voucher(
            code = "SHIP30OFF",
            type = VoucherType.DELIVERY,
            value = 30,
            minSpend = 80_000L,
            title = "Standard Delivery",
            description = "Min. spend 80k",
            badgeText = "30% OFF",
            expiryLabel = "EXPIRES 15 SEP"
        ),
        Voucher(
            code = "WEEKENDSHIP",
            type = VoucherType.DELIVERY,
            value = 100,
            minSpend = 0L,
            title = "Weekend Delivery",
            description = "Sat - Sun only",
            badgeText = "FREE",
            expiryLabel = "WEEKENDS ONLY"
        ),
        Voucher(
            code = "NEWAREA_FREE",
            type = VoucherType.DELIVERY,
            value = 100,
            minSpend = 0L,
            title = "New Area Promo",
            description = "First order in new areas",
            badgeText = "FREE",
            expiryLabel = "LIMITED TIME"
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
            badgeText = "25% OFF",
            expiryLabel = "EXPIRES 31 AUG",
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
            badgeText = "30% OFF",
            expiryLabel = "LIMITED TIME",
            isHidden = true
        ),
        Voucher(
            code = "HIHI",
            type = VoucherType.PRODUCT,
            value = 30,
            minSpend = 0L,
            title = "VIP Member",
            description = "Exclusive early access",
            badgeText = "30% OFF",
            expiryLabel = "LIMITED TIME",
            isHidden = true
        )
    )

    // ── Payment Methods ──────────────────────────────────────
    val paymentMethods: List<PaymentMethod> = listOf(
        PaymentMethod("card", "Credit Card", android.R.drawable.ic_menu_directions, "Ending in **4242"),
        PaymentMethod("momo", "MOMO", android.R.drawable.ic_menu_directions, "0123456789"),
        PaymentMethod("bank", "Bank Transfer", android.R.drawable.ic_menu_directions, "Agribank"),
        PaymentMethod("cash", "Cash on Delivery", android.R.drawable.ic_menu_directions, "Pay on delivery")
    )

    // ── Default Address ──────────────────────────────────────
    val defaultAddress: Address = Address(
        street = "53 Nguyen Du",
        ward = "Sai Gon Ward",
        district = "District 1",
        city = "HCMC",
        isDefault = true
    )
}
