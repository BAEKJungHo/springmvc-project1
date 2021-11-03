package hello.servelt.demo.web.frontcontroller.v3.controller;

import hello.servelt.demo.domain.member.Member;
import hello.servelt.demo.domain.member.MemberRepository;
import hello.servelt.demo.web.frontcontroller.ModelView;
import hello.servelt.demo.web.frontcontroller.MyView;
import hello.servelt.demo.web.frontcontroller.v3.ControllerV3;

import java.util.List;
import java.util.Map;

public class MemberListControllerV3 implements ControllerV3 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public ModelView process(Map<String, String> paramMap) {
        List<Member> members = memberRepository.findAll();
        ModelView mv = new ModelView("members");
        mv.getModel().put("members", members);
        return mv;
    }
}
