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
>
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
>
> 로컬에서 테스트하면 IPv6 정보가 나오는데, IPv4 정보를 보고 싶으면 다음 옵션을 VM options에
넣어주면 된다. -Djava.net.preferIPv4Stack=true

## HTTP 요청 데이터 

HTTP 요청 메시지를 통해 클라이언트에서 서버로 데이터를 전달하는 방법을 알아보자.

주로 3가지 방법을 사용한다.

- `GET - 쿼리 파라미터`
  - /url?username=dope&age=28
  - 메시지 바디 없이, URL 의 쿼리 파라미터에 데이터를 포함해서 전달
  - Ex. 검색, 필터, 페이징등에서 많이 사용
- `POST - HTML Form`
  - content-type : application/x-www/form-urlencoded
  - 메시지 바디에 쿼리 파라미터 형식으로 전달 username=dope&age=28
  - Ex. 회원 가입, 상품 주문, HTML Form 사용
- `HTTP message body 에 데이터를 직접 담아서 요청`
  - HTTP API 에서 주로 사용, JSON, XML, TEXT
 - 데이터 형식은 주로 JSON 사용
  - POST, PUT, PATCH

### 쿼리 파라미터 조회 메서드

```java
String username = request.getParameter("username"); // 단일 파라미터 조회
Enumeration<String> parameterNames = request.getParameterNames(); // 파라미터 이름들 모두 조회
Map<String, String[]> parameterMap = request.getParameterMap(); // 파라미터를 Map 으로 조회
String[] usernames = request.getParameterValues("username"); // 복수 파라미터 조회
```

> 참고
> content-type은 HTTP 메시지 바디의 데이터 형식을 지정한다.
> 
> GET URL 쿼리 파라미터 형식으로 클라이언트에서 서버로 데이터를 전달할 때는 HTTP 메시지 바디를 사용하지 않기 때문에 content-type 이 없다.
> 
> POST HTML Form 형식으로 데이터를 전달하면 HTTP 메시지 바디에 해당 데이터를 포함해서 보내기
때문에 바디에 포함된 데이터가 어떤 형식인지 content-type을 꼭 지정해야 한다. 이렇게 폼으로 데이터를
전송하는 형식을 `application/x-www-form-urlencoded` 라 한다

이런 간단한 테스트에 HTML form을 만들기는 귀찮다. 이때는 Postman을 사용하면 된다.

- Postman 테스트 주의사항
- POST 전송시
  - Body x-www-form-urlencoded 선택
  - Headers에서 content-type: application/x-www-form-urlencoded 로 지정된 부분 꼭 확인

> 복수 파라미터에서 단일 파라미터 조회 username=hello&username=kim 과 같이 파라미터 이름은 하나인데, 값이 중복이면 어떻게 될까?
request.getParameter() 는 하나의 파라미터 이름에 대해서 단 하나의 값만 있을 때 사용해야 한다. 
지금처럼 중복일 때는 request.getParameterValues() 를 사용해야 한다.
참고로 이렇게 중복일 때 request.getParameter() 를 사용하면 request.getParameterValues() 의
첫 번째 값을 반환한다.
>
> 보통은 중복으로 쓸 일이 거의 없다.

### HTTP 요청 데이터 - API 메시지 바디 - 단순 텍스트

- HTTP message body에 데이터를 직접 담아서 요청
  - HTTP API에서 주로 사용, JSON, XML, TEXT
  - 데이터 형식은 주로 JSON 사용
  - POST, PUT, PATCH
- 먼저 가장 단순한 텍스트 메시지를 HTTP 메시지 바디에 담아서 전송하고, 읽어보자.
- HTTP 메시지 바디의 데이터를 InputStream 을 사용해서 직접 읽을 수 있다.

> inputStream은 byte 코드를 반환한다. byte 코드를 우리가 읽을 수 있는 문자(String)로 보려면 문자표
(Charset)를 지정해주어야 한다. 여기서는 UTF_8 Charset을 지정해주었다.

- 문자 전송
  - POST http://localhost:8080/request-body-string
  - content-type: text/plain
  - message body: hello
  - 결과: messageBody = hello

### HTTP 요청 데이터 - API 메시지 바디 - JSON

이번에는 HTTP API에서 주로 사용하는 JSON 형식으로 데이터를 전달해보자.

- JSON 형식 전송
  - POST http://localhost:8080/request-body-json
  - content-type: application/json
  - message body: {"username": "hello", "age": 20}
  - 결과: messageBody = {"username": "hello", "age": 20}

> JSON 결과를 파싱해서 사용할 수 있는 자바 객체로 변환하려면 Jackson, Gson 같은 JSON 변환
라이브러리를 추가해서 사용해야 한다. 스프링 부트로 Spring MVC 를 선택하면 기본으로 Jackson 
라이브러리(ObjectMapper)를 함께 제공한다.
>
> HTML form 데이터도 메시지 바디를 통해 전송되므로 직접 읽을 수 있다. 하지만 편리한 파리미터 조회
기능( request.getParameter(...) )을 이미 제공하기 때문에 파라미터 조회 기능을 사용하면 된다.

## HttpServletResponse

### 역할

- HTTP 응답 메시지 생성
  - HTTP 응답 코드 지정
  - 헤더 생성
  - 바디 생성

- 편의 기능 제공
  - Content-Type, 쿠키, Redirect

### HTTP 응답으로 JSON 반환

HTTP 응답으로 JSON을 반환할 때는 content-type을 application/json 로 지정해야 한다.
Jackson 라이브러리가 제공하는 objectMapper.writeValueAsString() 를 사용하면 객체를 JSON 
문자로 변경할 수 있다.

