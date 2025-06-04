//package MVVMDessert.demo;
//
//import org.springframework.data.r2dbc.repository.Query;
//import org.springframework.data.repository.reactive.ReactiveCrudRepository;
//
//import MVVMDessert.demo.model.dessertTestWebflux;
//import reactor.core.publisher.Flux;
//
//public interface DessertRepository extends ReactiveCrudRepository<dessertTestWebflux, Integer> {
//
//	@Query("SELECT * FROM dessert ORDER BY dessert_id")
//	Flux<dessertTestWebflux> findAllDesserts();
//}