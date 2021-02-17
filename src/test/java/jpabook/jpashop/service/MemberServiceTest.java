package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Test
    public void 회원가입() throws Exception{
        //given
        Member member=new Member();
        member.setName("kim");

        //when
        Long id = memberService.join(member);


        //then

        assertThat(id).isEqualTo(member.getId());


    }

    @Test
    public void 중복_회원_예외() throws Exception{
        //given
        Member member=new Member();
        Member member2=new Member();
        member.setName("song");
        member2.setName("song");

        //when

        memberService.join(member);


        //then

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        assertThat("이미 존재하는 회원입니다.").isEqualTo(thrown.getMessage());


    }

}