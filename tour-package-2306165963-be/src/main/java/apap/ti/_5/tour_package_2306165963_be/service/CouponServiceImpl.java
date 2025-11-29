package apap.ti._5.tour_package_2306165963_be.service;

import apap.ti._5.tour_package_2306165963_be.dto.coupon.*;
import apap.ti._5.tour_package_2306165963_be.model.loyalty.*;
import apap.ti._5.tour_package_2306165963_be.repository.*;
import apap.ti._5.tour_package_2306165963_be.repository.loyalty.CouponRepository;
import apap.ti._5.tour_package_2306165963_be.repository.loyalty.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CouponServiceImpl implements CouponService {

    @Autowired private CouponRepository couponRepo;
    @Autowired private CustomerRepository customerRepo;
    @Autowired private PurchasedCouponRepository purchasedRepo;

    @Override
    public List<Coupon> getAllAvailableCoupons() {
        return couponRepo.findByIsDeletedFalse();
    }

    @Override
    public List<PurchasedCoupon> getPurchasedCoupons(String customerId) {
        return purchasedRepo.findByCustomerId(customerId);
    }

    @Override
    public Integer getCustomerPoints(String customerId) {
        return customerRepo.findById(customerId).map(Customer::getLoyaltyPoints).orElse(0);
    }

    @Override
    public Coupon createCoupon(CouponRequestDto dto) {
        Coupon c = new Coupon();
        c.setId(UUID.randomUUID().toString());
        c.setName(dto.getName());
        c.setDescription(dto.getDescription());
        c.setPoints(dto.getPoints());
        c.setPercentOff(dto.getPercentOff());
        return couponRepo.save(c);
    }

    @Override
    public Coupon updateCoupon(String id, CouponRequestDto dto) {
        Coupon c = couponRepo.findById(id).orElseThrow();
        c.setName(dto.getName());
        c.setDescription(dto.getDescription());
        c.setPoints(dto.getPoints());
        c.setPercentOff(dto.getPercentOff());
        return couponRepo.save(c);
    }

    @Override
    public PurchasedCoupon purchaseCoupon(PurchaseRequestDto dto) {
        Customer cust = customerRepo.findById(dto.getCustomerId()).orElseThrow(() -> new RuntimeException("Customer not found"));
        Coupon coupon = couponRepo.findById(dto.getCouponId()).orElseThrow(() -> new RuntimeException("Coupon not found"));

        // Validasi Poin
        if (cust.getLoyaltyPoints() < coupon.getPoints()) {
            throw new IllegalStateException("Insufficient loyalty points");
        }

        // Kurangi Poin
        cust.setLoyaltyPoints(cust.getLoyaltyPoints() - coupon.getPoints());
        customerRepo.save(cust);

        // Generate Unique Code: [5 char coupon] - [5 char cust] - [count]
        long count = purchasedRepo.count();
        String couponPart = coupon.getName().length() > 5 ? coupon.getName().substring(0, 5) : coupon.getName();
        String custPart = cust.getName().length() > 5 ? cust.getName().substring(0, 5) : cust.getName();
        String code = (couponPart + "-" + custPart + "-" + (count + 1)).toUpperCase().replace(" ", "");

        PurchasedCoupon pc = new PurchasedCoupon();
        pc.setCustomerId(cust.getId());
        pc.setCoupon(coupon);
        pc.setUniqueCode(code);
        pc.setIsUsed(false);
        
        return purchasedRepo.save(pc);
    }

    @Override
    public Customer addPoints(AddPointsRequestDto dto) {
        // API KEY Check bisa di skip utk demo, atau if (!apiKey.equals("...")) throw ...
        Customer cust = customerRepo.findById(dto.getCustomerId()).orElse(new Customer(dto.getCustomerId(), "New Customer", 0));
        cust.setLoyaltyPoints(cust.getLoyaltyPoints() + dto.getPoints());
        return customerRepo.save(cust);
    }

    @Override
    public Double useCoupon(UseCouponRequestDto dto) {
        // Logic: Cari purchased coupon by code
        PurchasedCoupon pc = purchasedRepo.findByUniqueCode(dto.getCode()).orElse(null);
        
        if (pc == null) return 0.0;
        if (!pc.getCustomerId().equals(dto.getCustomerId())) return 0.0; // Bukan punya dia
        if (pc.getIsUsed()) return 0.0; // Udah dipake

        pc.setIsUsed(true);
        purchasedRepo.save(pc);
        
        return pc.getCoupon().getPercentOff();
    }
}