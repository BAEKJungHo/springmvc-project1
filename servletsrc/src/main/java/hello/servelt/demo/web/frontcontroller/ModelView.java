package hello.servelt.demo.web.frontcontroller;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
public class ModelView {

    private String viewName;
    public Map<String, Object> model;

    public ModelView(String viewName) {
        this.viewName = viewName;
    }

}
