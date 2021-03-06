package com.example.demo;

import com.example.demo.dao.CustomerRepository;
import com.example.demo.entity.Customer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.devtools.RemoteSpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
/**
 * @SpringBootApplication =@EnableAutoConfiguration + @ComponentScan
 * + @Configuration
 * 扫描注解`@ComponentScan` 扫描的是应用所属的包
 * @Configuration 为额外的配置包
 * */
/**
 * `spring-boot-devtools`的使用，包含应用快速启动的支持。
 * 1. 默认属性
 * springboot使用缓存提高库的支持效率。例如,`template engines`缓存可以避免重复的转换临时文件.
 * 缓存模式在生产模式下非常有效,可能会阻碍你对应用的改变,所以默认情况下是关闭的.
 * 缓存属性可以在`application.properties`文件中设置,提高`spring.thymeleaf.cache`属性.
 * 不需要手动配置其他设置,`spring-boot-devtools`自动应用敏感配置.
 * 如果你需要获取更多开发的的信息,这个工具可以为web提供DEBUG功能,给出到来的请求信息.如果你需要打印请求信息,
 * 可以设置`spring.http.log-request-details`属性
 *
 * 2. 自动重启
 * 当文件的类路径发生改变的时候，会自动重启。使用IDE时很方便,给予代码修改的最快响应.默认情况下会监听类路径的改变.
 * 自动重启在热重载的情况下是非常有效的,
 * + 改变条件评估的日志记录
 * 默认情况下,每次重启的时候,都会记录状态变化评估,可以屏蔽记录的出现
 * + 配置不需要参加重启的资源
 * 一些资源重启的时候,即使发生了变化也不会触发重启,所以不需要对其进行修改.例如
 * --  /META-INF/maven
 * --  /META-INF/resources
 * --  /resources
 * --  /static
 * --  /public
 * --  /templates
 *
 * 3. 观测额外路径
 * 对非类路径的文件进行修改时,如果你希望它进行重启,你需要设置`spring.devtools.restart.additional-paths`
 * 配置额外的监控目录.可以结合`spring.devtools.restart.exclude`控制重启内容.
 *
 * 4. 关闭重启功能
 * 可以在`application.properties`中配置`spring.devtools.restart.enabled`重启属性.
 * 可以在SpringApplication.run(…​)前设置关闭重启
 *
 * 5. 使用触发文件
 * 当你使用IDE进行编程的时候，可以设置触发文件触发重启动作。
 * 使用`spring.devtools.restart.trigger-file`指定触发文件,触发文件必选处于类路径中
 * 文本结构如下:
 * src
 * +- main
 *     +- resources
 *         +- .reloadtrigger
 * 这样指定
 * spring.devtools.restart.trigger-file=.reloadtrigger
 *
 * 6. 自定义重启类加载器
 * 重启功能通过Restart和Reload的类加载器，对于大多数应用，这个方法都能够很好地运行。但是有时会造成类加载问题。
 * 默认情况下，IDE打开的项目使用`restart`类加载器加载,任何标准的.jar文件使用基础类加载器加载.如果你使用多个模块的项目,
 * 不是每个模块都会导入到IDE中.你需要设置自定义属性.这些做,你可以创建一个META-INF/spring-devtools.properties文件
 * 文件可以包含使用以`restart.exclude`为前缀的spring-devtools配置文件。添加的元素需要被拉取到`restart`加载器中.
 * 且排除的元素需要被排出到`base`类加载器中.参数的值是正则形式的.用于配置类路径.可以在`spring-devtools.properties`中配置.
 *  例如:
 *  {{{
 *      restart.exclude.companycommonlibs=/mycorp-common-[\\w\\d-\.]+\.jar
 *      restart.include.projectcommon=/mycorp-myproj-[\\w\\d-\.]+\.jar
 *  }}}
 *  7. 已知的限制
 *  通过标准的输入流将对象进行反序列化的时候,重启的效果就不好了.如果需要反序列化数据,需要Spring专有的类
 *  @ConfigurableObjectInputStream 对其进行反序列化,且需要结合#Thread.currentThread().getContextClassLoader()使用。
 *  但是不幸的是，多数三方库没有考虑到上下文类加载器的反序列化。如果找到这样的问题，需要向作者请求获取相关的修改。
 *
 *  8. 存活加载
 *  spring-boot-devtools模块包含了嵌入式的存活加载服务器,这个服务器可以用于在资源改变的时候触发浏览器刷新.
 *  存活加载的浏览器插件对于Chrome,FireFix和Safari来说是免费的.可以从(livereload.com获取)
 *  应用启动的时候不打算启动存活加载服务器，可以设置@spring.devtools.livereload.enabled =false
 *  仅仅可以一个时刻运行存活加载服务器，在你启动应用之前，可以保证没有其他存活加载服务器处于运行状态。
 *  如果启动了多个应用，仅仅一个能够支持存活加载。
 *
 *  9. 全局配置
 *  可以配置全局devtools配置，通过添加下述文件到@$HOME/.config/spring-boot 文件夹中
 *  分别是:
 *      1. spring-boot-devtools.properties
 *      2. spring-boot-devtools.yaml
 *      3. spring-boot-devtools.yml
 *  任何添加到这些配置的文件会以全局变量的形式应用到开发机器上。例如,配置重启,用于触发文件,可以添加下述文件.
 *  {{{
 *      # ~/.config/spring-boot/spring-boot-devtools.properties
 *      spring.devtools.restart.trigger-file=.reloadtrigger
 *  }}}
 *
 *  10. 远程应用
 *  spring boot开发者没有被限制本地开发,可以使用多种特征用于远程开发.远程支持是`opt-in`的,
 *  因为开启了权限则会有安全风险.应该仅仅在运行可信的网络或者使用SSL配置完全设置时候才能允许操作.
 *  如果这两个配置你都无法达到,就不能够使用DevTools的远程支持功能.需要在开发部署环境中开启支持.
 *  为了确保开启了,需要保证`devtools`的插件加载了,显示如下:
 *  {{{
 *  <build>
 *     <plugins>
 *         <plugin>
 *             <groupId>org.springframework.boot</groupId>
 *             <artifactId>spring-boot-maven-plugin</artifactId>
 *             <configuration>
 *                 <excludeDevtools>false</excludeDevtools>
 *             </configuration>
 *         </plugin>
 *     </plugins>
 * </build>
 *  }}}
 *  然后必须设置@spring.devtools.remote.secret属性.与其他密码或者密钥类似,这个值是唯一
 *  的,且安全性高的,使其不能够被轻易的猜测到或者破解.
 *  远程devtools支持提供了两个部分:
 *  a) 服务端的后台,这里可以接受连接和客户端应用(在IDE中设置的)
 *  b) 设置完@spring.devtools.remote.secret之后,服务器组件自动启动,
 *  客户端组件必须要手动运行
 *
 *  11. 运行远程客户端应用
 *  远程客户端应用用于在IDE中运行。需要在运行
 *  @org.springframework.boot.devtools.RemoteSpringApplication 的时候使用一些
 *  类路径,这些是远程项目需要连接的类路径.这个应用单个需要的参数就是远端URL地址.
 *  例如,如果使用Eclipse中的项目,例如`my-app`,可以部署到云端.
 *  可以参考如下配置:
 *   1. 从运行菜单中选择`Run Configurations`
 *   2. 创建新的java应用(运行配置)
 *   3. 浏览`my-app`项目
 *   4. 使用@org.springframework.boot.devtools.RemoteSpringApplication 作为主类
 *   5. 添加`https://myapp.cfapps.io`到程序的参数中（就是远端URL地址）
 *
 *   12. 远程更新
 *   远程客户端监视了你的类路径的改变,就如同在本地启动一样.如何更新的资源会被推送到远端应用.且
 *   触发重启.如果在一个特征上进行迭代(这个特征使用了本地不存在的云服务)这样是很有效的.
 *   总体来时,远端更新和重启要比全部重新构建和部署要快.
 *
 *   13. 配置文件系统监视器
 *   文件系统监视器通过对类的轮询工作,轮询需要设置轮询的时间周期.且等待预先设置后台执行周期.用于
 *   保证没有其他的改变.改变会更新到远程应用中.在一个慢速研发环境中,可能发生这样的状况:
 *   后台周期不足,且类的变化被分成多个批次.服务器在首个批次类的改变之后进行重启,下一个批次这个时候
 *   不能发送给应用,因为服务器处于重启状态,不能接受这些更新信息.
 *   这个典型地可以通过@RemoteSpringApplication 中的警告信息提示出来,信息的内容为`加载类失败`
 *   的日志信息,且会显示重试的次数.
 *   但是可能会导致应用代码的数据不一致,且在第一批次改变加载之后重启失败的情况.
 *   如果你发现了一些进程发生的问题,尝试增加@spring.devtools.restart.poll-interval 轮询周期。
 *   给予足够的重启处理时间。和@spring.devtools.restart.quiet-period 去适配开发环境。
 *
 *  14. 生产环境下应用打包
 *  执行jar包可以用于生成部署,因为是独立的.将其部署在云端也是合适的.
 *  对于准备生成的特征来说,比如说健壮性`health`,监察`auditing`,REST度量,和JMX后台.考虑到添加
 * @spring-boot-actuator 可以参考特征配置的文档进行设置。
 *
 * curl -i -H "Content-Type:application/json" -d '{"name": "Frodo", "age": 18,"address":"127.0.0.1"}' http://localhost:8080/people HTTP/1.1 201 Created
 * curl -i -H "Content-Type:application/json" -d '{"firstName": "Frodo", "lastName": "Baggins"}' http://localhost:8080/people
 * HTTP/1.1 201 Created
 *
 * */


