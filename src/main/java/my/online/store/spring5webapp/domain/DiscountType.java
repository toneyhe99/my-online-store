package my.online.store.spring5webapp.domain;

public enum DiscountType {
    Over12Items(5),
    SpecialItem2nd(10),  //but A, then buy B get 10% discount of A's price
    Christmas(5);

    private int discount;

    public int getDiscount() {
        return discount;
    }

    DiscountType(int dis){
        this.discount = dis;
    }
}
