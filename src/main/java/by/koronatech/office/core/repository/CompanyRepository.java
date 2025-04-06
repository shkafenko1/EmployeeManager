package by.koronatech.office.core.repository;

import by.koronatech.office.core.model.Company;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByName(String name);

    Optional<Company> findByName(String name);
}
