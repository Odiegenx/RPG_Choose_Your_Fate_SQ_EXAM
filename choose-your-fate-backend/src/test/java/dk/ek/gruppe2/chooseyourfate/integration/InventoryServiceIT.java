package dk.ek.gruppe2.chooseyourfate.integration;

import dk.ek.gruppe2.chooseyourfate.dto.InventoryResponseDTO;
import dk.ek.gruppe2.chooseyourfate.model.mysql.CharacterAvatar;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Inventory;
import dk.ek.gruppe2.chooseyourfate.model.mysql.InventoryHasItem;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Item;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.CharacterAvatarRepository;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.InventoryHasItemRepository;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.InventoryRepository;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.ItemRepository;
import dk.ek.gruppe2.chooseyourfate.service.InventoryService;
import dk.ek.gruppe2.chooseyourfate.service.ItemService;
import dk.ek.gruppe2.chooseyourfate.TestContainerConfig;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@Transactional
public class InventoryServiceIT {

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", TestContainerConfig.MYSQL::getJdbcUrl);
        registry.add("spring.datasource.password", TestContainerConfig.MYSQL::getPassword);
        registry.add("spring.datasource.username", TestContainerConfig.MYSQL::getUsername);
    }

    @Autowired
    InventoryRepository inventoryRepository;
    @Autowired
    InventoryHasItemRepository inventoryHasItemRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    CharacterAvatarRepository characterAvatarRepository;

    @Autowired
    ItemService itemService;

    @Autowired
    InventoryService inventoryService;

    @Test
    void getInventoryByCharacterId_ShouldReturnDTO_WhenInventoryExists() {
        Integer query = 1;
        InventoryResponseDTO responseDTO = inventoryService.getInventoryByCharacterId(query);
        assert(responseDTO.getCharacterName().equals("Lyra"));
    }

}
