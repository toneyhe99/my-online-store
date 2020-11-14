package my.online.store.spring5webapp.bootstrap;

import my.online.store.spring5webapp.controllers.OrderController;
import my.online.store.spring5webapp.domain.LineItem;
import my.online.store.spring5webapp.domain.MyOrder;
import my.online.store.spring5webapp.domain.Product;
import my.online.store.spring5webapp.repositories.LineItemRepository;
import my.online.store.spring5webapp.repositories.ProductRepository;
import my.online.store.spring5webapp.services.DiscountUtil;
import my.online.store.spring5webapp.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;

@Component
public class BootStrapData implements CommandLineRunner {

    private final OrderController orderController;
    private final ProductRepository productRepository;
    private final LineItemRepository lineItemRepository;
    private final OrderService orderService;
    @Autowired
    DiscountUtil discountUtil;

    public BootStrapData(ProductRepository productRepository, LineItemRepository lineItemRepository, OrderController orderController, OrderService orderService) {
        this.productRepository = productRepository;
        this.lineItemRepository = lineItemRepository;
        this.orderController = orderController;
        this.orderService = orderService;
    }

    private Product saveProduct( Product p){
        return productRepository.save(p);
    }

    //will call just after the application has started up
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        //1. first init all the mandatory properties
        discountUtil.initItemMap();

        //2. set up dummy DB
        System.out.println("Started in Bootstrap");
        Product p1 = saveProduct(new Product("sock1", new BigDecimal("10.00")));
        Product p2 = saveProduct(new Product("sock2", new BigDecimal("12.00")));
        Product p3 = saveProduct(new Product("sock3", new BigDecimal("8.00")));
        Product p4 = saveProduct(new Product("pant1", new BigDecimal("18.00")));
        Product p5 = saveProduct(new Product("pant2", new BigDecimal("28.00")));
        Product p6 = saveProduct(new Product("pant3", new BigDecimal("38.00")));
        System.out.println(productRepository.findAll());

        MyOrder o1 = new MyOrder(new java.sql.Date(new Date().getTime()), new BigDecimal("1.00"));
        MyOrder o2 = new MyOrder(new java.sql.Date(new Date().getTime()), null);
        MyOrder o3 = new MyOrder(new java.sql.Date(new Date().getTime()), null);
        LineItem item1 = new LineItem(p1, 10);
        LineItem item2 = new LineItem(p2, 3);
        o1.addLineItem(item1);
        o1.addLineItem(item2);
        orderService.saveOrUpdateOrder(o1); //auto save all items too!
        System.out.println("all order: "+orderService.findAll());
    }
}
