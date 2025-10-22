package com.wtech.ecommerce.application.repositories

import com.wtech.ecommerce.domain.product.model.Product


interface ProductRepository {
    fun findByID(id: String): Product?
    fun put(product: Product)
    fun getAll(): List<Product>
    fun delete(id: String)
}