package com.itbulls.learnit.controllers.userprofile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.itbulls.learnit.facades.FlightFacade;
import com.itbulls.learnit.facades.OrderFacade;
import com.itbulls.learnit.pojos.Order;
import com.itbulls.learnit.pojos.User;

@WebServlet(name = "MyFlights", urlPatterns = {"/dashboard/book/flight", "/profile/myflights", "/profile/myflights/flight"}, loadOnStartup = 1)
public class MyFlights extends HttpServlet {
	private static final long serialVersionUID = 1L;
	FlightFacade flightFacade = new FlightFacade();
	OrderFacade orderFacade = new OrderFacade();
	HttpSession session;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		List<Order> orderslist = orderFacade.getOrdersByUserId(user.getId()).stream().filter(o -> Objects.nonNull(o)).filter(o -> o.getFlightId()!= 0).collect(Collectors.toList());
		String json = new Gson().toJson(orderslist);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int userId = Integer.parseInt(request.getParameter("user"));
		int flightlId = Integer.parseInt(request.getParameter("id"));
		session = request.getSession();
		Integer orderId = (Integer) session.getAttribute("orderId");
		if (orderId == null) {
			orderId = orderFacade.createOrder(userId);
			session.setAttribute("order", orderId);
		}
		flightFacade.orderFlight(orderId, flightlId);
	}
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		int orderId = Integer.parseInt(req.getParameter("orderid"));
		flightFacade.deleteOrderedFlight(orderId);

	}

}
