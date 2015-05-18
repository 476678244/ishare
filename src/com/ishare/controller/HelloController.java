package com.ishare.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ishare.bean.SitePointBean;
import com.ishare.bean.UserBean;
import com.ishare.bean.enums.RoleEnum;
import com.ishare.service.InfoService;
import com.ishare.service.InformationPrepareService;
import com.ishare.service.dao.MysqlDAO;
import com.ishare.test.Main;
import com.ishare.test.service.PoolServiceTest;
import com.ishare.util.PropertyUtil;
import com.ishare.util.TransformerUtil;

@Controller
@Component("helloController")
@RequestMapping("/request/hello")
public class HelloController {

	public final static Logger logger = LoggerFactory
			.getLogger(HelloController.class);

	@Autowired
	InformationPrepareService informationPrepareService;

	@Autowired
	PoolController pooController;

	@Autowired
	InfoService infoService;

	@Autowired
	@Qualifier("mysqlDAO")
	MysqlDAO mysqlDAO;

	@RequestMapping(value = "/register")
	@ResponseBody
	public long register(HttpSession session) {
		UserBean user = new UserBean();
		user.setUsername("zonghan1");
		user.setPassword("pwd");
		user.setNickname("zwu");
		user.setRole(RoleEnum.PASSENGER.getValue());
		return this.infoService.regiserUser(user);
	}

	@RequestMapping(value = "/regStraight")
	@ResponseBody
	public long regStraight(HttpSession session) {
		UserBean user = new UserBean();
		user.setUsername("zonghan1");
		user.setPassword("pwd");
		user.setNickname("zwu");
		user.setRole(RoleEnum.PASSENGER.getValue());
		return this.infoService.regiser(user);
	}

	@RequestMapping(value = "/get")
	@ResponseBody
	public String getHelloValue(HttpSession session) {
		session.setAttribute("sessionId", session.getId());
		return "hello world!";
		// return mysqlDAO.getHello();
	}

	@RequestMapping(value = "/auth")
	@ResponseBody
	public String auth(HttpSession session) {
		String sessionId = (String) (session.getAttribute("sessionId"));
		if (sessionId == session.getId()) {
			logger.info("request authoried!");
		}
		return sessionId;
	}

	@RequestMapping(value = "/token")
	@ResponseBody
	public String generateToken(HttpSession session) {
		String sessionId = session.getId();
		return sessionId;
	}

	@RequestMapping(value = "/json")
	@ResponseBody
	public Point getJson(HttpSession session) {
		Point point = new Point();
		return point;
	}

	class Point {
		public int x = 10;
		public int y = 20;
		public String name = "Point";
		public List<String> names = new ArrayList<String>();

		Point() {
			names.add("point1");
			names.add("point2");
		}
	}

	@RequestMapping(value = "/deleteAll")
	@ResponseBody
	public void deleteData() {
		Main.deleteData();
	}

	@RequestMapping(value = "/createOrder")
	@ResponseBody
	public long createOrder() throws Exception {
		Main.refreshData();
		PoolServiceTest service = new PoolServiceTest();
		long orderId = service.createOrder();
		return orderId;
	}

	@RequestMapping(value = "/createOrder2")
	@ResponseBody
	public long createOrder2() throws Exception {
		PoolServiceTest service = new PoolServiceTest();
		long orderId = service.createOrder();
		return orderId;
	}

	@RequestMapping(value = "/intoJoinOrder")
	@ResponseBody
	public void intoJoinOrder() throws Exception {
		long orderId = 1;
		long userId = 1;
		SitePointBean end = new SitePointBean();
		end.setLongtitude(1);
		end.setLaitude(1);
		end.setAddress("1");
		this.pooController.joinOrder(userId, orderId, 1,
				TransformerUtil.SitePointBeanToJsonString(end), "token0");
	}

	@RequestMapping(value = "/getConcurrencyUtil")
	@ResponseBody
	public void getConcurrencyUtil() throws Exception {
		long orderId = 1;
		long userId = 3;
		SitePointBean end = new SitePointBean();
		end.setLongtitude(1);
		end.setLaitude(1);
		end.setAddress("1");
		this.pooController.joinOrder(userId, orderId, 1,
				TransformerUtil.SitePointBeanToJsonString(end), "token2");
	}

	@RequestMapping(value = "uploadfile", method = { RequestMethod.POST })
	@ResponseBody
	public String uploadImages(
			@RequestParam(value = "file", required = false) MultipartFile file,
			HttpServletRequest request, ModelMap model) {
		String picDir = PropertyUtil.readProperty("savePicUrl");
		String fileName = file.getOriginalFilename();
		File targetFile = new File(picDir, fileName);
		try {
			file.transferTo(targetFile);
		} catch (IllegalStateException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return "uploadSuccess";
	}
}
