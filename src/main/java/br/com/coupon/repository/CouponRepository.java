package br.com.coupon.repository;

import br.com.coupon.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<CouponEntity, Long> {

    Optional<CouponEntity> findByCode(String code);

    List<CouponEntity> findAllByPublishedTrue();
    List<CouponEntity> findAllByPublishedFalse();
    List<CouponEntity> findAllByActiveTrue();
    List<CouponEntity> findAllByActiveFalse();

    boolean existsByCode(String code);
}
