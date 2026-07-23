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
        Voucher("SAVE10", VoucherType.PRODUCT, 10, 50_000, 0xFF4CAF50.toInt()),
        Voucher("SAVE20", VoucherType.PRODUCT, 20, 100_000, 0xFF2196F3.toInt()),
        Voucher("SALE50", VoucherType.PRODUCT, 50, 200_000, 0xFFF44336.toInt()),
        Voucher("FREESHIP", VoucherType.DELIVERY, 100, 30_000, 0xFFFF9800.toInt()),
        Voucher("VIP2026", VoucherType.PRODUCT, 30, 0, 0xFF9C27B0.toInt(), isHidden = true)
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
