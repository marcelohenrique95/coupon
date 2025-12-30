package br.com.coupon.service;

import br.com.coupon.dto.CouponDTO;
import br.com.coupon.entity.CouponEntity;
import br.com.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository repository;

    public CouponDTO create(CouponDTO dto) {

        repository.findByCode(dto.getCode())
                .ifPresent(c -> {
                    throw new IllegalArgumentException("Já existe um cupom com esse código. Cupom: " + dto.getCode());
                });

        CouponEntity newCoupon = new CouponEntity(dto);

        CouponEntity coupon = repository.save(newCoupon);

        return toDTO(coupon);
    }

    public List<CouponDTO> listAllByStatus(Boolean active, Boolean published) {
        return repository.findAll()
                .stream()
                .filter(c -> active == null || c.getActive().equals(active))
                .filter(c -> published == null || c.getPublished().equals(published))
                .map(this::toDTO)
                .toList();
    }

    public List<CouponDTO> listAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public CouponDTO getByCode(String code) {
        CouponEntity coupon = repository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Cupom não encontrado."));
        return toDTO(coupon);
    }

    public void deleteById(Long id) {
        CouponEntity coupon = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cupom não encontrado."));

        coupon.setPublished(false);
        coupon.setActive(false);
        repository.save(coupon);
    }

    private CouponDTO toDTO(CouponEntity entity) {
        return new CouponDTO(
                entity.getId(),
                entity.getCode(),
                entity.getDescription(),
                entity.getDiscountValue(),
                entity.getExpirationDate(),
                entity.getPublished(),
                entity.getActive()
        );
    }
}