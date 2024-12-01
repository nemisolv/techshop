package net.nemisolv.techshop.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nemisolv.techshop.core._enum.RoleName;
import net.nemisolv.techshop.entity.*;
import net.nemisolv.techshop.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final BrandRepository brandRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting database seeding...");
        seedPermissions();
        seedRoles();
        seedAdminUser();
        seedBrands();
        seedCategories();
        seedProductsAndInventory();
        log.info("Database seeding completed.");
    }

    private void seedPermissions() {
        if (permissionRepository.count() == 0) {
            log.info("Seeding permissions...");
            List<Permission> permissions = List.of(
                    // User Management
                    Permission.builder().name("CREATE_USER").description("Can create users").build(),
                    Permission.builder().name("UPDATE_USER").description("Can update users").build(),
                    Permission.builder().name("DELETE_USER").description("Can delete users").build(),
                    Permission.builder().name("VIEW_USER").description("Can view users").build(),

                    // Product Management
                    Permission.builder().name("CREATE_PRODUCT").description("Can create products").build(),
                    Permission.builder().name("UPDATE_PRODUCT").description("Can update products").build(),
                    Permission.builder().name("DELETE_PRODUCT").description("Can delete products").build(),
                    Permission.builder().name("VIEW_PRODUCT").description("Can view products").build(),

                    // Inventory Management
                    Permission.builder().name("VIEW_INVENTORY").description("Can view inventory").build(),
                    Permission.builder().name("UPDATE_INVENTORY").description("Can update inventory").build(),

                    // Order Management
                    Permission.builder().name("CREATE_ORDER").description("Can create orders").build(),
                    Permission.builder().name("UPDATE_ORDER").description("Can update orders").build(),
                    Permission.builder().name("DELETE_ORDER").description("Can delete orders").build(),
                    Permission.builder().name("VIEW_ORDER").description("Can view orders").build(),

                    // Report Management
                    Permission.builder().name("VIEW_SALES_REPORT").description("Can view sales reports").build(),
                    Permission.builder().name("VIEW_INVENTORY_REPORT").description("Can view inventory reports").build(),

                    // Inventory Management
                    Permission.builder().name("MANAGE_INVENTORY").description("Can manage inventory").build(),
                    Permission.builder().name("UPDATE_STOCK").description("Can update stock levels").build(),

                    // Brand Management
                    Permission.builder().name("CREATE_BRAND").description("Can create brands").build(),
                    Permission.builder().name("UPDATE_BRAND").description("Can update brands").build(),
                    Permission.builder().name("DELETE_BRAND").description("Can delete brands").build(),
                    Permission.builder().name("VIEW_BRAND").description("Can view brands").build()
            );
            permissionRepository.saveAll(permissions);
        }
    }

    private void seedRoles() {
        if (roleRepository.count() == 0) {
            log.info("Seeding roles...");
            List<Permission> allPermissions = permissionRepository.findAll();
            Role adminRole = Role.builder()
                    .name(RoleName.ADMIN)
                    .description("Administrator with all permissions")
                    .permissions(Set.copyOf(allPermissions))
                    .build();

            Role managerRole = Role.builder()
                    .name(RoleName.MANAGER)
                    .description("Manager with restricted permissions")
                    .permissions(allPermissions.stream()
                            .filter(p -> !p.getName().equals("DELETE_USER"))
                            .collect(Collectors.toSet()))
                    .build();

            Role staffRole = Role.builder()
                    .name(RoleName.STAFF)
                    .description("Staff with view-only permissions")
                    .permissions(allPermissions.stream()
                            .filter(p -> p.getName().startsWith("VIEW_"))
                            .collect(Collectors.toSet()))
                    .build();

            Role assistantRole = Role.builder()
                    .name(RoleName.ASSISTANT)
                    .description("Assistant with limited permissions")
                    .permissions(allPermissions.stream()
                            .filter(p -> p.getName().startsWith("VIEW_") || p.getName().equals("CREATE_ORDER"))
                            .collect(Collectors.toSet()))
                    .build();

            roleRepository.saveAll(List.of(adminRole, managerRole, staffRole, assistantRole));
        }
    }

    private void seedAdminUser() {
        if (userRepository.count() == 0) {
            log.info("Seeding admin user...");
            Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));

            User admin = User.builder()
                    .username("admin")
                    .email("admin@techshop.com")
                    .password(passwordEncoder.encode("admin"))
                    .role(adminRole)
                    .build();

            userRepository.save(admin);
            log.info("Admin user created: email = admin@techshop.com, password = admin");
        }
    }

    private void seedBrands() {
        if (brandRepository.count() == 0) {
            log.info("Seeding brands...");
            List<Brand> brands = List.of(
                    Brand.builder().name("Apple").description("Premium tech devices").build(),
                    Brand.builder().name("Samsung").description("Innovative electronics").build(),
                    Brand.builder().name("Dell").description("High-performance computers").build(),
                    Brand.builder().name("Sony").description("Electronics and entertainment").build(),
                    Brand.builder().name("Lenovo").description("Reliable laptops and PCs").build(),
                    Brand.builder().name("Asus").description("Gaming and versatile laptops").build(),
                    Brand.builder().name("Generic").description("Unbranded or unknown products").build()
            );
            brandRepository.saveAll(brands);
            log.info("Brands seeded.");
        }
    }


    private void seedCategories() {
        if (categoryRepository.count() == 0) {
            log.info("Seeding categories...");
            List<Category> categories = List.of(
                    Category.builder().name("Electronics").description("Electronic gadgets and devices").build(),
                    Category.builder().name("Clothing").description("Apparel and fashion items").build(),
                    Category.builder().name("Home Appliances").description("Appliances for home use").build()
            );
            categoryRepository.saveAll(categories);
        }
    }

    private void seedProductsAndInventory() {
        if (productRepository.count() == 0) {
            log.info("Seeding products and inventory...");

            // Lấy danh mục từ database
            Category electronics = getCategoryByName("Electronics");
            Category clothing = getCategoryByName("Clothing");
            Category homeAppliances = getCategoryByName("Home Appliances");

            // Lấy thương hiệu từ database
            Brand apple = getBrandByName("Apple");
            Brand samsung = getBrandByName("Samsung");
            Brand dell = getBrandByName("Dell");
            Brand lenovo = getBrandByName("Lenovo");
            Brand generic = getBrandByName("Generic");  // Hàng không có thương hiệu

            // Tạo các sản phẩm
            Product smartphone = Product.builder()
                    .name("Smartphone")
                    .description("High-end smartphone with the latest features")
                    .price(BigDecimal.valueOf(799.99))
                    .category(electronics)
                    .brand(apple)
                    .build();

            Product laptop = Product.builder()
                    .name("Laptop")
                    .description("Powerful laptop for work and play")
                    .price(BigDecimal.valueOf(1200.00))
                    .category(electronics)
                    .brand(dell)
                    .build();

            Product tShirt = Product.builder()
                    .name("T-Shirt")
                    .description("Comfortable cotton t-shirt")
                    .price(BigDecimal.valueOf(19.99))
                    .category(clothing)
                    .brand(generic)  // Không có thương hiệu
                    .build();

            Product vacuumCleaner = Product.builder()
                    .name("Vacuum Cleaner")
                    .description("High-power vacuum cleaner")
                    .price(BigDecimal.valueOf(150.00))
                    .category(homeAppliances)
                    .brand(lenovo)
                    .build();

            // Lưu các sản phẩm vào database
            productRepository.saveAll(List.of(smartphone, laptop, tShirt, vacuumCleaner));

            // Tạo các bản ghi kho (Inventory) cho từng sản phẩm
            List<Inventory> inventories = List.of(
                    Inventory.builder().product(smartphone).quantity(50).build(),
                    Inventory.builder().product(laptop).quantity(40).build(),
                    Inventory.builder().product(tShirt).quantity(100).build(),
                    Inventory.builder().product(vacuumCleaner).quantity(30).build()
            );

            // Lưu kho vào database
            inventoryRepository.saveAll(inventories);
            log.info("Products and inventory seeded.");
        }
    }


    private Category getCategoryByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new RuntimeException("Category not found: " + name));
    }
    private Brand getBrandByName(String name) {
        return brandRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new RuntimeException("Brand not found: " + name));
    }
}
