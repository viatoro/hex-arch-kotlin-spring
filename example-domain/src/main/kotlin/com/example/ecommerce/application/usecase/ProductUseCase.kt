package com.example.ecommerce.application.usecase

import com.example.ecommerce.application.repositories.ProductRepository
import com.example.ecommerce.domain.product.model.Product
import java.io.FileNotFoundException

class ProductUseCase(
    val productRepository: ProductRepository,
) {
    fun create(product: Product) {
        productRepository.put(product)
    }
    fun delete(id: String) {
        productRepository.findByID(id) ?: throw FileNotFoundException()
        productRepository.delete(id)
    }
    fun update(product: Product) {

    }
    fun list(): List<Product> {
        return productRepository.getAll()
    }
    fun get(id: String): Product? {
        return productRepository.findByID(id)
    }
}