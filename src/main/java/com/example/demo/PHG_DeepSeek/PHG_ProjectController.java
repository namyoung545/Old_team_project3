package com.example.demo.PHG_DeepSeek;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PHG_ProjectController {

	@Autowired
	private OllamaManager ollamaManager;

	@GetMapping("/deepSeek")
	public String deepSeek(Model model) throws IOException {
		ollamaManager.startOllamaIfNotRunning();
		return "/PHG/PHG_deepSeek";
	}
}
