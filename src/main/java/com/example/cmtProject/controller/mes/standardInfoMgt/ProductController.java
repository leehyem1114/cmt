package com.example.cmtProject.controller.mes.standardInfoMgt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.cmtProject.entity.Product;
import com.example.cmtProject.service.mes.standardInfoMgt.ProductService;

@Controller
@RequestMapping("/pdt")
public class ProductController {

	@Autowired
	private ProductService productService;
	
	@GetMapping("")
	public String main() {
		return "mes/standardInfoMgt/product/pdt";
	}
	
	@GetMapping("/list")
    public String list(Model model) throws Exception {
		
        List<Product> productList = productService.list();
        System.out.println("productList:" + productList);
        model.addAttribute("productList", productList);
        
        return "mes/standardInfoMgt/product/list";      
    }
}
