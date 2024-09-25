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
import com.example.tabitabi.model.Product.ProductStatus;
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
    public void createProduct(Product product, List<ProductImage> productImages) {
    	if(product.getStock() > 0) product.setStatus(ProductStatus.AVAILABLE);
        productRepository.save(product);
        if (productImages != null && !productImages.isEmpty()) {
            for (ProductImage productImage : productImages) {
                productImage.setProduct(product);
                productImageRepository.save(productImage);
            }
        }
    }

    public Product findProduct(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        Optional<Product> optionalProduct = productRepository.findById(productId);

        return optionalProduct.orElseThrow(() -> new NoSuchElementException("Product not found for ID: " + productId));
    }
    
    public Page<Product> findSearch(String searchText, Pageable pageable) {
		Page<Product> searchList = productRepository.findByNameContaining(searchText, pageable);
		return searchList;
	}

    @Transactional
    public void updateProduct(Product updateProduct, boolean areFilesRemoved, List<MultipartFile> files) {
        Product findProduct = findProduct(updateProduct.getProductId());

        findProduct.setName(updateProduct.getName());
        findProduct.setDescription(updateProduct.getDescription());
        findProduct.setPrice(updateProduct.getPrice());
        findProduct.setStatus(updateProduct.getStatus());
        findProduct.setCategory(updateProduct.getCategory());
        
        if(updateProduct.getStock() > 0) findProduct.setStatus(ProductStatus.AVAILABLE);
        else findProduct.setStock(updateProduct.getStock());

        // 기존 파일 확인 및 삭제
        List<ProductImage> existingImages = findFilesByProduct(findProduct);

        // 이미지 삭제 조건 확인
        if (existingImages != null && !existingImages.isEmpty() && areFilesRemoved) {
            for (ProductImage image : existingImages) {
                removeFile(image);
                productImageRepository.delete(image); // 데이터베이스에서 이미지 삭제
            }
        }

        // 새 파일이 있는 경우 저장
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file != null && file.getSize() > 0) {
                    ProductImage savedFile = fileService.saveFile(file);
                    savedFile.setProduct(findProduct);
                    productImageRepository.save(savedFile);
                }
            }
        }

        // 제품 정보 업데이트
        productRepository.save(findProduct);
    }
    
    public List<ProductImage> findFilesByProduct(Product product) {
        return productImageRepository.findByProduct(product);
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
    
    public ProductImage findFileByProduct(Product product) {
        // findByProduct가 여러 결과를 반환할 수 있으므로 적절한 메서드로 수정
        List<ProductImage> images = productImageRepository.findByProduct(product);
        if (images.isEmpty()) {
            return null; // 또는 적절한 처리
        }
        return images.get(0); // 첫 번째 이미지만 반환 (또는 원하는 처리를 수행)
    }

    @Transactional
    public void deleteProduct(Product product) {
        // 모든 이미지를 삭제하도록 수정
        List<ProductImage> productImages = findFilesByProduct(product);
        for (ProductImage productImage : productImages) {
            removeFile(productImage);
        }
        productRepository.deleteById(product.getProductId());
    }

	public Product findProductById(Long productId) {
		Product product = productRepository.getById(productId);
		return product;
	}

    public Product findByProductId(Long productId) {
    	Optional<Product> product = productRepository.findById(productId);
    	return product.orElse(null);
    }
    
    public List<Product> getProductsBySellerId(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }
    
    public List<Product> findProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
}