> application/json 은 스펙상 utf-8 형식을 사용하도록 정의되어 있다. 그래서 스펙에서 charset=utf-8 
과 같은 추가 파라미터를 지원하지 않는다. 따라서 application/json 이라고만 사용해야지
> application/json;charset=utf-8 이라고 전달하는 것은 의미 없는 파라미터를 추가한 것이 된다.
> response.getWriter()를 사용하면 추가 파라미터를 자동으로 추가해버린다. 이때는
response.getOutputStream()으로 출력하면 그런 문제가 없다.

## [MVC 패턴](https://github.com/BAEKJungHo/designpattern/blob/master/DESIGNPATTERN/pattern/MVC.md)

### 개요

#### 너무 많은 역할

하나의 서블릿이나 JSP만으로 비즈니스 로직과 뷰 렌더링까지 모두 처리하게 되면, 너무 많은 역할을
하게되고, 결과적으로 유지보수가 어려워진다. 비즈니스 로직을 호출하는 부분에 변경이 발생해도 해당
코드를 손대야 하고, UI를 변경할 일이 있어도 비즈니스 로직이 함께 있는 해당 파일을 수정해야 한다. 
HTML 코드 하나 수정해야 하는데, 수백줄의 자바 코드가 함께 있다고 상상해보라! 또는 비즈니스 로직을
하나 수정해야 하는데 수백 수천줄의 HTML 코드가 함께 있다고 상상해보라.

#### 변경의 라이프 사이클

사실 이게 정말 중요한데, 진짜 문제는 둘 사이에 변경의 라이프 사이클이 다르다는 점이다. 예를 들어서 UI
를 일부 수정하는 일과 비즈니스 로직을 수정하는 일은 각각 다르게 발생할 가능성이 매우 높고 대부분
서로에게 영향을 주지 않는다. 이렇게 변경의 라이프 사이클이 다른 부분을 하나의 코드로 관리하는 것은
유지보수하기 좋지 않다. (물론 UI가 많이 변하면 함께 변경될 가능성도 있다.)

#### 기능 특화

특히 JSP 같은 뷰 템플릿은 화면을 렌더링 하는데 최적화 되어 있기 때문에 이 부분의 업무만 담당하는 것이
가장 효과적이다.

#### Model View Controller

MVC 패턴은 지금까지 학습한 것 처럼 하나의 서블릿이나, JSP로 처리하던 것을 컨트롤러(Controller)와
뷰(View)라는 영역으로 서로 역할을 나눈 것을 말한다. 웹 애플리케이션은 보통 이 MVC 패턴을 사용한다.

- 컨트롤러: HTTP 요청을 받아서 파라미터를 검증하고, 비즈니스 로직을 실행한다. 그리고 뷰에 전달할 결과
데이터를 조회해서 모델에 담는다.

- 모델: 뷰에 출력할 데이터를 담아둔다. 뷰가 필요한 데이터를 모두 모델에 담아서 전달해주는 덕분에 뷰는
비즈니스 로직이나 데이터 접근을 몰라도 되고, 화면을 렌더링 하는 일에 집중할 수 있다.

- 뷰: 모델에 담겨있는 데이터를 사용해서 화면을 그리는 일에 집중한다. 여기서는 HTML을 생성하는 부분을
말한다.

> 컨트롤러에 비즈니스 로직을 둘 수도 있지만, 이렇게 되면 컨트롤러가 너무 많은 역할을 담당한다. 그래서
일반적으로 비즈니스 로직은 서비스(Service)라는 계층을 별도로 만들어서 처리한다. 그리고 컨트롤러는
비즈니스 로직이 있는 서비스를 호출하는 담당한다. 참고로 비즈니스 로직을 변경하면 비즈니스 로직을
호출하는 컨트롤러의 코드도 변경될 수 있다. 앞에서는 이해를 돕기 위해 비즈니스 로직을 호출한다는 표현
보다는, 비즈니스 로직이라 설명했다.

## JSP

- `/WEB-INF`
  - 이 경로안에 JSP가 있으면 외부에서 직접 JSP를 호출할 수 없다. 우리가 기대하는 것은 항상 컨트롤러를
통해서 JSP를 호출하는 것이다.
- `redirect vs forward`
  - 리다이렉트는 실제 클라이언트(웹 브라우저)에 응답이 나갔다가, 클라이언트가 redirect 경로로 다시
요청한다. 따라서 클라이언트가 인지할 수 있고, URL 경로도 실제로 변경된다. 반면에 포워드는 서버
내부에서 일어나는 호출이기 때문에 클라이언트가 전혀 인지하지 못한다.

## [프론트 컨트롤러 패턴](https://github.com/BAEKJungHo/designpattern/blob/master/DESIGNPATTERN/pattern/Front%20Controller.md)

### v1

![IMAGES](/images/v1.JPG)

### v2

![IMAGES](/images/v2.JPG)

### v3

![IMAGES](/images/v3.JPG)

### v4

![IMAGES](/images/v4.JPG)

### v5

![IMAGES](/images/v5.JPG)

## 스프링 MVC

![IMAGES](/images/springmvc3.JPG)

- 직접 만든 프레임워크 스프링 MVC 비교
  - FrontController > DispatcherServlet
  - handlerMappingMap > HandlerMapping
  - MyHandlerAdapter > HandlerAdapter
  - ModelView > ModelAndView
  - viewResolver > ViewResolver
  - MyView > View

### DispatcherServlet

`org.springframework.web.servlet.DispatcherServlet`

스프링 MVC 도 프론트 컨트롤러 패턴으로 구현되어 있다. 스프링 MVC 의 프론트 컨트롤러가 바로 DispatcherServlet 이다. 따라서, 스프링 MVC 의 핵심은 `DispatcherServlet` 이다.

#### DispatcherServlet 등록

DispatcherServlet 도 부모 클래스에서 HttpServlet 을 상속 받아서 사용하고, 서블릿으로 동작한다.

> DispatcherServlet > FrameworkServlet > HttpServletBean > HttpServlet

