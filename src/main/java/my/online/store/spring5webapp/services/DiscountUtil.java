package my.online.store.spring5webapp.services;

import my.online.store.spring5webapp.domain.DiscountType;
import my.online.store.spring5webapp.domain.LineItem;
import my.online.store.spring5webapp.domain.MyOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
public class DiscountUtil {
    @Value("${special.item.discount}")
    private String specialItemDiscount;

    private Map<Long, Long> specialItemMap = null;
    public void initItemMap(){
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
        System.out.println("\n\nspecialItemDiscount:"+specialItemDiscount+", specialItemMap: "+specialItemMap);
    }

    private BigDecimal getSpecialItem2ndDisCount(MyOrder o){
        BigDecimal discount = new BigDecimal("0");
        for(LineItem i : o.getLineItems()){
            Long k = i.getProduct().getId();
            Long v = specialItemMap.get(k);
            if(v!=null){
                discount = discount.add(getItemDiscount(i, v, o));
            }
        }
        return discount;
    }

    private BigDecimal getItemDiscount(LineItem a, Long productBid, MyOrder o){
        for(LineItem i : o.getLineItems()){
            if(i.getProduct().getId()==productBid){//suppose all same products in only one lineItem!
                return getItemDiscount(a.getProduct().getPrice(), a.getQuantity(), i.getProduct().getPrice(), i.getQuantity());
            }
        }
        return new BigDecimal("0");
    }

    private BigDecimal getItemDiscount(BigDecimal priceA, int quantityA, BigDecimal priceB, int quantityB){
        int quantity = Math.min(quantityA, quantityB);
        double discount = Math.min( priceA.doubleValue() * (DiscountType.SpecialItem2nd.getDiscount()/100.00), priceB.doubleValue());  //The max discount one B can get
        return new BigDecimal(discount*quantity);
    }

    private BigDecimal getChristmasDisCount(MyOrder o){//only boxing day special 25,26 two days
        BigDecimal dis= new BigDecimal("0");
        LocalDate start = LocalDate.of(LocalDate.now().getYear(), 12, 25);
        LocalDate end = LocalDate.of(LocalDate.now().getYear(), 12, 27);
        LocalDate now = LocalDate.now();
        if(!now.isBefore(start) && now.isBefore(end)){
            dis = o.itemsTotal().multiply( new BigDecimal(DiscountType.Christmas.getDiscount()/100.00));
        }
        return dis;
    }

    private BigDecimal getOver12ItemsDisCount(MyOrder o){
        BigDecimal dis= new BigDecimal("0");
        if(o.totalProductsCount()>=12){
            dis = o.itemsTotal().multiply( new BigDecimal(DiscountType.Over12Items.getDiscount()/100.00));
        }
        System.out.println("getOver12ItemsDisCount:"+dis);
        return dis;
    }

    public BigDecimal calculateDiscount(MyOrder o1){
        BigDecimal dis1 = getOver12ItemsDisCount(o1);
        BigDecimal dis2 = getSpecialItem2ndDisCount(o1);
        BigDecimal dis3 = getChristmasDisCount(o1);
        BigDecimal dis = new BigDecimal(Math.max(Math.max(dis1.floatValue(), dis2.floatValue()), dis3.floatValue())); //only one discount can apply!
        System.out.println("The final DisCount:"+dis);
        return dis.setScale(2, RoundingMode.HALF_DOWN);
    }
}
