package br.com.coupon.service;

import br.com.coupon.dto.CouponDTO;
import br.com.coupon.entity.CouponEntity;
import br.com.coupon.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @InjectMocks
    private CouponService service;

    @Mock
    private CouponRepository repository;

    @Test
    @DisplayName("Deve lançar exceção quando o valor do desconto for menor que 0.5.")
    void shouldThrowExceptionDiscountLessThan() {
        CouponDTO dto = CouponDTO.builder()
                .code("ABC123")
                .description("Cupom 4% de desconto")
                .discountValue(BigDecimal.valueOf(0.4)) // Valor inválido
                .expirationDate(LocalDate.now().plusDays(1).atStartOfDay())
                .published(true)
                .build();

        when(repository.findByCode("ABC123")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.create(dto));

        assertEquals("O valor de desconto deve ser no mínimo 0,5.", ex.getMessage());

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a data for menor que a data atual.")
    void shouldThrowExceptionExpirationDateIsBeforeToday() {
        CouponDTO dto = CouponDTO.builder()
                .code("CUPOM2")
                .description("Cupom 2025 promobug")
                .discountValue(BigDecimal.valueOf(1))
                .expirationDate(LocalDate.now().minusDays(1).atStartOfDay())
                .published(false)
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.create(dto));

        assertEquals("Data de expiração não pode ser anterior a data de hoje.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o código do cupom tiver caracteres especiais ou tamanho inválido.")
    void shouldThrowExceptionCodeInvalid() {
        CouponDTO dto = CouponDTO.builder()
                .code("CUP#@1")
                .discountValue(BigDecimal.valueOf(1))
                .expirationDate(LocalDate.now().plusDays(1).atStartOfDay())
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.create(dto));

        assertEquals("O código do cupom deve conter 6 caracteres.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o código do cupom já existir.")
    void shouldThrowExceptionCodeAlreadyExists() {
        CouponDTO dto = CouponDTO.builder()
                .code("CUPOM6")
                .discountValue(BigDecimal.valueOf(1))
                .expirationDate(LocalDate.now().plusDays(1).atStartOfDay())
                .build();

        CouponEntity existingCoupon = new CouponEntity();
        existingCoupon.setId(1L);
        existingCoupon.setCode("CUPOM6");
        existingCoupon.setDescription(dto.getDescription());
        existingCoupon.setDiscountValue(dto.getDiscountValue());
        existingCoupon.setExpirationDate(dto.getExpirationDate());
        existingCoupon.setPublished(dto.getPublished());
        existingCoupon.setActive(true);

        when(repository.findByCode("CUPOM6")).thenReturn(java.util.Optional.of(existingCoupon));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.create(dto));

        assertEquals("Já existe um cupom com esse código. Cupom: CUPOM6", ex.getMessage());
    }

    @Test
    @DisplayName("Deve criar um cupom com sucesso.")
    void shouldCreatedSuccess() {
        CouponDTO dto = CouponDTO.builder()
                .code("CUP123")
                .discountValue(BigDecimal.valueOf(10))
                .expirationDate(LocalDate.now().plusDays(5).atStartOfDay())
                .build();

        when(repository.findByCode("CUP123")).thenReturn(java.util.Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> {
            CouponEntity entity = inv.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        CouponDTO result = service.create(dto);

        assertNotNull(result);
        assertEquals("CUP123", result.getCode());
        assertEquals(BigDecimal.valueOf(10), result.getDiscountValue());
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Deve formatar o código removendo caracteres especiais e salvar com sucesso.")
    void shouldFormatCodeAndSaveSuccess() {
        CouponDTO dto = CouponDTO.builder()
                .code("cup-123!")
                .discountValue(BigDecimal.valueOf(10))
                .expirationDate(LocalDate.now().plusDays(5).atStartOfDay())
                .published(true)
                .build();

        when(repository.findByCode("cup-123!")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CouponDTO result = service.create(dto);

        assertEquals("CUP123", result.getCode());
    }

    @Test
    @DisplayName("Deve aceitar cupom com valor exatamente 0.5.")
    void shouldAcceptMinimumDiscountValue() {
        CouponDTO dto = CouponDTO.builder()
                .code("CUP005")
                .discountValue(new BigDecimal("0.5"))
                .expirationDate(LocalDate.now().plusDays(1).atStartOfDay())
                .build();

        when(repository.findByCode(anyString())).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        CouponDTO result = service.create(dto);
        assertEquals(new BigDecimal("0.5"), result.getDiscountValue());
    }

    @Test
    @DisplayName("Deve lançar exceção quando campos obrigatórios forem nulos.")
    void shouldThrowExceptionWhenFieldsAreNull() {
        CouponDTO dto = CouponDTO.builder().build();

        assertThrows(RuntimeException.class, () -> service.create(dto));
    }
}
