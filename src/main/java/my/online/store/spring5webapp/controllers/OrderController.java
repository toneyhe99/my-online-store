package my.online.store.spring5webapp.controllers;

import my.online.store.spring5webapp.domain.LineItem;
import my.online.store.spring5webapp.domain.MyOrder;
import my.online.store.spring5webapp.domain.OrderStatus;
import my.online.store.spring5webapp.domain.Product;
import my.online.store.spring5webapp.repositories.LineItemRepository;
import my.online.store.spring5webapp.repositories.ProductRepository;
import my.online.store.spring5webapp.services.DiscountUtil;
import my.online.store.spring5webapp.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Controller
public class OrderController {
    @Autowired
    private ProductRepository productRepository;  //TODO : create service just like the orderSerivce
    @Autowired
    private LineItemRepository lineItemRepository;  //TODO : create service just like the orderSerivce
    @Autowired
    private OrderService orderService;
    @Autowired
    DiscountUtil discountUtil;

    @RequestMapping("/orders")
    public String getOrders(Model model){
        model.addAttribute("orders", orderService.findAll());
        return "/orders/list";
    }

    @RequestMapping("/revenue")
    public String getThisYearRevenue(Model model){  //this year revenue up to now
        //hard code time period and productId here as we just want show java side logic, ignore the html form data passing in logic..
        //the revenue period is [startDate, endDate), e.g. include the startDate, but not include the endDate.
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now().plusDays(3);
        Long productId = 1L;

        BigDecimal revenue = new BigDecimal("0");
        BigDecimal revenue1 = new BigDecimal("0");
        for(MyOrder o : orderService.findAll()){ //we treat the ordered order as revenue due, no matter paid or not, it is receivable any way. So just count all orders
            LocalDate oDate = o.getOrderedDate().toLocalDate();
            if(!oDate.isBefore(startDate) && oDate.isBefore(endDate)){
                revenue = revenue.add(o.total());
                revenue1 = revenue1.add(o.totalForProduct(productId));
            }
        }
        model.addAttribute("revenueAllInRange", revenue);
        model.addAttribute("revenueProductInRange", revenue1);
        return "/orders/revenue";
    }

    @PostMapping("/save")
    public String saveOrUpdate(@ModelAttribute MyOrder o){
        //hard code as we just want show java side logic, ignore the html form data passing in logic..
        MyOrder o1 = getDummyOrder();
        o1.setDiscount(discountUtil.calculateDiscount(o1));
        o1.setStatus(OrderStatus.Ordered.name());

        o1 = orderService.saveOrUpdateOrder(o1);
        System.out.println("after save o1:" +o1);
        return "redirect:/orders/list";
    }

    //if you type a url in address bar of a browser and hit enter, it's always a GET request, so you had to specify POST request by postman etc
    @PostMapping("/paid/{orderId}")
    public String update(@PathVariable Long orderId){
        //hard code as we just want show java side logic, ignore the html form data passing in logic..
        MyOrder o1 = orderService.findById(orderId).get();
        o1.setStatus(OrderStatus.Paid.name());
        o1 = orderService.saveOrUpdateOrder(o1);
        System.out.println("after paid o1:" +o1);
        return "redirect:/orders/list";
    }

    private MyOrder getDummyOrder(){
        Product p1 = productRepository.findById(3L).get();
        Product p2 = productRepository.findById(4L).get();
        LineItem item1 = new LineItem(p1, 10);
        LineItem item2 = new LineItem(p2, 3);
        MyOrder o1 = new MyOrder(new java.sql.Date(new Date().getTime()), new BigDecimal("0.00"));
        o1.addLineItem(item1);
        o1.addLineItem(item1);
        return o1;
    }
}