__스프링 부트는 DispatcherServlet 을 서블릿으로 자동으로 등록하면서 모든 경로(urlPatterns="/")에 대해서 매핑한다.__

> 더 자세한 경로가 우선순위가 높다. 그래서 기존에 등록한 서블릿도 함께 동작한다.

#### 요청 흐름

- 서블릿이 호출되면 HttpServlet 이 제공하는 service() 가 호출된다.
- 스프링 MVC 는 DispatcherServlet 의 부모인 FrameworkServlet 에서 service() 를 오버라이드 해두었다.
- FrameworkServlet.service() 를 시작으로 여러 메서드가 호출되면서 DispatcherServlet.doDispatch() 가 호출된다.

지금부터 DispacherServlet 의 핵심인 doDispatch() 코드를 분석해보자. 최대한 간단히 설명하기
위해 예외처리, 인터셉터 기능은 제외했다.

```java
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
    HttpServletRequest processedRequest = request;
    HandlerExecutionChain mappedHandler = null;
    ModelAndView mv = null;
    
    // 1. 핸들러 조회
    mappedHandler = getHandler(processedRequest);
    if (mappedHandler == null) {
      noHandlerFound(processedRequest, response);
      return;
    }
    
    // 2. 핸들러 어댑터 조회 - 핸들러를 처리할 수 있는 어댑터
    HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
    
    // 3. 핸들러 어댑터 실행 -> 4. 핸들러 어댑터를 통해 핸들러 실행 -> 5. ModelAndView 반환
    mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
    processDispatchResult(processedRequest, response, mappedHandler, mv,dispatchException);
}

private void processDispatchResult(HttpServletRequest request,HttpServletResponse response, HandlerExecutionChain mappedHandler, ModelAndView mv, Exception exception) throws Exception {
    // 뷰 렌더링 호출
    render(mv, request, response);
}

protected void render(ModelAndView mv, HttpServletRequest request,HttpServletResponse response) throws Exception {
    View view;
    String viewName = mv.getViewName();
    
    // 6. 뷰 리졸버를 통해서 뷰 찾기, 7. View 반환
    view = resolveViewName(viewName, mv.getModelInternal(), locale, request);
    
    // 8. 뷰 렌더링
    view.render(mv.getModelInternal(), request, response);
}
```

#### 동작 순서

1. 핸들러 조회: 핸들러 매핑을 통해 요청 URL에 매핑된 핸들러(컨트롤러)를 조회한다.
2. 핸들러 어댑터 조회: 핸들러를 실행할 수 있는 핸들러 어댑터를 조회한다.
3. 핸들러 어댑터 실행: 핸들러 어댑터를 실행한다.
4. 핸들러 실행: 핸들러 어댑터가 실제 핸들러를 실행한다.
5. ModelAndView 반환: 핸들러 어댑터는 핸들러가 반환하는 정보를 ModelAndView로 변환해서
반환한다.
6. viewResolver 호출: 뷰 리졸버를 찾고 실행한다.
JSP의 경우: InternalResourceViewResolver 가 자동 등록되고, 사용된다.
7. View 반환: 뷰 리졸버는 뷰의 논리 이름을 물리 이름으로 바꾸고, 렌더링 역할을 담당하는 뷰 객체를
반환한다.
JSP의 경우 InternalResourceView(JstlView) 를 반환하는데, 내부에 forward() 로직이 있다.
8. 뷰 렌더링: 뷰를 통해서 뷰를 렌더링 한다.

#### 인터페이스 살펴보기

스프링 MVC의 큰 강점은 DispatcherServlet 코드의 변경 없이, 원하는 기능을 변경하거나 확장할 수 있다는 점이다. 지금까지 설명한 대부분을 확장 가능할 수 있게 인터페이스로 제공한다.
이 인터페이스들만 구현해서 DispatcherServlet 에 등록하면 여러분만의 컨트롤러를 만들 수도 있다.

> 사실 해당 기능을 직접 확장하거나 나만의 컨트롤러를 만드는 일은 없으므로 걱정하지 않아도 된다. 왜냐하면 스프링
MVC는 전세계 수 많은 개발자들의 요구사항에 맞추어 기능을 계속 확장왔고, 그래서 여러분이 웹
애플리케이션을 만들 때 필요로 하는 대부분의 기능이 이미 다 구현되어 있다.

#### 주요 인터페이스 목록

- 핸들러 매핑 : org.springframework.web.servlet.HandlerMapping
- 핸들러 어댑터 : org.springframework.web.servlet.HandlerAdapter
- 뷰 리졸버 : org.springframework.web.servlet.ViewResolver
- 뷰 : org.springframework.web.servlet.View

## 핸들러 매핑과 핸들러 어댑터

- `HandlerMapping(핸들러 매핑)`
  - 핸들러 매핑에서 이 컨트롤러를 찾을 수 있어야 한다.
  - 예) 스프링 빈의 이름으로 핸들러를 찾을 수 있는 핸들러 매핑이 필요하다.
- `HandlerAdapter(핸들러 어댑터)`
  - 핸들러 매핑을 통해서 찾은 핸들러를 실행할 수 있는 핸들러 어댑터가 필요하다.
  - 예) Controller 인터페이스를 실행할 수 있는 핸들러 어댑터를 찾고 실행해야 한다.

### HandlerMapping

- 0 순위 = RequestMappingHandlerMapping : 애노테이션 기반의 컨트롤러인 @RequestMapping에서 사용
- 1 순위 = BeanNameUrlHandlerMapping : 스프링 빈의 이름으로 핸들러를 찾는다.

### HanlderAdapter

- 0 순위 = RequestMappingHandlerAdapter : 애노테이션 기반의 컨트롤러인 @RequestMapping에서 사용
- 1 순위 = HttpRequestHandlerAdapter : HttpRequestHandler 처리
- 2 순위 = SimpleControllerHandlerAdapter : Controller 인터페이스(애노테이션X, 과거에 사용) 처리

