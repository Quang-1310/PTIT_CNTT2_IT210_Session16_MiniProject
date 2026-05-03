package ra.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ra.model.dto.CategoryDTO;
import ra.model.dto.ProductDTO;
import ra.model.entity.Category;
import ra.model.entity.Product;
import ra.service.CategoryService;
import ra.service.ProductService;

@Controller
@RequestMapping("/admin/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("")
    public String listProduct(@RequestParam(defaultValue = "1") int page,
                              Model model,
                              HttpSession session) {

        Object user = session.getAttribute("userLogin");
        if (user == null) {
            return "redirect:/auth/login";
        }

        Page<Product> products = productService.getAllProducts(page, 5);

        model.addAttribute("userLogin", user.toString());
        model.addAttribute("products", products.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());

        return "admin/product/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model, HttpSession session) {
        if (session.getAttribute("userLogin") == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("product", new ProductDTO());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/product/add";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("product") ProductDTO dto,
                      BindingResult result,
                      Model model) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/product/add";
        }

        Product p = new Product();
        p.setProName(dto.getProName());
        p.setProDesc(dto.getProDesc());
        p.setPrice(dto.getPrice());
        p.setQuantity(dto.getQuantity());
        p.setCategory(categoryService.getCategoryById(dto.getCategoryId()));
        p.setDelete(false);

        productService.addProduct(p);

        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, HttpSession session) {
        if (session.getAttribute("userLogin") == null) {
            return "redirect:/auth/login";
        }
        Product p = productService.getProductById(id);
        ProductDTO dto = new ProductDTO();
        dto.setProId(p.getProId());
        dto.setProName(p.getProName());
        dto.setProDesc(p.getProDesc());
        dto.setPrice(p.getPrice());
        dto.setQuantity(p.getQuantity());
        dto.setCategoryId(p.getCategory().getCatId());

        model.addAttribute("product", dto);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/product/edit";
    }

    @PostMapping("/edit")
    public String showEditForm(@Valid @ModelAttribute("product") ProductDTO productDTO , BindingResult result , Model model, HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/product/edit";
        }
        Product p = new Product();
        p.setProId(productDTO.getProId());
        p.setProName(productDTO.getProName());
        p.setProDesc(productDTO.getProDesc());
        p.setDelete(false);
        p.setPrice(productDTO.getPrice());
        p.setQuantity(productDTO.getQuantity());
        p.setCategory(categoryService.getCategoryById(productDTO.getCategoryId()));
        productService.updateProduct(p);
        return "redirect:/admin/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, HttpSession session) {
        if (session.getAttribute("userLogin") == null) {
            return "redirect:/auth/login";
        }
        productService.deleteProduct(id);
        return "redirect:/admin/products";
    }
}