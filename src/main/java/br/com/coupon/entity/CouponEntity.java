package br.com.coupon.entity;


import br.com.coupon.dto.CouponDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String description;
    private BigDecimal discountValue;
    private LocalDateTime expirationDate;
    private Boolean published;
    private Boolean active;

    public CouponEntity(CouponDTO couponDTO) {
        this.code = formatCode(couponDTO.getCode());
        this.description = couponDTO.getDescription();
        this.discountValue = couponDTO.getDiscountValue();
        this.expirationDate = couponDTO.getExpirationDate();
        this.published = couponDTO.getPublished() != null && couponDTO.getPublished();
        this.active = true;

        validate();
    }

    private void validate() {
        if (this.discountValue == null)
            throw new IllegalArgumentException("O valor de desconto é obrigatório.");

        if (this.discountValue.compareTo(new BigDecimal("0.5")) < 0)
            throw new IllegalArgumentException("O valor de desconto deve ser no mínimo 0,5.");

        if (expirationDate.isBefore(LocalDate.now().atStartOfDay()))
            throw new IllegalArgumentException("Data de expiração não pode ser anterior a data de hoje.");

        if (code.length() != 6)
            throw new IllegalArgumentException("O código do cupom " + code + " deve conter 6 caracteres.");
    }

    private String formatCode(String code) {
        if (code == null || code.isBlank()) throw new IllegalArgumentException("Código do cupom é obrigatório.");

        String formatted = code.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();

        if (formatted.length() != 6) throw new IllegalArgumentException(String.format(
                "O código do cupom deve conter 6 caracteres."));

        return formatted.toUpperCase();
    }
}