> 핸들러 매핑도, 핸들러 어댑터도 모두 순서대로 찾고 만약 없으면 다음 순서로 넘어간다.

### 과거버전 Controller Interface 사용하는 경우

1. 핸들러 매핑으로 핸들러 조회
  - HandlerMapping 을 순서대로 실행해서, 핸들러를 찾는다.
  - 이 경우 빈 이름으로 핸들러를 찾아야 하기 때문에 이름 그대로 빈 이름으로 핸들러를 찾아주는 BeanNameUrlHandlerMapping 가 실행에 성공하고 핸들러인 OldController 를 반환한다.
2. 핸들러 어댑터 조회
  - HandlerAdapter 의 supports() 를 순서대로 호출한다.
  - SimpleControllerHandlerAdapter 가 Controller 인터페이스를 지원하므로 대상이 된다.
3. 핸들러 어댑터 실행
  - 디스패처 서블릿이 조회한 SimpleControllerHandlerAdapter 를 실행하면서 핸들러 정보도 함께넘겨준다.
  - SimpleControllerHandlerAdapter 는 핸들러인 OldController 를 내부에서 실행하고, 그 결과를 반환한다.

- 정리 - OldController 핸들러매핑, 어댑터
  - OldController 를 실행하면서 사용된 객체는 다음과 같다.
  - HandlerMapping = BeanNameUrlHandlerMapping
  - HandlerAdapter = SimpleControllerHandlerAdapter

### HttpRequestHandler

HttpRequestHandler 핸들러(컨트롤러)는 서블릿과 가장 유사한 형태의 핸들러이다.

```java
public interface HttpRequestHandler {
  void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
```

1. 핸들러 매핑으로 핸들러 조회
  - HandlerMapping 을 순서대로 실행해서, 핸들러를 찾는다.
  - 이 경우 빈 이름으로 핸들러를 찾아야 하기 때문에 이름 그대로 빈 이름으로 핸들러를 찾아주는 BeanNameUrlHandlerMapping 가 실행에 성공하고 핸들러인 MyHttpRequestHandler 를 반환한다.
2. 핸들러 어댑터 조회
  - HandlerAdapter 의 supports() 를 순서대로 호출한다.
  - HttpRequestHandlerAdapter 가 HttpRequestHandler 인터페이스를 지원하므로 대상이 된다.
3. 핸들러 어댑터 실행
  - 디스패처 서블릿이 조회한 HttpRequestHandlerAdapter 를 실행하면서 핸들러 정보도 함께 넘겨준다.
  - HttpRequestHandlerAdapter 는 핸들러인 MyHttpRequestHandler 를 내부에서 실행하고, 그 결과를 반환한다.

- 정리 - MyHttpRequestHandler 핸들러매핑, 어댑터 MyHttpRequestHandler 를 실행하면서 사용된 객체는 다음과 같다.
  - HandlerMapping = BeanNameUrlHandlerMapping
  - HandlerAdapter = HttpRequestHandlerAdapter

#### @RequestMapping

조금 뒤에서 설명하겠지만, 가장 우선순위가 높은 핸들러 매핑과 핸들러 어댑터는 RequestMappingHandlerMapping, RequestMappingHandlerAdapter 이다.
@RequestMapping 의 앞글자를 따서 만든 이름인데, 이것이 바로 지금 스프링에서 주로 사용하는
애노테이션 기반의 컨트롤러를 지원하는 매핑과 어댑터이다. 실무에서는 99.9% 이 방식의 컨트롤러를
사용한다.

## 뷰 리졸버

뷰 리졸버 - InternalResourceViewResolver
스프링 부트는 InternalResourceViewResolver 라는 뷰 리졸버를 자동으로 등록하는데, 이때
application.properties 에 등록한 spring.mvc.view.prefix , spring.mvc.view.suffix 설정 정보를 사용해서 등록한다.
참고로 권장하지는 않지만 설정 없이 다음과 같이 전체 경로를 주어도 동작하기는 한다.
return new ModelAndView("/WEB-INF/views/new-form.jsp");

### 스프링 부트가 자동 등록하는 뷰 리졸버

실제로는 더 많지만, 중요한 부분 위주로 설명하기 위해 일부 생략

```
1 = BeanNameViewResolver : 빈 이름으로 뷰를 찾아서 반환한다. (예: 엑셀 파일 생성 기능에 사용)
2 = InternalResourceViewResolver : JSP를 처리할 수 있는 뷰를 반환한다.
```

1. 핸들러 어댑터 호출 : 핸들러 어댑터를 통해 new-form 이라는 논리 뷰 이름을 획득한다.
2. ViewResolver 호출 : new-form 이라는 뷰 이름으로 viewResolver를 순서대로 호출한다.
BeanNameViewResolver 는 new-form 이라는 이름의 스프링 빈으로 등록된 뷰를 찾아야 하는데 없다.
InternalResourceViewResolver 가 호출된다.
3. InternalResourceViewResolver : 이 뷰 리졸버는 InternalResourceView 를 반환한다.
4. 뷰 - InternalResourceView : InternalResourceView 는 JSP처럼 포워드 forward() 를 호출해서 처리할 수 있는 경우에 사용한다.
5. view.render() : view.render() 가 호출되고 InternalResourceView 는 forward() 를 사용해서 JSP를 실행한다.

> 참고
> InternalResourceViewResolver 는 만약 JSTL 라이브러리가 있으면 InternalResourceView 를
상속받은 JstlView 를 반환한다. JstlView 는 JSTL 태그 사용시 약간의 부가 기능이 추가된다.
> 
> 참고
> 다른 뷰는 실제 뷰를 렌더링하지만, JSP의 경우 forward() 통해서 해당 JSP로 이동(실행)해야 렌더링이
된다. JSP를 제외한 나머지 뷰 템플릿들은 forward() 과정 없이 바로 렌더링 된다.
> 
> 참고
> Thymeleaf 뷰 템플릿을 사용하면 ThymeleafViewResolver 를 등록해야 한다. 최근에는 라이브러리만
추가하면 스프링 부트가 이런 작업도 모두 자동화해준다.