@SpringBootApplication(scanBasePackages = {"com.example.demo.dao","com.example.demo.entity"})
public class Application {

	public static final Logger log=LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
//		System.setProperty("spring.devtools.restart.enabled","false"); // 关闭重启功能
		SpringApplication.run(Application.class,args);
	}

	@Bean
	public CommandLineRunner demo(CustomerRepository repository){
		return (args) ->{
			// save a few customers
			repository.save(new Customer("Jack", "Bauer"));
			repository.save(new Customer("Chloe", "O'Brian"));
			repository.save(new Customer("Kim", "Bauer"));
			repository.save(new Customer("David", "Palmer"));
			repository.save(new Customer("Michelle", "Dessler"));

			// fetch all customers
			log.info("Customers found with findAll():");
			log.info("-------------------------------");
			for (Customer customer : repository.findAll()) {
				log.info(customer.toString());
			}
			log.info("");

			// fetch an individual customer by ID
			Customer customer = repository.findById(1L);
			log.info("Customer found with findById(1L):");
			log.info("--------------------------------");
			log.info(customer.toString());
			log.info("");

			// fetch customers by last name
			log.info("Customer found with findByLastName('Bauer'):");
			log.info("--------------------------------------------");
			repository.findByLastName("Bauer").forEach(bauer -> {
				log.info(bauer.toString());
			});
			// for (Customer bauer : repository.findByLastName("Bauer")) {
			//  log.info(bauer.toString());
			// }
			log.info("");
		};
	}
}
