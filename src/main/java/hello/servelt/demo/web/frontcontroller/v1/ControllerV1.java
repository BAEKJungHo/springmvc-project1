package hello.servelt.demo.web.frontcontroller.v1;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ControllerV1 {

    // servlet 의 service 와 똑같은 모양의 메서드 생성
    void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

}