## 스프링 MVC 실용적인 방식

실무에서는 지금부터 설명하는 방식을 주로 사용한다.

```java
/**
 * v3
 * Model 도입
 * ViewName 직접 반환
 * @RequestParam 사용
 * @RequestMapping -> @GetMapping, @PostMapping
 */
@Controller
@RequestMapping("/springmvc/v3/members")
public class SpringMemberControllerV3 {
 
 private MemberRepository memberRepository = MemberRepository.getInstance();
 
 @GetMapping("/new-form")
 public String newForm() {
    return "new-form";
 }
 
 @PostMapping("/save")
 public String save(@RequestParam("username") String username, @RequestParam("age") int age, Model model) {
     Member member = new Member(username, age);
     memberRepository.save(member);
     model.addAttribute("member", member);
     return "save-result";
 }
 @GetMapping
 public String members(Model model) {
     List<Member> members = memberRepository.findAll();
     model.addAttribute("members", members);
     return "members";
 }
}
```

- Model 파라미터
  - save() , members() 를 보면 Model을 파라미터로 받는 것을 확인할 수 있다. 스프링 MVC도 이런 편의기능을 제공한다.
- ViewName 직접 반환
  - 뷰의 논리 이름을 반환할 수 있다.
- @RequestParam 사용
  - 스프링은 HTTP 요청 파라미터를 @RequestParam 으로 받을 수 있다. @RequestParam("username") 은 request.getParameter("username") 와 거의 같은 코드라 생각하면 된다.
  - 물론 GET 쿼리 파라미터, POST Form 방식을 모두 지원한다.
- @RequestMapping @GetMapping, @PostMapping

## @RequestMapping

- @RequestMapping
  - RequestMappingHandlerMapping
  - RequestMappingHandlerAdapter

앞서 보았듯이 가장 우선순위가 높은 핸들러 매핑과 핸들러 어댑터는 RequestMappingHandlerMapping, RequestMappingHandlerAdapter 이다.
@RequestMapping 의 앞글자를 따서 만든 이름인데, 이것이 바로 지금 스프링에서 주로 사용하는
애노테이션 기반의 컨트롤러를 지원하는 핸들러 매핑과 어댑터이다. 실무에서는 99.9% 이 방식의
컨트롤러를 사용한다.

## 스프링 MVC 기본 기능

