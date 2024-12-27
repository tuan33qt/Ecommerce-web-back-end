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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;


    @Override
    public Product createProduct(ProductDTO productDTO, MultipartFile file) throws Exception {
        // Kiểm tra Category tồn tại
        Category existsCategory = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find category id " + productDTO.getCategoryId()));

        // Kiểm tra file có tồn tại không, nếu có thì chuyển đổi thành Base64
        String base64Url = null;
        if (file != null && !file.isEmpty()) {
            base64Url = convertFileToBase64(file); // Chuyển file thành Base64
        }

        // Tạo sản phẩm mới và gán các thông tin
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .url(base64Url)  // Lưu Base64 vào trường URL
                .description(productDTO.getDescription())
                .quantity(productDTO.getQuantity())
                .color(productDTO.getColor())
                .color2(productDTO.getColor2())
                .code(productDTO.getCode())
                .category(existsCategory)
                .build();

        // Lưu sản phẩm vào cơ sở dữ liệu
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(long id) throws DataNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("can not find product id" +id));
    }

    @Override
    public List<Product> getAllProducts() {
        // lấy danh sách sản phẩm theo trang và giới hạn
        return productRepository.findAll();
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
            existsProduct.setCode(productDTO.getCode());
            existsProduct.setColor2(productDTO.getColor2());
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
    public List<ProductImage> getProductImagesByProductId(Long productId) {
        return productImageRepository.findByProductId(productId);
    }
    private String convertFileToBase64(MultipartFile file) throws IOException {
        // Đọc toàn bộ dữ liệu hình ảnh vào mảng byte
        byte[] fileBytes = file.getBytes();

        // Mã hóa mảng byte thành chuỗi Base64
        return Base64.getEncoder().encodeToString(fileBytes);
    }
}
