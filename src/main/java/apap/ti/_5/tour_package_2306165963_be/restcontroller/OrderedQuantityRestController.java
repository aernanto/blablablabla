package apap.ti._5.tour_package_2306165963_be.restcontroller;

import apap.ti._5.tour_package_2306165963_be.dto.DtoMapper;
import apap.ti._5.tour_package_2306165963_be.dto.orderedquantity.*;
import apap.ti._5.tour_package_2306165963_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306165963_be.service.OrderedQuantityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderedQuantityRestController {

    @Autowired private OrderedQuantityService oqService;
    @Autowired private DtoMapper dtoMapper;

    @PostMapping("/plans/{planId}/ordered-quantities")
    public ResponseEntity<ReadOrderedQuantityDto> create(@PathVariable String planId, @Valid @RequestBody CreateOrderedQuantityDto dto) {
        OrderedQuantity oq = dtoMapper.toEntity(dto);
        OrderedQuantity saved = oqService.createOrderedQuantity(planId, oq);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toReadDto(saved));
    }

    @DeleteMapping("/ordered-quantities/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (oqService.deleteOrderedQuantity(id)) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }
}