스프링 부트에 Jar 를 사용하면 /resources/static/index.hml 위치에 index.html 파일을 두면
Welcome 페이지로 처리해준다. (스프링 부트가 지원하는 정적 컨텐츠 위치에 /index.html 이 있으면
된다.

### 로깅(logging)

#### 로깅 라이브러리

스프링 부트 라이브러리를 사용하면 스프링 부트 로깅 라이브러리(`spring-boot-starter-logging`)가 함께 포함된다.

스프링 부트 로깅 라이브러리는 기본으로 다음 로깅 라이브러리를 사용한다.

- SLF4J - http://www.slf4j.org
- Logback - http://logback.qos.ch

로그 라이브러리는 Logback, Log4J, Log4J2 등등 수 많은 라이브러리가 있는데, 그것을 통합해서 인터페이스로 제공하는 것이 바로 `SLF4J` 라이브러리다.

쉽게 이야기해서 `SLF4J는 인터페이스`이고, 그 `구현체로 Logback 같은 로그 라이브러리`를 선택하면 된다. 실무에서는 스프링 부트가 기본으로 제공하는 Logback을 대부분 사용한다.

#### 로그 선언

- private Logger log = LoggerFactory.getLogger(getClass());
- private static final Logger log = LoggerFactory.getLogger(Xxx.class)
- @Slf4j : 롬복 사용 가능

#### 로그 호출

- log.info("hello")
- System.out.println("hello")
- 시스템 콘솔로 직접 출력하는 것 보다 로그를 사용하면 다음과 같은 장점이 있다. 실무에서는 항상 로그를 사용해야 한다.

#### 매핑 정보

- @RestController
  - @Controller 는 반환 값이 String 이면 뷰 이름으로 인식된다. 그래서 뷰를 찾고 뷰가 랜더링 된다.
  - @RestController 는 반환 값으로 뷰를 찾는 것이 아니라, HTTP 메시지 바디에 바로 입력한다. 따라서 실행 결과로 ok 메세지를 받을 수 있다. @ResponseBody 와 관련이 있는데, 뒤에서 더 자세히
설명한다.

#### 테스트

- 로그가 출력되는 포멧 확인
  - 시간, 로그 레벨, 프로세스 ID, 쓰레드 명, 클래스명, 로그 메시지
- 로그 레벨 설정을 변경해서 출력 결과를 보자.
  - LEVEL: TRACE > DEBUG > INFO > WARN > ERROR
  - 개발 서버는 `debug` 출력
  - 운영 서버는 `info` 출력
- @Slf4j 로 변경

#### 로그 레벨 설정

- application.yml(.properties)

```yml
#전체 로그 레벨 설정(기본 info)
logging.level.root=info

#hello.springmvc 패키지와 그 하위 로그 레벨 설정
logging.level.hello.springmvc=debug
```

#### 올바른 로그 사용법

- `log.debug("data="+data)`
  - 로그 출력 레벨을 info로 설정해도 해당 코드에 있는 "data="+data가 실제 실행이 되어 버린다. 결과적으로 문자 더하기 연산이 발생한다.
- `log.debug("data={}", data)`
  - 로그 출력 레벨을 info로 설정하면 아무일도 발생하지 않는다. 따라서 앞과 같은 의미없는 연산이 발생하지 않는다.

#### 로그 사용 시 장점

- 쓰레드 정보, 클래스 이름 같은 부가 정보를 함께 볼 수 있고, 출력 모양을 조정할 수 있다.
- 로그 레벨에 따라 개발 서버에서는 모든 로그를 출력하고, 운영서버에서는 출력하지 않는 등 로그를 상황에 맞게 조절할 수 있다.
- 시스템 아웃 콘솔에만 출력하는 것이 아니라, 파일이나 네트워크 등, 로그를 별도의 위치에 남길 수 있다. 
- 특히 파일로 남길 때는 일별, 특정 용량에 따라 로그를 분할하는 것도 가능하다.
- 성능도 일반 System.out보다 좋다. (내부 버퍼링, 멀티 쓰레드 등등) 그래서 실무에서는 꼭 로그를 사용해야 한다.

#### 참고

로그에 대해서 더 자세한 내용은 slf4j, logback을 검색해보자.

- SLF4J - http://www.slf4j.org
- Logback - http://logback.qos.ch
- 스프링 부트가 제공하는 로그 기능은 다음을 참고하자.
  - https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#boot-features-loggin

### HTTP 요청 - 기본 헤더 조회

```java
@Slf4j
@RestController
public class RequestHeaderController {
    @RequestMapping("/headers")
    public String headers(HttpServletRequest request,
                          HttpServletResponse response,
                          HttpMethod httpMethod,
                          Locale locale,
                          @RequestHeader MultiValueMap<String, String> headerMap,
                          @RequestHeader("host") String host,
                          @CookieValue(value = "myCookie", required = false) String cookie
    ) {
        log.info("request={}", request);
        log.info("response={}", response);
        log.info("httpMethod={}", httpMethod);
        log.info("locale={}", locale);
        log.info("headerMap={}", headerMap);
        log.info("header host={}", host);
        log.info("myCookie={}", cookie);
        return "ok";
    }
}
```

- HttpServletRequest
- HttpServletResponse
- HttpMethod : HTTP 메서드를 조회한다. org.springframework.http.HttpMethod
- Locale : Locale 정보를 조회한다.
- @RequestHeader MultiValueMap<String, String> headerMap
  - 모든 HTTP 헤더를 MultiValueMap 형식으로 조회한다.
- @RequestHeader("host") String host
  - 특정 HTTP 헤더를 조회한다.
  - 속성
    - 필수 값 여부: required
    - 기본 값 속성: defaultValue
- @CookieValue(value = "myCookie", required = false) String cookie
  - 특정 쿠키를 조회한다.
  - 속성
    - 필수 값 여부: required
    - 기본 값: defaultValue

- MultiValueMap
  - MAP 과 유사한데, 하나의 키에 여러 값을 받을 수 있다.
  - HTTP header, HTTP 쿼리 파라미터와 같이 하나의 키에 여러 값을 받을 때 사용한다. 
  - keyA=value1&keyA=value2

```java
MultiValueMap<String, String> map = new LinkedMultiValueMap();
map.add("keyA", "value1");
map.add("keyA", "value2");
//[value1,value2]
List<String> values = map.get("keyA");
```

> 참고
> @Conroller 의 사용 가능한 파라미터 목록은 다음 공식 메뉴얼에서 확인할 수 있다.
> https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-ann-arguments
> 
> 참고
> @Conroller 의 사용 가능한 응답 값 목록은 다음 공식 메뉴얼에서 확인할 수 있다.
> https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-ann-return-type
>
> Jar 를 사용하면 webapp 경로를 사용할 수 없다. 이제부터 정적 리소스도 클래스 경로에 함께 포함해야한다.

#### @RequestParam

- @RequestParam.required
  - 파라미터 필수 여부
  - 기본값이 파라미터 필수(`true`)이다.
- /request-param 요청
  - username 이 없으므로 400 예외가 발생한다.

- 주의! - 파라미터 이름만 사용
  - /request-param?username=
  - 파라미터 이름만 있고 값이 없는 경우 빈문자로 통과

- 주의! - 기본형(primitive)에 null 입력
  - /request-param 요청
  - @RequestParam(required = false) int age
  - null 을 int 에 입력하는 것은 불가능(500 예외 발생)
  - 따라서 null 을 받을 수 있는 Integer 로 변경하거나, 또는 다음에 나오는 defaultValue 사용

```java
/**
 * @RequestParam
 * - defaultValue 사용
 *
 * 참고: defaultValue는 빈 문자의 경우에도 적용
 * /request-param?username=
 */
@ResponseBody
@RequestMapping("/request-param-default")
public String requestParamDefault(
        @RequestParam(required = true, defaultValue = "guest") String username,
        @RequestParam(required = false, defaultValue = "-1") int age) {
    log.info("username={}, age={}", username, age);
    return "ok";
}
```

#### 파라미터를 Map으로 조회하기 - requestParamMap

```java
/**
 * @RequestParam Map, MultiValueMap
 * Map(key=value)
 * MultiValueMap(key=[value1, value2, ...] ex) (key=userIds, value=[id1, id2])
 */
@ResponseBody
@RequestMapping("/request-param-map")
public String requestParamMap(@RequestParam Map<String, Object> paramMap) {
    log.info("username={}, age={}", paramMap.get("username"),
            paramMap.get("age"));
    return "ok";
}
```

- @RequestParam Map, 
  - `Map(key=value)`
- @RequestParam MultiValueMap
  - `MultiValueMap(key=[value1, value2, ...] ex) (key=userIds, value=[id1, id2])`

파라미터의 값이 1개가 확실하다면 Map 을 사용해도 되지만, 그렇지 않다면 MultiValueMap 을 사용하자.

### @ModelAttribute

- 롬복 @Data
  - @Getter , @Setter , @ToString , @EqualsAndHashCode , @RequiredArgsConstructor 를 자동으로 적용해준다.

- 스프링MVC는 @ModelAttribute 가 있으면 다음을 실행한다.
- HelloData 객체를 생성한다.
- 요청 파라미터의 이름으로 HelloData 객체의 프로퍼티를 찾는다. 그리고 해당 프로퍼티의 setter 를 호출해서 파라미터의 값을 입력(바인딩)한다.
- 예) 파라미터 이름이 username 이면 setUsername() 메서드를 찾아서 호출하면서 값을 입력한다.

