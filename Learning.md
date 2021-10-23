### Eureka with ribbon

 * If we have run more than 2 servers of same microservices and want them to be load balanced then we need to provide the below property
 * We can crosscheck this by access this url on which eureka server is running : http://localhost:5555/eureka/apps/FRIENDMS
 * eureka.instance.instance-id = ${spring.cloud.client.hostname}:${spring.application.name}:${spring.application.instance_id:${random.value}}
 
### Hystrix

*  Add the dependency in the infytel-customer service

<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>

*  Add the @EnableCircuitBreaker annotation in the application class of infytel-customer. This tells Spring Cloud that the application uses the Circuit Breaker pattern

* Add the below method in the controller class with @HystrixCommand annotation. This annotation makes the method call fault tolerant. 

		@HystrixCommand(fallbackMethod="getCustomerProfileFallback")
		@GetMapping(value = "/customers/{phoneNo}")  
		public CustomerDTO getCustomerProfile(@PathVariable Long phoneNo) {
				CustomerDTO custDTO=custService.getCustomerProfile(phoneNo);
		PlanDTO planDTO=template.getForObject("http://PLANMS"+"/plans/"+custDTO.getCurrentPlan().getPlanId(), PlanDTO.class);
				custDTO.setCurrentPlan(planDTO);
		List<Long> friends=template.getForObject("http://FRIENDFAMILYMS"+"/customers/"+phoneNo+"/friends", List.class);
				custDTO.setFriendAndFamily(friends);
				return custDTO;
}
* Create the below method in the same class:

	public CustomerDTO getCustomerProfileFallback(Long phoneNo) {
			return new CustomerDTO();
		}
* Add the below properties in the CustomerMS.properties
	
	These properties means that request should be failing at least 10 times in 10 second with 50% failure or error
	
	hystrix.command.default.circuitBreaker.requestVolumeThreshold=10
	hystrix.command.default.metrics.rollingStats.timeInMilliseconds=10000
	hystrix.command.default.circuitBreaker.errorThresholdPercentage=50
	hystrix.command.default.circuitBreaker.sleepWindowInMilliseconds=10000
	
	sleepwindow means: circit breaker will be open for 10 seconds and post that it will try to close the circuit again.

* Run the code

* @HystrixCommand annotation provides another attribute ignoreExceptions which helps to configure a list of Exceptions that can be ignored.

	@HystrixCommand(fallbackMethod="getCustomerProfileFallback" , ignoreExceptions= TypeMismatchException.class)
	@GetMapping(value = "/customers/{phoneNo}") 
	public CustomerDTO getCustomerProfile(@PathVariable Long phoneNo){

	In this code snippet, if the method getCustomerProfile thows TypeMismatchException then hystrix will not trigger fallback logic present in getCustomerProfileFallback() method. Instead, it will wrap the actual exception inside HystrixBadRequestException and rethrow it to the caller method.

	We can also specify a list of Exception classes as below:

	ignoreExceptions= TypeMismatchException.class, IllegalStateException.class
	
*  Once the specified number of requests (threshold volume) are sent within the specified time (rolling stats time window) and if the specified percentage of requests end up as errors (error percentage), the hystrix opens the circuit. Once the circuit is open, no more requests will be sent to the infytel-friend-family service. After a specified time interval ( sleep window ), hystrix will again close the circuit and pass one request. If that request fails, then again circuit is automatically closed for the specified time again. This repeats in a cycle.	
 