package ra.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.model.dto.CategoryDTO;
import ra.model.entity.Category;
import ra.model.entity.Product;
import ra.service.CategoryService;
import ra.service.ProductService;

import java.util.List;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping("")
    public String list(Model model, HttpSession session) {

        Object user = session.getAttribute("userLogin");
        if (user == null) {
            return "redirect:/auth/login";
        }
        Object role = session.getAttribute("role");
        if(role != null && role.equals("user")){
            return "redirect:/client/home";
        }
        model.addAttribute("userLogin", user.toString());
        model.addAttribute("categories", categoryService.getAllCategories());

        return "admin/category/list";
    }

    @GetMapping("/add")
    public String showAdd(Model model, HttpSession session) {
        if (session.getAttribute("userLogin") == null) {
            return "redirect:/auth/login";
        }
        Object role = session.getAttribute("role");
        if(role != null && role.equals("user")){
            return "redirect:/client/home";
        }
        model.addAttribute("category", new CategoryDTO());
        return "admin/category/add";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("category") CategoryDTO dto,
                      BindingResult result) {

        if (result.hasErrors()) {
            return "admin/category/add";
        }

        Category c = new Category();
        c.setCatName(dto.getCatName());

        categoryService.addCategory(c);

        return "redirect:/admin/categories";
    }

    @GetMapping("/edit/{id}")
    public String showEdit(@PathVariable("id") Long id, Model model, HttpSession session) {
        Object user = session.getAttribute("userLogin");
        if (user == null) {
            return "redirect:/auth/login";
        }
        Object role = session.getAttribute("role");
        if(role != null && role.equals("user")){
            return "redirect:/client/home";
        }
        Category category = categoryService.getCategoryById(id);
        CategoryDTO dto = new CategoryDTO();
        dto.setCatId(category.getCatId());
        dto.setCatName(category.getCatName());
        model.addAttribute("category", dto);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/category/edit";
    }

    @PostMapping("/edit")
    public String showEdit(@Valid @ModelAttribute("category") CategoryDTO categoryDTO , BindingResult result , Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "admin/category/edit";
        }
        Category category = new Category();
        category.setCatId(categoryDTO.getCatId());
        category.setCatName(categoryDTO.getCatName());
        categoryService.updateCategory(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        Object user = session.getAttribute("userLogin");
        if (user == null) {
            return "redirect:/auth/login";
        }
        Object role = session.getAttribute("role");
        if(role != null && role.equals("user")){
            return "redirect:/client/home";
        }
        List<Product> activeProducts = productService.getActiveProductByCategory(id);
        if (!activeProducts.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "Không thể xóa danh mục đang có sản phẩm!");
            return "redirect:/admin/categories";
        }
        categoryService.deleteCategoryById(id);

        redirectAttributes.addFlashAttribute("success",
                "Xóa danh mục thành công!");

        return "redirect:/admin/categories";
    }

}