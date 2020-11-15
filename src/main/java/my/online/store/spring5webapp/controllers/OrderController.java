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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
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

    @RequestMapping("/myCart")
    public String showMyCart(HttpSession session, Model model){
        MyOrder o1 = (MyOrder)session.getAttribute("myCart");
        if(o1!=null){
            o1.setDiscount(discountUtil.calculateDiscount(o1));
        }
        model.addAttribute("myCart", o1);
        return "/orders/cart";
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
        for(MyOrder o : orderService.findAll()){
            LocalDate oDate = o.getOrderedDate().toLocalDate();
            if(!oDate.isBefore(startDate) && oDate.isBefore(endDate) && OrderStatus.Paid.name().equals(o.getStatus())){
                revenue = revenue.add(o.total());
                revenue1 = revenue1.add(o.totalForProduct(productId));
            }
        }
        model.addAttribute("revenueAllInRange", revenue);
        model.addAttribute("revenueProductInRange", revenue1);
        return "/orders/revenue";
    }

    @PostMapping("/checkOutCart")
    public ModelAndView checkOutCart(HttpSession session){
        MyOrder o1 = (MyOrder)session.getAttribute("myCart");
        /*
         * Note: myCart should has user payment information and here call third party bank to collect money,
         *      if failed, return with error.
         *      Optionally, we can just set Order status as 'ordered' here, user will pay by other microservice,
         *      and once paid, call "/paid/{orderId}" url to update status.
         *  Ignore all these logic for a simple implement only
         */
        o1.setStatus(OrderStatus.Paid.name());
        o1.setOrderedDate(new java.sql.Date(new Date().getTime()));
        o1.setDiscount(discountUtil.calculateDiscount(o1));
        o1 = orderService.saveOrUpdateOrder(o1);
        session.removeAttribute("myCart");
        System.out.println("after save o1:" +o1);
        return new ModelAndView("redirect:/orders");
    }

    //if you type a url in address bar of a browser and hit enter, it's always a GET request, so you had to specify POST request by postman etc
    @PostMapping("/paid/{orderId}")
    public String updateAsPaid(@PathVariable Long orderId){
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
