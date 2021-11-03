package hello.servelt.demo.web.frontcontroller.v3;

import hello.servelt.demo.web.frontcontroller.ModelView;

import java.util.Map;

public interface ControllerV3 {

    // v2 에 있던 서블릿의 종속성을 제거
   ModelView process(Map<String, String> paramMap);

}
