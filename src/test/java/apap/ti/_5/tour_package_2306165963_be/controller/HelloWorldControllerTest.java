package apap.ti._5.tour_package_2306165963_be.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * Unit tests for the HelloWorldController
 */
@WebMvcTest(HelloWorldControllerTest.class)
public class HelloWorldControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test the root endpoint (Panduan)
     */
    @Test
    public void testHelloWorldEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("hello"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("title"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("message"));
    }

    /**
     * Test the path parameter endpoint (Latihan)
     */
    @Test
    public void testHelloWithPathParamEndpoint() throws Exception {
        String testName = "Tara";
        
        mockMvc.perform(MockMvcRequestBuilders.get("/" + testName)) 
                .andExpect(MockMvcResultMatchers.status().isOk()) 
                .andExpect(MockMvcResultMatchers.view().name("hello"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("title"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("message"));
    }
}