package by.koronatech.office.core.cache;

import by.koronatech.office.api.dto.EmployeeDto;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Cache {

    private final Map<Long, EmployeeDto> employeeCache;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Cache(@Value("${cache.employee.capacity:10}") int capacity) {
        this.employeeCache = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(final Map.Entry<Long, EmployeeDto> eldest) {
                return size() > capacity;
            }
        };
    }

    public EmployeeDto getEmployee(Long id) {
        logger.info("Checking cache for employee id: {}", id);
        return employeeCache.get(id);
    }

    public void putEmployee(Long id, EmployeeDto employeeDto) {
        logger.debug("Caching employee with id: {}", id);
        employeeCache.put(id, employeeDto);
    }

    public void removeEmployee(Long id) {
        logger.debug("Removing employee with id: {} from cache", id);
        employeeCache.remove(id);
    }
}
