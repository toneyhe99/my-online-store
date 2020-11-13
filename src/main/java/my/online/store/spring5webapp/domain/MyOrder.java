package my.online.store.spring5webapp.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class MyOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal discount;
    private java.sql.Date orderedDate;
    private String status;

    @OneToMany(mappedBy = "associatedOrder",  fetch = FetchType.EAGER)
    private Set<LineItem> lineItems = new HashSet<>();

    public MyOrder() {
    }

    public MyOrder(Date orderedDate, BigDecimal discount) {
        this.discount = discount;
        this.orderedDate = orderedDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public Date getOrderedDate() {
        return orderedDate;
    }

    public void setOrderedDate(Date orderedDate) {
        this.orderedDate = orderedDate;
    }

    public Set<LineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(Set<LineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public void addLineItem(LineItem lineItem){
        this.lineItems.add(lineItem);
        lineItem.setAssociatedOrder(this);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal total(){
        BigDecimal total = new BigDecimal("0");
        for(LineItem i: this.lineItems){
            total = total.add(i.getProduct().getPrice().multiply(new BigDecimal(i.getQuantity()))) ;
        }
        System.out.println("total: "+total);
        System.out.println("total subtract: "+total.subtract(discount));
        return discount==null ? total : total.subtract(discount);
    }

    public BigDecimal totalForProduct(Long productId){
        BigDecimal total = new BigDecimal("0");
        for(LineItem i: this.lineItems){
            if(i.getProduct().getId()==productId){
                total = total.add(i.getProduct().getPrice().multiply(new BigDecimal(i.getQuantity()))) ;
            }
        }
        return total;
    }

    public int totalProductsCount(){
        int counter = 0;
        for(LineItem i: this.lineItems){
            counter += i.getQuantity();
        }
        return counter;
    }

    @Override
    public String toString() {
        return "MyOrder{" +
                "id=" + id +
                ", discount=" + discount +
                ", orderedDate=" + orderedDate +
                ", lineItems=" + lineItems +
                '}';
    }
}
