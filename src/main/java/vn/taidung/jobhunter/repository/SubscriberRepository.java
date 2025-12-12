package vn.taidung.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.taidung.jobhunter.domain.Subscriber;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long>, JpaSpecificationExecutor<Subscriber> {
    Boolean existsById(long id);

    Boolean existsByEmail(String email);

    Subscriber findByEmail(String email);
}
