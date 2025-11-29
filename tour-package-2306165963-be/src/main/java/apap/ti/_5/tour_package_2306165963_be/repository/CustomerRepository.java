package apap.ti._5.tour_package_2306165963_be.repository;
import apap.ti._5.tour_package_2306165963_be.model.loyalty.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {}