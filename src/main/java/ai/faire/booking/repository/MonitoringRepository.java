package ai.faire.booking.repository;

import ai.faire.booking.model.Monitoring;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoringRepository extends CrudRepository<Monitoring, Long>{
    
    @Query("select t from Monitoring t where t.date > :date and t.accountNumber = :accountNumber")
    List<Monitoring> findAllWithAccountNumberAndDateAfter(@Param("date") Date date,
            String accountNumber);
    
    @Query("select t from Monitoring t where t.date < :date and t.accountNumber = :accountNumber")
    List<Monitoring> findAllWithAccountNumberAndDateBefore(@Param("date") Date date,
            String accountNumber);
    
    @Query("select t from Monitoring t where t.date = :date and t.accountNumber = :accountNumber")
    Monitoring findAllWithAccountNumberAndDate(@Param("date") Date date,
            String accountNumber);    
}
