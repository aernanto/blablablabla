package apap.ti._5.tour_package_2306165963_be.service;

import apap.ti._5.tour_package_2306165963_be.model.OrderedQuantity;
import java.util.Optional;

public interface OrderedQuantityService {
    OrderedQuantity createOrderedQuantity(String packageId, String planId, OrderedQuantity oqData);
    Optional<OrderedQuantity> getOrderedQuantityById(String packageId, String planId, String oqId);
    OrderedQuantity updateOrderedQuantity(String packageId, String planId, String oqId, OrderedQuantity updatedOq);
    boolean deleteOrderedQuantity(String packageId, String planId, String oqId);
}