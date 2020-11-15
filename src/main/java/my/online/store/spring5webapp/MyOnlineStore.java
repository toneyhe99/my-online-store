package my.online.store.spring5webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * -  list products in catalog by name.
 * -  place an order.
 * -  check out order.
 * -  Promotion when checkout based on rules, (i.e., when customer buy 12+ socks, can get a 5% discount, or can get a discount for A when buy B,  or get 5% discount on A during Christmas etc)
 * -  be able to run reports like total revenue for a time period,  revenue by product for a time period .
 */
@SpringBootApplication
public class MyOnlineStore {

	public static void main(String[] args) {
		SpringApplication.run(MyOnlineStore.class, args);
	}

}
