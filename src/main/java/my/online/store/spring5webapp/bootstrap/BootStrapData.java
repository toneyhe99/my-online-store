package my.online.store.spring5webapp.bootstrap;

import my.online.store.spring5webapp.domain.LineItem;
import my.online.store.spring5webapp.domain.MyOrder;
import my.online.store.spring5webapp.domain.Product;
import my.online.store.spring5webapp.repositories.LineItemRepository;
import my.online.store.spring5webapp.repositories.OrderRepository;
import my.online.store.spring5webapp.repositories.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by jt on 12/23/19.
 */
@Component
public class BootStrapData implements CommandLineRunner {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final LineItemRepository lineItemRepository;

    public BootStrapData(OrderRepository orderRepository, ProductRepository productRepository, LineItemRepository lineItemRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.lineItemRepository = lineItemRepository;
    }

    private Product saveProduct( Product p){
        return productRepository.save(p);
    }

    @Override
    public void run(String... args) throws Exception {

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
        orderRepository.save(o1); //now o1 has id value, to set in lineItem
        //now save line item when o1 has id
        lineItemRepository.save(item1);
        lineItemRepository.save(item2);
        System.out.println("item1: "+ item1);
        System.out.println("all order: "+orderRepository.findAll());
        System.out.println("all line: "+lineItemRepository.findAll());
    }
}
