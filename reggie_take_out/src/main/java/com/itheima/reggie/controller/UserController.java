package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
//默认通过名称注入，如名称无法找到，则通过类型注入 名称：userService,类型UserService
    @Autowired
    private UserService userService;
//默认通过类型注入，如存在多个类型则通过名称注入
    @Resource
    private JavaMailSender javaMailSender;//我们需要用这个进行邮件发送
    //注意这里我们将发送者从配置文件注入进来
    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    private R<String> sendMsg(@RequestBody User user, HttpSession session){
        //这里用163邮箱去发送验证码
        //获取到前端提交过来的邮箱帐号
        String phone = user.getPhone();
        //这里工具类判是否为空
        if(StringUtils.isNotEmpty(phone)){
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);

            //构建一个邮件的对象
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            // 设置邮件发件者
            simpleMailMessage.setFrom(from);
            //设置邮件接受者
            simpleMailMessage.setTo(phone);
            //设置有纪念的主题
            simpleMailMessage.setSubject("登录验证码");
            //设置邮件的征文
            String text = "验证码测试，验证码为" + code + "是否收到";
            simpleMailMessage.setText(text);

            //将生成的验证码保存到Session
            //将我们生成的手机号和验证码放到session里面，我们后面用户填入验证码之后，我们验证的时候就从这里去取然后进行比对
            //这里我们需要一个异常捕获
            session.setAttribute(phone, code);
            //return R_.success("手机验证码短信发送成功");
            try {
                //javaMailSender.send(simpleMailMessage);
                return R.success("手机验证码短信发送成功");
            } catch (MailException e) {
                e.printStackTrace();
            }
        }

        return R.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    //这里使用map来接收前端传过来的值
    private R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
//        使用map来接收参数,接收键值参数、
//        编写处理逻辑
//        获取到手机号
//        获取到验证码
//        从Session中获取到保存的验证码
//     将session中获取到的验证码和前端提交过来的验证码进行比较，这样就可以实现一个验证的方式
//        比对页面提交的验证码和session中
//判断当前的手机号在数据库查询是否有记录，如果没有记录，说明是一个新的用户，然后自动将这个手机号进行注册
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
//获取session中phone字段对应的验证码
        Object codeInSession = session.getAttribute(phone);
        //进行验证码的比对（页面提交的验证码和Session中保存的验证码比对）
        if(codeInSession != null && codeInSession.equals(code)){
            //如果能够比对成功，说明登录成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
 //         在表中根据号码来查询是否存在该邮箱用户
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if(user == null){
                //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
//          这里我们将user存储进去，后面各项操作，我们会用，其中拦截器那边会判断用户是否登录，所以我们将这个存储进去，
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }
}
