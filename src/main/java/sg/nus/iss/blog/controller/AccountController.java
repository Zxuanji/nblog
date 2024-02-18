package sg.nus.iss.blog.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import sg.nus.iss.blog.model.BlogUserStatusEnum;
import sg.nus.iss.blog.model.BlogUser;
import sg.nus.iss.blog.model.EmailDetails;
import sg.nus.iss.blog.service.EmailService;
import sg.nus.iss.blog.service.UserService;
import sg.nus.iss.blog.validator.AccountValidator;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/account")
public class AccountController {
    
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AccountValidator accountValidator;
    
    @InitBinder
    private void initCourseBinder(WebDataBinder binder) {
        binder.addValidators(accountValidator);
    }

    @GetMapping("/create")
    public String createAccount(Model model) {
        model.addAttribute("user", new BlogUser());

        return "account-create";
    }

    @PostMapping("/create")
    public String createAccount(@Valid @ModelAttribute("user") BlogUser inUser, BindingResult bindingResult, HttpSession sessionObj, Model model) {
        if (bindingResult.hasErrors()) {
            return "account-create";
        }
        else {
            inUser.setSignupTime(LocalDate.now());
            inUser.setUserStatus(BlogUserStatusEnum.ACTIVE);
            userService.createUser(inUser);
            sessionObj.setAttribute("activeUser", inUser);
            EmailDetails emailDetails = new EmailDetails(inUser.getEmail(), "Welcome to NBlog, "+inUser.getDisplayName()+"!", "Have a rewarding time exploring NBlog!");
            emailService.sendEmail(emailDetails); 
            return "redirect:/home/list";
        }
    }

    @GetMapping("/login")
    public String login(Model model, HttpSession sessionObj) {
        BlogUser activeUser = (BlogUser) sessionObj.getAttribute("activeUser");
        if (activeUser == null) {
            model.addAttribute("user", new BlogUser());
            return "account-login";
        }
        else {
            return "redirect:/home/list";
        }
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user") BlogUser inUser, HttpSession sessionObj, Model model) {
        List<BlogUser> users = userService.findAllUsers();
        boolean validAccount = false;
        for (BlogUser u: users) {
            if (inUser.getEmail().equals(u.getEmail()) && inUser.getPassword().equals(u.getPassword())) {
                validAccount = true;
                sessionObj.setAttribute("activeUser", u);
            }
        }

        if (validAccount == true) {
            return "redirect:/home/list"; //change to redirect
        }
        else {
            model.addAttribute("invalidAccount", "The e-mail address and/or password you specified are not correct.");
            return "account-login";
        }
    }

    @GetMapping("/logout")
	public String logout(HttpSession sessionObj) {
		sessionObj.invalidate();
		return "redirect:/nblog";
	}

}
