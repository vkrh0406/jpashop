package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Long> {

    // 스픠링jpa가 놀랍게도 알아서 구현해줌
    //select m from Member m where m.name = ?
    List<Member> findByName(String name);
}
