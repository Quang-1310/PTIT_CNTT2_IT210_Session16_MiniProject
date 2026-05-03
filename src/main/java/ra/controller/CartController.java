package ra.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.model.dto.OrderDTO;
import ra.model.entity.CartItem;
import ra.model.entity.Order;
import ra.model.entity.Product;
import ra.model.entity.User;
import ra.repository.OrderRepository;
import ra.service.OrderService;
import ra.service.ProductService;
import ra.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable("id") Long id, RedirectAttributes redirectAttributes , HttpSession session){
        if (session.getAttribute("userLogin") == null){
            return "redirect:/auth/login";
        }
        Product product = productService.getProductById(id);
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null){
            cart = new ArrayList<>();
        }
        boolean checkFind = false;

        for(CartItem item : cart){
            if (item.getProduct().getProId().equals(product.getProId())){
                item.setQuantity(item.getQuantity() + 1);
                checkFind = true;
                break;
            }
        }
        if (!checkFind){
            cart.add(new CartItem(product , 1));
        }

        session.setAttribute("cart", cart);
        redirectAttributes.addFlashAttribute("inform", "Thêm vào giỏ hàng thành công.");
        return "redirect:/client/home";
    }

    @GetMapping("/listCart")
    public String listCart(HttpSession session , Model model){
        if (session.getAttribute("userLogin") == null){
            return "redirect:/auth/login";
        }
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null){
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        model.addAttribute("cart", cart);
        return "/client/cart";
    }

    @GetMapping("/delete/{id}")
    public String deleteCart(@PathVariable("id") Long id, HttpSession session){
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null){
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        for(CartItem item : cart){
            if (item.getProduct().getProId().equals(id)){
                cart.remove(item);
                break;
            }
        }
        return  "redirect:/client/home";
    }

    @GetMapping("/payment")
    public String payment(HttpSession session ,  Model model){
        if (session.getAttribute("userLogin") == null){
            return "redirect:/auth/login";
        }
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        model.addAttribute("cart", cart);
        model.addAttribute("orderDTO" , new OrderDTO());
        return "/client/checkout";
    }

    @PostMapping("/payment")
    public String payment(@Valid @ModelAttribute("orderDTO") OrderDTO orderDTO, BindingResult bindingResult, HttpSession session, RedirectAttributes redirectAttributes , Model model){
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        Object username =session.getAttribute("userLogin");
        User userLogin = userService.getUserByUsername(username.toString());
        if (bindingResult.hasErrors()){
            model.addAttribute("cart", cart);
            return "/client/checkout";
        }
        try {
            for (CartItem item : cart) {
                Product p = productService.getProductById(item.getProduct().getProId());
                if (p.getQuantity() < item.getQuantity()) {
                    model.addAttribute("error", "Sản phẩm " + p.getProName() + " chỉ còn " + p.getQuantity() + " sản phẩm.");
                    model.addAttribute("cart", cart);
                    return "/client/checkout";
                }
            }
            Order order = new Order();
            order.setCreatedAt(LocalDateTime.now());
            order.setReceiverName(orderDTO.getReceiverName());
            order.setPhone(orderDTO.getPhone());
            order.setAddress(orderDTO.getAddress());
            order.setNote(orderDTO.getNote());
            order.setUser(userLogin);
            orderService.addOrder(order , cart);
            session.removeAttribute("cart");
            return "redirect:/client/home?success=true";
        } catch (Exception e){
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            model.addAttribute("cart", cart);
            return "/client/checkout";
        }
    }

    @GetMapping("/listOrders")
    public String listOrders(HttpSession session , Model model){
        Object username = session.getAttribute("userLogin");
        if (username == null) {
            return "redirect:/auth/login";
        }
        List<Order> orders = orderRepository.findAll();
        User userLogin = userService.getUserByUsername(username.toString());
        model.addAttribute("orders", orders);
        return "/client/listOrders";
    }
}
