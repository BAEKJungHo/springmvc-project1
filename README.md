# Inflearn. 스프링 MVC 1편

스프링 부트는 서블릿을 직접 등록해서 사용할 수 있도록 `@ServletComponentScan` 을 지원한다.

```java
@ServletComponentScan // 서블릿 자동 등록
@SpringBootApplication
public class ServletApplication {
  public static void main(String[] args) {
    SpringApplication.run(ServletApplication.class, args);
  }
}
```

- @WebServlet
 - name : 서블릿 이름
 - urlPatterns : URL 패턴
 - name 과 urlPatterns 는 중복 될 수 없다.

```java
/**
 * 서블릿을 만들기 위해서 HttpServlet 을 상속 받고, @WebServlet 어노테이션을 사용해야 한다.
 */
@WebServlet(name = "helloServlet", urlPatterns = "/hello")
public class HelloServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("HelloServlet.service");
        System.out.println("request = " + request);
        System.out.println("response = " + response);

        String username = request.getParameter("username");
        System.out.println("username = " + username);

        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write("hello " + username);
    }

}
```

HTTP 요청을 통해 매핑된 URL이 호출되면 서블릿 컨테이너는 다음 메서드를 실행한다.

`protected void service(HttpServletRequest request, HttpServletResponse response)`

설정파일(application.yml or properties)에 아래의 설정 추가

`logging.level.org.apache.coyote.http11=debug`

서버를 다시 시작하고, 요청해보면 서버가 받은 HTTP 요청 메시지를 출력하는 것을 확인할 수 있다.

## 서블릿 컨테이너 동작 방식

> WAS(Tomcat 등) = HTTP + Servlet 의 기능 까지 제공

![IMAGES](/images/tomcat.JPG)

> HTTP 응답에서 Content-Length 과 같은 정보는 웹 애플리케이션 서버가 자동으로 생성해준다.

## HttpServletRequest

### 역할

HTTP 요청 메시지를 개발자가 직접 파싱해서 사용해도 되지만, 매우 불편할 것이다. `서블릿(servlet)`은 개발자가 HTTP 요청 메시지를 편리하게 사용할 수 있도록 개발자 대신에 HTTP 요청 메시지를 파싱한다. 그리고 그 결과를 `HttpServletRequest` 객체에 담아서 제공한다.

- 부가 기능
  - 임시 저장소 기능
    - 해당 HTTP 요청이 시작부터 끝날 때 까지 유지되는 임시 저장소 기능
      - 저장: request.setAttribute(name, value)
      - 조회: request.getAttribute(name)
  - 세션 관리 기능
    - request.getSession(create: true)

>  HttpServletRequest, HttpServletResponse를 사용할 때 가장 중요한 점은 이 객체들이 HTTP 요청
메시지, HTTP 응답 메시지를 편리하게 사용하도록 도와주는 객체라는 점이다. 따라서 이 기능에 대해서
깊이있는 이해를 하려면 HTTP 스펙이 제공하는 요청, 응답 메시지 자체를 이해해야 한다.
