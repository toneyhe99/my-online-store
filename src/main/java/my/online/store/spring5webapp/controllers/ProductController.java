package my.online.store.spring5webapp.controllers;

import my.online.store.spring5webapp.repositories.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by jt on 12/24/19.
 */
@Controller
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @RequestMapping("/products")
    public String getBooks(Model model){

        model.addAttribute("products", productRepository.findAll());

        return "products/list";
    }
}
