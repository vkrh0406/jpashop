package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class OrderServiceTest {
    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception{
       //given
        Member member = createMember("회원1", "서울", "강가", "123-123");

        Book book = createBook("", 10000, 10);

        int orderCount=2;
       //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);


        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertThat(OrderStatus.ORDER).isEqualTo(getOrder.getStatus());
        assertThat(1).isEqualTo(getOrder.getOrderItems().size());
        assertThat(10000*orderCount).isEqualTo(getOrder.getTotalPrice());
        assertThat(8).isEqualTo(book.getStockQuantity());
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book=new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);

        em.persist(book);
        return book;
    }

    private Member createMember(String name, String city, String street, String zipcode) {
        Member member   = new Member();
        member.setName(name);
        member.setAddress(new Address(city, street, zipcode));

        em.persist(member);
        return member;
    }

    @Test
    public void 상품주문_재고수량초과() throws Exception{
        //given
        Member member=createMember("회원1", "서울", "강가", "123-123");

        Item item  = createBook("시골 jpa", 10000, 10);

        int orderCount=11;

        //when

        //then

        NotEnoughStockException ex = assertThrows(NotEnoughStockException.class, () -> {
            orderService.order(member.getId(), item.getId(), orderCount);
                }
                );
        assertThat(ex.getMessage()).isEqualTo("need more stock");
        //Assertions.assertThat("need more stock").isEqualTo(notEnoughStockException.getMessage());

    }

    @Test
    public void 주문취소() throws Exception{

        //given
        Member member=createMember("회원1", "서울", "강가", "123-123");
        Book item  = createBook("시골 jpa", 10000, 10);

        int orderCount=2;

        Long orderId= orderService.order(member.getId(), item.getId(),orderCount);
        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertThat(OrderStatus.CANCEL).isEqualTo(getOrder.getStatus());
        assertThat(10).isEqualTo(item.getStockQuantity());
    }


}