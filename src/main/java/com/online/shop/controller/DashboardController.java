package com.online.shop.controller;

import com.online.shop.dto.ProductDto;
import com.online.shop.entity.Product;
import com.online.shop.entity.User;
import com.online.shop.service.ProductService;
import com.online.shop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
public class DashboardController {

    private final ProductService productService;
    private final UserService userService;

    public DashboardController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        List<Product> products = productService.getAllProducts();
        
        model.addAttribute("user", user);
        model.addAttribute("products", products);
        model.addAttribute("newProduct", new ProductDto());
        
        return "dashboard/index";
    }

    @GetMapping("/products")
    public String productsPage(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        model.addAttribute("newProduct", new ProductDto());
        return "dashboard/products";
    }

    @PostMapping("/products")
    public String createProduct(@Valid @ModelAttribute("newProduct") ProductDto productDto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            List<Product> products = productService.getAllProducts();
            model.addAttribute("products", products);
            return "dashboard/products";
        }

        productService.createProduct(productDto);
        redirectAttributes.addFlashAttribute("successMessage", "Product created successfully!");
        return "redirect:/products";
    }

    @GetMapping("/products/{id}/edit")
    public String editProductPage(@PathVariable UUID id, Model model) {
        Product product = productService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        
        ProductDto productDto = productService.convertToDto(product);
        model.addAttribute("product", productDto);
        return "dashboard/edit-product";
    }

    @PostMapping("/products/{id}/edit")
    public String updateProduct(@PathVariable UUID id,
                               @Valid @ModelAttribute("product") ProductDto productDto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "dashboard/edit-product";
        }

        productService.updateProduct(id, productDto);
        redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully!");
        return "redirect:/products";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully!");
        return "redirect:/products";
    }

    @GetMapping("/orders")
    public String ordersPage(Model model) {
        List<Product> availableProducts = productService.getAvailableProducts();
        model.addAttribute("products", availableProducts);
        return "dashboard/orders";
    }
} 