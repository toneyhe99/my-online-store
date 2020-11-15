package my.online.store.spring5webapp.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class LineItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private MyOrder associatedOrder;

    @OneToOne
    private Product product;
    private int quantity;

    public LineItem() {
    }

    public LineItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MyOrder getAssociatedOrder() {
        return associatedOrder;
    }

    public void setAssociatedOrder(MyOrder associatedOrder) {
        this.associatedOrder = associatedOrder;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "LineItem{" +
                "id=" + id +
                ", associatedOrder=" + associatedOrder.getId() +
                ", product=" + product +
                ", quantity=" + quantity +
                '}';
    }
}
