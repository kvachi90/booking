package ai.faire.booking.repository;

import ai.faire.booking.model.Transactions;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionsRepository extends CrudRepository<Transactions, Long>{
        
}
