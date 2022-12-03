package kr.co.bigsnow;
 
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

 
@SpringBootApplication
@EnableSwagger2
public class BigSnowApplication extends SpringBootServletInitializer {
 
	  @Override

	  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {

	    return builder.sources(BigSnowApplication.class);

	  }	

	public static void main(String[] args) {
		
		  SpringApplication.run(BigSnowApplication.class, args);
		  
			/*
			 * SpringApplication app = new
			 * SpringApplication(BigSnowApplication.class);
			 * app.setBannerMode(Mode.OFF); //배너끄기, 켜기
			 * 
			 * app.run(args);
			 */
 
	}
 
	  @Bean //앱 구동 후 바로 동작 
	  public ApplicationListener<ContextRefreshedEvent>
	  startupListener() { return new ApplicationListener<ContextRefreshedEvent>() {
			  public void onApplicationEvent(ContextRefreshedEvent event) {
			  
				  System.out.println("===================================================================");
				  System.out.println(" - Start to listen : " +
			      LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"))); 
			      System.out.println(" - Porject build : Show API"); 
			      System.out.println("===================================================================");
			  }    
			      
		 }; 
	  }
 
}
