package com.example.demo.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    @NotBlank(message = "title is required")
    private String name;
    @Min(value = 0,message = "price must be greater or equal to 0")
    private Float price;
    private String url;
    private String description;
    private Long  quantity;
    @JsonProperty("category_id")
    private Long categoryId;
    private String color;
    private String code;
}
