package ra.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ra.model.entity.Category;
import ra.model.entity.Product;
import ra.service.CategoryService;
import ra.service.ProductService;

@Controller
@RequestMapping("/client")
public class ClientController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/home")
    public String home(
            @RequestParam(required = false) String proName,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Long catId,
            @RequestParam(defaultValue = "1") int page,
            Model model,
            HttpSession session
    ) {

        if (session.getAttribute("userLogin") == null) {
            return "redirect:/auth/login";
        }

        Object role = session.getAttribute("role");
        if(role != null && role.equals("admin")){
            return "redirect:/admin/products";
        }

        int size = 5;

        Category category = null;
        if (catId != null) {
            category = categoryService.getCategoryById(catId);
        }

        Page<Product> productPage = productService.getProductByProNameAndPriceAndCategory(
                proName,
                minPrice,
                maxPrice,
                category,
                page,
                size
        );

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());

        model.addAttribute("proName", proName);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("catId", catId);

        model.addAttribute("categories", categoryService.getAllCategories());

        return "client/home";
    }
}