@ModelAttribute 는 생략할 수 있다. 그런데 @RequestParam 도 생략할 수 있으니 혼란이 발생할 수 있다.

스프링은 해당 생략시 다음과 같은 규칙을 적용한다.

- String, int, Integer 같은 단순 타입 = @RequestParam
- 나머지 : @ModelAttribute (argument resolver 로 지정해둔 타입 외) 

### HTTP 요청 메시지 - 단순 

요청 파라미터와 다르게, HTTP 메시지 바디를 통해 데이터가 직접 데이터가 넘어오는 경우는 @RequestParam , @ModelAttribute 를 사용할 수 없다. (물론 HTML Form 형식으로 전달되는 경우는
요청 파라미터로 인정된다.)

- HTTP 메시지 바디의 데이터를 InputStream 을 사용해서 직접 읽을 수 있다.

```java
/**
 * InputStream(Reader): HTTP 요청 메시지 바디의 내용을 직접 조회
 * OutputStream(Writer): HTTP 응답 메시지의 바디에 직접 결과 출력
 */
@PostMapping("/request-body-string-v2")
public void requestBodyStringV2(InputStream inputStream, Writer responseWriter)
        throws IOException {
    String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
    log.info("messageBody={}", messageBody);
    responseWriter.write("ok");
}
```

스프링 MVC는 다음 파라미터를 지원한다.

- InputStream(Reader): HTTP 요청 메시지 바디의 내용을 직접 조회
- OutputStream(Writer): HTTP 응답 메시지의 바디에 직접 결과 출력

```java
/**
 * HttpEntity: HTTP header, body 정보를 편라하게 조회
 * - 메시지 바디 정보를 직접 조회(@RequestParam X, @ModelAttribute X)
 * - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
 *
 * 응답에서도 HttpEntity 사용 가능
 * - 메시지 바디 정보 직접 반환(view 조회X)
 * - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
 */
@PostMapping("/request-body-string-v3")
public HttpEntity<String> requestBodyStringV3(HttpEntity<String> httpEntity) {
    String messageBody = httpEntity.getBody();
    log.info("messageBody={}", messageBody);
    return new HttpEntity<>("ok");
}
```

스프링 MVC는 다음 파라미터를 지원한다.

- HttpEntity: HTTP header, body 정보를 편리하게 조회
  - 메시지 바디 정보를 직접 조회
  - 요청 파라미터를 조회하는 기능과 관계 없음 @RequestParam X, @ModelAttribute X
- HttpEntity는 응답에도 사용 가능
  - 메시지 바디 정보 직접 반환
  - 헤더 정보 포함 가능
  - view 조회 X
- HttpEntity 를 상속받은 다음 객체들도 같은 기능을 제공한다.
- RequestEntity
  - HttpMethod, url 정보가 추가, 요청에서 사용
- ResponseEntity
  - HTTP 상태 코드 설정 가능, 응답에서 사용
  - return new ResponseEntity<String>("Hello World", responseHeaders, HttpStatus.CREATED)
  
> 스프링 MVC 내부에서 HTTP 메시지 바디를 읽어서 문자나 객체로 변환해서 전달해주는데, 이때 HTTP 
메시지 컨버터(`HttpMessageConverter`)라는 기능을 사용한다. 이것은 조금 뒤에 HTTP 메시지
컨버터에서 자세히 설명한다.
  
```java
/**
 * @RequestBody
 * - 메시지 바디 정보를 직접 조회(@RequestParam X, @ModelAttribute X)
 * - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
 *
 * @ResponseBody
 * - 메시지 바디 정보 직접 반환(view 조회X)
 * - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
 */
@ResponseBody
@PostMapping("/request-body-string-v4")
public String requestBodyStringV4(@RequestBody String messageBody) {
    log.info("messageBody={}", messageBody);
    return "ok";
}
```
  
 - @RequestBody
  - @RequestBody 를 사용하면 HTTP 메시지 바디 정보를 편리하게 조회할 수 있다. 참고로 헤더 정보가 필요하다면 HttpEntity 를 사용하거나 @RequestHeader 를 사용하면 된다.
이렇게 메시지 바디를 직접 조회하는 기능은 요청 파라미터를 조회하는 @RequestParam, @ModelAttribute 와는 전혀 관계가 없다.
- 요청 파라미터 vs HTTP 메시지 바디
  - 요청 파라미터를 조회하는 기능: @RequestParam , @ModelAttribute
  - HTTP 메시지 바디를 직접 조회하는 기능: @RequestBody
- @ResponseBody
  - @ResponseBody 를 사용하면 응답 결과를 HTTP 메시지 바디에 직접 담아서 전달할 수 있다. 물론 이 경우에도 view 를 사용하지 않는다.
  
### HTTP 요청 메시지 - JSON
  
```java
  /**
 * {"username":"hello", "age":20}
 * content-type: application/json
 */
@Slf4j
@Controller
public class RequestBodyJsonController {
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @PostMapping("/request-body-json-v1")
    public void requestBodyJsonV1(HttpServletRequest request,
                                  HttpServletResponse response) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        log.info("messageBody={}", messageBody);
        HelloData data = objectMapper.readValue(messageBody, HelloData.class);
        log.info("username={}, age={}", data.getUsername(), data.getAge());
        response.getWriter().write("ok");
    }
}
```
  
