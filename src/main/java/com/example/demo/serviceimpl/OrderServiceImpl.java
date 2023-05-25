package com.example.demo.serviceimpl;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.dao.IOrderDao;
import com.example.demo.dao.IOrderItemDao;
import com.example.demo.dao.IProductDao;
import com.example.demo.dao.IStockDao;
import com.example.demo.dao.IUSerDao;
import com.example.demo.dao.IWareTransactionDao;
import com.example.demo.dao.InvoiceDao;
import com.example.demo.dto.request.OrderItemDTO;
import com.example.demo.dto.request.OrderRequestDTO;
import com.example.demo.dto.response.OrderDTO;
import com.example.demo.entity.Invoice;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.Stock;
import com.example.demo.entity.User;
import com.example.demo.entity.WareTransaction;
import com.example.demo.entity.WareTransactionDetail;
import com.example.demo.enums.InvoiceStatusType;
import com.example.demo.enums.OrderStatusType;
import com.example.demo.enums.OrderType;
import com.example.demo.enums.ProductStatus;
import com.example.demo.enums.WareTransactionType;
import com.example.demo.jwt.JWTFilter;
import com.example.demo.service.IOrderService;
import com.example.demo.utils.CafeUtils;
import com.example.demo.utils.DateUtils;
import com.example.demo.utils.EmailUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class OrderServiceImpl implements IOrderService {

	@Autowired
	private IOrderDao orderDao;

	@Autowired
	private InvoiceDao invoiceDao;

	@Autowired
	private EmailUtils emailUtils;

	@Autowired
	private IUSerDao userDao;

	@Autowired
	private IProductDao productDao;

	@Autowired
	private IOrderItemDao oderItemDao;

	@Autowired
	private IStockDao stockDao;

	@Autowired
	private JWTFilter jwtFilter;

	@Autowired
	DateUtils dateUtils;

	@Autowired
	private IWareTransactionDao wareTransactionDao;

	@Override
	public OrderDTO findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OrderDTO> findAll() {

		return orderDao.findAll().stream().map(order -> new OrderDTO(order)).collect(Collectors.toList());
	}

	public User getCuurentUser(String userName) {
		return userDao.findByUserName(userName).orElse(null);
	}

	@Override
	@Transactional
	public ResponseEntity<?> newOrder(OrderRequestDTO orderDTO) {

		validateOrderDTO(orderDTO);

		Order newOrder = new Order();
		BeanUtils.copyProperties(orderDTO, newOrder);

		newOrder.setTypeOrder(orderDTO.getOrderType());
		newOrder.setStatus(OrderStatusType.Shipping);

		Optional<User> user = userDao.findByUserName(orderDTO.getUser().getUserName());

		String userName = jwtFilter.getCurrentUser();
		User createTransactionUser = getCuurentUser(userName);

		if (user.isPresent() && !user.get().getStatus()) {
			return CafeUtils.getResponseData("User is not active", HttpStatus.BAD_REQUEST, null);
		}

		log.info("test User " + user);
		newOrder.setUser(user.isEmpty() ? createTransactionUser : user.get());

		newOrder.setOrderItem(new ArrayList<>(orderDTO.getQuantity()));
		newOrder.setOrderDate(dateUtils.convertDateToLocalDateTime(new Date()));

		List<Stock> stocksToRemove = new ArrayList<>();

		Product product = productDao.findByProductCode(orderDTO.getProductCode());
		System.out.println("asdaddsad " + product);
		if (product == null) {
			return CafeUtils.getResponse("Product Not Found", HttpStatus.NOT_FOUND);
		} else if (product.getListStock().size() < orderDTO.getQuantity()) {
			log.info("list stock " + product.getListStock().size());
			return CafeUtils.getResponseData("Product out of stock", HttpStatus.BAD_REQUEST, null);
		}

		double totalAmount = 0.0;

		for (int i = 1; i <= orderDTO.getQuantity(); i++) {
			OrderItem orderItem = new OrderItem();
			orderItem.setQuantity(i);
			orderItem.setOrderId(newOrder);
			orderItem.setProductId(product);

			newOrder.getOrderItem().add(orderItem);

			double itemTotal = product.getPrice().doubleValue();

			totalAmount += itemTotal;

			Stock stock = product.getListStock().remove(0);
			stocksToRemove.add(stock);
		}

		WareTransaction wareTransaction = new WareTransaction();
		wareTransaction.setUser(user.isEmpty() ? createTransactionUser : user.get());
		wareTransaction.setTransactionDate(dateUtils.convertDateToLocalDateTime(new Date()));
		wareTransaction.setTransactionType(WareTransactionType.EXPORT);
		wareTransaction.setTransactionDetails(new ArrayList<>());

		List<WareTransactionDetail> transactionDetails = new ArrayList<>();

		for (Stock stock : stocksToRemove) {
			WareTransactionDetail detail = new WareTransactionDetail();
			detail.setWareTransaction(wareTransaction);
			detail.setStock(stock);

			transactionDetails.add(detail);
		}

		wareTransaction.setTransactionDetails(transactionDetails);

		for (Stock stock : stocksToRemove) {
			stockDao.delete(stock);
		}

//		stockDao.deleteAll(stocksToRemove);

		if (product.getListStock().isEmpty()) {
			product.setProductStatus(ProductStatus.OUT_OF_STOCK);
		}

		productDao.save(product);

		Order savedOrder = orderDao.save(newOrder);

		BigDecimal discount = orderDTO.getDiscount();

		sendOrderConfirmationEmail(savedOrder, totalAmount, discount, createTransactionUser); // Send order confirmation
																								// email

		OrderDTO newOrderDTO = new OrderDTO(savedOrder);

		return CafeUtils.getResponseData("Create order successfully", HttpStatus.OK, newOrderDTO);

	}

	private void sendOrderConfirmationEmail(Order order, double totalValue, BigDecimal discount, User user) {
//		emailUtils.sendOrderConfirmationEmail(order.getUser().getEmail(), String.valueOf(order.getId()),
//				String.valueOf(order.getStatus()));

		// If the user accepts the order, update the order status to Delivered
		// and create an invoice for the order
		boolean isAccepted = true; // Assume the user accepts the order for simplicity
		if (isAccepted) {
			order.setStatus(OrderStatusType.Delivered);
			orderDao.save(order);

			Invoice newInvoice = new Invoice();
			newInvoice.setOrder(order);
			// PENDING because the customer has not confirmed the payment.
			// When the customer confirms the payment by calling the Payment API.
			// The status of that Invoice will change to "PROCESSED"
			newInvoice.setStatus(InvoiceStatusType.PENDING);
			newInvoice.setDate(dateUtils.convertDateToLocalDateTime(new Date()));

			// Set payment deadline is 7 days from the time of invoice creation
			LocalDateTime localDateTime = LocalDateTime.now();

			Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
			Date date = Date.from(instant);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.DAY_OF_YEAR, 7);
			newInvoice.setDueDate(dateUtils.convertDateToLocalDateTime(calendar.getTime()));

			if (discount == null) {
				BigDecimal newAmonut = new BigDecimal(Double.toString(totalValue));
				newInvoice.setTotal(newAmonut);
				newInvoice.setDiscount(BigDecimal.ZERO);
			} else {
				BigDecimal newAmonut = new BigDecimal(Double.toString(totalValue));
				newInvoice.setTotal(newAmonut.multiply(discount));
				newInvoice.setDiscount(discount);
			}
			newInvoice.setUser(user);
			newInvoice.setStatus(InvoiceStatusType.PROCESSED);
			BigDecimal taxValue = new BigDecimal("0551004");
			newInvoice.setTax(taxValue);

			Invoice savedInvoice = invoiceDao.save(newInvoice);
			order.addInvoice(savedInvoice);
		}
	}

	@Override
	public void processOrder(Long orderId) {
		Order order = orderDao.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order Not Found"));

		if (order.getStatus() == OrderStatusType.Processed || order.getInvoices() != null) {
			return;
		}

//		sendOrderConfirmationEmail(order);
	}

	@Override
	public void cancelOrder(Long orderId) {
		Order order = orderDao.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found"));

		if (order.getStatus() == OrderStatusType.Processed || order.getStatus() == OrderStatusType.Cancellation) {
			return;
		}
		order.setStatus(OrderStatusType.Cancellation);

		orderDao.save(order);
	}

	@Override
	public List<OrderDTO> listOrderByOrderDate(Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<?> updateStatusOrder(Map<String, String> request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<?> updateInforOrder(OrderDTO orderDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	private void validateOrderDTO(OrderRequestDTO orderDTO) {
		log.info("inside validate product", orderDTO);
		if (orderDTO.getProductCode() == null) {
			throw new IllegalArgumentException("ProductCode cannot be null. Please select an order status.");
		}
	}

}
