package edu.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ujym {
@GetMapping("/")
public String page()
{
	return "NewFile";
}

}