- HttpServletRequest 를 사용해서 직접 HTTP 메시지 바디에서 데이터를 읽어와서, 문자로 변환한다.
- 문자로 된 JSON 데이터를 Jackson 라이브러리인 objectMapper 를 사용해서 자바 객체로 변환한다.
  
```java
/**
 * @RequestBody
 * HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
 *
 * @ResponseBody
 * - 모든 메서드에 @ResponseBody 적용
 * - 메시지 바디 정보 직접 반환(view 조회X)
 * - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
 */
@ResponseBody
@PostMapping("/request-body-json-v2")
public String requestBodyJsonV2(@RequestBody String messageBody) throws
        IOException {
    HelloData data = objectMapper.readValue(messageBody, HelloData.class);
    log.info("username={}, age={}", data.getUsername(), data.getAge());
    return "ok";
}
```
  
문자로 변환하고 다시 json으로 변환하는 과정이 불편하다. @ModelAttribute처럼 한번에 객체로 변환할 수는 없을까?
  
- @RequestBody 객체 변환
  
```java
/**
 * @RequestBody 생략 불가능(@ModelAttribute 가 적용되어 버림)
 * HttpMessageConverter 사용 -> MappingJackson2HttpMessageConverter (content-type: application/json)
 */
@ResponseBody
@PostMapping("/request-body-json-v3")
public String requestBodyJsonV3(@RequestBody HelloData data) {
    log.info("username={}, age={}", data.getUsername(), data.getAge());
    return "ok";
}
```
  
- @RequestBody 객체 파라미터
  - @RequestBody HelloData data
  - @RequestBody 에 직접 만든 객체를 지정할 수 있다.

HttpEntity, @RequestBody 를 사용하면 HTTP 메시지 컨버터가 HTTP 메시지 바디의 내용을 우리가 원하는 문자나 객체 등으로 변환해준다.

HTTP 메시지 컨버터는 문자 뿐만 아니라 JSON도 객체로 변환해주는데, 우리가 방금 V2에서 했던 작업을 대신 처리해준다. 자세한 내용은 뒤에 HTTP 메시지 컨버터에서 다룬다.

- @RequestBody는 생략 불가능

@ModelAttribute 에서 학습한 내용을 떠올려보자.
스프링은 @ModelAttribute , @RequestParam 해당 생략시 다음과 같은 규칙을 적용한다.

- String, int, Integer 같은 단순 타입 = @RequestParam
- 나머지 = @ModelAttribute (argument resolver 로 지정해둔 타입 외)
  
따라서 이 경우 HelloData에 @RequestBody 를 생략하면 @ModelAttribute 가 적용되어버린다.
HelloData data @ModelAttribute HelloData data
따라서 생략하면 HTTP 메시지 바디가 아니라 요청 파라미터를 처리하게 된다.
  
> HTTP 요청시에 content-type이 application/json인지 꼭! 확인해야 한다. 그래야 JSON을 처리할 수 있는 HTTP 메시지 컨버터가 실행된다.
  
물론 앞서 배운 것과 같이 HttpEntity를 사용해도 된다.
  
```java
@ResponseBody
@PostMapping("/request-body-json-v4")
public String requestBodyJsonV4(HttpEntity<HelloData> httpEntity) {
    HelloData data = httpEntity.getBody();
    log.info("username={}, age={}", data.getUsername(), data.getAge());
    return "ok";
}
```

```java
/**
 * @RequestBody 생략 불가능(@ModelAttribute 가 적용되어 버림)
 * HttpMessageConverter 사용 -> MappingJackson2HttpMessageConverter (content-type: application/json)
 *
 * @ResponseBody 적용
 * - 메시지 바디 정보 직접 반환(view 조회X)
 * - HttpMessageConverter 사용 -> MappingJackson2HttpMessageConverter 적용
(Accept: application/json)
 */
@ResponseBody
@PostMapping("/request-body-json-v5")
public HelloData requestBodyJsonV5(@RequestBody HelloData data) {
    log.info("username={}, age={}", data.getUsername(), data.getAge());
    return data;
}
```
  
- @ResponseBody
  - 응답의 경우에도 @ResponseBody 를 사용하면 해당 객체를 HTTP 메시지 바디에 직접 넣어줄 수 있다. 물론 이 경우에도 HttpEntity 를 사용해도 된다.
- @RequestBody 요청
  - `JSON 요청 > HTTP 메시지 컨버터 > 객체`
- @ResponseBody 응답
  - `객체 > HTTP 메시지 컨버터 > JSON 응답`
  
## HTTP 응답 - 정적 리소스, 뷰 템플릿

- 정적 리소스
  - Ex. 웹 브라우저에 정적인 HTML, css, js를 제공할 때는, 정적 리소스를 사용한다.
- 뷰 템플릿 사용
  - Ex. 웹 브라우저에 동적인 HTML을 제공할 때는 뷰 템플릿을 사용한다.
- HTTP 메시지 사용
  - HTTP API를 제공하는 경우에는 HTML이 아니라 데이터를 전달해야 하므로, HTTP 메시지 바디에 JSON 같은 형식으로 데이터를 실어 보낸다.  
  
#### 정적 리소스

스프링 부트는 클래스패스의 다음 디렉토리에 있는 정적 리소스를 제공한다.

`/static , /public , /resources , /META-INF/resources`
  
`src/main/resources` 는 리소스를 보관하는 곳이고, 또 클래스패스의 시작 경로이다.
따라서 다음 디렉토리에 리소스를 넣어두면 스프링 부트가 정적 리소스로 서비스를 제공한다.
  
- 정적 리소스 경로
  - src/main/resources/static 다음 경로에 파일이 들어있으면
  - src/main/resources/static/basic/hello-form.html 웹 브라우저에서 다음과 같이 실행하면 된다.
  - http://localhost:8080/basic/hello-form.html
  - 정적 리소스는 해당 파일을 변경 없이 그대로 서비스하는 것이다.
  
