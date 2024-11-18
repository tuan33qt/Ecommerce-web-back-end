package com.example.demo.controller;

import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.ProductImageDTO;
import com.example.demo.exceptions.DataNotFoundException;
import com.example.demo.models.Product;
import com.example.demo.models.ProductImage;
import com.example.demo.response.ProductResponse;
import com.example.demo.services.ProductService;
import com.github.javafaker.Faker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.print.attribute.standard.PageRanges;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {
    private final ProductService productService;


    @PostMapping( "")
    public ResponseEntity<?> createProduct (@Valid @RequestBody
                                                     ProductDTO productDTO
//
    ) throws IOException, DataNotFoundException {
        Product newProduct= productService.createProduct(productDTO);
        return ResponseEntity.ok(newProduct);
    }
    @PostMapping(value = "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(@PathVariable("id") Long productId,
                                          @RequestParam("files") List<MultipartFile> files) {
        try {
            // Kiểm tra sản phẩm tồn tại
            Product existsProduct = productService.getProductById(productId);
            if (existsProduct == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
            }

            // Kiểm tra files có null không, nếu null thì khởi tạo danh sách trống
            files = files == null ? new ArrayList<MultipartFile>() : files;

            // Kiểm tra số lượng file, không quá 5 file
            if (files.size() > ProductImage.MAXIMUM_IMAGES) {
                return ResponseEntity.badRequest().body("Maximum of 5 files are allowed");
            }

            // Danh sách lưu trữ ảnh sản phẩm
            List<ProductImage> productImages = new ArrayList<>();

            // Xử lý từng file
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    // Kiểm tra kích thước file
                    if (file.getSize() == 0) {
                        continue;  // Bỏ qua tệp có kích thước 0
                    }

                    if (file.getSize() > 10 * 1024 * 1024) { // Kích thước > 10MB
                        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                                .body("File is too large! Maximum size is 10MB");
                    }

                    // Kiểm tra kiểu tệp (phải là ảnh)
                    String contentType = file.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) {
                        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                                .body("File must be an image");
                    }

                    // Lưu file vào hệ thống và lấy tên file
                    String filename = storeFile(file);
                    if (filename == null) {
                        throw new IllegalArgumentException("File storage failed.");
                    }
                    // Lưu thông tin ảnh vào cơ sở dữ liệu
                    ProductImage productImage = productService.createProductImage(existsProduct.getId(),
                            ProductImageDTO.builder()
                                    .url(filename)
                                    .build());
                    productImages.add(productImage);
                }
            }

            // Trả về danh sách các ảnh đã lưu
            return ResponseEntity.ok().body(productImages);
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File upload error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    private String storeFile(MultipartFile file) throws IOException{
        if (!isImageFile(file) || file.getOriginalFilename()== null) {
            throw new IOException("Invalid image format");
        }
        String filename= StringUtils.cleanPath(file.getOriginalFilename());//làm sạch tên file
        // thêm UUID vào trước tên file để đảm bảo tên file là duy nhât
        String uniqueFilename= UUID.randomUUID().toString() + "_" + filename;
        //đường dẫn mà bạn muốn lưu file
        Path uploadDir= Paths.get("uploads");
        //kiểm tra và tạo thư mục nếu nó không tồn tại
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        // đường dẫn đầy đủ đến file
        Path destination=Paths.get(uploadDir.toString(),uniqueFilename);
        //sao chép file vào thư mục đích
        Files.copy(file.getInputStream(),destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }
    private boolean isImageFile(MultipartFile file) {
        String contentType=file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
    @GetMapping("")
    public ResponseEntity<ProductResponse> getProducts(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        // tạo pageable từ thông tin page và limit
        PageRequest pageRequest=PageRequest.of(page,limit,
                Sort.by("createAt").descending());
        Page<Product> productPage=productService.getAllProducts(pageRequest);
        //lấy tổng số trang
        int totalPages=productPage.getTotalPages();
        List<Product> products=productPage.getContent();
        ProductResponse response = new ProductResponse(products, totalPages);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long productId) {
        try {
            Product existProduct=productService.getProductById(productId);
            return ResponseEntity.ok(existProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long id) {
        try {
           productService.deleteProduct(id);
            return ResponseEntity.ok(String.format("product with id = %d deleted successfully " ,id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @PostMapping("/generateFakeProducts")
    public ResponseEntity<String> generateFakeProducts() {
        Faker faker=new Faker();
        for ( int i=0;i<1000;i++) {
            String productName=faker.commerce().productName();
            if(productService.existsByName(productName)) {
                continue;
            }
            ProductDTO productDTO=ProductDTO.builder()
                    .name(productName)
                    .price((float)faker.number().numberBetween(1,9000000))
                    .description(faker.lorem().sentence())
                    .quantity((long)faker.number().numberBetween(1,20))
                    .categoryId((long)faker.number().numberBetween(2,5))
                    .build();
            try {
                productService.createProduct(productDTO);
            } catch (DataNotFoundException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Fake products created successfully");
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") Long id,@RequestBody ProductDTO productDTO) {
        try {
            Product updateProduct=productService.updateProduct(id,productDTO);
            return ResponseEntity.ok(updateProduct);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}