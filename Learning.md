### Eureka with ribbon

 * If we have run more than 2 servers of same microservices and want them to be load balanced then we need to provide the below property
 * We can crosscheck this by access this url on which eureka server is running : http://localhost:5555/eureka/apps/FRIENDMS
 * eureka.instance.instance-id = ${spring.cloud.client.hostname}:${spring.application.name}:${spring.application.instance_id:${random.value}}