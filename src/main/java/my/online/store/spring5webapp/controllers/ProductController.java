package my.online.store.spring5webapp.controllers;

import my.online.store.spring5webapp.domain.MyOrder;
import my.online.store.spring5webapp.domain.Product;
import my.online.store.spring5webapp.repositories.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

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

    @PostMapping("/buyProduct")
    public ModelAndView buyProduct(HttpSession session, @ModelAttribute("productForm") Product product){
        MyOrder o = (MyOrder)session.getAttribute("myCart");
        if(o==null){
            o = new MyOrder();
            session.setAttribute("myCart", o);
        }
        o.addOneProduct(product);
        return new ModelAndView("redirect:/products");
    }
}
