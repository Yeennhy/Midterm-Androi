package com.example.midterm.data

import com.example.midterm.data.repository.CartRepository
import com.example.midterm.data.repository.ProductRepository
import com.example.midterm.data.repository.SeminarRepository
import com.example.midterm.data.repository.VoucherRepository

object ServiceLocator {

    val cartRepository: CartRepository by lazy { CartRepository() }
    val productRepository: ProductRepository by lazy { ProductRepository() }
    val voucherRepository: VoucherRepository by lazy { VoucherRepository() }
    val seminarRepository: SeminarRepository by lazy { SeminarRepository() }
}
