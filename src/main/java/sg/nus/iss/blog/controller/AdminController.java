package sg.nus.iss.blog.controller;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import sg.nus.iss.blog.model.Blog;
import sg.nus.iss.blog.model.BlogHistory;
import sg.nus.iss.blog.model.BlogUser;
import sg.nus.iss.blog.service.BlogHistoryService;
import sg.nus.iss.blog.service.BlogService;
import sg.nus.iss.blog.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private final int launchedYear = 2023;
    private final Month launchedMonth = Month.DECEMBER;
    private final DateTimeFormatter df1 = DateTimeFormatter.ofPattern("MMM yyyy");

    @Autowired
    UserService userService;
    @Autowired
    BlogService blogService;
    @Autowired
    BlogHistoryService blogHistoryService;

    // display main page after admin login
    @GetMapping("/main")
    public String displayDashboardMain(Model model) {
        
        LocalDate today = LocalDate.now();
        LocalDate firstDateOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        long numberOfDaysThisMonth = ChronoUnit.DAYS.between(firstDateOfMonth, today) + 1;

        String[] xData = new String[(int) numberOfDaysThisMonth];
        for (int i = 0; i < numberOfDaysThisMonth; i++) {
            DateTimeFormatter df2 = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            xData[i] = firstDateOfMonth.plusDays(i).format(df2);
        }
        
        int[] yData = new int[(int) numberOfDaysThisMonth];
        List<Blog> blogposts = blogService.findAllPostedBlog();
        for (int i = 0; i < numberOfDaysThisMonth; i++) {
            int postCounts = 0;
            for (Blog b: blogposts) {
                DateTimeFormatter df1 = DateTimeFormatter.ofPattern ("MMM dd, yyyy");
                LocalDate blogTime = LocalDate.parse(b.getBlogTime(), df1);
                if (blogTime.isEqual(firstDateOfMonth.plusDays(i))) {
                    postCounts += 1;
                }
            }
            yData[i] = postCounts;
        }
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        for (String a: xData){
            System.out.println(a);
        }
        for (int b: yData){
            System.out.println(b);
        }

        LocalDate lastDayOfMonth = getPreviousMonth(LocalDate.now());
        //header
        model.addAttribute("allMonths", getAllMonths());
        model.addAttribute("month", YearMonth.from(lastDayOfMonth).format(df1));
        //blogpost
        model.addAttribute("xData", xData);
        model.addAttribute("yData", yData);
        //user
        model.addAttribute("monthActiveUsers", monthActiveUser(today));
        model.addAttribute("monthUserGrowth", monthUserGrowth(today));
        model.addAttribute("monthPostReadCount", monthPostReadCount(today));
        return "admin-dashboard";
    }

    // display admin selected month
    @GetMapping("/main/{m}")
    public String displayDashboardPast(@PathVariable("m") String monthSelected, Model model) {
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaa"+monthSelected);
        DateTimeFormatter df2 = DateTimeFormatter.ofPattern("MMM yyyy");
        YearMonth yearMonth = YearMonth.parse(monthSelected, df2);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaa"+lastDayOfMonth);
        //header
        model.addAttribute("allMonths", getAllMonths());
        model.addAttribute("month", YearMonth.from(lastDayOfMonth).format(df1));
        //blogpost
        model.addAttribute("xData", datesOfMonth(lastDayOfMonth));
        model.addAttribute("yData", monthBlogPostPerDay(lastDayOfMonth));
        //user
        model.addAttribute("monthActiveUsers", monthActiveUser(lastDayOfMonth));
        model.addAttribute("monthUserGrowth", monthUserGrowth(lastDayOfMonth));
        model.addAttribute("monthPostReadCount", monthPostReadCount(lastDayOfMonth));
        return "admin-dashboard";
    }

    public LocalDate getPreviousMonth(LocalDate date) {
        // dashboard data cut off date is end of last month
        // get the first day of the month and then subtract one day to get the last day of the previous month
        return date.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
    }

    // all months since the our application is launched
    public List<String> getAllMonths() {
        List<String> months = new ArrayList<>();
        LocalDate lastMonth = LocalDate.now();
        LocalDate launchedDate = LocalDate.of(launchedYear, launchedMonth, 1);
        
        while (YearMonth.from(launchedDate).isBefore(YearMonth.from(lastMonth)) || YearMonth.from(launchedDate).equals(YearMonth.from(lastMonth))) {
            if (!months.contains(YearMonth.from(launchedDate).format(df1))) {
                months.add(YearMonth.from(launchedDate).format(df1));
            }
            launchedDate = launchedDate.plusMonths(1);
        }
        return months;
    }

    // for x-axis of Daily Blog Post graph
    public String[] datesOfMonth(LocalDate lastDayOfMonth) {
        YearMonth yearMonth = YearMonth.from(lastDayOfMonth);
        String[] xData = new String[yearMonth.lengthOfMonth()];
        for (int i = 0; i < yearMonth.lengthOfMonth(); i++) {
            DateTimeFormatter df2 = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            xData[i] = lastDayOfMonth.minusDays(yearMonth.lengthOfMonth() - 1 - i).format(df2);
        }
        return xData;
    }

    // for y-axis of Daily Blog Post graph
    public int[] monthBlogPostPerDay(LocalDate lastDayOfMonth) {
        List<Blog> blogposts = blogService.findAllPostedBlog();

        YearMonth yearMonth = YearMonth.from(lastDayOfMonth);
        int[] yData = new int[yearMonth.lengthOfMonth()];
        for (int i = 0; i < yearMonth.lengthOfMonth(); i++) {
            int postCounts = 0;
            for (Blog b: blogposts) {
                DateTimeFormatter df1 = DateTimeFormatter.ofPattern ("MMM dd, yyyy");
                LocalDate blogTime = LocalDate.parse(b.getBlogTime(), df1);
                if (blogTime.isEqual(lastDayOfMonth.minusDays(yearMonth.lengthOfMonth() - i))) {
                    postCounts += 1;
                }
            }
            yData[i] = postCounts;
        }
        return yData;
    }

    // number of active users as at the selected month
    public int monthActiveUser(LocalDate lastDayOfMonth) {
        int activeUsers = 0;
        for (BlogUser u: userService.findAllActiveUsers()) {
            if(u.getSignupTime().isBefore(lastDayOfMonth) || u.getSignupTime().isEqual(lastDayOfMonth)) {
                activeUsers += 1;
            }
        }
        return activeUsers;
    }

    // users growth as compared to the previous month
    public String monthUserGrowth(LocalDate lastDayOfMonth) {
        LocalDate previousMonth = getPreviousMonth(lastDayOfMonth);
        List<BlogUser> users = userService.findAllActiveUsers();

        List<BlogUser> usersMonth = new ArrayList<>();
        List<BlogUser> usersPreviousMonth = new ArrayList<>();
        for (BlogUser u: users) {
            if (u.getSignupTime().isBefore(lastDayOfMonth) || u.getSignupTime().isEqual(lastDayOfMonth)) {
                usersMonth.add(u);
            }
            if (u.getSignupTime().isBefore(previousMonth) || u.getSignupTime().isEqual(previousMonth)) {
                usersPreviousMonth.add(u);
            }
        }
        double monthUserGrowth = usersPreviousMonth.size() != 0 ? (double) (usersMonth.size() - usersPreviousMonth.size()) / usersPreviousMonth.size() * 100 : 0;
        String formattedGrowth = String.format("%.2f%%", monthUserGrowth);
        return formattedGrowth;
    }

    // total number of times user read a blog post for the selected month
    public int monthPostReadCount(LocalDate lastDayOfMonth) {
        LocalDate previousMonth = getPreviousMonth(lastDayOfMonth);
        List<BlogHistory> histories = blogHistoryService.findAllBlogHistories();

        List<BlogHistory> historiesOfMonth = new ArrayList<>();
        for (BlogHistory h: histories) {
            if (h.getReadDate().isBefore(lastDayOfMonth) || h.getReadDate().isEqual(lastDayOfMonth) && h.getReadDate().isAfter(previousMonth)) {
                historiesOfMonth.add(h);
            }
        }
        return historiesOfMonth.size();
    }
}