package br.com.coupon.controller;

import br.com.coupon.dto.CouponDTO;
import br.com.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
@Tag(name = "Cupons")
public class CouponController {

    private final CouponService service;

    @PostMapping
    public ResponseEntity<CouponDTO> create(@RequestBody CouponDTO dto) {
        CouponDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/published")
    public ResponseEntity<List<CouponDTO>> listAllPublished() {
        return ResponseEntity.ok(service.listAllByStatus(null, true));
    }

    @GetMapping("/active")
    public ResponseEntity<List<CouponDTO>> listAllActive() {
        return ResponseEntity.ok(service.listAllByStatus(true, null));
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<CouponDTO>> listAllInactive() {
        return ResponseEntity.ok(service.listAllByStatus(false, null));
    }

    @GetMapping
    public ResponseEntity<List<CouponDTO>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/{code}")
    public ResponseEntity<CouponDTO> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.getByCode(code));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
 }
