package com.example.midterm.data.source

import com.example.midterm.data.model.Address
import com.example.midterm.data.model.PaymentMethod
import com.example.midterm.data.model.Product
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

    // ── Products ─────────────────────────────────────────────
    val products: List<Product> = listOf(
        Product("p1", "Bút bi Thiên Long", 5_000, "Văn phòng phẩm", android.R.drawable.ic_menu_edit),
        Product("p2", "Bút chì 2B", 3_000, "Văn phòng phẩm", android.R.drawable.ic_menu_edit),
        Product("p3", "Vở 200 trang", 15_000, "Giấy tờ", android.R.drawable.ic_menu_edit),
        Product("p4", "Áo thun nam", 120_000, "Thời trang", android.R.drawable.ic_menu_edit),
        Product("p5", "Nón kết", 50_000, "Phụ kiện", android.R.drawable.ic_menu_edit),
        Product("p6", "Móc khóa", 20_000, "Phụ kiện", android.R.drawable.ic_menu_edit)
    )

    // ── Vouchers ──────────────────────────────────────────────
    val vouchers: List<Voucher> = listOf(
        Voucher("GIAM10", VoucherType.PERCENT, 10, 50_000, 0xFF4CAF50.toInt()),
        Voucher("GIAM20", VoucherType.PERCENT, 20, 100_000, 0xFF2196F3.toInt()),
        Voucher("FREESHIP", VoucherType.SHIPPING, 0, 30_000, 0xFFFF9800.toInt()),
        Voucher("SALE50", VoucherType.PERCENT, 50, 200_000, 0xFFF44336.toInt())
    )

    // ── Payment Methods ──────────────────────────────────────
    val paymentMethods: List<PaymentMethod> = listOf(
        PaymentMethod("cash", "Tiền mặt", android.R.drawable.ic_menu_directions, "Thanh toán khi nhận hàng"),
        PaymentMethod("bank", "Chuyển khoản", android.R.drawable.ic_menu_directions, "Chuyển khoản ngân hàng"),
        PaymentMethod("momo", "Ví MoMo", android.R.drawable.ic_menu_directions, "Thanh toán qua ví điện tử")
    )

    // ── Default Address ──────────────────────────────────────
    val defaultAddress: Address = Address(
        street = "123 Nguyễn Huệ",
        ward = "Phường Bến Nghé",
        district = "Quận 1",
        city = "TP. Hồ Chí Minh",
        isDefault = true
    )
}
