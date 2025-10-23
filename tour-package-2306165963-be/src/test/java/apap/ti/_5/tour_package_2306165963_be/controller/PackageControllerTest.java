package apap.ti._5.tour_package_2306165963_be.controller;

import apap.ti._5.tour_package_2306165963_be.model.Package;
import apap.ti._5.tour_package_2306165963_be.service.PackageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PackageController.class)
@ActiveProfiles("test")
class PackageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PackageService packageService;

    private String existingId;
    private String processedId;
    private String fakeId = UUID.randomUUID().toString();
    private Package pendingPackage;
    private Package processedPackage;

    @BeforeEach
    void setup() {
        existingId = UUID.randomUUID().toString();
        processedId = UUID.randomUUID().toString();
        
        pendingPackage = Package.builder()
                .id(existingId)
                .packageName("Pending Package")
                .status("Pending")
                .quota(5)
                .price(1000000L)
                .startDate(LocalDateTime.now().plusDays(10))
                .endDate(LocalDateTime.now().plusDays(15))
                .build();

        processedPackage = Package.builder()
                .id(processedId)
                .packageName("Processed Package")
                .status("Processed")
                .quota(10)
                .price(2000000L)
                .startDate(LocalDateTime.now().plusDays(10))
                .endDate(LocalDateTime.now().plusDays(15))
                .build();

        Mockito.when(packageService.getAllPackages())
               .thenReturn(List.of(pendingPackage, processedPackage));
        
        Mockito.when(packageService.getPackageById(existingId))
               .thenReturn(Optional.of(pendingPackage));
        
        Mockito.when(packageService.getPackageById(processedId))
               .thenReturn(Optional.of(processedPackage));
        
        Mockito.when(packageService.getPackageById(fakeId))
               .thenReturn(Optional.empty());

        Mockito.when(packageService.deletePackage(existingId)).thenReturn(true);
        Mockito.when(packageService.deletePackage(fakeId)).thenReturn(false);
        
        Mockito.when(packageService.createPackage(any(Package.class)))
               .thenReturn(pendingPackage);
        Mockito.when(packageService.updatePackage(any(Package.class)))
               .thenReturn(pendingPackage);
        Mockito.when(packageService.processPackage(any(String.class)))
               .thenReturn(processedPackage);
    }

    @Test
    void testGetAllPackagesSuccess() throws Exception {
        mockMvc.perform(get("/package"))
                .andExpect(status().isOk())
                .andExpect(view().name("package/view-all"))
                .andExpect(model().attributeExists("listPackage"));
    }

    @Test
    void testGetPackageByIdFound() throws Exception {
        mockMvc.perform(get("/package/" + existingId))
                .andExpect(status().isOk())
                .andExpect(view().name("package/detail"))
                .andExpect(model().attributeExists("currentPackage"));
    }

    @Test
    void testGetPackageByIdNotFound() throws Exception {
        mockMvc.perform(get("/package/" + fakeId))
                .andExpect(status().isOk())
                .andExpect(view().name("error/404"))
                .andExpect(model().attributeExists("message"));
    }
    
    @Test
    void testFormCreatePackage() throws Exception {
        mockMvc.perform(get("/package/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("package/form"))
                .andExpect(model().attribute("isEdit", false))
                .andExpect(model().attributeExists("packageData"));
    }

    @Test
    void testCreatePackageSuccess() throws Exception {
        mockMvc.perform(post("/package/create")
                .param("packageName", "New Test Package")
                .param("quota", "3")
                .param("price", "5000000")
                .param("startDate", "2026-01-01T10:00")
                .param("endDate", "2026-01-05T18:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/package"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testCreatePackageFailure_InvalidData() throws Exception {
        Mockito.when(packageService.createPackage(any(Package.class)))
               .thenThrow(new IllegalArgumentException("Invalid package data."));
        
        mockMvc.perform(post("/package/create")
                .param("packageName", "Invalid Package")
                .param("quota", "0") 
                .param("price", "10000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/package/create"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void testFormEditPackageFound_Pending() throws Exception {
        mockMvc.perform(get("/package/update/" + existingId))
                .andExpect(status().isOk())
                .andExpect(view().name("package/form"))
                .andExpect(model().attribute("isEdit", true))
                .andExpect(model().attributeExists("packageData"));
    }

    @Test
    void testFormEditPackageFound_Processed_CannotEdit() throws Exception {
        mockMvc.perform(get("/package/update/" + processedId))
                .andExpect(status().isOk())
                .andExpect(view().name("error/404")) // Harusnya mengarah ke 404/Error view
                .andExpect(model().attribute("title", "Cannot Edit Package"));
    }

    @Test
    void testUpdatePackageSuccess() throws Exception {
        mockMvc.perform(post("/package/update/" + existingId) 
                .param("packageName", "Updated Package Name")
                .param("quota", "10")
                .param("price", "10000000")
                .param("status", "Pending") // Harus 'Pending' agar bisa diupdate
                .param("startDate", "2025-11-01T09:00") 
                .param("endDate", "2025-11-07T18:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/package/" + existingId)) // Perbaikan: Redirect ke detail, bukan view-all
                .andExpect(flash().attributeExists("successMessage"));
    }
    
    @Test
    void testUpdatePackageFailure_ServiceError() throws Exception {
        Mockito.when(packageService.updatePackage(any(Package.class)))
               .thenThrow(new IllegalStateException("Update validation failed."));
        
        mockMvc.perform(post("/package/update/" + existingId)
                .param("packageName", "Updated Package Name")
                .param("quota", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/package/update/" + existingId))
                .andExpect(flash().attributeExists("errorMessage"));
    }
    
    @Test
    void testDeletePackageSuccess() throws Exception {
        mockMvc.perform(post("/package/delete/" + existingId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/package"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testDeletePackageNotFound() throws Exception {
        mockMvc.perform(post("/package/delete/" + fakeId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/package"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void testProcessPackageSuccess() throws Exception {
        mockMvc.perform(post("/package/process/" + existingId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/package/" + existingId))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testProcessPackageNotFound() throws Exception {
        Mockito.when(packageService.getPackageById(fakeId)).thenReturn(Optional.empty()); 

        Mockito.when(packageService.processPackage(fakeId))
               .thenThrow(new IllegalStateException("Package not found for processing."));

        mockMvc.perform(post("/package/process/" + fakeId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/package/" + fakeId))
                .andExpect(flash().attributeExists("errorMessage"));
    }
    
    @Test
    void testProcessPackageFailure_ServiceError() throws Exception {
        Mockito.when(packageService.processPackage(existingId))
               .thenThrow(new IllegalStateException("Cannot process package: all plans must be complete."));

        mockMvc.perform(post("/package/process/" + existingId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/package/" + existingId))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}