package my.online.store.spring5webapp.controllers;

import my.online.store.spring5webapp.domain.*;
import my.online.store.spring5webapp.repositories.LineItemRepository;
import my.online.store.spring5webapp.repositories.OrderRepository;
import my.online.store.spring5webapp.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class OrderController {
    @Value("${special.item.discount}")
    private String specialItemDiscount;

    private Map<Long, Long> specialItemMap = null;
    private void initItemMap(){
        specialItemMap = new HashMap<>();
        if(specialItemDiscount!=null){
            String[] pairs = specialItemDiscount.trim().split(";");
            for(String pair : pairs){
                String[] item2 = pair.split(",");  //must two product ids seperated by ","
                Long k = Long.parseLong(item2[0]);
                Long v = Long.parseLong(item2[1]);
                specialItemMap.put(k,v);
            }
        }
    }

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private LineItemRepository lineItemRepository;

    @RequestMapping("/orders")
    public String getOrders(Model model){
        model.addAttribute("orders", orderRepository.findAll());
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
        for(MyOrder o : orderRepository.findAll()){ //we treat the ordered order as revenue due, no matter paid or not, it is receivable any way. So just count all orders
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

    @PostMapping("/save/{recipeId}")
    public String saveOrUpdate(@ModelAttribute MyOrder o){
        //hard code as we just want show java side logic, ignore the html form data passing in logic..
        MyOrder o1 = getDummyOrder();
        BigDecimal dis1 = getOver12ItemsDisCount(o1);
        BigDecimal dis2 = getSpecialItem2ndDisCount(o1);
        BigDecimal dis3 = getChristmasDisCount(o1);
        o1.setDiscount(new BigDecimal(Math.max(Math.max(dis1.floatValue(), dis2.floatValue()), dis3.floatValue())) ); //only one discount can apply!
        o1.setStatus(OrderStatus.Ordered.name());
        o1 = orderRepository.save(o1);
        System.out.println("after save o1:" +o1);
        return "redirect:/orders/list";
    }

    //if you type a url in address bar of a browser and hit enter, it's always a GET request, so you had to specify POST request by postman etc
    @PostMapping("/paid/{orderId}")
    public String update(@PathVariable Long orderId){
        //hard code as we just want show java side logic, ignore the html form data passing in logic..
        MyOrder o1 = orderRepository.findById(orderId).get();
        o1.setStatus(OrderStatus.Paid.name());
        o1 = orderRepository.save(o1);
        System.out.println("after paid o1:" +o1);
        return "redirect:/orders/list";
    }

    //below is saving order logic, better in service class, put here for simplicity only
    private MyOrder saveOrderService(MyOrder o1){
        MyOrder o = orderRepository.save(o1);
        for (LineItem l : o1.getLineItems()){
            l.setAssociatedOrder(o1);
            lineItemRepository.save(l);
        }
        return o;
    }

    private BigDecimal getSpecialItem2ndDisCount(MyOrder o){
        if(specialItemMap==null){
            synchronized (this){
                if(specialItemMap==null){
                    initItemMap();
                }
            }
        }
        BigDecimal discount = new BigDecimal("0");
        for(LineItem i : o.getLineItems()){
            Long k = i.getProduct().getId();
            Long v = specialItemMap.get(k);
            if(v!=null){
                discount = discount.add(getDiscount(i, v, o));
            }
        }
        return discount;
    }

    private BigDecimal getDiscount(LineItem a, Long productBid, MyOrder o){
        for(LineItem i : o.getLineItems()){
            if(i.getProduct().getId()==productBid){//suppose all same products in only one lineItem!
                return getItemDiscount(a.getProduct().getPrice(), a.getQuantity(), i.getProduct().getPrice(), i.getQuantity());
            }
        }
        return new BigDecimal("0");
    }

    private BigDecimal getItemDiscount(BigDecimal priceA, int quantityA, BigDecimal priceB, int quantityB){
        int quantity = Math.min(quantityA, quantityB);
        double discount = Math.min( priceA.doubleValue() * (DiscountType.SpecialItem2nd.getDiscount()), priceB.doubleValue());  //The max discount one B can get
        return new BigDecimal(discount*quantity);
    }

    private BigDecimal getChristmasDisCount(MyOrder o){//only boxing day special 25,26 two days
        BigDecimal dis= new BigDecimal("0");
        LocalDate start = LocalDate.of(LocalDate.now().getYear(), 12, 25);
        LocalDate end = LocalDate.of(LocalDate.now().getYear(), 12, 27);
        LocalDate ordered = o.getOrderedDate().toLocalDate();
        if(!ordered.isBefore(start) && ordered.isBefore(end)){
            dis = o.total().multiply( new BigDecimal(DiscountType.Christmas.getDiscount()/100.00));
        }
        return dis;
    }

    private BigDecimal getOver12ItemsDisCount(MyOrder o){
        BigDecimal dis= new BigDecimal("0");
        if(o.totalProductsCount()>=12){
            dis = o.total().multiply( new BigDecimal(DiscountType.Over12Items.getDiscount()/100.00));
        }
        System.out.println("getOver12ItemsDisCount:"+dis);
        return dis;
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
