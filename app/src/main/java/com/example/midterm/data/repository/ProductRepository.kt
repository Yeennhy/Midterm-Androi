package com.example.midterm.data.repository

import com.example.midterm.data.model.Product
import com.example.midterm.data.source.LocalMockData

/**
 * Repository providing access to the product catalog.
 *
 * MVVM Purpose: Repositories abstract the data source (mock, network, DB)
 * from the ViewModel. The ViewModel depends on the interface, not the
 * concrete implementation, making the app testable and source-swappable.
 *
 * Manual DI: ProductRepository is instantiated in the ViewModel's
 * ViewModelProvider.Factory and passed via constructor injection.
 */
class ProductRepository {

    private val products: List<Product> = LocalMockData.products

    fun getProducts(): List<Product> = products

    fun getProductById(id: String): Product? = products.find { it.id == id }
}
