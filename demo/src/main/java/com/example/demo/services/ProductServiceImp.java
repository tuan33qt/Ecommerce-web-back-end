package com.example.demo.services;

import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.ProductImageDTO;
import com.example.demo.exceptions.DataNotFoundException;
import com.example.demo.models.Category;
import com.example.demo.models.Product;
import com.example.demo.models.ProductImage;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.repositories.ProductImageRepository;
import com.example.demo.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    @Override
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category existsCategory= categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() ->new DataNotFoundException("can not find category id" + productDTO.getCategoryId()));
        Product newProduct=Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .url(productDTO.getUrl())
                .description(productDTO.getDescription())
                .quantity(productDTO.getQuantity())
                .color(productDTO.getColor())
                .code(productDTO.getCode())
                .category(existsCategory)
                .build();
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(long id) throws DataNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("can not find product id" +id));
    }

    @Override
    public Page<Product> getAllProducts(PageRequest pageRequest) {
        // lấy danh sách sản phẩm theo trang và giới hạn
        return productRepository.findAll(pageRequest);
    }

    @Override
    public Product updateProduct(long id, ProductDTO productDTO)throws DataNotFoundException {
        Product existsProduct=getProductById(id);
        if (existsProduct != null) {
            Category existsCategory= categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() ->new DataNotFoundException("can not find category id" + productDTO.getCategoryId()));
            existsProduct.setName(productDTO.getName());
            existsProduct.setCategory(existsCategory);
            existsProduct.setPrice(productDTO.getPrice());
            existsProduct.setDescription(productDTO.getDescription());
            existsProduct.setQuantity(productDTO.getQuantity());
            existsProduct.setUrl(productDTO.getUrl());
            existsProduct.setColor(productDTO.getColor());
            existsProduct.setDescription(productDTO.getCode());
            return productRepository.save(existsProduct);
        }
        return null;
    }

    @Override
    public void deleteProduct(long id) {
        productRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }
    @Override
    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws  DataNotFoundException {
        Product existsProduct=productRepository.findById(productId)
                .orElseThrow(() ->new DataNotFoundException("can not find product with id" +productImageDTO.getProductId()));
        ProductImage newProductImage=ProductImage.builder()
                .product(existsProduct)
                .url(productImageDTO.getUrl())
                .build();
        //kh cho insert quá 5 ảnh cho 1 sp
        int size=productImageRepository.findByProductId(productId).size();
        if(size >=ProductImage.MAXIMUM_IMAGES) {
            throw new DataNotFoundException("size must be large 5");
        }
         return  productImageRepository.save(newProductImage);
    }
}
