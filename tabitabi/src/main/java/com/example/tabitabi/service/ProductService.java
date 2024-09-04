package com.example.tabitabi.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.tabitabi.exception.ResourceNotFoundException;
import com.example.tabitabi.model.Product.Product;
import com.example.tabitabi.model.Product.ProductImage;
import com.example.tabitabi.model.seller.Seller;
import com.example.tabitabi.repository.FileRepository;
import com.example.tabitabi.repository.ProductImageRepository;
import com.example.tabitabi.repository.ProductRepository;
import com.example.tabitabi.util.FileService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductService {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final ProductImageRepository productImageRepository;
    
    private final FileService fileService;
    private final FileRepository fileRepository;

    public Page<Product> getProductsBySeller(Seller seller, Pageable pageable) {
        return productRepository.findBySeller(seller, pageable);
    }

    @Transactional
    public void createProduct(Product product, ProductImage productImage) {
        productRepository.save(product);
        if (productImage != null) {
        	productImage.setProduct(product);
            productImageRepository.save(productImage);

        }
    }

    public Product findProduct(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        Optional<Product> optionalProduct = productRepository.findById(productId);

        return optionalProduct.orElseThrow(() -> new NoSuchElementException("Product not found for ID: " + productId));
    }

    @Transactional
    public void updateProduct(Product updateProduct, boolean isFileRemoved, MultipartFile file) {
        Product findProduct = findProduct(updateProduct.getProductId());

        findProduct.setName(updateProduct.getName());
        findProduct.setDescription(updateProduct.getDescription());
        findProduct.setPrice(updateProduct.getPrice());
        findProduct.setStock(updateProduct.getStock());
        findProduct.setStatus(updateProduct.getStatus());
        findProduct.setCategory(updateProduct.getCategory());

        // 기존 파일 확인 및 삭제
        ProductImage productImage = findFileByProductId(findProduct);
        if (productImage != null && (isFileRemoved || (file != null && file.getSize() > 0))) {
            removeFile(productImage);
        }

        // 새 파일이 있는 경우 저장
        if (file != null && file.getSize() > 0) {
            ProductImage savedFile = fileService.saveFile(file);
            savedFile.setProduct(findProduct);
            createProduct(findProduct, savedFile);
        }

        productRepository.save(findProduct);
    }




    @Transactional
    public void removeFile(ProductImage productImage) {
        fileRepository.deleteById(productImage.getProductimageid());
        String fullPath = uploadPath + "/" + productImage.getSaved_filename();
        fileService.deleteFile(fullPath);
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public ProductImage findFileByProductId(Product product) {
        ProductImage productImage = fileRepository.findByProduct(product);
        return productImage;
    }

    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));
    }

    @Transactional
    public void deleteProduct(Product product) {
        
        ProductImage productImage = findFileByProductId(product);
        if (productImage != null) {
            removeFile(productImage);
        }
        productRepository.deleteById(product.getProductId());
    }

	public Product findProductById(Long productId) {
		Product product = productRepository.getById(productId);
		return product;
	}
}
