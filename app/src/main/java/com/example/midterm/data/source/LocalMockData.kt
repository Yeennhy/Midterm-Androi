package com.example.midterm.data.source

import com.example.midterm.data.model.Address
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
 * In a real app this would be replaced with network/DB calls, but the
 * Repository pattern ensures the rest of the app is unaffected by the swap.
 */
object LocalMockData {

    // Standard delivery fee charged when no DELIVERY voucher is applied.
    const val DEFAULT_SHIPPING_FEE: Long = 30_000L

    // ── Product Variants ─────────────────────────────────────
    // Options shown in the variant picker menu (e.g. VariantSelectorSheet)
    // when a product is tapped. colorHex drives an optional swatch dot.
    private val penVariants: List<ProductVariant> = listOf(
        ProductVariant("p1-red", "Red", colorHex = 0xFFF44336.toInt()),
        ProductVariant("p1-blue", "Blue", colorHex = 0xFF2196F3.toInt()),
        ProductVariant("p1-black", "Black", colorHex = 0xFF000000.toInt())
    )

    // ── Products ─────────────────────────────────────────────
    val products: List<Product> = listOf(
        Product("p1", "Ballpoint Pen", 5_000, "Stationery", android.R.drawable.ic_menu_edit, penVariants),
        Product("p2", "2B Pencil", 3_000, "Stationery", android.R.drawable.ic_menu_edit),
        Product("p3", "200-Page Notebook", 15_000, "Paper Goods", android.R.drawable.ic_menu_edit),
        Product("p4", "Men's T-Shirt", 120_000, "Fashion", android.R.drawable.ic_menu_edit),
        Product("p5", "Cap", 50_000, "Accessories", android.R.drawable.ic_menu_edit),
        Product("p6", "Keychain", 20_000, "Accessories", android.R.drawable.ic_menu_edit)
    )

    // ── Vouchers ──────────────────────────────────────────────
    // Visible vouchers show up in the Discount page's Product/Delivery lists.
    // Hidden vouchers (isHidden = true) are omitted from those lists and only
    // apply once the user types the matching code into the "enter code" field.
    val vouchers: List<Voucher> = listOf(
        Voucher(
            code = "DELIVERY_20",
            type = VoucherType.DELIVERY,
            value = 20,
            minSpend = 100_000,
            title = "Standard Delivery",
            description = "Min. spend 100k • Cap 10k",
            badgeText = "20% OFF",
            expiryLabel = "EXPIRES 30 SEP"
        ),
        Voucher(
            code = "FREESHIP_NOW",
            type = VoucherType.DELIVERY,
            value = 100,
            minSpend = 0,
            title = "Standard Delivery",
            description = "No minimum spend",
            badgeText = "FREE",
            expiryLabel = "EXPIRES IN 2 DAYS"
        ),
        Voucher(
            code = "FREESHIP50",
            type = VoucherType.DELIVERY,
            value = 100,
            minSpend = 50_000,
            title = "Standard Delivery",
            description = "Min. spend 50k",
            badgeText = "FREE",
            expiryLabel = "EXPIRES 10 AUG"
        ),
        Voucher(
            code = "EXPRESS10",
            type = VoucherType.DELIVERY,
            value = 10,
            minSpend = 0,
            title = "Express Delivery",
            description = "Cap 15k",
            badgeText = "10% OFF",
            expiryLabel = "EXPIRES IN 5 DAYS"
        ),
        Voucher(
            code = "SHIP30OFF",
            type = VoucherType.DELIVERY,
            value = 30,
            minSpend = 80_000,
            title = "Standard Delivery",
            description = "Min. spend 80k",
            badgeText = "30% OFF",
            expiryLabel = "EXPIRES 15 SEP"
        ),
        Voucher(
            code = "WEEKENDSHIP",
            type = VoucherType.DELIVERY,
            value = 100,
            minSpend = 0,
            title = "Weekend Delivery",
            description = "Sat - Sun only",
            badgeText = "FREE",
            expiryLabel = "WEEKENDS ONLY"
        ),
        Voucher(
            code = "NEWAREA_FREE",
            type = VoucherType.DELIVERY,
            value = 100,
            minSpend = 0,
            title = "New Area Promo",
            description = "First order in new areas",
            badgeText = "FREE",
            expiryLabel = "LIMITED TIME"
        ),
        Voucher(
            code = "WELCOME",
            type = VoucherType.PRODUCT,
            value = 15,
            minSpend = 0,
            title = "New Member Bonus",
            description = "New customers only",
            badgeText = "15K OFF",
            expiryLabel = "ONE-TIME USE"
        ),
        Voucher(
            code = "SALE10",
            type = VoucherType.PRODUCT,
            value = 10,
            minSpend = 50_000,
            title = "Weekend Sale",
            description = "Min. spend 50k",
            badgeText = "10% OFF",
            expiryLabel = "EXPIRES 5 AUG"
        ),
        Voucher(
            code = "SALE30",
            type = VoucherType.PRODUCT,
            value = 30,
            minSpend = 150_000,
            title = "Flash Sale",
            description = "Min. spend 150k • Cap 50k",
            badgeText = "30% OFF",
            expiryLabel = "EXPIRES IN 3 DAYS"
        ),
        Voucher(
            code = "STUDENT15",
            type = VoucherType.PRODUCT,
            value = 15,
            minSpend = 0,
            title = "Student Discount",
            description = "Valid with student ID",
            badgeText = "15% OFF",
            expiryLabel = "NO EXPIRY"
        ),
        Voucher(
            code = "LOYALTY25",
            type = VoucherType.PRODUCT,
            value = 25,
            minSpend = 100_000,
            title = "Loyalty Reward",
            description = "For returning customers",
            badgeText = "25% OFF",
            expiryLabel = "EXPIRES 31 DEC"
        ),
        Voucher(
            code = "MEGA40",
            type = VoucherType.PRODUCT,
            value = 40,
            minSpend = 300_000,
            title = "Mega Sale",
            description = "Min. spend 300k • Cap 80k",
            badgeText = "40% OFF",
            expiryLabel = "EXPIRES 20 OCT"
        ),
        Voucher(
            code = "HIHI",
            type = VoucherType.PRODUCT,
            value = 30,
            minSpend = 0,
            title = "VIP Member",
            description = "Exclusive early access",
            badgeText = "30% OFF",
            expiryLabel = "LIMITED TIME",
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
